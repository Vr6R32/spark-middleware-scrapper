package com.spark.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontViewController {

    @GetMapping("/chart-ws")
    public String getWebSocketChart(){
        return "chart-websocket.html";
    }

    @GetMapping("/chart-rest")
    public String getRestApiChart(){
        return "chart-rest.html";
    }
}
