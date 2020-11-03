package com.opusmagus.controller;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.DescribeLogStreamsRequest;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.LogStream;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
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
import com.opusmagus.dto.TradeMetaData;
import com.opusmagus.dto.TradeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.ByteBuffer;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
	@Autowired private AWSLogs cloudWatchLogger;
	@Autowired private Calendar calendar;
	@Autowired private SimpMessagingTemplate template;

	@PostMapping(path = "/book-trade")
	public Trade bookTrade(@RequestBody  Trade trade) {
		logMessage("Booking trade...");
		if(trade != null && trade.TradeAmount != null) {
			trade.TradeId = UUID.randomUUID().toString();
			trade.TradeDate = now();
			logMessage("Trade=" + json.toJson(trade));
			PostToTradeQueue(trade);
		}
		else logMessage("Trade was null, not doing anything");
		return trade;
	}

	@PostMapping(path = "/mass-book-trade")
	public Trade massBookTrade(@RequestBody  Trade trade) {
		logMessage("Mass booking trades...");
		if(trade != null && trade.TradeAmount != null) {
			for(int i=0;i<100;i++) {
				trade.TradeId = UUID.randomUUID().toString();
				trade.EventTime = now();
				PostToTradeQueue(trade);
				logMessage("Trade=" + json.toJson(trade));
			}
		}
		else logMessage("Trade was null, not doing anything");
		return trade;
	}

	@PostMapping(path = "/get-trades")
	public TradeResponse getTrades(String userId) throws Exception {
		logMessage("Getting trades...");
		AWSSimpleSystemsManagement ssmClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
		GetParameterRequest getparameterRequest = new GetParameterRequest().withName("iac-demo-rds-secret-arn");
		final GetParameterResult result = ssmClient.getParameter(getparameterRequest);
		String rdsSecretArn = result.getParameter().getValue();
		logMessage("iac-demo-rds-secret-arn=" + rdsSecretArn);
		DBSecret secret = getSecret(rdsSecretArn);
		logMessage("iac-demo-rds-secret-dbname=" + secret.dbname);
		logMessage("iac-demo-rds-secret-username=" + secret.username);

		Connection conn = null;
		TradeResponse tradeResponse = new TradeResponse();
		try {
			conn =	DriverManager.getConnection("jdbc:mysql://" + secret.host + "/" + secret.dbname + "?" + "user=" + secret.username + "&password="+ secret.password);
			tradeResponse.Trades = getTrades(conn, userId);
			tradeResponse.TradeMetaData = getTradeMetaData(conn, userId);
			return tradeResponse;
		} catch (SQLException ex) {
			logError(ex.getMessage());
			logError("SQLException: " + ex.getMessage());
			logError("SQLState: " + ex.getSQLState());
			logError("VendorError: " + ex.getErrorCode());
			//throw ex;
			return tradeResponse;
		}
	}

	private void logMessage(String message, String logGroupName, String logStreamName) {
		PutLogEventsRequest logEventsRequest = new PutLogEventsRequest();
		List<InputLogEvent> logEvents = new ArrayList<>();
		InputLogEvent inputLogEvent = new InputLogEvent();
		inputLogEvent.withTimestamp(new Date().getTime()).withMessage(message);
		logEvents.add(inputLogEvent);

		//String sequenceToken = logEventsRequest.withLogGroupName(logGroupName).withLogStreamName("iac-demo-web-log-stream").getSequenceToken();
		String sequenceToken = null;
		DescribeLogStreamsRequest describeLogStreamsRequest = new DescribeLogStreamsRequest();
		describeLogStreamsRequest.setLogGroupName(logGroupName);
		List<LogStream> logStreamList= cloudWatchLogger.describeLogStreams(describeLogStreamsRequest).getLogStreams();
		for (LogStream logStream: logStreamList) {
			if (logStream.getLogStreamName().equals(logStreamName)) {
				sequenceToken = logStream.getUploadSequenceToken();
				//System.out.println("sequenceToken="+sequenceToken);
			}
		}
		if(sequenceToken != null)
			logEventsRequest.withLogGroupName(logGroupName).withLogStreamName(logStreamName).withSequenceToken(sequenceToken).setLogEvents(logEvents);
		else
			logEventsRequest.withLogGroupName(logGroupName).withLogStreamName(logStreamName).setLogEvents(logEvents);
		cloudWatchLogger.putLogEvents(logEventsRequest);
	}

	private void logMessage(String message) {
		logMessage(message, "iac-demo-web-log-group", "iac-demo-web-log-stream");
	}

	private void logError(String message) {
		logMessage(message, "iac-demo-web-log-group", "iac-demo-web-err-stream");
	}

	private List<Trade> getTrades(Connection conn, String userId) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		List<Trade> trades = new ArrayList<>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM trade LIMIT 100");
			while(rs.next()) {
				// trade_id,user_id,trade_status,trade_isin,trade_amount,quote,trade_date
				Trade trade = new Trade();
				trade.TradeId = rs.getString("trade_id");
				trade.TradeDate = rs.getString("trade_date");
				trade.TradeISIN = rs.getString("trade_isin");
				trade.TradeAmount = rs.getString("trade_amount");
				trade.TradeStatus = rs.getString("trade_status");
				trade.UserId = rs.getString("user_id");

				trades.add(trade);
			}
			return trades;
		}
		catch (SQLException ex) {
			logError(ex.getMessage());
			logError("SQLException: " + ex.getMessage());
			logError("SQLState: " + ex.getSQLState());
			logError("VendorError: " + ex.getErrorCode());
			return null;
		}
		finally {
			if (rs != null) {try {rs.close();} catch (SQLException sqlEx) { } rs = null; }
			if (stmt != null) { try { stmt.close(); } catch (SQLException sqlEx) { } stmt = null; }
		}
	}

	private TradeMetaData getTradeMetaData(Connection conn, String userId) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		List<Trade> trades = new ArrayList<>();
		try {
			stmt = conn.createStatement();
			TradeMetaData tradeMetaData = new TradeMetaData();
			rs = stmt.executeQuery("SELECT count(1) as trade_count FROM trade");
			if(rs.next()) { tradeMetaData.TotalTrades = rs.getInt("trade_count"); }
			rs = stmt.executeQuery("SELECT count(1) as trade_count FROM trade WHERE trade_status = 'VALID'");
			if(rs.next()) { tradeMetaData.ValidTrades = rs.getInt("trade_count"); }
			rs = stmt.executeQuery("SELECT count(1) as trade_count FROM trade WHERE trade_status = 'INVALID'");
			if(rs.next()) { tradeMetaData.InvalidTrades = rs.getInt("trade_count"); }
			rs = stmt.executeQuery("SELECT count(1) as trade_count FROM trade WHERE trade_status = 'PENDING'");
			if(rs.next()) { tradeMetaData.PendingTrades = rs.getInt("trade_count"); }
			return tradeMetaData;
		}
		catch (SQLException ex) {
			logError(ex.getMessage());
			logError("SQLException: " + ex.getMessage());
			logError("SQLState: " + ex.getSQLState());
			logError("VendorError: " + ex.getErrorCode());
			//throw ex;
			return null;
		}
		finally {
			if (rs != null) {try {rs.close();} catch (SQLException sqlEx) { } rs = null; }
			if (stmt != null) { try { stmt.close(); } catch (SQLException sqlEx) { } stmt = null; }
		}
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
			logError("The request was invalid due to: " + e.getMessage());
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
		logMessage("iac-demo-sqs-queue-url=" + queueUrl);
		SendMessageRequest sendMessageRequest = new SendMessageRequest()
			.withQueueUrl(queueUrl)
			.withMessageBody(json.toJson(trade))
			.withDelaySeconds(5);
		sqs.sendMessage(sendMessageRequest);
		logMessage("Message was put on queue");
		template.convertAndSend("/topic/trade-updates", trade);
	}

}
