<?php
include_once 'db_functions.php';

//Create object of db functions
$db = new DB_FUNCTIONS();

if(!empty($_POST["name"])){

    $contact_number = $_POST["name"];

    $saveData = $db->storeUser($contact_number);


    if($saveData){
        $status = 'OK';

    }
    else{
        $status = 'FAILED';

    }

    echo json_encode(array("response" => $status, "value" => $contact_number));

    $db->close();


}

else{
    echo json_encode(array("response" => "Hey I havent recieved any data"));

}


?>