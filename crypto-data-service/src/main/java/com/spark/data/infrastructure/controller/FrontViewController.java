package com.spark.data.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontViewController {

    @GetMapping("/chart")
    public String getChart(){
        return "chart-rest.html";
    }

    @GetMapping("/chart-ws")
    public String getWebSocketChart(){
        return "chart-websocket.html";
    }
}
