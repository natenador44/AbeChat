package com.abechat.server.api;

import jakarta.servlet.ServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingController.class);

    @GetMapping(value = {"/ping", "/ping/"})
    public ResponseEntity<Void> ping(ServletRequest request) {
        LOGGER.info("[{}] API Pinged", request.getRemoteAddr());
        return ResponseEntity.ok().build();
    }
}
