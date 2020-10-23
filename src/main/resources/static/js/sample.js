$(function() {
	console.log("jquery is enabled!");
	var trade = new Trade();
	$("#btnBookTrade").click(function() { trade.bookTrade() });
});

function Trade() {

	var _this = this;
	_this.appRoot = $("#hfAppRoot").val();
	
	this.bookTrade = function() {
		var isin = $("#tradeISIN").val();
		var amount = $("#tradeAmount").val();
	    var trade = { Id:100, TradeId:100, TradeISIN:isin, TradeAmount:amount};
		$.ajax({
			  type: "POST",
			  url: _this.appRoot + "book-trade",
			  contentType: "application/json; charset=utf-8",
			  data: JSON.stringify(trade),
			  success: function(data) { 
				console.log("bookTrade() success!");
			  },
			  error: function(err, err2) {
				  console.log("bookTrade() error!");
				  console.log("bookTrade() error!");
				  console.log("bookTrade() error!");
			  },			  
			  complete: function() { 
				console.log("bookTrade() complete.");
			  }
		});
	};

}