var oldId = "No Path Selected";
function triggerSelect(clicked_ID) {
	"use strict";
	var el = document.getElementById(clicked_ID);
	el.style.fontWeight = "bold";
	el.classList.add("checked");
	if (oldId !== "No Path Selected") {
		var p = document.getElementById(oldId);
		p.style.fontWeight = "normal";
		p.classList.remove("checked");
	}
	oldId = clicked_ID;
}

function onChooseLocation() {
	"use strict";
	document.getElementById("chosenFolder").value = oldId;
	document.getElementById("location").innerHTML = oldId;
	modal.style.display = "none";
}

function prepareList() {
	"use strict";
	$("#myModal #expList").find("li:has(ul)")
		.click(function (event) {
			if (this == event.target) {
				$(this).toggleClass("collapsed");
				$(this).toggleClass("expanded");
				$(this).children("ul").toggle("medium");
			}
			return false;
		})
		.addClass("collapsed")
		.children("ul").hide();

//Hack to add links inside the cv
	$("#expList a").unbind("click").click(function () {
		window.open($(this).attr("href"));
		return false;
	});

	//Create the button funtionality
	$("#expandList")
		.unbind("click")
		.click(function () {
			$(".collapsed").children("ul").show("medium");
		});
	$("#collapseList")
		.unbind("click")
		.click(function () {
			$(".expanded").children("ul").hide("medium");
		});
}

var xhr = ajax();
xhr.onload = function () {
	"use strict";
	if (xhr.readyState === 4) {
		if (xhr.status === 200) {
			document.getElementById("demo").innerHTML = xhr.responseText;
		} else {
			alert("error: " + xhr.responseText);
		}
	}
};
xhr.open("POST", "/", true);
$(document).ready(function () {
	"use strict";
	prepareList();
});

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