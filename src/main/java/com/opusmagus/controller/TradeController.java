package com.opusmagus.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.opusmagus.dto.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.UUID;

/** Note that we have annotated the DemoController class with @Controller and @RequestMapping("/welcome"). 
 * When Spring scans our package, it will recognize this bean as being a Controller bean for processing requests. 
 * The @RequestMapping annotation tells Spring that this Controller should process all requests beginning with /welcome 
 * in the URL path. That includes /welcome/* and /welcome.html
 */
//@RequestMapping(value = "/pure-rest", method = RequestMethod.POST)
@RestController
public class TradeController {
	@Autowired private Gson json;
	@Autowired private Calendar calendar;

	@PostMapping(path = "/book-trade")
	public Trade bookTrade(@RequestBody  Trade trade) {
		if(trade != null && trade.TradeAmount != null) {
			trade.TradeId = UUID.randomUUID().toString();
			trade.EventTime = calendar.getTime().toString();
			System.out.println("Trade=" + json.toJson(trade));
			PostToTradeQueue(trade);
		}
		else System.out.println("Trade was null, not doing anything");
		return trade;
	}

	@PostMapping(path = "/mass-book-trade")
	public Trade massBookTrade(@RequestBody  Trade trade) {
		if(trade != null && trade.TradeAmount != null) {
			for(int i=0;i<100;i++) {
				trade.TradeId = UUID.randomUUID().toString();
				trade.EventTime = calendar.getTime().toString();
				PostToTradeQueue(trade);
				System.out.println("Trade=" + json.toJson(trade));
			}
		}
		else System.out.println("Trade was null, not doing anything");
		return trade;
	}

	// https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-visibility-timeout.html
	public void PostToTradeQueue(Trade trade) {
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		var queueUrl = "https://sqs.eu-central-1.amazonaws.com/299199322523/iac-demo-sqs";
		SendMessageRequest send_msg_request = new SendMessageRequest()
				.withQueueUrl(queueUrl)
				.withMessageBody(json.toJson(trade))
				.withDelaySeconds(5);
		sqs.sendMessage(send_msg_request);
		System.out.println("Message was put on queue");
	}

}
