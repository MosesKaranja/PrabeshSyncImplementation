<?php
class DB_CONNECT{

    //constructor
    function __construct()
    {
        
    }

    function __destruct()
    {
        //$this->close();
        
    }

    public function connect(){
        require_once 'config.php';

        //Connect to mysql
        $con = mysqli_connect(DB_HOST,DB_USER,DB_PASSWORD,DB_DATABASE);

        if(!$con){
            echo "Connection failure: ".mysqli_connect_error();
            return false;

        }
        else{
            return $con;

        }
    
    }

    public function close(){
        $connectionObject = $this->connect();
        mysqli_close($connectionObject);

    }



}

?>