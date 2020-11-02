package com.opusmagus.controller;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import com.opusmagus.dto.DBSecret;
import com.opusmagus.dto.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
	@Autowired private SimpMessagingTemplate template;

	@PostMapping(path = "/book-trade")
	public Trade bookTrade(@RequestBody  Trade trade) {
		if(trade != null && trade.TradeAmount != null) {
			trade.TradeId = UUID.randomUUID().toString();
			trade.EventTime = now();
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
				trade.EventTime = now();
				PostToTradeQueue(trade);
				System.out.println("Trade=" + json.toJson(trade));
			}
		}
		else System.out.println("Trade was null, not doing anything");
		return trade;
	}

	@PostMapping(path = "/get-trades")
	public List<Trade> getTrades(String userId) throws Exception {
		AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
		GetParameterRequest getparameterRequest = new GetParameterRequest().withName("iac-demo-rds-secret-arn");
		final GetParameterResult result = ssmClient.getParameter(getparameterRequest);
		String rdsSecretArn = result.getParameter().getValue();
		System.out.println("iac-demo-rds-secret-arn=" + rdsSecretArn);
		DBSecret secret = getSecret(rdsSecretArn);
		System.out.println("iac-demo-rds-secret-dbname=" + secret.dbname);
		System.out.println("iac-demo-rds-secret-username=" + secret.username);

		Connection conn = null;
		try {
			conn =	DriverManager.getConnection("jdbc:mysql://" + secret.host + "/" + secret.dbname + "?" + "user=" + secret.username + "&password="+ secret.password);
			return getTrades(conn, userId);
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw ex;
		}

		/*if(trade != null && trade.TradeAmount != null) {
			trade.TradeId = UUID.randomUUID().toString();
			trade.EventTime = now();
			System.out.println("Trade=" + json.toJson(trade));
			PostToTradeQueue(trade);
		}
		else System.out.println("Trade was null, not doing anything");
		return trade;*/
		//return null;
	}

	private List<Trade> getTrades(Connection conn, String userId) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		List<Trade> trades = new ArrayList<>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM trade LIMIT 10");
			// Now do something with the ResultSet ....
			while(rs.next()) {
				Trade trade = new Trade();
				trade.TradeId = rs.getString("trade_id");
				trades.add(trade);
			}
			return trades;
		}
		catch (SQLException ex){
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw ex;
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { } // ignore
				rs = null;
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore
				stmt = null;
			}
		}
		//return null;
	}

	private DBSecret getSecret(String secretName) throws Exception {
		AWSSecretsManager secretsManager = AWSSecretsManagerClientBuilder.defaultClient();
		String secret;
		ByteBuffer binarySecretData;
		GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName).withVersionStage("AWSCURRENT");
		GetSecretValueResult getSecretValueResult = null;
		try {
			getSecretValueResult = secretsManager.getSecretValue(getSecretValueRequest);

		} catch(Exception e) {
			System.out.println("The request was invalid due to: " + e.getMessage());
			throw e;
		}

		if(getSecretValueResult == null) {
			return null;
		}

		// Depending on whether the secret was a string or binary, one of these fields will be populated
		if(getSecretValueResult.getSecretString() != null) {
			secret = getSecretValueResult.getSecretString();
			return json.fromJson(secret, DBSecret.class);
			//System.out.println(secret);
			//return secret;
		}
		else {
			binarySecretData = getSecretValueResult.getSecretBinary();
			//System.out.println(binarySecretData.toString());
			//return binarySecretData.toString();
			throw new Exception("Not implemented exception");
		}
	}

	private String now() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
		return dateTimeFormatter.format(now);
	}

	/*** 
		https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-visibility-timeout.html
		https://docs.aws.amazon.com/cdk/latest/guide/get_ssm_value.html
	 	https://gist.github.com/davidrosenstark/4a33f2c0eab59d9d7e429bd1c20aea92
	 ***/
	private void PostToTradeQueue(Trade trade) {
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		//GetParameterRequest getparameterRequest = new GetParameterRequest().withName("my-key").withWithDecryption(encryption);
		AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
		GetParameterRequest getparameterRequest = new GetParameterRequest().withName("iac-demo-sqs-queue-url");
		final GetParameterResult result = ssmClient.getParameter(getparameterRequest);
		String queueUrl = result.getParameter().getValue();
		System.out.println("iac-demo-sqs-queue-url=" + queueUrl);
		SendMessageRequest sendMessageRequest = new SendMessageRequest()
			.withQueueUrl(queueUrl)
			.withMessageBody(json.toJson(trade))
			.withDelaySeconds(5);
		sqs.sendMessage(sendMessageRequest);
		System.out.println("Message was put on queue");
		template.convertAndSend("/topic/trade-updates", trade);
	}

}
