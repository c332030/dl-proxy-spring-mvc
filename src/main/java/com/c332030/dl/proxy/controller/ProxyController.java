package com.c332030.dl.proxy.controller;

import java.net.MalformedURLException;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.Objects;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

import com.c332030.controller.CAbstractController;
import com.c332030.util.SpringWebUtils;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * <p>
 * Description: controler
 * </p>
 *
 * @author c332030
 * @version 1.0
 */
@Slf4j
@Controller
public class ProxyController extends CAbstractController {

    private static final String URL_STR = "url";

    private static final String ATTACHMENT = "attachment";

    private static final ResponseEntity<String> UNKNOWN_PATH = ResponseEntity.ok("unknown url");

    private static final String CONTENT_DISPOSITION_TEMPLATE = "attachment; filename=\"{0}\"";

    @Autowired
    private OkHttpClient okHttpClient;

    @GetMapping("proxy")
    public ResponseEntity<?> proxy(@RequestParam(name = URL_STR, required = false) String urlStr) {
        try {

            log.info("{}: {}", URL_STR, urlStr);
            if (StringUtils.isBlank(urlStr)) {
                return UNKNOWN_PATH;
            }

            var okHeadersBuilder = new Headers.Builder();
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {

                if(HttpHeaders.HOST.equalsIgnoreCase(headerName)) {
                    return;
                }
                okHeadersBuilder.set(headerName, request.getHeader(headerName));
            });

            var okRequest = new Request.Builder().get()
                .url(urlStr)
                .headers(okHeadersBuilder.build())
                .build();

            var okResponse = okHttpClient.newCall(okRequest).execute();
            response.setStatus(okResponse.code());

            var contentDisposition = new String[1];
            okResponse.headers().iterator().forEachRemaining(pair -> {

                var key = pair.getFirst();
                var value = pair.getSecond();

                if(HttpHeaders.CONTENT_DISPOSITION.equals(key)) {
                    contentDisposition[0] = value;
                }
                response.setHeader(key, value);
            });
            updateContentDisposition(contentDisposition[0], urlStr);

            var inputStream = Objects.requireNonNull(okResponse.body()).byteStream();
            try(inputStream) {
                inputStream.transferTo(response.getOutputStream());
            }
        } catch (MalformedURLException e) {

            log.error("error url", e);
            return ResponseEntity.ok("error url：" + urlStr);
        } catch (ClientAbortException | SocketException e) {

            log.debug("ignore exception", e);
            return SpringWebUtils.RESPONSE_ENTITY_EMPTY;
        } catch (Exception e) {

            log.error("unknown error", e);
            return ResponseEntity.ok(e.getMessage());
        }

        return null;
    }

    private void updateContentDisposition(String contentDisposition, String urlStr) {

        if(StringUtils.isEmpty(contentDisposition)
            || !ATTACHMENT.equals(contentDisposition)) {



            var fileName = FilenameUtils.getName(urlStr);
            var newContentDisposition = MessageFormat.format(CONTENT_DISPOSITION_TEMPLATE, fileName);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, newContentDisposition);
        }
    }

}
