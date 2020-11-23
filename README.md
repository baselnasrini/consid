# Consid - code test

##### This project is done by Mohammed Basel Nasrini as a code test for Consid 
The backend is run at the port 3303 and will start working on front-end soon

### API:
You can check the API used for connection between front and back-end in the following link:
https://documenter.getpostman.com/view/13601880/TVewYPDB#e5c6449a-8d2e-4f86-8098-e78fba4a2115

#### Backend:
You can import the backend as a Maven project.

To run the backend you have first to change the username and password of the MySQL database at the following files:
 - backend/src/main/resources/application.yml
 - backend/src/main/java/com/consid/backend/LuncherClass.java

I used MySQL Workbech for database.
Additionally, if the database is not on the port 3306, you have to change the port in the previous files too.

To run the backend you only need to run the LuncherClass.
