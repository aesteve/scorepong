'use strict';

import Reflux from 'reflux';
import http from 'superagent';
import SockJS from 'sockjs-client';

var Actions = Reflux.createActions({
	"fetchGames":{children: ["completed","failed"]},
	"fetchGame":{children: ["completed","failed"]},
	"createGame":{children: ["completed","failed"]},
	"connect":{async:false},
	"startGame":{async:false},
	"scorePoint":{async:false},
	"msgReceived":{async:false}
});

var postJSON = function(path, data, cb) {
	http.post(path)
		.send(data)
		.set('Content-Type', 'application/json')
		.set('Accept', 'application/json')
		.end(cb);
};

var getJSON = function(path, cb) {
	http.get(path)
		.set('Content-Type', 'application/json')
		.set('Accept', 'application/json')
		.end(cb);
};

Actions.fetchGames.listen(function(){
	var self = this;
	getJSON('/api/match/', function(err, res) {
		if (err) {
			self.failed(err);
		} else {
			self.completed(res.body);
		}
	});
});

Actions.createGame.listen(function(game){
	var self = this;
	postJSON('/api/match/', game, function(err, res) {
		if (err) {
			self.failed(err);
		} else {
			self.completed(res.body);
		}
	});
});

Actions.fetchGame.listen(function(id){
	var self = this;
	getJSON('/api/match/'+id, function(err, res) {
		if (err) {
			self.failed(err);
		} else {
			self.completed(res.body);
		}
	});
});

Actions.connect.listen(function(id){
	var startMsg = {
		action:"connect",
		game:id
	};
	tryToSend(startMsg);
});

Actions.startGame.listen(function(id){
	var startMsg = {
		action:"start",
		game:id
	};
	socket.send(JSON.stringify(startMsg));
});

Actions.scorePoint.listen(function(player){
	var msg = {
		action:"point",
		player:player
	};
	socket.send(JSON.stringify(msg));
});

var socket = new SockJS('/sockjs');
var pendingMsgs = [];
socket.onmessage = function(msg) {
	var data = msg.data;
	Actions.msgReceived(JSON.parse(data));
};
socket.onopen = function() {
	if (pendingMsgs && pendingMsgs.length > 0) {
		pendingMsgs.forEach(msg => {
			socket.send(JSON.stringify(msg));
		});
	}
};
var tryToSend = function(msg) {
	if (socket.status !== 'OPEN') {
		pendingMsgs.push(msg);
		return;
	}
	socket.send(JSON.stringify(msg));
};
export default Actions;