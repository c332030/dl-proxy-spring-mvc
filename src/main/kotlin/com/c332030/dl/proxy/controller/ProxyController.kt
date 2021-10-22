package com.c332030.dl.proxy.controller

import com.c332030.CLogger
import com.c332030.controller.CAbstractController
import com.c332030.util.SpringWebUtils
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.StringUtils
import org.eclipse.jetty.io.EofException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.net.MalformedURLException
import java.net.SocketException
import java.text.MessageFormat

@Controller
class ProxyController(@Autowired val okHttpClient: OkHttpClient) : CAbstractController() {

  private val log = CLogger(this.javaClass)

  private val unknownPath = ResponseEntity.ok("unknown url")

  @GetMapping("proxy")
  fun proxy(url: String?): ResponseEntity<*>? {

    log.info { "url : $url" }

    try {

      if (url.isNullOrBlank()) {
        return unknownPath
      }

      val okHeadersBuilder = Headers.Builder()
      request.headerNames.asIterator().forEachRemaining { headerName ->
        if (!StringUtils.equalsIgnoreCase(HttpHeaders.HOST, headerName)) {
          okHeadersBuilder[headerName] = request.getHeader(headerName)
        }
      }

      val okRequest = Request.Builder().get()
        .url(url)
        .headers(okHeadersBuilder.build())
        .build()

      val okResponse = okHttpClient.newCall(okRequest).execute()
      response.status = okResponse.code

      val contentDisposition = arrayOf("")
      okResponse.headers.iterator().forEachRemaining { pair ->

        val key = pair.first
        val value = pair.second

        if (HttpHeaders.CONTENT_DISPOSITION == key) {
          contentDisposition[0] = value
        }
        response.setHeader(key, value)
      }

      updateContentDisposition(contentDisposition[0], url)

      val inputStream = okResponse.body?.byteStream()
      inputStream?.use {
        inputStream.transferTo(response.outputStream)
      }
    } catch (e: Exception) {
      return when(e) {
        is MalformedURLException -> {
          log.error({ "error url" }, e)
          ResponseEntity.ok("error url：$url")
        }
        is SocketException, is EofException -> {
          log.debug({ "ignore exception" }, e)
          SpringWebUtils.RESPONSE_ENTITY_EMPTY
        }
        else -> {
          log.error({ "unknown error" }, e)
          ResponseEntity.ok(e.message)
        }
      }
    }
    return null
  }

  private val attachment = "attachment"

  fun updateContentDisposition(contentDisposition: String, url: String) {

    if (StringUtils.isEmpty(contentDisposition)
      || attachment != contentDisposition
    ) {

      val fileName = FilenameUtils.getName(url)
      val newContentDisposition = MessageFormat.format(url, fileName)
      response.setHeader(HttpHeaders.CONTENT_DISPOSITION, newContentDisposition)
    }
  }
}
