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
    body {
    padding-top: 100px; /* Required padding for .navbar-fixed-top. Remove if using .navbar-static-top. Change if height of navigation changes. */
}

footer {
    margin: 50px 0;
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
              <a class="navbar-brand" href="index.html">BiblioBase</a>
            </div>
            <div class="navbar-collapse collapse">
              <ul class="nav navbar-nav">
                <li class="active"><a href="index.html">Home</a></li>
                <li><a href="about.html">About</a></li>
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


	<!-- Page Content -->
    <div class="container">

        <div class="row">

            <!-- Blog Entries Column -->
            <div class="col-md-8">

                <h1 class="page-header">
                    My Desk
                    <small>Welcome back <?php echo $_SESSION['username'];?></small>
                </h1>

                <!-- First Blog Post -->
                <h2>
                    <a href="#">Favorites</a>
                </h2>
                <p class="lead">
                    by <?php echo $_SESSION['username'];?>
                </p>
                <!-- Timestamp code we can use later if needed
                <p><span class="glyphicon glyphicon-time"></span> Posted on December 8, 2014 at 10:00 PM</p>
                -->
                <hr>
                <img class="img-responsive" src="http://samkillermann.com/wp-content/uploads/2014/07/desk-cacti.jpg" alt="">
                <hr>
                <p>Tables and things checked out goes here.</p>

                <hr>

                <!-- If we wanted to add more pages, we can add it here. Pager function.
                <ul class="pager">
                    <li class="previous">
                        <a href="#">&larr; Account Info</a>
                    </li>
                    <li class="next">
                        <a href="#">Newest books &rarr;</a>
                    </li>
                </ul>
                -->

            </div>

            <!-- Sidebar Widgets Column -->
            <div class="col-md-4">

                <!-- Search Well -->
                <div class="well">
                    <h4>Search</h4>
                    <div class="input-group">
                        <input type="text" class="form-control">
                        <span class="input-group-btn">
                            <button class="btn btn-default" type="button">
                                <span class="glyphicon glyphicon-search"></span>
                        </button>
                        </span>
                    </div>
                    <!-- /.input-group -->
                </div>

                <!-- Components Well -->
                <div class="well">
                    <h4>Components</h4>
                    <div class="row">
                        <div class="col-lg-6">
                            <ul class="list-unstyled">
                                <li><a href="#">Books</a>
                                </li>
                                <li><a href="#">Movies</a>
                                </li>
                            </ul>
                        </div>
                        <!-- /.col-lg-6 -->
                        <div class="col-lg-6">
                            <ul class="list-unstyled">
                                <li><a href="#">Music</a>
                                </li>
                                <li><a href="#">Events</a>
                                </li>
                            </ul>
                        </div>
                        <!-- /.col-lg-6 -->
                    </div>
                    <!-- /.row -->
                </div>

                <!-- Side Widget Well -->
                <div class="well">
                    <h4>Welcome!</h4>
                    <p>Bibliobase is a new, convenient way to search and track your favorite books, music, movies, and more at your local library! Use the search function above to get started.</p>
                </div>

            </div>

        </div>
        <!-- /.row -->

        <hr>

        <!-- Footer -->
        <footer>
            <div class="row">
                <div class="col-lg-12">
                    <p>Copyright &copy; Bibliobase 2014</p>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
        </footer>

    </div>
    <!-- /.container -->

    <!-- jQuery -->
    <script src="view/js/jquery.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="view/js/bootstrap.min.js"></script>

</body>

</html>