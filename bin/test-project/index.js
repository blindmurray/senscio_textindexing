function ajax() {
  "use strict";
  if(window.XMLHttpRequest) {
    return new XMLHttpRequest();
  } else if(window.ActiveXObject) {
    try {
      return new ActiveXObject("Msxml2.DOMDocument.6.0");
    } catch(e) {}
    try {
      return new ActiveXObject("Msxml2.DOMDocument.3.0");
    } catch(f) {}
    try {
      return new ActiveXObject("Microsoft.XMLHTTP");
    } catch(g) {}
  } else {
    alert("newxhr XMLHTTPRequest object failed");
    return null;
  }
}

function b1click() {
  //when search button is clicked, sends all info thru ajax to nodejs
  "use strict";
  var searchterm = document.getElementById("t1").value;
  var exten = document.getElementById("t2").value;
  var dateFrom = document.getElementById("t3").value;
  var dateTo = document.getElementById("t4").value;
  var num = document.getElementById("t8").value;
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
  xhr.onload = function() {
    if(xhr.readyState === 4 && xhr.status === 200) {
      document.getElementById("demo").innerHTML = xhr.responseText;
    } else {
      alert("Error: " + xhr.responseText);
    }
  };
  xhr.open("POST", "/", true);
  xhr.setRequestHeader("Content-Type", "text/plain");
  xhr.send(search);
  console.dir(search);
  xhr.end();
}

function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();
  var id_token = googleUser.getAuthResponse().id_token;
  var name = profile.getName();
  var email = profile.getEmail();
  if(email.length > 19 && email.substring(email.length - 19, email.length) == "@sensciosystems.com") {
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
    var signIn = {
      "id": "signIn",
      "idtoken": id_token
    };
    signIn = JSON.stringify(signIn);
    var xhr = ajax();
    xhr.open('POST', '/');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function() {
      if(xhr.readyState === 4 && xhr.status === 200) {
        console.log('Signed in as: ' + xhr.responseText);
      } else {
        alert("Error: " + xhr.responseText);
      }
    };
    xhr.send(signIn);
  } else {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function() {
      alert("You must sign in with a Senscio email.");
    });
  }
}

function signOut() {
  var auth2 = gapi.auth2.getAuthInstance();
  auth2.signOut().then(function() {
    console.log('User signed out.');
  });
}