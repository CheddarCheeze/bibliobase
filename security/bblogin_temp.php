<script type="text/javascript" src ="BBscript.js"></script>

<div id="content">

<?php session_start(); ?>
<title>Bibliobase Login</title>
<html><body><fieldset>
	<h2>Member login</h2>
	<a href="accounts.json">Debugging-Accounts List</a>
	<form action = "<?php echo $_SERVER['SCRIPT_NAME']; ?>" method="post">
	Username:<br><input type ="text" name = "username"><br>
	Password:<br><input type ="password" name = "password"><br>
	<input type = "submit" value = "Login" style="background-color: #336699; color: #ffffff;" name = "login" onmouseover="Bfocus(this,1)" onmouseout = "Bfocus(this,2)">
</form></fieldset></body></html>

<?php
function CheckAccounts($username,$password){
$useraccounts = json_decode(file_get_contents("accounts.json"));
	if(isset($useraccounts)){
		for($i=0;$i<count($useraccounts);$i++){
			if($useraccounts[$i]->username == $username && $useraccounts[$i]->password == $password){
				$_SESSION['email'] = $useraccounts[$i]->email;
				$_SESSION['seclevel'] = $useraccounts[$i]->seclevel;
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
			header("Location:members.php");
			exit;
		}
		else{
			echo "<script language=javascript>alert('Invalid username/password!')</script>";
		}
	}
}
?>

</br></br></br>
<html><body><fieldset>
	<h2>Register Account</h2>
	<form name ="register" action = "<?php echo $_SERVER['SCRIPT_NAME']; ?>" 
	onsubmit ="return validateForm()" method="post">
	Username:</br><input type ="text" name = "newuser"></br>
	Password:</br><input type ="password" name = "newpass"></br>
	Re-enter Password:</br><input type ="password" name = "secpass"></br>
	Email:</br><input type = "text" name = "email"></br>
	Security Level:</br><input type = "text" name= "seclevel"></br>
	<input type = "submit" value = "Register" name = "register" style="background-color: #336699; color: #ffffff;" onmouseover="Bfocus(this,1)" onmouseout = "Bfocus(this,2)">
</form></fieldset></body></html>

<?php
class Accounts{}
	if(isset($_POST['register'])){
			$myob = new Accounts();
			$myob->username = $_POST['newuser'];
			$myob->password = $_POST['newpass'];
			$myob->email = $_POST['email'];
			$myob->seclevel = $_POST['seclevel'];
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
