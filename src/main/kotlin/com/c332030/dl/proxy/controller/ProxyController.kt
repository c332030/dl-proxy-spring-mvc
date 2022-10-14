package com.c332030.dl.proxy.controller

import com.c332030.CLogger
import com.c332030.controller.CAbstractController
import com.c332030.dl.proxy.service.ProxyService
import com.c332030.util.SpringWebUtils
import org.eclipse.jetty.io.EofException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.net.MalformedURLException
import java.net.SocketException

@Controller
class ProxyController(
  private val proxyService: ProxyService
) : CAbstractController() {

  private val log = CLogger(this.javaClass)

  private val unknownPath = ResponseEntity.ok("unknown url")

  @GetMapping("proxy")
  fun proxy(url: String?): ResponseEntity<*>? {

    try {

      if (url.isNullOrBlank()) {
        return unknownPath
      }
      proxyService.proxy(url)
    } catch (e: Exception) {
      return when (e) {
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
}
