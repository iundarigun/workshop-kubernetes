package br.com.devcave.workshop.kubernetes.proxy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        log.info("M=ping");
        return "ping";
    }
}
