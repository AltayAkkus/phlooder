This script is used to test/demonstrate the functional behaviour of Phlooder.

Usage:
------
- Copy this directory to your web servers document root (the web server has to be 
able to run PHP scripts). Set the copied directory writeable. 

- Set up index.xml by editing the content of the output->entries->entry->url field 
making it point to 

	http://<server>/PhlooderFunctionTest/phishing.php 

where <server> is the domain name of your web server (usually localhost).

- Optionally edit phishing.html. Do not remove the hidden field named "form_sent"!

- Run Phlooder with the command line argument:

	http://<server>/PhlooderFunctionTest/index.xml

where <server> is the domain name of your web server (usually localhost).
