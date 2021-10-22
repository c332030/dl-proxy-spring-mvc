package com.c332030.dl.proxy.test

import lombok.SneakyThrows
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.junit.jupiter.api.Test

/**
 * Description: ProxyTest
 *
 * @author c332030
 * @version 1.0
 */

private val log = KotlinLogging.logger {}

@Slf4j
class ProxyTest {

  @Test
  @SneakyThrows
  fun test() {
    log.info("result")
  }

}
