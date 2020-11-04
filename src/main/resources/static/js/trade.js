$(function () {
	console.log("jquery is enabled!");
	console.log("Trade JS file initialized.");
	new Infrastructure().updateInstanceId();
	var trade = new Trade();	
	$("#btnBookTrade").click(function () { trade.bookTrade() });
	$("#btnMassBookTrade").click(function () { trade.massBookTrade() });
	$("#btnGetTrades").click(function () { trade.getTrades() });
	setInterval(function() { trade.getTrades(); }, 2 * 1000); // 60 * 1000 milsec
});

function Trade() {

	var _this = this;
	_this.appRoot = $("#hfAppRoot").val();

	this.bookTrade = function () {
		var isin = $("#tradeISIN").val();
		var amount = $("#tradeAmount").val();
		var userId = "DEMO-USER";
		var trade = { TradeISIN: isin, TradeAmount: amount, UserId: userId };
		$.ajax({
			type: "POST",
			url: _this.appRoot + "book-trade",
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(trade),
			success: function (data) {
				console.log(data);
				console.log("bookTrade() success!");
			},
			error: function (err, err2) {
				console.error("bookTrade() error!");
			},
			complete: function () {
				console.log("bookTrade() complete.");
			}
		});
	};

	this.massBookTrade = function () {
		var isin = $("#tradeISIN").val();
		var amount = $("#tradeAmount").val();
		var userId = "DEMO-USER";
		var trade = { TradeISIN: isin, TradeAmount: amount, UserId: userId };
		$.ajax({
			type: "POST",
			url: _this.appRoot + "mass-book-trade",
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify(trade),
			success: function (data) {
				console.log("massBookTrade() success!");
			},
			error: function (err, err2) {
				console.error("massBookTrade() error!");
			},
			complete: function () {
				console.log("massBookTrade() complete.");
			}
		});
	};	

	this.getTrades = function () {
		$.ajax({
			type: "POST",
			url: _this.appRoot + "get-trades",
			contentType: "application/json; charset=utf-8",
			data: "DEMO-USER",
			success: function (data) {
				//console.log("getTrades() success!");
				$("#tradeItems").empty();

				for(var i=0; i<data.Trades.length; i++) {
					var clone = $("#tradeItemTemplate").clone();
					var tradeId = data.Trades[i].TradeId;
					var quote = data.Trades[i].Quote;
					var tradeAmount = data.Trades[i].TradeAmount;
					var tradeDate = data.Trades[i].TradeDate;
					$(clone).removeAttr("id");
					$(clone).removeAttr("id");
					$(clone).find(".trade-id").html(tradeId.substring(tradeId.length-12));
					$(clone).find(".trade-isin").html(data.Trades[i].TradeISIN);
					$(clone).find(".trade-date").html(tradeDate.substring(5, tradeDate.length-3));
					$(clone).find(".user-id").html(data.Trades[i].UserId);
					$(clone).find(".trade-status").html(data.Trades[i].TradeStatus);
					$(clone).find(".trade-amount").html(Number(tradeAmount).toFixed(0));
					$(clone).find(".quote").html(Number(quote).toFixed(2));
					$("#tradeItems").append(clone);
					if(i > 6) break;
				}

				$("#totalTrades").html(data.TradeMetaData.TotalTrades);
				$("#pendingTrades").html(data.TradeMetaData.PendingTrades);
				$("#validTrades").html(data.TradeMetaData.ValidTrades);
				$("#invalidTrades").html(data.TradeMetaData.InvalidTrades);
			},
			error: function (err, err2) {
				console.error("getTrades() error!");
			},
			complete: function () {
				//console.log("getTrades() complete.");
			}
		});
	};	
}

function Infrastructure() {
	var _this = this;
	_this.appRoot = $("#hfAppRoot").val();

	this.updateInstanceId = function () {
		$.ajax({
			type: "GET",
			url: _this.appRoot + "instance-id",
			contentType: "application/json; charset=utf-8",
			data: {},
			success: function (data) {
				$("#instanceId").html("Instance ID:" + data);
				console.log(data);
				console.log("instanceId() success!");
			},
			error: function (err, err2) {
				console.error("instanceId() error!");
			},
			complete: function () {
				console.log("instanceId() complete.");
			}
		});
	};
}
