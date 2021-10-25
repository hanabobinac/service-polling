#Service polling

##Introduction
Service poller keeps a list of services (defined by a URL), periodically does a HTTP GET to each and
service and saves the response ("OK" or "FAIL"). User can add, update and delete services.


##Technologies
- Java
- Gradle
- Vert.x
- Sqlite

##Launch
Run "MainVerticle" in Intellij IDEA, using existing Gradle configuration.

##Examples of use
###List all services 
GET /services

###Get one service by URL
GET /services/:url

###Add new service
POST /services/:url/:name

###Update service's name
PATCH /services/:url/:name

###Delete service
DELETE /services/:url