This is a Java Web Service client for the Certification Authority Web Service.

The client uses the wsimport tool to generate classes that can invoke the web service and
perform the Java to XML data conversion.

The client needs access to the WSDL file, either using HTTP or using the local file system.


Instructions using Maven:
------------------------

ca-ws must be running first.

The default WSDL file location is at the announced URL.

The jaxws-maven-plugin is run at the "generate-sources" Maven phase
(which happens before the compile phase).

To verify remote server (run integration tests):
  mvn verify

To verify local server (run integration tests):
  mvn verify -Dws.url=http://localhost:8080/ca-ws/endpoint

To install module skipping integration tests:
  mvn install -DskipITs

  
To configure the project in Eclipse:
-----------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'.
'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2017-04-18
leic-sod@disciplinas.tecnico.ulisboa.pt
