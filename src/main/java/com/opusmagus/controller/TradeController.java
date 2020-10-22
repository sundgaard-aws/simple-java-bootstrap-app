package com.opusmagus.controller;

import com.opusmagus.dto.Trade;
import org.springframework.web.bind.annotation.*;

import com.opusmagus.dto.Trade;

/** Note that we have annotated the DemoController class with @Controller and @RequestMapping("/welcome"). 
 * When Spring scans our package, it will recognize this bean as being a Controller bean for processing requests. 
 * The @RequestMapping annotation tells Spring that this Controller should process all requests beginning with /welcome 
 * in the URL path. That includes /welcome/* and /welcome.html
 */
//@RestController
//@RequestMapping(value = "/pure-rest", method = RequestMethod.POST)
@RestController
public class TradeController {
	
	@PostMapping(path = "/book-trade")
	//@RequestMapping("/customer")
	public Trade customer(@RequestBody  Trade trade) {
		if(trade != null && trade.TradeAmount != null)
			System.out.println("TradeAmount=" + trade.TradeAmount);
		else
			System.out.println("Trade or trade amount was null");
		/*Customer customer = new Customer();
		customer.Name = "Michael Sundgaard";
		customer.Gender = "Male";
		customer.Address = "Mosede Kærvej 37";
		customer.Id = id;*/
		return trade;
	}

}