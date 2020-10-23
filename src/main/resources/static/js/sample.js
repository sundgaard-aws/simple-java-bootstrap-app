$(function() {
	console.log("jquery is enabled!");
	var trade = new Trade();
	$("#btnBookTrade").click(function() { trade.bookTrade() });
	$("#btnMassBookTrade").click(function() { trade.massBookTrade() });
});

function Trade() {

	var _this = this;
	_this.appRoot = $("#hfAppRoot").val();
	
	this.bookTrade = function() {
		var isin = $("#tradeISIN").val();
		var amount = $("#tradeAmount").val();
	    var trade = { TradeISIN:isin, TradeAmount:amount};
		$.ajax({
			  type: "POST",
			  url: _this.appRoot + "book-trade",
			  contentType: "application/json; charset=utf-8",
			  data: JSON.stringify(trade),
			  success: function(data) { 
				console.log("bookTrade() success!");
			  },
			  error: function(err, err2) {
				  console.error("bookTrade() error!");
			  },			  
			  complete: function() { 
				console.log("bookTrade() complete.");
			  }
		});
	};

	this.massBookTrade = function() {
		var isin = $("#tradeISIN").val();
		var amount = $("#tradeAmount").val();
	    var trade = { TradeISIN:isin, TradeAmount:amount};
		$.ajax({
			  type: "POST",
			  url: _this.appRoot + "mass-book-trade",
			  contentType: "application/json; charset=utf-8",
			  data: JSON.stringify(trade),
			  success: function(data) { 
				console.log("massBookTrade() success!");
			  },
			  error: function(err, err2) {
				  console.error("massBookTrade() error!");
			  },			  
			  complete: function() { 
				console.log("massBookTrade() complete.");
			  }
		});
	};
}