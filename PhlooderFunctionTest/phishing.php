<?php
/**
* @author The Blue Overdose Project
* @version 0.1
* This script is used to test/demonstrate the functional behaviour of Phlooder.
* Displays the contents of phishing.html and logs all the POST and GET requests 
* into db.txt.
*/
$resource=NULL;

if ($_POST['form_sent']=='Yes')
	$resource=$_POST;
if ($_GET['form_sent']=='Yes')
	$resource=$_GET;
	
if($resource!==NULL){
	echo "Data written.<br/>";
	if (!$handle = fopen('db.txt', 'a')) {
    	echo "Cannot open file !";
    	exit;
	}
	foreach($resource as $key=>$value){
    	if (fwrite($handle, $key." : ".$value."\n") === FALSE) {
        	echo "Cannot write to file ($filename)";
	        exit;
    	}
    }
    fclose($handle);
}

include("phishing.html");
?>
