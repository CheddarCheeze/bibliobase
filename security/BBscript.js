function validateForm()
{
var username=document.forms["register"]["newuser"].value;
var newpass=document.forms["register"]["newpass"].value;
var secpass=document.forms["register"]["secpass"].value;
var email=document.forms["register"]["email"].value;
var atpos=email.indexOf("@");
var dotpos=email.lastIndexOf(".");

if (username==null || username=="" || newpass==null || newpass=="" 
	|| email==null || email=="")
	{
		alert("All fields must be filled out!");
		return false;
	}
else if(newpass!=secpass)
	{
		alert("Passwords did not match");
		return false;
	}
else if (atpos<1 || dotpos<atpos+2 || dotpos+2>=email.length)
	{
		alert("Not a valid e-mail address!");
		return false;
	}
}

function Bfocus(x,y){
if(y==1){
	x.style.background="blue";
	x.style.color="#000000";
	}
else{
	x.style.background="#336699";
	x.style.color="#ffffff";
	}
}

function httpPost(url, payload, callback) {
  var request = new XMLHttpRequest();
  request.onreadystatechange = function() {
    if(request.readyState == 4) {
      if(request.status == 200)
        callback(request.responseText);
      else
      {
        if(request.status == 0 && request.statusText.length == 0)
          alert("Request blocked by same-origin policy");
        else
          alert("Server returned status " + request.status +
            ", " + request.statusText);
      }
    }
  }
  request.open('POST', url, true);
  request.setRequestHeader('Content-Type',
    'application/json');
  request.send(payload);
}

function callback(response){
	document.getElementById("result").innerHTML=response;
}

function newPost(){
httpPost("A9Directory.php",null,callback);
}

function newDir(){
	var dirname = document.getElementById('folder').value;
	var payload = dirname;
	httpPost("A9Directory.php",payload,callback);
}
