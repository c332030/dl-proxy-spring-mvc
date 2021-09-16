package com.c332030.dl.proxy.server.app.controller;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import com.c332030.controller.BaseController;

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
public class ProxyController extends BaseController {

    private static final String URL_STR = "url";

    private static final String ATTACHMENT = "attachment";

    private static final ResponseEntity<String> UNKNOWN_PATH = newResponseEntityOK("unknown url");

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
                IOUtils.copy(inputStream, response.getOutputStream());
            }

            return null;
        } catch (MalformedURLException e) {

            log.error("error url", e);
            return newResponseEntityOK("error url：" + urlStr);
        } catch (Exception e) {

            log.error("unknown error", e);
            return newResponseEntityOK(e.getMessage());
        }
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
