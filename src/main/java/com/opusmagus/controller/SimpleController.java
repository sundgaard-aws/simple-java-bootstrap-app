package com.opusmagus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class SimpleController {

	@RequestMapping("/hello")
	public String hello() {
		return "Hello from Spring Boot 222!";
	}

	@RequestMapping("/echo")
	public String echo() {
		return "Echo";
	}

	@GetMapping(path = "/instance-id")
	public String instanceId() {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = null;
		try {
			String url = "http://169.254.169.254/latest/meta-data/instance-id";
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			String responseBody = response.body();
			return responseBody;
		}
		catch (Exception e)
		{
			String url = "https://api.ipify.org/?format=text";
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
			try {
				response = client.send(request, HttpResponse.BodyHandlers.ofString());
				String responseBody = response.body();
				return responseBody;
			} catch (Exception ex) { ex.printStackTrace();	}
		}

		return "Unknown";
	}

}