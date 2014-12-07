<?php 
session_start();

//if not logged in set security settings to guest
if(!isset($_SESSION['username'])){
	$_SESSION['security'] = "guest";
}

//if logout button is clicked, supposed to unset login info
//currently doesn't work...
if($_POST['logout']){
	//unset($_SESSION['username'], $_SESSION['security'], $_SESSION['email']);
	session_unset();
}

//setting path name to variable
$path = dirname(__FILE__);

//run javac on BibliobaseDBMS.java once
$count = 0;
if($count == 0){
	exec("javac database_mgmt/BiblioBaseDBMS.java");
	$count = 1;
}

//sets variable to search text field if set and if not to *
if(!isset($_POST["atextsearch"]) || $_POST["atextsearch"] == null){
	$searchterm = "*";
}
else
{
	$searchterm = $_POST["atextsearch"];
}

//set variables to search type {book, dvd, music cd, book cd}
$searchtype = $_POST["searchType"];

//set variable for search by field
if(!isset($_POST["searchKind"]) || $_POST["searchKind"] == null){
	$searchkind = "title";
}
else{
	$searchkind = $_POST["searchKind"];
}


//when search button is clicked run program to connect to database
if($_POST["asearch"]){
	if($searchterm == "*"){
		$output = shell_exec("cd $path" . "&& java database_mgmt/BiblioBaseDBMS test \"select * from $searchtype;\"");
		$data = json_decode($output, TRUE);
	}
	else{
		$output = shell_exec("cd $path" . "&& java database_mgmt/BiblioBaseDBMS test \"select * from $searchtype where $searchkind = '$searchterm';\"");
		$data = json_decode($output, TRUE);
	}
	
}

if($_POST["checkOut"]){
	//do something here to check the book out :) not implemented yet
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
				if($_SESSION['security'] != "guest"){	
				?>
				<li><a href="first.php">My Desk</a></li>
				<?php }?>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Tools <span class="caret"></span></a>
                  <ul class="dropdown-menu" role="menu">
                    <li><a href="search.php">Search</a></li>
					<?php 
					//only show if person logged in is a patron or admin
					if(($_SESSION['security'] == "Patron") || ($_SESSION['security'] == "Administrator")){?>
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
			Search By: <br><select name="searchKind" id="searchKind">
				<option selected="selected"></option>
				<option value="title">Title</option>
				<option value="author">Author</option>
				<option value="isbn">ISBN</option>
				<option value="genre">Genre</option>
			</select>
			<br>*Type: <br><select name="searchType" id="searchType">
				<option selected="selected"></option>
				<option value="Book">Book</option>
				<option value="DVD">DVD</option>
				<option value="MCD">Music CD (MCD)</option>
				<option value="BCD">Book CD (BCD)</option>
			</select>
		</div>
		<input type="submit" value="Search" name="asearch" class="btn btn-default">
		</form>
	</div>		
	<div class="container" id="myResults">
		<?php 
		//displays if search type has been set
		if(isset($searchtype)){
		?>
		<p><br>
			<?php 
			print "You have searched $searchtype $searchterm.";
			if(!isset($data) || $data == null){
				echo "<p>Did not find any $searchtype $searchterm $searchkind.</p>";
			}
			else{
		?></p>
			<h2>Results: <?php echo $searchtype?></h2>
		<table class="table table-bordered" id="resultList">
			<thead><tr>
				<th>Select</th>
				<th>Title</th>
				<th>Author</th>
				<th>Genre</th>
				<th>Availability</th>
			</tr></thead>
			<?php
			for($i = 0; $i < count($data); $i++){
				$isbn = $data[$i]["isbn"];
				$title = $data[$i]["title"];
				$author = $data[$i]["author"];
				$genre = $data[$i]["genre"];
				$avail = $data[$i]["avail"];
			?>
			<tbody id="listThem" name="listThem">
				<th><input type="checkbox" name="check_list[]" value=<?php echo $isbn?>></th>
				<th><a href="#"><?php echo $title?></a></th>
				<th><?php echo $author?></th>
				<th><?php echo $genre?></th>
				<th><?php echo $avail?></th>
			</tbody>
			<?php
				}
			?>
		</table>
		<button type="button" name="checkOut" class="btn btn-default">Check Out</button>
		<?php }}
		else{
			//displays to remind the person to select a type
			?><p>*You must select a type.</p><?php
			//echo $_SESSION['security']; //used these as debug tools
			//echo $_SESSION['email'];
			//echo "\"select * from $searchtype where $searchkind = '$searchterm';\"";
		}
		?>	
	</div>
  </body>
</html>
