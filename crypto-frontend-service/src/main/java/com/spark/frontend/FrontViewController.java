package com.spark.frontend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontViewController {

    @Value("${websocket.url}")
    private String websocketUrl;

    @GetMapping("/chart-ws")
    public String getWebSocketChart(Model model){
        model.addAttribute("websocketUrl", websocketUrl);
        return "chart-websocket.html";
    }

    @GetMapping("/chart-rest")
    public String getRestApiChart(){
        return "chart-rest.html";
    }
}
