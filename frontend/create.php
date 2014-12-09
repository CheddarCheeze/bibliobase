<?php 
session_start();

//if not logged in set security settings to guest
if(!isset($_SESSION['username'])){
	$_SESSION['security'] = 0;
}

//setting path name to variable
//$path = dirname(dirname(__FILE__));
$path = dirname(__FILE__);

//run javac on BibliobaseDBMS.java 
//exec("javac database_mgmt/BiblioBaseDBMS.java");

if($_POST["subType"]){
	$type = $_POST["newType"];
	$numcol = $_POST["amtCol"];
	$secLevel = $_POST["seclev"];
}

if($_POST["subCol"]){
	$type = $_POST["newType2"];
	$numcol = $_POST["amtCol2"];
	$secLevel = $_POST["seclev2"];
	$cols = array();
	$dataType = array();
	foreach($_POST['col'] as $v){
		if($v == null){
			$error = "Error: Missing Column Name. Please start over.";
			break;
		}
		array_push($cols, $v);
	}
	foreach($_POST['datatype'] as $b){
		if($b == null){
			$error = "Error";
			break;
		}
		array_push($dataType, $b);
	}
	
	for($i = 0; $i < count($cols); $i++){
		$sendData = $sendData . "'$cols[$i]' $dataType[$i],";
	}
	
	$remove = substr($sendData, 0, -1);
	
	$output1 = shell_exec("cd $path && java database_mgmt/BiblioBaseDBMS test \"create table $type ($remove);\"");
	$output2 = shell_exec("cd $path && java database_mgmt/BiblioBaseDBMS test \"insert into table_security values('$type','$secLevel');\"");
	
	$test ="cd $path && java database_mgmt/BiblioBaseDBMS test \"create table $type ($remove);\"";
	
}
	
?>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="favicon.ico">
	<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">

    <title>Software Engineering Project</title>

    <!-- Bootstrap core CSS -->
    <link href="view/css/bootstrap.min.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="../../assets/js/ie-emulation-modes-warning.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <!-- Custom styles for this template -->
    <link href="view/css/carousel.css" rel="stylesheet">
	<style>
	#backg {
		background-color: #777;
		background-size: 100% 100px;
		background-repeat: no-repeat;
	}
	</style>
	</head>
<!-- NAVBAR
================================================== -->
  <body>
    <div class="navbar-wrapper">
      <div class="container">

        <div class="navbar navbar-inverse navbar-static-top" role="navigation">
          <div class="container">
            <div class="navbar-header">
              <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
              <a class="navbar-brand" href="#">BiblioBase</a>
            </div>
            <div class="navbar-collapse collapse">
              <ul class="nav navbar-nav">
                <li class="active"><a href="index.html">Home</a></li>
                <li><a href="#about">About</a></li>
				<?php 
				//only show My Desk if person is logged in
				if($_SESSION['security'] != 0){	
				?>
				<li><a href="first.php">My Desk</a></li>
				<?php }?>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Tools <span class="caret"></span></a>
                  <ul class="dropdown-menu" role="menu">
                    <li><a href="search.php">Search</a></li>
					<?php 
					//only show if person logged in is a patron or admin
					if($_SESSION['security'] > 3){?>
                    <li><a href="add.php">Add Material</a></li>
					<li><a href="#">Delete Material</a></li>
					<?php }
					if($_SESSION['security'] == 4){
					?>
					<li><a href="create.php">Create Table</a></li>
					<li><a href="#">Modify Table</a></li>
					<?php }?>
                    <li class="divider"></li>
                    <li class="dropdown-header">Help/Services</li>
                    <li><a href="#">Questions?</a></li>
                    <li><a href="#">Request A Book</a></li>
                  </ul>
                </li>
              </ul>
			  <?php 
			  //makes the log in form disappear if someone is logged in and changes to
			  //show Welcome, username with log out button
			  if(!isset($_SESSION['username'])){ ?>
	              <form class="navbar-form navbar-right" action="security/bblogin.php" role="search">
	                    <div class="form-group">
	                        <input type="text" class="form-control" name="username" placeholder="Username">
	                    </div>
	                    <div class="form-group">
	                        <input type="text" class="form-control" name="password" placeholder="Password">
	                    </div>
	                    <label class="checkbox">
	          <input type="checkbox" value="remember-me"> Remember me
	          </label>
	                    <button type="submit" onclick="check(this.form)" class="btn btn-default">Sign In</button>
	                </form>
					<?php }
					else
					{
						?>
						<form  name="logout" class="navbar-form navbar-right" action="index.html">
							Welcome, <?php echo $_SESSION['username'];?><br>
							<button class="btn btn-default" name="logout"><a href="logout.php">Log Out</a></button></form><?
					}?>
            </div>
          </div>
        </div>
      </div>
    </div>
	<div id="backg">
		<br><br><br><br><br><br>
	</div>
	<div class="container" id="formCreate">
		<div class="container">
			<h2>Create a new Table:</h2><br>
			<form action=<?php echo $_SERVER['SCRIPT_NAME']?> role="form" method="post">
			<label>Type:</label>
			<input type="text" class="form-control" name="newType">
			<label>Number of Columns:</label>
			<input type="text" class="form-control" name="amtCol">
			<label>Security Level:</label>
				<br><select name="seclev">
						<option selected="selected"></option>
						<option value="0">Everyone</option>
						<option value="1">Patrons, Librarians, Admin</option>
						<option value="2">Librarians, Admin</option>
						<option value="3">Admin Only</option>
				</select><br>
			<input type="submit" name="subType" value="Next" class="btn btn-default">
			</form><br><br>
			<form action=<?php echo $_SERVER['SCRIPT_NAME']?> role="form" method="post">
				<input type="hidden" name="newType2" value=<?php echo $type;?>>
				<input type="hidden" name="amtCol2" value=<?php echo $numcol;?>>
				<input type="hidden" name="seclev2" value=<?php echo $secLevel;?>>
			<?php
			//echo $test;
			if(isset($type)){
			for($i = 0; $i < $numcol; $i++){
				?>
				<label>Column <?php echo ($i + 1);?></label>
				<input type="text" class="form-control" name="col[]">
				<label>Choose Data Type for Column <?php echo ($i + 1);?></label>
				<br><select name="datatype[]">
						<option selected="selected"></option>
						<option value="string">String</option>
						<option value="float">Number</option>
						<option value="date">Date</option>
				</select><br>
				<?php
			}
			//print_r($col);
			?>
			<input type="submit" class="btn btn-default" name="subCol" value="Create Table">
			</form>
			<?php }
			echo $error;
			//echo $remove;
			//echo $type;
			echo "$output1 $output2";
			?>
		</div>
	</div>		
	<div class="container">
	</div>
  </body>
</html>
