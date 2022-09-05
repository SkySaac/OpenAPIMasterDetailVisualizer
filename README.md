# My Bachelor

This project can be used as a starting point to create your own Vaadin application with Spring Boot.
It contains all the necessary configuration and some placeholder files to get you started.

## Running the application

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to import Vaadin projects to different 
IDEs](https://vaadin.com/docs/latest/flow/guide/step-by-step/importing) (Eclipse, IntelliJ IDEA, NetBeans, and VS Code).

## Deploying to Production

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the build completes.

Once the JAR file is built, you can run it using
`java -jar target/mybachelor-1.0-SNAPSHOT.jar`

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/components/vaadin-app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `frontend/` contains the client-side JavaScript views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorials at [vaadin.com/tutorials](https://vaadin.com/tutorials).
- Watch training videos and get certified at [vaadin.com/learn/training](https://vaadin.com/learn/training).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/components](https://vaadin.com/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Discover Vaadin's set of CSS utility classes that enable building any UI without custom CSS in the [docs](https://vaadin.com/docs/latest/ds/foundation/utility-classes). 
- Find a collection of solutions to common use cases in [Vaadin Cookbook](https://cookbook.vaadin.com/).
- Find Add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Discord channel](https://discord.gg/MYFq5RTbBn).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin/platform).


## Deploying using Docker

To build the Dockerized version of the project, run

```
docker build . -t mybachelor:latest
```

Once the Docker image is correctly built, you can test it locally using

```
docker run -p 8080:8080 mybachelor:latest
```

todo:

url that points to asdgs/{id} as in space api -> needs help & fix
css style für detailview array überschrift + pfad
post/put add direct input and add array

api ids search wieso zeigt body an

TEST post put delete
TEST -> listview -> nicht anzeigen von pfaden die in anderen primaryviews drinne sind -> chekc if fixed ienmal einfach alle in listviews anzeigen alssen
TEST /{id}/... testen
TEST rest parking ausprobieren
TEST unit test
TEST button to show all paths in listview
WICHTIG application/octet-stream support -> button for up n download -> optional
WICHTIG wrapped setting to select data
WICHTIG /{id} needs own view
WICHTIG readme schön machen
WICHTIG setting for parameter select
WICHTIG sequenzdiagram von generierungsablauf
Oneof in request
ArrayDetail -> change way to fill data
UI english -> Deutsch
required auch bei anderen dingen in post und put angeben (nicht nur query&path)
put/post object& array support
google stylesheet
merge to main branch


change to using tabs -> optional
support allOf -> optional
checksum sollte string sein oder long
Problem -> jackson doesnt know the difference between double and int
Problem -> Tagesschau gibt unter /api/news gibt in return object mehr parameter zurück als in apidoc defined -> geht also nicht


credentials clear
WICHTIG settings for array names in details tab
remove isFreeSchema
fix not showing tags on the left at first start -> nicht wieder gesehen
query params required -> optional
remove @Service von services die es nicht brauchen
FIXED I THINK.../{id}/... without GET has no view -> no mdv -> WICHTIG
FIXED I THINK ERROR GETTING PAGE FOR SCHEMA PagedModelObject at startup -> ??
FIXED I THINK localhost:8081 link leitet immer noch in app um und ruft nich direkt link auf -> wegen nur anderem port denk vaadin vl ?
FIXED I THINK vaadin just having a different port still navigates you with ui.navigate to your current application instead of the external one
api/utils/enums -> fehler, sollte objects statt arrays haben....
nav doesnt update sometimes...
