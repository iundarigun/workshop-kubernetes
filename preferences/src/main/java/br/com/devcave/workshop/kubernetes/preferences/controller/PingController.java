package br.com.devcave.workshop.kubernetes.preferences.controller;

import br.com.devcave.workshop.kubernetes.preferences.client.ProxyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PingController {

    @Autowired
    private ProxyClient proxyClient;

    @GetMapping("ping")
    public String ping(){
        log.info("M=ping");
        return proxyClient.ping();
    }
}
