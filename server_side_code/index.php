<?php
/**
 * File to handle all API requests
 * Accepts GET and POST
 * 
 * Each request will be identified by TAG
 * Response will be JSON data
 
  /**
 * check for POST request 
 */

if (isset($_POST['tag']) && $_POST['tag'] != '') 
{
    // get tag
    $tag = $_POST['tag'];
	
    // include db handler
    require_once 'include/DB_Functions.php';
    $db = new DB_Functions();
 
    // response Array
    $response = array("tag" => $tag, "success" => 0, "error" => 0);
 
    // check for tag type
    if ($tag == 'login') 
	{
        // Request type is check Login
        $email = $_POST['email'];
        $password = $_POST['password'];
 
        // check for user
        $user = $db->getUserByEmailAndPassword($email, $password);
        if ($user != false) 
		{
            // user found
            // echo json with success = 1
            $response["success"] = 1;
            $response["uid"] = $user["uid"];
            $response["user"]["name"] = $user["name"];
            $response["user"]["email"] = $user["email"];
            $response["user"]["created_at"] = $user["lastlogin_at"];
			
			//UPDATE lastlogin time
			$user2=$db->updateLoginTime($email);
			if($user2 )
			{
				//login time updated
				$response["success2"]=1;
			}
			else
			{
				//error in updating login time
				$response["error2_msg"]="Unable to update login time";
			}
			
            echo json_encode($response);
        } 
		else 
		{
            // user not found
            // echo json with error = 1
            $response["error"] = 1;
            $response["error_msg"] = "Incorrect email or password!";
            echo json_encode($response);
        }
    } 
	else if ($tag == 'register') 
	{
        // Request type is Register new user
        $name = $_POST['name'];
        $email = $_POST['email'];
        $password = $_POST['password'];
 
        // check if user is already existed
        if ($db->isUserExisted($email)) 
		{
            // user is already existed - error response
            $response["error"] = 2;
            $response["error_msg"] = "User already existed";
            echo json_encode($response);
        } 
		else 
		{
            // store user
            $user = $db->storeUser($name, $email, $password);
            if ($user) 
			{
                // user stored successfully
                $response["success"] = 1;
                $response["uid"] = $user["uid"];
                $response["user"]["name"] = $user["name"];
                $response["user"]["email"] = $user["email"];
                $response["user"]["created_at"] = $user["created_at"];
                $response["user"]["lastlogin_at"] = $user["lastlogin_at"];
                echo json_encode($response);
            } 
			else 
			{
                // user failed to store
                $response["error"] = 1;
                $response["error_msg"] = "Error occured in Registartion";
                echo json_encode($response);
            }
        }
    } 
	else if ($tag == 'add') 
	{
        // Request type is Register new user
		$uid=$_POST['uid'];
        $name = $_POST['name'];
        $organisation = $_POST['organisation'];
        $description = $_POST['description'];
        $place = $_POST['place'];
        $image = $_POST['image'];
 
        // check if user is already existed
        if (!$db->isUserIdExisted($uid)) 
		{
            // user trying to add contact does not exist - error response
            $response["error"] = 5;
            $response["error_msg"] = "User ID doesnot exists";
            echo json_encode($response);
        } 
		else 
		{
            // store contact
            $user = $db->addContact($uid,$name, $organisation, $description,$place,$image);
            if ($user) 
			{
                // contact stored successfully
                $response["success"] = 1;
                $response["contact_id"] = $user["contact_id"];
                $response["owner_id"] = $user["owner_id"];
                $response["user"]["name"] = $user["name"];
                $response["user"]["organisation"] = $user["organisation"];
                $response["user"]["description"] = $user["description"];
                $response["user"]["place"] = $user["place"];
                $response["user"]["image"] = $user["image"];
                echo json_encode($response);
            } 
			else 
			{
                // contact failed to store
				$response["owner_id"] = $uid;
                $response["user"]["name"] = $name;
                $response["user"]["organisation"] = $organisation;
                $response["user"]["description"] = $description;
                $response["user"]["place"] = $place;
                //$response["user"]["image"] = $image;
                $response["error"] = 6;
                $response["error_msg"] = "Error occured in adding contact";
                echo json_encode($response);
            }
        }
    } 
	else if ($tag == 'search') 
	{
        // Request type is search for a contact
		$uid=$_POST['uid'];
		$string1="";
		$string2="";
		$string3="";
	
		if(isset($_POST['string1']) && $_POST['string1'] != '')$string1=$_POST['string1'];
		if(isset($_POST['string2']) && $_POST['string2'] != '')$string2=$_POST['string2'];
		if(isset($_POST['string3']) && $_POST['string3'] != '')$string3=$_POST['string3'];
		
        // check if user Id already exists
        if (!$db->isUserIdExisted($uid)) 
		{
            // user ID does not exist - error response
            $response["error"] = 7;
            $response["error_msg"] = "User ID doesnot exists";
            echo json_encode($response);
        } 
		else 
		{
            // store contact
            $user = $db->searchContact($uid,$string1,$string2,$string3);
            if ($user) 
			{
                // contact stored successfully
                if($user==1)
				{
					$response["success"] = 1;
					$response["count"]=0;
				}
				else
				{
					$response["success"] = 1;
					$response["count"]=count($user);
				
					for($i=0;$i<count($user);$i++) 
					{
						$response["$i"]["contact_id"] = $user[$i]["contact_id"];
						$response["$i"]["owner_id"] = $user[$i]["owner_id"];
						$response["$i"]["name"] = $user[$i]["name"];
						$response["$i"]["organisation"] = $user[$i]["organisation"];
						$response["$i"]["description"] = $user[$i]["description"];
						$response["$i"]["place"] = $user[$i]["place"];
						$response["$i"]["image"] = $user[$i]["image"];
					}
					//$response["secret"]=$user;
					echo json_encode($response);
				}
			}				
			else 
			{
                // error occured in searching contact
                $response["error"] = 8;
                $response["error_msg"] = "Error occured in searching contact";
				$response["count"]=0;
				$response["string1"]=$string1;
				$response["string2"]=$string2;
				$response["string3"]=$string3;
				$response["secret"]=$user;
                echo json_encode($response);
            }
        }
    }
	else if ($tag == 'delete') 
	{
        // Request type is search for a contact
		$uid=$_POST['uid'];
		$contact_id=$_POST['contact_id'];
		
        // check if user Id already exists
        if (!$db->isUserIdExisted($uid)) 
		{
            // user ID does not exist - error response
            $response["error"] = 9;
            $response["error_msg"] = "User ID whose contacts are to be deleted doesnot exist";
            echo json_encode($response);
        } 
		else 
		{
            // store contact
            $user = $db->deleteContact($uid,$contact_id);
            if ($user) 
			{
                // contact stored successfully
				$response["success"] = 1;
				echo json_encode($response);
			}				
			else 
			{
                // error occured in searching contact
                $response["error"] = 10;
                $response["error_msg"] = "Error occured in deleting a contact";
				$response["uid"]=$uid;
				$response["contact_id"]=$contact_id;
                echo json_encode($response);
            }
        }
    }
	else if ($tag == 'edit') 
	{
        // Request type is search for a contact
		$uid=$_POST['uid'];
		$contact_id=$_POST['contact_id'];
		$name = $_POST['name'];
        $organisation = $_POST['organisation'];
        $description = $_POST['description'];
        $place = $_POST['place'];
        $image = $_POST['image'];
		
        // check if user Id already exists
        if (!$db->isUserIdExisted($uid)) 
		{
            // user ID does not exist - error response
            $response["error"] = 11;
            $response["error_msg"] = "User ID whose contacts are to be updated doesnot exist";
            echo json_encode($response);
        } 
		else 
		{
            // store contact
            $user = $db->updateContact($uid,$contact_id,$name,$organisation,$description,$place,$image);
            if ($user) 
			{
                // contact updated successfully
                $response["success"] = 1;
                $response["contact_id"] = $user["contact_id"];
                $response["owner_id"] = $user["owner_id"];
                $response["user"]["name"] = $user["name"];
                $response["user"]["organisation"] = $user["organisation"];
                $response["user"]["description"] = $user["description"];
                $response["user"]["place"] = $user["place"];
                $response["user"]["image"] = $user["image"];
                echo json_encode($response);
            } 
			else 
			{
                // contact failed to store
				$response["owner_id"] = $uid;
                $response["user"]["name"] = $name;
                $response["user"]["organisation"] = $organisation;
                $response["user"]["description"] = $description;
                $response["user"]["place"] = $place;
                //$response["user"]["image"] = $image;
                $response["error"] = 12;
                $response["error_msg"] = "Error occured in updating contact";
                echo json_encode($response);
            }
        }
    }
	else if ($tag == 'recent') 
	{
        // Request type is search for a contact
		$uid=$_POST['uid'];
		$count=$_POST['count'];
		
        // check if user Id already exists
        if (!$db->isUserIdExisted($uid)) 
		{
            // user ID does not exist - error response
            $response["error"] = 13;
            $response["error_msg"] = "User ID whose recent contacts are to be retrieved doesnot exist";
            echo json_encode($response);
        } 
		else 
		{
            // store contact
            $user = $db->recentContact($uid,$count);
            if ($user) 
			{
				if($user==1)
				{
					$response["success"] = 1;
					$response["count"]=0;
					$response["msg"]="No entry present";
				}
				else
				{
					// contact stored successfully
					$response["success"] = 1;
					$response["count"]=count($user);
				
					for($i=0;$i<count($user);$i++) 
					{
						$response["$i"]["contact_id"] = $user[$i]["contact_id"];
						$response["$i"]["owner_id"] = $user[$i]["owner_id"];
						$response["$i"]["name"] = $user[$i]["name"];
						$response["$i"]["organisation"] = $user[$i]["organisation"];
						$response["$i"]["description"] = $user[$i]["description"];
						$response["$i"]["place"] = $user[$i]["place"];
						$response["$i"]["image"] = $user[$i]["image"];
					}
					//$response["secret"]=$user;
					echo json_encode($response);
				}
			}				
			else 
			{
                // error occured in searching contact
                $response["error"] = 14;
                $response["error_msg"] = "Error occured in retrieving recent contacts";
				$response["uid"]=$uid;
				$response["count"]=$count;
                echo json_encode($response);
            }
        }
    }
	else 
	{
        echo "Invalid Request";
    }
} 
else 
{
    echo "Access Denied";
}
?>