<?php
 
class DB_Connect {
 
    // constructor
    function __construct() {
         
    }
 
    // destructor
    function __destruct() {
        // $this->close();
    }
 
    // Connecting to database
    public function connect() {
        //require_once 'config.php';
        // connecting to mysql
        //$con = mysql_connect(DB_HOST, DB_USER, DB_PASSWORD);
        $con = mysql_connect("localhost", "root", "");
        // selecting database
        mysql_select_db("android_api");
 
        // return database handler
        return $con;
    }
 
    // Closing database connection
    public function close() {
        mysql_close();
    }
 
}
 
?>