var net = require("net");
var http = require("http");
var url = require("url");
var fs = require("fs-extra");
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
        //communication with search engine for search, tree, and folder add
		client.write(data + "\n");
		console.log("data recieved:" + data);
		client.on("data", function (data) {
			results = data.toString();
			console.log("received:" + results);
			response.end(results);
		});
	} else if (p > -1) {
		ext = path.slice(p + 1);

		var mimeType = {
			"html": "text/html",
			"htm": "text/htm",
			"js": "text/javascript",
			"css": "text/css",
			"jpg": "image/jpg",
			"txt": "text/plain",
			"doc": "application/msword",
			"dot": "application/msword",
			"docx": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"pdf": "application/pdf",
			"rtf": "application/rtf",
			"odp": "application/vnd.oasis.opendocument.presentation",
			"odt": "application/vnd.oasis.opendocument.text",
			"ods": "application/vnd.oasis.opendocument.spreadsheet",
			"csv": "text/csv",
			"xls": "appication/vnd.ms-excel",
			"xlsx": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"pps": "application/vnd.ms-powerpoint",
			"ppt": "application/vnd.ms-powerpoint",
			"pptx": "application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"pages": "application/x-iWork-pages-sffkey",
			"key": "application/x-iWork-keynote-sffkey",
			"numbers": "application/x-iWork-numbers-sffkey"
		};
		if (mimeType[ext] !== undefined) {
            //load webpage
			var fn = path.slice(1);
			fs.readFile(fn, function (err, content) {
				response.writeHead(200, {"Content-Type": mimeType[ext], "content-Length": content.length});
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
    case "/login":
        var mysql = require("mysql");
        var connection = mysql.createConnection({
            host: "localhost",
            user: "root",
            password: "",
            database: "indexer"
        });
        console.log("reached");
        var form = new formidable.IncomingForm();
        form.multiples = "true";
        form.parse(request, function (err, fields, files) {
            var user = fields.uname;
            var pass = fields.psw;
            var sql = "INSERT INTO `indexer`.`account` (`username`, `userpass`) VALUES (?, ?)";
            var values = [user, pass];
            console.log(values);
            var query = mysql.format(sql, values);
            connection.query(query, function (error, results, fields) {
                if (error) {
                    throw error;
                }
                console.log(error);
            });
        });
        break;
    
    case "/fileupload":
        //communication with search engine for fileupload
        var form = new formidable.IncomingForm();
        form.multiples = "true";
        form.parse(request, function (err, fields, files) {
            //retrieve info from form
            var newthing = fields.chosenFolder;
            var filearray = files.filetoupload;
            if (!Array.isArray(filearray)) {
                filearray = [filearray];
            }
            var saved = {
                "id": "upload",
                "filepaths": [],
                "path_new": newthing,
                "terms": fields.keyterms
            };
            filearray = filearray.map(function(file){
                //save all files in temporary folder
                var f = file.name;
                var oldpath = file.path;

                var dup = false;
                
                console.log("worked, i think" + f);
                var npath = "/Users/linjiang/Documents/GitHub/senscio_textindexing/test-project/Temporary/" + f;
                //get all filepaths
                saved.filepaths.push(npath);
                fs.rename(oldpath, npath, function (err) {
                    if (err) {
                        throw err;
                    }
                    return npath;
                });
            });
            //send data
            saved = JSON.stringify(saved);
            console.log(client.write(saved + "\n"));
            console.log(saved);
            client.on("data", function (data) {
                console.log("2" + data);
                response.end("File(s) uploaded");
            });
        });
        break;
    default:
        var postdata = "";
        var path = url.parse(request.url).pathname;
        request.setEncoding("utf8");
        request.addListener("data", function (postDataChunk) {
            postdata += postDataChunk;
        });
    // end of any data sent with the HTTP request, go to our request handler and return a webpage:
        request.addListener("end", function () {
            route(request, response, postdata, path);
            //client.write(postdata + "\n");
        });
    }
}
//open socket
console.log("sockclnt.js");
var client = net.connect({port: 1221}, function () { //"connect" listener
    "use strict";
    console.log("client connected");
    client.setNoDelay();
    client.on("end", function () {
        console.log("client disconnected");
    });
});
console.log("sending...");