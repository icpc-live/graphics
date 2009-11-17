#!/usr/local/bin/thrift --gen java:beans --gen java --gen py

/**
 * Scoreboard Communication thrift interface declarations.
 */

namespace java se.kth.livetech.communication

struct Node {
  1: string name,
  2: string address,
  3: i32 port,
}

struct ContestEvent {
  1: i64 time,
  2: string type,
  3: map<string, string> properties,
}


service Service {
	// Node registration
	void attach(1:Node node),
	void detach(),
	
  // Server time
  i64 time(),

  // Code
  binary getClass(1:string className),
  async void classUpdate(1:string className),
  
  // Resources
  binary getResource(1:string resourceName),
  async void resourceUpdate(1:string resourceName),
  
  // Nodes
  void addNode(1:Node node),
  void removeNode(1:Node node),
  list<Node> getNodes(),

  // Properties
  map<string, string> getParameters(),
  string getProperty(1:string key),
  void setProperty(1:string key, 2:string value),

  // Updates
  async void propertyUpdate(1:string key, 2:string value),
  async void contestUpdate(1:ContestEvent event),
}


#exception Err {
#  1: string message
#}
#void err() throws (1:Err err)
