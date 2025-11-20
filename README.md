# TutoEasy

## Sumary

This project is for "capstone", a big homework for class "Software Development"

This project is building in monolithic architecture on layers, cause, according to our criteria, only makes sense
use MVC, Clean Architecture, C-S, etc., cause it's a simple CLI application. 

Anyway, TutoEasy has the main objective to manage and view many consulting meetings between
students, tutors, and admins.
This projects use maven to global configuration and the management of the next dependencies:
- [Picocli](https://picocli.info) - A framework for CLI applications
- [Hibernate](https://hibernate.org) with [JPA](https://mvnrepository.com/artifact/jakarta.persistence/jakarta.persistence-api) - An ORM for the queries for MySQL
- [Lombok](https://projectlombok.org) - A plugin for a faster development
- [JUnit](https://junit.org) - Framework for testing in Java, we're gonna use TDD to avoid problems in merge and final product

## Before installation

This projects can works in Java 17, but we suggest have installed for this project:
- IntellijIDEA or VSCode with Java plugins by Red Hat
- Docker for MySQL
- Git
- GitLab already auth with a valid ssh

## Instructions for installation

__For this is required have an auth with gitlab and git in your local machine__

- In a clean folder, you should to use:
````sql
cd existing_repo
git clone origin https://gitlab.com/jala-university1/cohort-6/ES.CSSD-113.GA.T2.25.M2/SC/capstone/team4/equipo4.git
````

- With help of Intellij Idea, synchronize the downloads in pom.xml, otherwise, you should to execute
the next command in your console:

````bash
mvn clean install ## This erase compilations already exists and install again according pom.xml says what maven should to download and install
mvn compile ## Or this, this will be enough for IntellijIDE's intelli-sense 
````

- Copy or rename the template.env to .env. After that, fill each value
- Before run this project, you should have a MySQL's Database working in your local machine. I suggest use Docker with docker-compose to keep clean your file system of your machine. If you want to use a local MySQL, make sure the credentials of .env and your database are the same.

- If you're going to use Docker, use the next command:
````bash
docker-compose up -d ##Suggested, this won't block your terminal
docker-compose up ## You can't use that session of your terminal, at least you force to end the process with __Ctrl+C__
````
- Using your DBSM, do the queries to have the awaited scheme to work with this application
- If you password is complex and long, you'd have issues with auth in your DBSM, so I suggest you to use a simple password in development time
- Maybe you'd have the issue of __docker-compose up -d__ won't work, first, open the application to have Docker's daemon working first before call the coomand to up our MySQL's database
- In this point you're ready to use the application. Do it with the help of IntellijIDE or:
`````bash
mvn clean package ## Maven will install each dependencie, compile the project and put it in a .jar file
java -jar target/tutoeasy-cli.jar [command] [options] [values] ## Execute the CLI
`````

## Architecture

Along classes, MVC was a common goal between our partners, according our experience, MVC will be efective if we work with routes, in this case is just a CLI application, so in this context, MVC lost its power in this
project.
Also, we're using not Apache or other server to use it, or even JavaJFX
One of the benefits of our implementation of monolithic architecture with flavor layers is keeping duties splitted.
Other benefit is in case we have the enough time to migrate Picocli to JavaFX/TornadoFX/GemsFX

````bash
cli.tutoeasy
├── main ## Here is the start point, the bootstrap of our application
├── config ## Here will be the connections with MySQL, get the envs or global config for all the project
├── model ## Here the entities for our Database
├── repository ## Here the management with Hibernate 
├── service ## The services will be our logic
├── command ## Here picocli implementation 
└── util ## Here we're gonna use common validators, the auth module, etc.
````
