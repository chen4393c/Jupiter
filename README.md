# Jupiter

An Event Ticket Recommender Web Service 

Preview: http://54.185.18.209:8080/EventRecommender 

<ul>
  <li>Built an interactive website for users to search events and purchase tickets with AJAX.</li>
  <li>Implemented event recommendation with Collaborative Filtering algorithm.</li>
  <li>Designed a RESTful APIs with Java Servlet to handle users' requests for fetching event info.</li>
  <li>Built MySQL database to capture event data from TicketMaster API. Created MongoDB shared cluster for storage.</li>
  <li>Performed Load Testing on the server deployed on AWS EC2 with Apache JMeter.</li>
</ul>

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them
<ul>
  <li>Java 1.8+</li>
  <li>Apache Tomcat server 9.0+</li>
  <li>Java API for JSON</li>
  <li>JDBC driver</li>
</ul>

### Installing

A step by step series of examples that tell you how to get a development env running

1. Create a new workspace on your new computer

2. Install and configure Apache Tomcat server with your IDE (Eclipse Java EE or Intellij IDEA Ultimate version)

4. Copy your existing projects Jupiter under you new workspace/project directory 
  
5. <b>In Eclipse</b>, click File -> Open Projects from File System. Click "show other specialized import wizards", and select "Existing Projects into Workspace" under General folder. Select Jupiter folder as root directory and click finish. <b>In Intellij</b>, select "Import Project" and select the Jupiter folder.

6. Launch the server.

7. Test the website with the following URL: 

For Eclipse: 
```
http://localhost:8080/[YOUR_PROJECT_FOLDER_NAME]
```

For Intellij IDEA: 

```
http://localhost:8080
```

You will see the home page asking you to log in or sign up.


## Running the tests

For search GET API to get nearby events:
```
url: http://localhost:8080/search?user_id=1111&lat=44.8670197&lon=-93.3489769
method: GET
```
For favorite GET API to get your favorite events:
```
url: http://localhost:8080/history?user_id=1111
method: GET

```
For favorite POST API to add an event into your favorite list:
```
url: http://localhost:8080/history
method: POST
body:
{
	"user_id": "1111",
	"favorite": ["vv16kZAoY0vZA867v7", "vv1FvOvfQoP0Z72A78"]
}
```
For recommendation API to get recommended events based on your favorite event list:
```
url: http://localhost:8080/recommendation?user_id=1111
method: GET
```
## Deployment

Generate the .war file and then you can deploy this web service with any cloud platform such as AWS EC2. 


## Authors

* **Chaoran Chen** - *Initial work* - [ChaoranChen](https://github.com/chen4393c)

## Acknowledgments

* Vincent Chen
* Sean
* Jenny
* Henry
