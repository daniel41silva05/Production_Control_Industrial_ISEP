# Order and Production Management

## Overview

This project was developed as a personal challenge to apply and consolidate all acquired programming knowledge, with special emphasis on:
- Pure programming without frameworks (including direct database access with SQL)
- Object-Oriented Principles
- Complex data structures
- Clean architecture and code organization
- Comprehensive unit testing

## Technologies
- **Language**: Java
- **Database**: Tembo (with raw SQL)
- **Documentation**: Complete (global artifacts + user stories)
- **Tests**: 100% unit test coverage

## Architecture
Followed the MVC (Model-View-Controller) model with well-defined layers:
- **UI**: User interface layer
- **Controller**: Control layer to mediate requests
- **Service**: Business logic layer
- **Domain**: Entity definition and business rules layer

## Documentation

The project includes complete technical documentation following software engineering best practices:
- Requirements Engineering
- Analysis and Modeling
- Design
- Testing
- Implementation

## Key Features

- **Customer Management**: registration, update, query
- **Order Management**: creation, tracking, completion
- **Product Management**: registration, product and category flow
- **Production Tree**: creation and query of production structures
- **Workstation Management**: registration, operations flow, availability, time tracking
- **Component and Material Management**: registration, monitoring, and stock purchasing
- **Supplier Management**: registration, update, query
- **Production Scenario Simulation**: full material flow, workstations, and operations for products

## How to generate the svg files

On project root folder, run the following script:

Remarks: it works for Linux and MacOS. For Windows, you have to adapt the script.

```shell
$ bin/generate-plantuml-diagrams.sh
```

## How the project is organized

This project uses Java and Maven.

We have to declare the maven-surefire-plugin in the pom.xml file and configure the dependencies of this plugin.

## How to run the unit tests
```
mvn clean test
```

## How to generate a Jar package for the project

Place the following plugin on the appropriate place of the pom.xml file.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-assembly-plugin</artifactId>
    <version>3.6.0</version>
    <configuration>
        <archive>
            <manifest>
                <mainClass>org.project.Main</mainClass>
            </manifest>
        </archive>
        <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run the following command on the project root folder. You can use IntelliJ to run the command or the command line of your computer if you hav Maven installed.

```
mvn package
```

## How to run the project from the generated Jar Package

Run the following command on the project root folder. You can use IntelliJ to run the command or the command line of your computer if you hav Maven installed.

```
java -jar target/ProductionControlIndustrial-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Contributions

This is a personal project not currently accepting external contributions, but improvement suggestions are welcome.

