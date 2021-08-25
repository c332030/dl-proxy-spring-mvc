package com.c332030.dl.proxy.server.app;

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
public class DlProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlProxyApplication.class, args);
    }

}
