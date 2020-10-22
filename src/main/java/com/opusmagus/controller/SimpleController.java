package com.opusmagus;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

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

}