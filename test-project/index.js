

function b1click() {
	"use strict";
	var searchterm = document.getElementById("t1").value;
	var exten = document.getElementById("t2").value;
	var dateFrom = document.getElementById("t3").value;
	var dateTo = document.getElementById("t4").value;
	var jobj = {"id":"search", "searchterm":searchterm, "exten":exten, "dateFrom":dateFrom, "dateTo": dateTo};
	var data = JSON.stringify(jobj);
	var xhr = ajax();
	xhr.onload = function () {
		if (xhr.readyState === 4) {
			if (xhr.status === 200) {
				document.getElementById("demo").innerHTML = xhr.responseText;
			} else {
				alert("error: " + xhr.responseText);
			}
		}
	};
	xhr.open("POST", "/", true);
	xhr.setRequestHeader("Content-Type", "text/plain");
	xhr.send(data);
	console.dir(data);
}
function ajax() {
	"use strict";
	if ( window.XMLHttpRequest) {
		return new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		try {return new ActiveXObject("Msxml2.DOMDocument.6.0");} catch (e) {}
		try {return new ActiveXObject("Msxml2.DOMDocument.3.0");} catch (f) {}
		try {return new ActiveXObject("Microsoft.XMLHTTP");} catch (g) {}
	} else {
		alert("newxhr XMLHTTPRequest object failed");
		return null;
	}
	// xhr
}