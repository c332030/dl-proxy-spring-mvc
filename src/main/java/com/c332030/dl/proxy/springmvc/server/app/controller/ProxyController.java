package com.c332030.dl.proxy.springmvc.server.app.controller;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

import com.c332030.controller.BaseController;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

    @RequestMapping("proxy")
    public ResponseEntity<?> proxy(@RequestParam(name = URL_STR, required = false) String urlStr) {
        try {

            log.info("{}: {}", URL_STR, urlStr);
            if (StringUtils.isBlank(urlStr)) {
                return UNKNOWN_PATH;
            }

            Headers.Builder okHeadersBuilder = new Headers.Builder();
            request.getHeaderNames().asIterator().forEachRemaining(headerName
                -> okHeadersBuilder.set(headerName, request.getHeader(headerName)));

            Request okRequest = new Request.Builder()
                .url(urlStr)
                .headers(okHeadersBuilder.build())
                .get()
                .build();

            // System.out.println("Request: " + JSONUtils.toJson(okRequest.headers()));

            Response okResponse = okHttpClient.newCall(okRequest).execute();

            // System.out.println("Response: " + JSONUtils.toJson(okResponse.headers()));

            var responseCode = okResponse.code();
            if (200 != responseCode) {
                return new ResponseEntity<>(okResponse.message(), HttpStatus.valueOf(responseCode));
            }

            okResponse.headers().iterator().forEachRemaining(pair -> {
                response.setHeader(pair.getFirst(), pair.getSecond());
            });

            var contentDisposition = okResponse.headers().get(HttpHeaders.CONTENT_DISPOSITION);
            var newContentDisposition = MessageFormat
                .format(CONTENT_DISPOSITION_TEMPLATE, FilenameUtils.getName(urlStr));
            log.info("HttpHeaders.CONTENT_DISPOSITION: {}, newContentDisposition: {}", contentDisposition, newContentDisposition);

            if(StringUtils.isEmpty(contentDisposition)
                || !ATTACHMENT.equals(contentDisposition)
            ) {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, newContentDisposition);
            }

            InputStream inputStream = Objects.requireNonNull(okResponse.body()).byteStream();
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

}
