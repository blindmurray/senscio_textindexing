var fs = require("fs");
var spaces = "";
var path = "C:/MICHELLE/txtfiles";
read(path);
function read(readpath) {
    "use strict";
    fs.readdir(readpath, function(err, items) {
        spaces += "    ";
        for (var i = 0; i < items.length; i += 1) {
            display(spaces, items[i], readpath)
        }
        spaces = spaces.substring(0, spaces.length-4);
    });
}
function display(spaces, x, readpath){
    if(fs.lstatSync(readpath + "/" + x).isDirectory()){
                console.log(spaces + x);
                read(readpath + "/" + x);
                
            }
            else{
                console.log(spaces + items[i]);
            }
}