<?php
class DB_Functions {
 
    private $db;
    //put your code here
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
        $uuid = uniqid('', true);
		$hash = $this->hashSSHA($password);
		$encrypted_password = $hash["encrypted"]; // encrypted password
		$salt = $hash["salt"]; // salt
		$query="INSERT INTO users(unique_id, name, email, encrypted_password, salt, created_at,lastlogin_at) VALUES('$uuid', '$name', '$email', '$encrypted_password', '$salt', NOW(),NOW())";
		$result = mysql_query($query);
		// check for successful store
		
		if($result)
		{
			$uid = mysql_insert_id(); 
			$x="$uid";
			$string='table_'.$x;
		
			//------------new-------------
			$query="CREATE TABLE $string(
					contact_id INT(11) PRIMARY KEY AUTO_INCREMENT,
					owner_id INT(11) NOT NULL,
					name VARCHAR(50) NOT NULL,
					organisation VARCHAR(100),
					description VARCHAR(100),
					place VARCHAR(80),
					updated_at DATETIME,
					image BLOB
				);";
			$result = mysql_query($query);
			
        
			if ($result) 
			{
				$result = mysql_query("SELECT * FROM users WHERE uid = $uid");
				// return user details
				return mysql_fetch_array($result);
			} 
			else 
			{
				return false;
			}
		}
		else
		{
			return false;
		}
		
		
		
        
    }
 
    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
        $result = mysql_query("SELECT * FROM users WHERE email = '$email'") or die(mysql_error());
        // check for result 
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            $result = mysql_fetch_array($result);
            $salt = $result['salt'];
            $encrypted_password = $result['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $result;
            }
        } else {
            // user not found
            return false;
        }
    }
	
	/*				NEW
					For adding Contacts
	*/
	 public function addContact($uid,$name, $organisation, $description,$place,$image) {
		$a=$uid;
		$string='table_'.$a;
		//------------new-------------
		$name=mysql_real_escape_string($name);
		$organisation=mysql_real_escape_string($organisation);
		$description=mysql_real_escape_string($description);
		$place=mysql_real_escape_string($place);
		$query="INSERT INTO $string(owner_id, name, organisation, description, place,updated_at, image) VALUES($uid,'$name', '$organisation', '$description', '$place',NOW(),'$image')";
		$result = mysql_query($query);
		
		if($result)
		{
			$contact_id = mysql_insert_id();
			$result = mysql_query("SELECT * FROM $string WHERE contact_id = $contact_id");
			if($result)
				return mysql_fetch_array($result);
			else 
				return false;
				
		}
		else
		{
			return false;
		}    
    }
	
	
	/*				NEW
					For deleting Contacts
	*/
	public function deleteContact($uid,$contact_id) {
		$a=$uid;
		$string='table_'.$a;
		//------------new-------------
		$query="DELETE FROM $string WHERE contact_id=$contact_id";
		$result = mysql_query($query);
		if($result)
		{
			return true;
		}
		else
		{
			return false;
		}    
    }
	
	/*				NEW 
					For  updating Contacts
	*/
	public function updateContact($uid,$contact_id,$name,$organisation,$description,$place,$image) {
		$a=$uid;
		$string='table_'.$a;
		//------------new-------------
		$query="UPDATE $string SET name='$name',organisation='$organisation',description='$description',place='$place',updated_at=NOW(),image='$image' WHERE contact_id=$contact_id";
		$result = mysql_query($query);
		
		if($result)
		{
			$result = mysql_query("SELECT * FROM $string WHERE contact_id = $contact_id");
			if($result)
				return mysql_fetch_array($result);
			else 
				return false;
		}
		else
		{
			return false;
		}    
    }
	
	/*				NEW
					For searching Contacts
	*/
	 public function searchContact($uid, $string1, $string2, $string3) 
	 {
		$a=$uid;
		$string='table_'.$a;
		//------------new-----------
		$string1=mysql_real_escape_string($string1);
		$string2=mysql_real_escape_string($string2);
		$string3=mysql_real_escape_string($string3);
		
		$data1 = preg_split('/\s+/', $string1);
		$data2 = preg_split('/\s+/', $string2);
		$data3 = preg_split('/\s+/', $string3);
		
		$new_data1='%';
		$new_data2='%';
		$new_data3='%';
		for($i=0;$i<count($data1);$i++) 
		{
			$new_data1.=  $data1[$i];
			$new_data1.='%';
		}
		for($i=0;$i<count($data2);$i++) 
		{
			$new_data2.=  $data2[$i];
			$new_data2.='%';
		}
		for($i=0;$i<count($data3);$i++) 
		{
			$new_data3.=  $data3[$i];
			$new_data3.='%';
		}
		
		$query="SELECT * FROM $string 
				WHERE
				(
					name LIKE '$new_data1'
				    OR organisation LIKE '$new_data1'
				    OR description LIKE '$new_data1'
				    OR place LIKE '$new_data1'
				)AND 
				(
					name LIKE '$new_data2'
				    OR organisation LIKE '$new_data2'
				    OR description LIKE '$new_data2'
				    OR place LIKE '$new_data2'
				)AND 
				(
					name LIKE '$new_data3'
				    OR organisation LIKE '$new_data3'
				    OR description LIKE '$new_data3'
				    OR place LIKE '$new_data3'
				)"
				;

		$result = mysql_query($query);
		//return mysql_error();
		if($result)
		{	
			// return user details
			
			$num=mysql_num_rows($result);
			if($num=0)
			{
				return 1;
			}
			else
			{
				$ans=array();
				while($row = mysql_fetch_array($result))
				{
					$ans[]=$row;
				}
				return $ans;
			}
		}
		else
		{
			return false;
		}    
    }
	
	/*				NEW
					For recently updated Contacts
	*/
	public function recentContact($uid,$count) {
		$a=$uid;
		$string='table_'.$a;
		//------------new-------------
		
		$result = mysql_query("SELECT * FROM $string ORDER BY updated_at DESC LIMIT $count");
		if($result)
		{
			$num=mysql_num_rows($result);
			if($num=0)
			{
				return 1;
			}
			else
			{
				$ans=array();
				while($row = mysql_fetch_array($result))
				{
					$ans[]=$row;
				}
				return $ans;
			}
		}
		else 
			return false;
    }
	
	
	/*
			Update Last login time
	*/
	public function updateLoginTime($email) {
        $result = mysql_query("UPDATE users SET lastlogin_at = NOW() WHERE email = '$email'") or die(mysql_error());
		if($result)
		{
			return true;
		}
		else
		{
			return false;
		}
        
    }
    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $result = mysql_query("SELECT email from users WHERE email = '$email'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }
	
	/**
     * Check if user id exists or not
     */
    public function isUserIdExisted($uid) {
        $result = mysql_query("SELECT uid from users WHERE uid = '$uid'");
        $no_of_rows = mysql_num_rows($result);
        if ($no_of_rows > 0) {
            // user existed 
            return true;
        } else {
            // user not existed
            return false;
        }
    }
	
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
 
} 
?>