var net = require("net");
var http = require("http");
var url = require("url");
var fs = require("fs");
var util = require("util");
var svr = http.createServer(requesthandler);
svr.listen(8888);
var formidable = require("formidable");
console.log("server created");
function route(request, response, data, path) {
	"use strict";
	var results = "";
	var p = path.lastIndexOf(".");
	var ext = "";
	if (data.length > 0) {
		client.write(data + "\n");
		console.log("data recieved:" + data);
		client.on("data", function (data) {
			results = data.toString();
			console.log("received:" + results + "\n");
		});
	} else if (p > -1) {
		ext = path.slice(p + 1);
		if (ext === "html" || ext === "htm" || ext === "js" || ext === "css") {
			var fn = path.slice(1);
			fs.readFile(fn, function (err, content) {
				if (ext === "html") {
					response.writeHead(200, {"Content-Type": "text/html", "content-Length": content.length});
				}
				if (ext === "htm") {
					response.writeHead(200, {"Content-Type": "text/htm", "content-Length": content.length});
				}
				if (ext === "js") {
					response.writeHead(200, {"Content-Type": "text/javascript", "content-Length": content.length});
				}
				if (ext === "css") {
					response.writeHead(200, {"Content-Type": "text/css", "content-Length": content.length});
				}
				response.write(content, function () {
					setTimeout(function endit() {
						response.end();
					}, 0);
				});
			});
		}
	} else {
		response.end();
	}
}
function requesthandler(request, response) {
	"use strict";
	switch (request.url) {
	case "/fileupload":

		var form = new formidable.IncomingForm();
		form.multiples = "true";
		form.parse(request, function (err, fields, files) {
			var newthing = fields.chosenFolder;
			var filearray = files.filetoupload;
			if(!Array.isArray(filearray)){
				filearray = [filearray];
			}
	
			var data = {
				"id":"upload",
				"filepaths":[],
				"pathway":newthing
				};
			filearray.map(function (file){
				var oldpath = file.path;
				var newpath = newthing + "/" + file.name;
				data.filepaths.push(newpath);
				fs.rename(oldpath, newpath, function (err) {
					if (err) {
						throw err;
					}
				});
			});
			data = JSON.stringify(data);
			client.write(data + "\n");
			console.log("data recieved:" + data);
			client.on("data", function (data) {
				var completed = data.toString();
				console.log("received: " + completed + "\n");
				response.end(data);
			});
			console.log("file uploaded");
			setTimeout(function endit() {
				response.end();
			}, 0);	
		});
		break;
	default:
		var postdata = "";
		var path = url.parse(request.url).pathname;
		request.setEncoding("utf8");
		request.addListener("data", function (postDataChunk) {
			postdata += postDataChunk;
		});
	// end of any data sent with he HTTP request, go to our request handler and return a webpage:
		request.addListener("end", function () {
			route(request, response, postdata, path);
			//client.write(postdata + "\n");
		});
	}
}
console.log("sockclnt.js");
var client = net.connect({port: 1221}, function () { //'connect' listener
	"use strict";
	console.log("client connected");
	client.on("end", function () {
		console.log("client disconnected");
	});
});
console.log("sending...");