function prepareList() {
console.log("loaded");
 $('#expList').find('li:has(ul)')
 .click( function(event) {
 if (this == event.target) {
	 	 $(this).toggleClass('collapsed');
		$(this).toggleClass('expanded');
	 	$(this).children('ul').toggle('medium');
 }
 return false;
 })
 .addClass('collapsed')
 .children('ul').hide();

//Hack to add links inside the cv
 $('#expList a').unbind('click').click(function() {
	 window.open($(this).attr('href'));
	 return false;
 });

//Create the button funtionality
 $('#expandList')
 .unbind('click')
 .click( function() {
 	$('.collapsed').children('ul').show('medium');
 })
 	$('#collapseList')
 .unbind('click')
 .click( function() {
	$('.expanded').children('ul').hide('medium');
 })

};

/**************************************************************/
 /* Functions to execute on loading the document               */
 /**************************************************************/
 $(document).ready( function() {
 	prepareList()
 });

function b1click() {
	"use strict";
	var exten = document.getElementById("t2").value;
	var data = document.getElementById("t1").value + "~s@" + exten;
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
function b2click() {
	"use strict";
	var fileName = document.getElementById("t6").value;
	fileName = fileName.replace("C:\\fakepath\\", "");
	alert(fileName);
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