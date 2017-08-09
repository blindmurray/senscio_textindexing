function ajax() {
    "use strict";
    if ( window.XMLHttpRequest) {
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        try {
            return new ActiveXObject("Msxml2.DOMDocument.6.0");
        } catch (e) {}
        try {
            return new ActiveXObject("Msxml2.DOMDocument.3.0");
        } catch (f) {}
        try {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } catch (g) {}
    } else {
        alert("newxhr XMLHTTPRequest object failed");
        return null;
    }
}
function b1click() {
    "use strict";
    var searchterm = document.getElementById("t1").value;
    var exten = document.getElementById("t2").value;
    var dateFrom = document.getElementById("t3").value;
    var dateTo = document.getElementById("t4").value;
    var num = document.getElementById("t8").value;
        if(num.length < 1){
        num = 20;
    }
    console.log(num);
    var search = {
        "id": "search",
        "searchterm": searchterm,
        "exten": exten,
        "dateFrom": dateFrom,
        "dateTo": dateTo,
        "num": num
    };

    search = JSON.stringify(search);
    var xhr = ajax();
    xhr.onload = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            document.getElementById("demo").innerHTML = xhr.responseText;
        } else {
            alert("Error: " + xhr.responseText);
        }
    };
    xhr.open("POST", "/", true);
    xhr.setRequestHeader("Content-Type", "text/plain");
    xhr.send(search);
    console.dir(search);
}