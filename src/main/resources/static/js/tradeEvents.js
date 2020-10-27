$(function () {
	console.log("TradeEvents JS file initialized.");
	var tradeEvents = new TradeEvents();
	tradeEvents.connectAndSubscribe();
});

function TradeEvents() {
	var _this = this;

	this.connectAndSubscribe = function () {
		var socket = new SockJS('/chat');
		stompClient = Stomp.over(socket);
		stompClient.connect({}, function (frame) {
			console.log("Wireup subscription for trade events.");
			stompClient.subscribe('/topic/trade-updates', function (trade) {
				console.log("Got a trade event.");
				if (trade && trade.body) {
					console.log(trade.body);
					$("#statusUpdateWidget").append("<div>" + trade.body + "</div>");
				}
			});
		});
	}

	this.sendStomp = function () {
		stompClient.send("/app/trades", {}, "trade-id");
	}
}


