<?php
$configs = include('config.php');

$server='localhost';
//$username='root';
$password='password';
$uname='Username';
$dbname='customer-client.accdb';
$cust_Type='accountType'||'accountTypex';

//Create connection
$conn= new mysqli($server, $uname, $password, $dbname);
if ($conn->connect_error){
    die("Connection failed: " . $conn->connect_error);
}
$sql;
if ($cust_Type==true){
$sql= "INSERT INTO customer-client (Username, Customer, Password) VALUES ($uname,'true',$password)";
}else {
    $sql= "INSERT INTO customer-client (Username, Artist, Password) VALUES ($uname,'true',$password)";
}
if ($conn->query($sql) === TRUE) {
    echo "Welcome : ".$uname." ".$u_sname;
  } else {
    echo "Error: " . $sql . "<br>" . $conn->error;
  }


$conn->close();
?>