$(function () {
	console.log("jquery is enabled!");
	var trade = new Trade();
	$("#btnBookTrade").click(function () { trade.bookTrade() });
	$("#btnMassBookTrade").click(function () { trade.massBookTrade() });
	$("#btnConnectToTradeSocket").click(function() { connectStomp() });
	$("#btnSendtoTradeSocket").click(function() { sendStomp() });
});

function Trade() {

	var _this = this;
	_this.appRoot = $("#hfAppRoot").val();

	this.bookTrade = function () {
		var isin = $("#tradeISIN").val();
		var amount = $("#tradeAmount").val();
		var trade = { TradeISIN: isin, TradeAmount: amount };
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
		var trade = { TradeISIN: isin, TradeAmount: amount };
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
}


var ws;
function connect() {
    //var username = document.getElementById("username").value;
    
    var host = document.location.host;
    var pathname = document.location.pathname;
    
    ws = new WebSocket("ws://" + host  + pathname + "trade-socket/");

    ws.onmessage = function(event) {
    var log = document.getElementById("log");
        console.log(event.data);
        //var message = JSON.parse(event.data);
        //log.innerHTML += message.from + " : " + message.content + "\n";
    };
}

function send() {
	//var content = document.getElementById("msg").value;
	var content = "message from client";
    var json = JSON.stringify({
        "content":content
    });
    ws.send(json);
}


//STOMP
var stompClient;
connectStomp();
function connectStomp() {
	var socket = new SockJS('/chat');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function (frame) {
		console.log("Wireup subscription for trade events.");
	  	stompClient.subscribe('/topic/trade-updates', function (trade) {
			console.log("Got a trade event.");
			if(trade && trade.body) {
				console.log(trade.body);
			}
	  	});
	});
}
   
function sendStomp() {
	stompClient.send("/app/trades", {}, "trade-id");
}