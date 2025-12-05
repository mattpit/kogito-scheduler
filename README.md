Running : 
mvn quarkus:dev

Trigger POC test : 
curl -i -X POST  "http://localhost:8080/api/schedule/solve?directive=FAST"  -H "Accept: text/plain"

<ID_returned>

Get result table : 
curl -i "http://localhost:8080/api/schedule/result?problemId=<ID_returned>"
