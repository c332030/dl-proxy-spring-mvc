package com.c332030.dl.proxy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {

  @GetMapping("/")
  fun root(): String{
    return "dl-proxy"
  }

}
