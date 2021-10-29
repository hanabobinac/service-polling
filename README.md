# Service polling

## Introduction
Service poller keeps a list of services (defined by a URL), periodically does a HTTP GET to each and
service and saves the response ("OK" or "FAIL"). User can add, update and delete services.


## Technologies
- Java
- Gradle
- Vert.x
- Sqlite
- JavaScript
- HTML

## Launch
- Start server by running "MainVerticle" in Intellij IDEA, using existing Gradle configuration. 
- Start web UI by opening webroot/index.html in a browser.
- Database file poller.db and table "services" will be created if they don't exist

## Examples of UI use
- Services are shown in a table in browser.
- To add a new service, enter URL and service name, and press button "Insert".
- To update name of an existing service, enter URL and service name, and press button "Update".
- To delete a service, enter URL and press button "Delete".

## Examples of API use
### List all services 
GET /services

### Get one service by URL
GET /services/:url

### Add new service
POST /services/:url/:name

### Update service's name
PATCH /services/:url/:name

### Delete service
DELETE /services/:url
