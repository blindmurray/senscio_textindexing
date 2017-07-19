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
			response.end(results);
		});
	} else if (p > -1) {
		ext = path.slice(p + 1);
		if (ext === "html" || ext === "htm" || ext === "js" || ext === "css" || 
			ext === "doc" || ext === "docx" || ext === "pdf" ||ext === "rtf" || 
			ext === "odp" || ext ===  "ods" || ext === "odt"|| ext === "csv"|| 
			ext === "xlsx"|| ext === "xls"|| ext === "pps" || ext ==="ppt" ||
			ext === "pptx" || ext === "pages" || ext === "key" || ext === "numbers" ||
			ext === "txt") {
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
				if (ext === "jpg"){
					response.writeHead(200, {"Content-Type": "image/jpg", "content-Length": content.length})
				}
				if (ext === "doc" || ext === "docx") {
					response.writeHead(200, {"Content-Type": "application/msword", "content-Length": content.length});
				}
				if (ext === "pdf") {
					response.writeHead(200, {"Content-Type": "application/pdf", "content-Length": content.length});
				}
				if (ext === "rtf") {
					response.writeHead(200, {"Content-Type": "application/rtf", "content-Length": content.length});
				}
				if (ext === "odp") {
					response.writeHead(200, {"Content-Type": "application/vnd.oasis.opendocument.presentation", "content-Length": content.length});
				}
				if (ext === "odt") {
					response.writeHead(200, {"Content-Type": "application/vnd.oasis.opendocument.text", "content-Length": content.length});
				}
				if (ext === "ods") {
					response.writeHead(200, {"Content-Type": "application/vnd.oasis.opendocument.spreadsheet", "content-Length": content.length});
				}
				if (ext === "csv") {
					response.writeHead(200, {"Content-Type": "text/csv", "content-Length": content.length});
				}
				if (ext === "xls" || ext === "xlsx") {
					response.writeHead(200, {"Content-Type": "application/vnd.ms-excel", "content-Length": content.length});
				}
				if (ext === "pps" || ext === "ppt" || ext === "pptx") {
					response.writeHead(200, {"Content-Type": "application/vnd.ms-powerpoint", "content-Length": content.length});
				}
				if (ext === "pages") {
					response.writeHead(200, {"Content-Type": "application/x-iWork-pages-sffkey", "content-Length": content.length});
				}
				if (ext === "key") {
					response.writeHead(200, {"Content-Type": "application/x-iWork-keynote-sffkey", "content-Length": content.length});
				}
				if (ext === "numbers") {
					response.writeHead(200, {"Content-Type": "application/x-iWork-numbers-sffkey", "content-Length": content.length});
				}
				if (ext === "txt") {
					response.writeHead(200, {"Content-Type": "text/plain", "content-Length": content.length});
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
};
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
	
			var info = {
				"id":"upload",
				"filepaths":[],
				"pathway":newthing
				};
			filearray.map(function (file){
				var oldpath = file.path;
				var newpath = newthing + "/" + file.name;
				info.filepaths.push(newpath);
			});
			info = JSON.stringify(info);
			client.write(info + "\n");
			console.log("data recieved:" + info);
			client.on("data", function (data) {
				var completed = data.toString();				
				console.log("received: " + completed + "\n");
				if(completed === "no duplicates".valueOf()){
					console.log("1");
					
					filearray.map(function (file){
						var oldpath1 = file.path;
    					var terms = document.getElementById("keyterms").value;
						var newpath1 = newthing + "/" + file.name;
						fs.rename(oldpath1, newpath1, function (err) {
							if (err) {
								throw err;
							}
							var saved = {
								"id":"saved",
								"filepaths":newpath1,
								"terms": terms
							};
							saved = JSON.stringify(saved);
							client.write(saved);
							client.on("data",function(data){
							response.end("Files(s) uploaded");
							});
						});

					});
				}
				else{
					response.end(completed);
				}
			});
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