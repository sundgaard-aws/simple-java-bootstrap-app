package com.opusmagus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.opusmagus.dto.Customer;

/** Note that we have annotated the DemoController class with @Controller and @RequestMapping("/welcome"). 
 * When Spring scans our package, it will recognize this bean as being a Controller bean for processing requests. 
 * The @RequestMapping annotation tells Spring that this Controller should process all requests beginning with /welcome 
 * in the URL path. That includes /welcome/* and /welcome.html
 */
@Controller
public class DemoController {
	
	/**
	 * The ModelAndView object also contains a message with key �message� and Detailed value. 
	 * This is the data that we are passing to our view. Normally this will be a value object in 
	 * form of java bean that will contain the data to be displayed on our view. Here we are simply 
	 * passing a string.
	 * @return ModelAndView
	 */
	@RequestMapping("/welcome")
	public ModelAndView welcome() {
		return new ModelAndView("welcome");
	}
	
	@RequestMapping("/customer")
	public @ResponseBody Customer customer(String id) {
		Customer customer = new Customer();
		customer.Name = "Michael Sundgaard";
		customer.Gender = "Male";
		customer.Address = "Mosede K�rvej 37";
		customer.Id = id;
		return customer;
	}
	
	@RequestMapping("/status")
	public @ResponseBody String status() {
		return "{status:success}";
	}	
}
