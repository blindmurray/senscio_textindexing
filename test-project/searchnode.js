var net = require('net'); 
var http = require('http');
var url = require('url'); 
var fs = require('fs');
var svr = http.createServer(requesthandler);
svr.listen(8888);
console.log('server created');

function requesthandler (request, response){ 
	var postdata = "";
	var path = url.parse(request.url).pathname;
	request.setEncoding("utf8");
	request.addListener("data", function(postDataChunk){ 
		postdata += postDataChunk;
	});
    // end of any data sent with he HTTP request, go to our request handler and return a webpage:                                                              
    request.addListener("end", function() {    
        route(request, response, postdata, path);
        //client.write(postdata + "\n");
    });
} 
function route(request, response, data, path){
    if(data.length>0){
    	client.write(data + "\n");
        console.log('data recieved:'+ data);
        response.writeHead (200, {'Content-Type': 'text/html', 'content-Length': data.length});
        response.write(data);
        response.end();
        data = "";

    }
    var p = path.lastIndexOf("."); 
    var ext = "";
    if (p > -1){
        ext = path.slice(p+1);
        if (ext=="html"|| ext=="htm" || ext=="js" ||ext=="css"){
          	var fn = path.slice(1);
           	fs.readFile (fn, function (err, content){
           		response.writeHead (200, {'Content-Type': 'text/html', 'content-Length': content.length});
           		response.write(content);
       			response.end();
   			});
        }
    }	
}
console.log("sockclnt.js");
var client = net.connect({port: 1221}, function() { //'connect' listener
	console.log('client connected');
	client.on('data', function(data) {
		console.log("received:"+ data.toString() +"\n");

	//client.end();
	});
	client.on('end', function() {
		console.log('client disconnected');
	});
})
var c = 0;
console.log("sending "+c); 

	/*var timeout = setTimeout( function(){clearInterval(interval); 
		client.write("bye"); 
		console.log("ending..."); 
		client.end();}, 10000);*/