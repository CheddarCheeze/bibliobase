<?php 
session_start();

//if not logged in set security settings to guest
if(!isset($_SESSION['username'])){
	$_SESSION['security'] = "0";
}

//if logout button is clicked, supposed to unset login info
//currently doesn't work...
if($_POST['logout']){
	//unset($_SESSION['username'], $_SESSION['security'], $_SESSION['email']);
	session_destroy();
}

//setting path name to variable
//$path = dirname(dirname(__FILE__));
$path = dirname(__FILE__);
$change = array();

$thissecurity = $_SESSION['security'];

$level = shell_exec("cd $path && java database_mgmt/BiblioBaseDBMS test \"select * from table_security where security_lvl <= $thissecurity;\"");

$addtype = json_decode($level, TRUE);

//run javac on BibliobaseDBMS.java 
//exec("javac database_mgmt/BiblioBaseDBMS.java");
if($_POST["next"]){
	$type = $_POST["type"];
	
	$info = shell_exec("cd $path && java database_mgmt/BiblioBaseDBMS test \"get attributes from $type;\"");
	$change = json_decode($info, TRUE);
}

if($_POST["addConfirm"]){
	$type = $_POST['newtype'];
	$attr = $_POST['newattr'];
	$num = $_POST['numcopy'];
	
	foreach($_POST['textinput'] as $b){
		$parenth = $parenth . "'$b',";
	}
	
	$newparenth = substr($parenth, 0, -1);
	
	for($i = 0; $i < $num; $i++){
		$output = shell_exec("cd $path && java database_mgmt/BiblioBaseDBMS test \"insert into $type values($newparenth);\"");
	}
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
	
	<?php if($_SESSION['security'] <= 1){
		?><meta http-equiv="refresh" content="1; url=index.html"><?php
	}
	?>
	
	<script>
	function turnOff()
	{
		document.getElementById("addForm").style.visibility = "hidden";
	}
	</script>

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
				if($_SESSION['security'] >= "1"){	
				?>
				<li><a href="first.php">My Desk</a></li>
				<?php }?>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Tools <span class="caret"></span></a>
                  <ul class="dropdown-menu" role="menu">
                    <li><a href="search.php">Search</a></li>
					<?php 
					//only show if person logged in is a patron or admin
					if($_SESSION['security'] >= "2"){?>
                    <li><a href="add.php">Add Material</a></li>
					<?php }
					if($_SESSION['security'] >= "3"){
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
	<div class="container">
		<h2>Add Materials</h2>
	</div>
	<div class="container">
		<form role="form" action=<?php echo $_SERVER['SCRIPT_NAME']?> method="post">
		<label>Type:</label>
		<br><select name="type" id="type">
			<option selected="selected"></option>
			<?php for($i = 0; $i < count($addtype); $i++){
				$newtype = $addtype[$i]["table_name"];
			?>
			<option value=<?php echo $newtype;?>><?php echo $newtype;?></option>
			<?php }?>
		</select><br>
		<input type="submit" value="Next" name="next" class="btn btn-default"><br>
	</form>
	<?php if(isset($type)){?>
		<p id="addForm">Please fill out all fields.
		<form role="form" action=<?php echo $_SERVER['SCRIPT_NAME']?> method="post">
			<div class="form-group">
				<input type="hidden" name="newtype" value=<?php echo $type;?>>
				<?php for($i = 0; $i < (count($change) - 1); $i++){
					$tothis = $change[$i + 1]["name"];
				?>
			<label><?php echo $tothis;?>:</label>
			<input type="text" name="textinput[]" id="title" class="form-control">
			<?php }?>
			<label>Number of Copies:</label>
			<input type="text" name="numcopy" class="form-control">
		</div>
			<input type="submit" value="Add" name="addConfirm" id="addConfirm" class="btn btn-default">
		</form>
		<?php //print($parenth);
	}
		?>
		<?php //echo "$change $output";?>
	</p>
	</div>
  </body>
</html>
