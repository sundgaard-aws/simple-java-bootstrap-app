package com.opusmagus.controller;

import com.opusmagus.dto.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

// https://www.baeldung.com/java-websockets
// https://www.baeldung.com/spring-websockets-send-message-to-user
//@ServerEndpoint(value = "/trade-socket/")
@Controller
public class TradeMessageHandler {

    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/trades")
    @SendTo("/topic/trade-updates")
    public Trade send(String message) throws Exception {
        System.out.print("TradeMessageHandler.send() called...");
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        Trade trade = new Trade();
        trade.EventTime = new SimpleDateFormat("HH:mm").format(new Date());
        trade.TradeId = "100";

        /*for(int i=0;i<10;i++) {
            template.convertAndSend("/topic/trade-updates", trade);
        }*/

        return trade;
    }
}
