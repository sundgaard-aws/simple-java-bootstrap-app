package com.opusmagus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.GetMapping;

import com.opusmagus.dto.Customer;

/** Note that we have annotated the DemoController class with @Controller and @RequestMapping("/welcome"). 
 * When Spring scans our package, it will recognize this bean as being a Controller bean for processing requests. 
 * The @RequestMapping annotation tells Spring that this Controller should process all requests beginning with /welcome 
 * in the URL path. That includes /welcome/* and /welcome.html
 */
//@RestController
//@RequestMapping(value = "/pure-rest", method = RequestMethod.POST)
@RestController
public class PureRestController {
	
	@GetMapping(path = "/customer")
	//@RequestMapping("/customer")
	public Customer customer(String id) {
		Customer customer = new Customer();
		customer.Name = "Michael Sundgaard";
		customer.Gender = "Male";
		customer.Address = "Mosede Kærvej 37";
		customer.Id = id;
		return customer;
	}

}
