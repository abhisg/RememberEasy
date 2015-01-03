Application:
----------------
The application code is present in the folder TestApp,which can be imported directly into eclipse for execution.The IP address for the server is hardcoded in the files UserFunctions.java in the 'library' package and Search.java and Favourites.java in the main package in form of the String 'URL',which has to be modified to the appropriate server address.If the address is incorrect,you will get the 'connection timed out' message on the android device on which the application is installed.
If eclipse cannot be used,the project must be built using ant.For this purpose,navigate to the root directory and type:
ant release
This will create TestApp-unsigned.apk in the /bin folder which can be run using an android device or the emulator

Server:
------------
The server side code needs to be hosted on a xampp server with the index file index.php


No further installation is needed for running the application.