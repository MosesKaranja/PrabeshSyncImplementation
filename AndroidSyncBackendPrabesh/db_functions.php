<?php
class DB_FUNCTIONS{
    private $db;

    function __construct()
    {
        include_once 'db_connect.php';
        $myVar = new DB_CONNECT;
        $this->db = $myVar->connect();
        
    }

    function __destruct()
    {
        
    }

    public function storeUser($contact_number){

        $result = mysqli_query($this->db,"INSERT INTO  users_new(`user_name`) VALUES ('$contact_number')");

        if($result){
            //return "Yeah We created a user". $result;
            return true;

        }
        else{
            //return "NOOOO, Failed Crreating User". mysqli_error($this->db);
            //return mysqli_error($this->db);
            //The value above can be very useful when debugging.
            //Just have to find how to output it with false to show this is the error and the reason is dis.
            return false;

        }

    }

    public function getAllUsers(){
        $result = mysqli_query($this->db, "SELECT * FROM `contacts`");
        return $result;


    }

    
    public function close(){
        $connectionObject = $this->db;
        mysqli_close($connectionObject);

    }
}

?>