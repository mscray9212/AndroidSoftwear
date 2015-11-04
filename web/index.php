<?php
	session_start();
  include('connection.php');

  $msg = "";

  if(isset($_SESSION['loggedin']) && $_SESSION['loggedin'] == true){
      header("location: admin.php");
  }

  else {

    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        $name = $_POST['user'];
        $pass = $_POST['pass'];
        if ($name == '' || $pass == '') {
            $msg = "You must enter all fields";
        } else {
            $sql = "SELECT * FROM Customers WHERE User_Name = '$name' AND Password = '$pass'";
            $result = @mysql_query($sql);
            while($row = @mysql_fetch_array($result)) {
                header("location: admin.php");
                if($row['Admin'] == 1) {
                  $_SESSION['loggedin'] = true;
                  header("location: admin.php");
                } else {
                  echo "This account does not have Administrative privileges!";
                }
                exit;
            }
        }
    }

  }
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <title>Login</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <link rel="stylesheet" type="text/css" href="admin.css">
  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js" type="text/javascript"></script>
  <script src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.14.0/jquery.validate.js" type="text/javascript"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <script src="script/common.js"></script>
</head>
<body>

<div class="container-fluid">
  <div class="jumbotron">
    <h1>Softwear</h1>
    <p><center>Clothing for computer scientists by computer scientists!</center></p> 
  </div>
</div>
<div>
<div class="login">
  <div class="heading">
    <h2>Admin Login</h2>
    <form method="POST">

      <div class="input-group input-group-lg">
        <span class="input-group-addon"><i class="fa fa-user"></i></span>
        <input type="text" class="form-control" name="user" placeholder="Username">
          </div>

        <div class="input-group input-group-lg">
          <span class="input-group-addon"><i class="fa fa-lock"></i></span>
          <input type="password" class="form-control" name="pass" placeholder="Password">
        </div>
        <button id="login_btn" class="primary_btn" type="submit">Login</button>
        <button id="send_btn" class="secondary_btn" type="reset" value="Reset">Reset</button>
       </form>
    </div>
 </div>
</div>
  

</body>
</html>