package com.c332030.dl.proxy.test

import com.c332030.CLogger
import org.apache.commons.io.FilenameUtils
import org.junit.jupiter.api.Test

/**
 * Description: ProxyTest
 *
 * @author c332030
 * @version 1.0
 */

class ProxyTest {

  private val log = CLogger(this.javaClass)

  @Test
  fun test() {
    log.info { "result" }
  }

  @Test
  fun attachment() {
    log.info { "attachment; filename=\"${FilenameUtils.getName("c:\\1.txt")}\"" }
  }

}
