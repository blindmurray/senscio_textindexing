var fs = require('fs');
var spaces = "";
var path = "C:/Users/dxia/Documents/commons-lang3-3.6-src";
 
read(path);
function read(path){
	fs.readdir(path, function(err, items) {
		spaces += " ";
		for (var i=0; i<items.length; i++) {
			if(fs.lstatSync(path + "/" + items[i]).isDirectory()){
				console.log(spaces + items[i]);
				read(path + "/" + items[i], function(){
					spaces = spaces.substring(0, spaces.length-1);
				});
			}
		}
	});
}