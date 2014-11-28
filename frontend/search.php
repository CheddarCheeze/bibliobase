<?php 
session_start();

$searchterm = $_POST["atextsearch"];
$searchtype = $_POST["searchlist"];
	
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
				<li><a href="first.php">Your Desk</a></li>
                <li><a href="#contact">Contact</a></li>
                <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Tools <span class="caret"></span></a>
                  <ul class="dropdown-menu" role="menu">
                    <li><a href="#">Search</a></li>
                    <li><a href="#">Modify</a></li>
                    <li><a href="#">Create</a></li>
                    <li class="divider"></li>
                    <li class="dropdown-header">Nav header</li>
                    <li><a href="#">Separated link</a></li>
                    <li><a href="#">One more separated link</a></li>
                  </ul>
                </li>
				<li><a href="search.php">Search</a></li>
              </ul>
            </div>
          </div>
        </div>

      </div>
    </div>
	<div id="backg">
		<br><br><br><br><br><br>
	</div>
	<div id="search">
		<form action=<?php echo $_SERVER['SCRIPT_NAME']?> method="post">
			<input type="text" id="atextsearch" name="atextsearch">
			<select name="searchlist">
				<option selected="selected">Select Type</option>
				<option value="keyword">Keyword</option>
				<option value="author">Author</option>
				<option value="title">Title</option>
				<option vaule="subject">Subject</option>
			</select>
			<input type="submit" value="Search" id="asearch">
		</form>
	</div>		
	<div id="resultsearch">
		<p><br>
			<?php if(isset($searchtype)){echo "You have searched " . $searchtype . " " . $searchterm . ".";}?></p>
	</div>
	<div>
		<?php if(isset($searchtype)){?>
		<table border="1" style="width:75%">
			<thead><tr>
				<td>Title</td>
				<td>Author</td>
				<td>Type</td>
				<td>Other</td>
			</thead></tr>
			<tr>
				<td><?php echo $searchterm;?></td>
				<td>Author</td>
				<td>Music</td>
				<td>CD</td>
			</tr>
		</table>
		<?php }?>	
	</div>
  </body>
</html>