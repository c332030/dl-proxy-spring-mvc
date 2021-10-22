
package com.c332030.dl.proxy

import com.c332030.constant.PackageConstants
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = [PackageConstants.BASE])
@ConfigurationPropertiesScan(basePackages = [PackageConstants.BASE])
class DlProxyApplication

fun main(args: Array<String>) {
  runApplication<DlProxyApplication>(*args)
}
