# Usage
It is a simple webapp based on the Play framework. 
- start sbt and type backend/run
- the application won't actually start until you make a request
  to the HTTP API, the simplest way is to open the [frontend](http://localhost:9000/frontend)
- the frontend is a simple single page app built with Scala.js, Binding.scala, Leaflet and W3.CSS. It will
  display the last 10 even and odd greetings and the last 10 locations on a map.
- it is also possible to run via docker. type "docker:publishLocal"
  into the sbt prompt (docker needs to be running) and start the
  generated container from a shell using
  "docker run -p9000:9000 -p9011:9011 upvest-challenge:0.1-SNAPSHOT"
# Possible improvements
- currently the program will receive all messages, parse them and dump them into a BroadcastHub. The filtering (even/odd greetings, corrdinates) is done individually for each client. It would be more efficient to do the filtering only once and then do the Broadcast. PartitionHub might be useful here
- since the BroadcastHub's speed is limited by the slowest Sink, it's possible to DoS the application by requesting a stream and not reading the data. A fix would be to disconnect clients that don't read fast enough using backpressureTimeout. Buffering a bit of data is probably also useful to prevent slowdown for other clients when one client is slow to read
