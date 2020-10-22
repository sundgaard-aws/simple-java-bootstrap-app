package com.opusmagus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMethod;

import com.opusmagus.dto.Customer;

/** Note that we have annotated the DemoController class with @Controller and @RequestMapping("/welcome"). 
 * When Spring scans our package, it will recognize this bean as being a Controller bean for processing requests. 
 * The @RequestMapping annotation tells Spring that this Controller should process all requests beginning with /welcome 
 * in the URL path. That includes /welcome/* and /welcome.html
 */
@RestController
@RequestMapping(value = "/pure-rest", method = RequestMethod.POST)
public class PureRestController {
	
	@RequestMapping("/customer")
	public Customer customer(String id) {
		Customer customer = new Customer();
		customer.Name = "Michael Sundgaard";
		customer.Gender = "Male";
		customer.Address = "Mosede K�rvej 37";
		customer.Id = id;
		return customer;
	}

}
