<?php
include_once 'db_functions.php';

//Create object of db functions
$db = new DB_FUNCTIONS();


$json = $_POST['usersJSON'];


$json = stripslashes($json);

//Decode json into an Array;
$data = json_decode($json);

$a = array();
$b = array(); 

for($i=0;$i<count($data);$i++){
    $res = $db->storeUser($data[$i]->userId, $data[$i]->userName);

    if($res){
        $b["id"] = $data[$i]->userId;
        $b["status"] = 'yes';
        array_push($a, $b);

    }
    else{
        $b["id"] = $data[$i]->userId;
        $b["status"] = "no";
        array_push($a, $b);
        
    }

}


//$storeUser = $db->storeUser(2,"Mose Me");

//echo "Store user is:    ".$storeUser;

echo json_encode($a);

?>