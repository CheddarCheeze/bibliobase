<?php
session_start();
?>

<script type="text/javascript" src ="BBscript.js"></script>

<div id="content">
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"> 
    <meta charset="utf-8">
    <title>Bibliobase - Login/Signup</title>
    <meta name="generator" content="Bootply" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link href="../view/css/bootstrap.min.css" rel="stylesheet">
    
    <!--[if lt IE 9]>
      <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <link rel="shortcut icon" href="favicon.ico">

    <!-- This is because Macs require a different image -->
    <link rel="apple-touch-icon" href="/bootstrap/img/apple-touch-icon.png">
    <link rel="apple-touch-icon" sizes="72x72" href="/bootstrap/img/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="114x114" href="/bootstrap/img/apple-touch-icon-114x114.png">

    
    <style type="text/css">
      .modal-footer {   border-top: 0px; }
    </style>

    <!-- Chase's PHP login work -->
    <?php
      function CheckAccounts($username,$password){
      $useraccounts = json_decode(file_get_contents("accounts.json"));
        if(isset($useraccounts)){
          for($i=0;$i<count($useraccounts);$i++){
            if($useraccounts[$i]->username == $username && $useraccounts[$i]->password == $password){
              $_SESSION['email'] = $useraccounts[$i]->email;
              $_SESSION['security'] = $useraccounts[$i]->seclevel;
              return true;    
            }
          }
        }
      }
        
      if(isset($_POST['login'])){
        if(empty($_POST['username']) || empty($_POST['password']))
          echo "<script language=javascript>alert('Invalid username/password!')</script>";
        else{
          $username = $_POST['username'];
          $password = $_POST['password'];
          $check = CheckAccounts($username,$password);
          if($check==true){
            $_SESSION['username'] = $username;
            ?>
            <meta http-equiv="refresh" content="1; url=../first.php"><?php;
            exit;
          }
          else{
            echo "<script language=javascript>alert('Invalid username/password!')</script>";
          }
        }
      }
    ?>
    <!-- End of Chase's PHP login work -->

    <!-- More of Chase's PHP work -->
    <?php
    class Accounts{}
      if(isset($_POST['register'])){
          $myob = new Accounts();
          $myob->username = $_POST['newuser'];
          $myob->password = $_POST['newpass'];
          $myob->email = $_POST['email'];
          if(file_exists("accounts.json"))
            $accounts = json_decode(file_get_contents("accounts.json"));
          if(isset($accounts)){
            for($i=0; $i<count($accounts); $i++)
            {
              if($_POST['newuser']==$accounts[$i]->username)
              { 
                echo "<script language=javascript>alert('Username already exists!')</script>";
                return false;
              }                 
            }
          }
          
          $fh = fopen("accounts.json", 'w');
          if($fh === false)
            die("Failed to open accounts.json for writing.");
          else
          {
            $accounts []= $myob;
            fwrite($fh, json_encode($accounts));
            fclose($fh);
          }
      }
    ?>
    </div>
    <!-- End of More of Chase's PHP work -->


  </head>
    
    <body>
        
      <!--login modal-->
<div id="loginModal" class="modal show" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
  <div class="modal-content">
      <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
          <h1 class="text-center">Login</h1>
      </div>
      <div class="modal-body">
          <form class="form col-md-12 center-block" action = "<?php echo $_SERVER['SCRIPT_NAME']; ?>" method="post">
            <div class="form-group">
              <input type="text" class="form-control input-lg" placeholder="Username" name="username">
            </div>
            <div class="form-group">
              <input type="password" class="form-control input-lg" placeholder="Password" name="password">
            </div>
            <div class="form-group">
              <button class="btn btn-primary btn-lg btn-block" type="submit" value="Login" name="login" onmouseover="Bfocus(this,1)" onmouseout="Bfocus(this,2)">Sign In</button>
              <span class="pull-right"><a data-toggle="modal" href="#signupModal">Register</a></span>
              <span><a href="../index.html">Go back</a></span>
            </div>
          </form>
      </div>
      <div class="modal-footer">
          <div class="col-md-12">
          <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
		  </div>	
      </div>
  </div>
  </div>
</div>



      <!--signup modal-->
<div id="signupModal" class="modal" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog">
  <div class="modal-content">
      <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
          <h1 class="text-center">Sign up</h1>
      </div>
      <div class="modal-body">
          <form class="form col-md-12 center-block" name ="register" action="<?php echo $_SERVER['SCRIPT_NAME']; ?>" onsubmit="return validateForm()" method="post">
            <div class="form-group">
              <input type="text" class="form-control input-lg" placeholder="Username" name="newuser">
            </div>
            <div class="form-group">
              <input type="password" class="form-control input-lg" placeholder="Password" name="newpass">
            </div>
            <div class="form-group">
              <input type="password" class="form-control input-lg" placeholder="Re-enter Password" name="secpass">
            </div><div class="form-group">
              <input type="text" class="form-control input-lg" placeholder="Email" name="email">
            </div>
            <div class="form-group">
              <button class="btn btn-primary btn-lg btn-block" type="submit" value="Register" name="register" onmouseover="Bfocus(this,1)" onmouseout="Bfocus(this,2)">Create Account</button>
              <span class="pull-right"><a data-dismiss="modal" href="#">Login</a></span>
              <!-- <span><a href="#">Need help?</a></span>-->
            </div>
          </form>
      </div>
      <div class="modal-footer">
          <div class="col-md-12">
          <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
      </div>  
      </div>
  </div>
  </div>
</div>

    <script type='text/javascript' src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
    <script type='text/javascript' src="http://netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>

    <!-- JavaScript jQuery code from Bootply.com editor  -->
    <!--
    <script type='text/javascript'>
    $(document).ready(function() {
    });
    </script>
    -->

    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
      ga('create', 'UA-40413119-1', 'bootply.com');
      ga('send', 'pageview');
    </script>


    <script type='text/javascript'>
        
    $(document).ready(function() {
        $('#openBtn').click(function(){ $('#myModal').modal({show:true}) });

        $('.modal').on('hidden.bs.modal', function( event ) {
          $(this).removeClass( 'fv-modal-stack' );
          $('body').data( 'fv_open_modals', $('body').data( 'fv_open_modals' ) - 1 );
        });

        $( '.modal' ).on( 'shown.bs.modal', function ( event ) {
               
          // keep track of the number of open modals 
          if ( typeof( $('body').data( 'fv_open_modals' ) ) == 'undefined' ) {
            $('body').data( 'fv_open_modals', 0 );
          }
                                    
          // if the z-index of this modal has been set, ignore.
          if ( $(this).hasClass( 'fv-modal-stack' ) ) {
            return;
          }

          $(this).addClass( 'fv-modal-stack' );

          $('body').data( 'fv_open_modals', $('body').data( 'fv_open_modals' ) + 1 );

          $(this).css('z-index', 1040 + (10 * $('body').data( 'fv_open_modals' )));

          $( '.modal-backdrop' ).not( '.fv-modal-stack' )
            .css( 'z-index', 1039 + (10 * $('body').data( 'fv_open_modals' )));

          $( '.modal-backdrop' ).not( 'fv-modal-stack' )
            .addClass( 'fv-modal-stack' ); 
        });
    });
    </script>
        
    </body>
</html>
</div>
