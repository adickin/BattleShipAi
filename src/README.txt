How to compile
-Install the latest version of eclipse from http://www.eclipse.org/downloads/
-Create a new workspace and project.
-Move the source code packages into the src folder.
-Refresh the project and hit Ctrl-F11 or if that doesnt work hit the green arrow
-The client should start running with the default server name and port.


How to run with non default parameters.
Right click on the project name and go to Run As->Run Configurations.
- Click on the Arguments tab.
-In the program arguments section put the following.
ServerNameHere.ca PORT
-The output of a win/loss/fail will be printed to the console within eclipse.

How to change the Username and password.
-The username and passwords are hardcoded into the Network.java file.
-The method they are in is requestLogin().