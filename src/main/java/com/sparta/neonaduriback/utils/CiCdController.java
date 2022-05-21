package com.sparta.neonaduriback.utils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CiCdController {

    @GetMapping("/health")
    public String checkHealth() {
        System.out.println("헬스체크");
        return "congrat!";
    }

}
