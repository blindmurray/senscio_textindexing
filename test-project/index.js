function b1click(){
	var data = document.getElementById("t1").value;
	var xhr = ajax();
	xhr.onload =function(){
		if (xhr.readyState===4) {
			if (xhr.status===200) {
				alert ("success: " + xhr.responseText);
			}
			else{
				alert ("error: " + xhr.responseText);
			}
		}
	}
		xhr.open ("POST", "/", true);
		xhr.setRequestHeader("Content-Type","text/plain");
		xhr.send(data);
		console.dir(data);
		
}

function b2click(){
	var fileName = document.getElementById("t6").value
	fileName = fileName.replace("C:\\fakepath\\","");
	alert(fileName);
}

function ajax(){
	if ( window.XMLHttpRequest) { return new XMLHttpRequest(); } 
	else if (window.ActiveXObject) {
		try {return new ActiveXObject("Msxml2.DOMDocument.6.0");} catch (e) {}
		try {return new ActiveXObject("Msxml2.DOMDocument.3.0");} catch (e) {}
		try {return new ActiveXObject("Microsoft.XMLHTTP");} catch (e) {}
	}
	else{
		alert('newxhr XMLHTTPRequest object failed');
		return null;
	}
   // xhr 
}