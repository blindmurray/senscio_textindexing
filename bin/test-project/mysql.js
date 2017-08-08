var mysql      = require('mysql');
var connection = mysql.createConnection({
  host     : '10.0.55.100',
  user     : 'root',
  password : '',
  database : 'indexer'
});

function b2click() {
    console.log("reached");
    var user = document.getElementById("user").value;
    var pass = document.getElementById("pass").value;
    console.log("hey");
    connection.query('INSERT INTO `indexer`.`account` (`username`, `userpass`) VALUES (user, pass)', function (error, results, fields) {
        if (error) throw error;
          console.log('The solution is!!');
        });
};
