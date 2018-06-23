# transactionTracker
The main use case for our API is to
calculate realtime statistic from the last 60 seconds. There will be two APIs, one of them is
called every time a transaction is made. It is also the sole input of this rest API. The other one
returns the statistic based of the transactions of the last 60 seconds<br/>
## to build the prject: 
  mvn clean install -X
## to run the project:
  cd target
  java -jar transactionsTracker-0.0.1-SNAPSHOT.jar
  
## endpoints examples:
  1- url: http://localhost:8080/v1/transactions 
     <br/> HTTP Method: POST
      <br/>requestBody(JSON formatted): 
      {
          "amount":13.50,
          "timestamp":1529779784968
      }<br/>
  2- url: http://localhost:8080/v1/statistics 
     <br/> HTTP Method: GET
     <br/> with no requestBody
