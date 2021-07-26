package com.c332030.dl.proxy.springmvc.server.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import static com.c332030.constant.PackageConstants.BASE;

/**
 * <p>
 * Description: ProxySpringMvcApplication
 * </p>
 *
 * @author c332030
 * @version 1.0
 */
@SpringBootApplication(scanBasePackages = BASE)
@ConfigurationPropertiesScan(basePackages = BASE)
// @MapperScan(basePackages = {
//     BASE_DAO
// })
public class DlProxySpringMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlProxySpringMvcApplication.class, args);
    }

}
