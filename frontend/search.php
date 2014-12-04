<?php 
session_start();

if(!isset($_SESSION['username'])){
	
	$_SESSION['security'] = "guest";
}


if($_POST['logout']){
	//unset($_SESSION['username'], $_SESSION['security'], $_SESSION['email']);
	session_unset();
}

$searchterm = $_POST["atextsearch"];
$searchtype = $_POST["searchType"];
$data = "select " . $searchterm . " " . $searchtype;
$output = shell_exec('java ');
	
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
				<?php if($_SESSION['security'] != "guest"){?>
				<li><a href="first.php">My Desk</a></li>
				<?php }?>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Tools <span class="caret"></span></a>
                  <ul class="dropdown-menu" role="menu">
                    <li><a href="search.php">Search</a></li>
					<?php if(($_SESSION['security'] == "Patron") || ($_SESSION['security'] == "Administrator")){?>
                    <li><a href="add.php">Add Material</a></li>
					<li><a href="#">Delete Material</a></li>
					<?php }?>
                    <li class="divider"></li>
                    <li class="dropdown-header">Help/Services</li>
                    <li><a href="#">Questions?</a></li>
                    <li><a href="#">Request A Book</a></li>
                  </ul>
                </li>
              </ul>
			  <?php if(!isset($_SESSION['username'])){ ?>
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
							<input type="submit" name="logout" id="logout" class="btn btn-default" value="Log Out"></form><?
					}?>
            </div>
          </div>
        </div>

      </div>
    </div>
	<div id="backg">
		<br><br><br><br><br><br>
	</div>
	<div class="container" id="testing">
		<h2>Search for Materials</h2>
		<form role="form" action=<?php echo $_SERVER['SCRIPT_NAME']?> method="post">
			<div class="form-group">
			Search: <input type="text" id="atextsearch" name="atextsearch" class="form-control">
			*Type: <br><select name="searchType" id="searchType">
				<option selected="selected"></option>
				<option value="Book">Book</option>
				<option value="DVD">DVD</option>
				<option value="MCD">Music CD</option>
				<option value="BCD">Book CD</option>
			</select>
		</div>
		<input type="submit" value="Search" id="asearch" class="btn btn-default">
		</form>
	</div>		
	<div class="container" id="myResults">
		<?php 
		if(isset($searchtype)){
		?>
		<p><br>
			<?php if(isset($searchtype)){echo "You have searched " . $searchtype . " " . $searchterm . ".";}?></p>
			<h2>Results</h2>
		<table class="table table-bordered" id="resultList">
			<thead><tr>
				<th>Title</th>
				<th>Author</th>
				<th>Genre</th>
				<th>ISBN</th>
				<th>Availability</th>
			</tr></thead>
			<tbody id="listThem">
			</tbody>
		</table>
		<?php echo $data;}
		else{
			?><p>*You must select a type.</p><?php
			//echo $_SESSION['security']; //used these as debug tools
			//echo $_SESSION['email'];
		}
		?>	
	</div>
  </body>
</html>
