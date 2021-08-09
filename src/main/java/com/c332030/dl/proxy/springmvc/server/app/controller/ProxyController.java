package com.c332030.dl.proxy.springmvc.server.app.controller;


import java.net.*;
import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.c332030.controller.BaseController;
import com.c332030.web.servlet.util.CServletUtils;

import lombok.extern.slf4j.Slf4j;

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

    @RequestMapping("proxy")
    public ResponseEntity<?> proxy(@RequestParam(name = URL_STR, required = false) String urlStr) {
        try {

            log.info("{}: {}", URL_STR, urlStr);
            if (StringUtils.isBlank(urlStr)) {
                return UNKNOWN_PATH;
            }

            var conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod(RequestMethod.GET.name());
            CServletUtils.setHeaders(request, conn);

            var responseCode = conn.getResponseCode();
            response.setStatus(responseCode);

            CServletUtils.setHeaders(conn, response);

            var contentDispositionList = conn.getHeaderFields().get(HttpHeaders.CONTENT_DISPOSITION);
            var newContentDisposition = MessageFormat.format(CONTENT_DISPOSITION_TEMPLATE,
                FilenameUtils.getName(urlStr));
            log.info("HttpHeaders.CONTENT_DISPOSITION: {}, newContentDisposition: {}", contentDispositionList, newContentDisposition);

            if(CollectionUtils.isEmpty(contentDispositionList)
                || !ATTACHMENT.equals(contentDispositionList.get(0))
            ) {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, newContentDisposition);
            }

            IOUtils.copy(conn.getInputStream(), response.getOutputStream());

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
