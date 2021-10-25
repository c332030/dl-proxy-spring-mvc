package com.c332030.dl.proxy.test

import com.c332030.CLogger
import lombok.SneakyThrows
import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test

/**
 * Description: ProxyTest
 *
 * @author c332030
 * @version 1.0
 */

@Slf4j
class ProxyTest {

  private val log = CLogger(this.javaClass)

  @Test
  @SneakyThrows
  fun test() {
    log.info { "result" }
  }

}