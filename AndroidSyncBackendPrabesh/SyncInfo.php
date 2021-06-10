<?php
include_once 'db_functions.php';

//Create object of db functions
$db = new DB_FUNCTIONS();

if(!empty($_POST["contact_number"])){

    $contact = $_POST["contact_number"];

    $saveData = $db->storeUser($contact);


    if($saveData){
        $status = 'OK';

    }
    else{
        $status = 'FAILED';

    }

    echo json_encode(array("response" => $status));

    $db->close();


}

else{
    echo json_encode(array("response" => "Hey I havent recieved any data"));

}


?>