#!/usr/local/bin/thrift --gen java:beans --gen java --gen py

/**
 * Scoreboard Communication thrift interface declarations.
 */

namespace java kth.communication
//TODO: change namespace to java se.kth.livetech.communication

struct Node {
  1: string kind,
  2: string addr,
  3: i32 port
}

struct Attr {
  1: string key,
  2: string value
}

struct Attrs {
  1: i64 time,
  2: string type,
  3: list<Attr> properties
}


service Service {
  // Time
  i64 time(),
  i64 ping(1:i64 clock),
  async void asyncPing(1:Node node, 2:i64 clock, 3:i64 ping),

  // Code
  async void classUpdate(1:string className),
  binary getClass(1:string className)
}


/** Spider man */
service SpiderService extends Service {
  // Nodes
  void addNode(1:Node node),
  void removeNode(1:Node node),
  list<Node> getNodes(),
  list<Node> getNodesOfKind(1:string kind),

  #// Ports
  #i32 getFreePort(1:string kind, 2:string addr),

  #// Contest
  #list<Attrs> getAttrs(),
  #list<Attrs> getAttrsSince(1:i64 since),
  async void contestUpdate(1:Attrs attrs),
  // Parameters
  map<string, string> getParameters(1:Node node),
  string getParameter(1:Node node, 2:string key),
  void setParameter(1:Node node, 2:string key, 3:string value)
}


service UpdateService extends Service {
  async void parameterUpdate(2:string key, 3:string value),
  async void contestUpdate(1:Attrs attrs),
}


#exception Err {
#  1: string message
#}
#void err() throws (1:Err err)
