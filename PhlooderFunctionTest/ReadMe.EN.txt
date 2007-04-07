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

- Run Phlooder 

- Set the content of the text field in the Test panel to:

	http://<server>/PhlooderFunctionTest/index.xml

where <server> is the domain name of your web server (usually localhost).

- Check the check box in the Test panel

- Select a test site from the Phishing site combo box

- Set up the Flood Configuration Fields

- Click "Phlood it!"

- Check db.txt for the results

For detailed information see the users documentation!
