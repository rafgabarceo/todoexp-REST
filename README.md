# ToDoEXP
## Game-ify your productivity
ToDoEXP is a Java web application that intends to serve as a simple todo-list that gives its users an additional feel of satisfcation by employing a reward system. Much like in a videogame, ToDoEXP offers its users simple experience points that come along with the tasks they input. As they gather more experience, users will be able to spend them on various in-app purchases. 

### Dependencies
ToDoEXP utilizes Apache Maven to build and manage dependencies. By inspecting the `pom.xml` file, there are several dependencies from the Spring Framework project.

- Spring Data JPA
- Spring Thymeleaf
- Spring Web 
- Spring HATEOAS 
- Spring Boot Security
- Postgresql Driver
- Spring Boot Test

The DBMS utilized is PostgreSQL. If the settings for PostgreSQL is not default, edit `applications.properties` in the project's root folder and edit it accordingly. 

### Developer environment
Because the back-end is written in Java, back-end development utilizes Jetbrains IntelliJ IDEA. For the front-end development, Visual Studio Code is used. However, it is up to the contributor in which tools they are most comfortable with. 

### Running the back-end locally
It is important to note that the back-end requires a PostgreSQL instance running in the background with a database called `todoexp`. This can be edited in application.yaml to fit the certain database table. 
