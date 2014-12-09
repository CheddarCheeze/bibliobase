<?php 
session_start();

if(!isset($_SESSION['username'])){
	$_SESSION['security'] = "0";
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

    <title>Software Engineering Project</title>

    <!-- Bootstrap core CSS -->
    <link href="view/css/bootstrap.min.css" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
    <script src="../../assets/js/ie-emulation-modes-warning.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
	
	<?php if($_SESSION['security'] == 0){
		?><meta http-equiv="refresh" content="1; url=index.html"><?php
	}
	?>

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
	#search {
		position: aboslute;
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
	<div id="homepage">
		<p>This is where your favorites and such will be. This is also where the last <br> things you checked out will be if you are a patron.</p>
	</div>
  </body>
</html>
