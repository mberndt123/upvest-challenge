# Usage
It is a simple webapp based on the Play framework. 
- start sbt and type "run"
- the application won't actually start until you make a request
  to the HTTP API, e. g. curl http://localhost:9000/greets/even

# Possible improvements
- currently the program will receive all messages, parse them and dump them into a BroadcastHub. The filtering (even/odd greetings, corrdinates) is done individually for each client. It would be more efficient to do the filtering only once and then do the Broadcast. PartitionHub might be useful here
- since the BroadcastHub's speed is limited by the slowest Sink, it's possible to DoS the application by requesting a stream and not reading the data. A fix would be to disconnect clients that don't read fast enough using backpressureTimeout. Buffering a bit of data is probably also useful to prevent slowdown for other clients when one client is slow to read
- docker support should be easy to add with sbt-native-packager
- combinator parsing rather than regexes :-). However I don't know a good parser combinator library for Scala – scala-parser-combinators is slow and buggy. I tried fastparse but didn't like the API much. 
