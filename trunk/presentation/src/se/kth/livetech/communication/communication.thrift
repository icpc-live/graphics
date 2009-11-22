#!/usr/local/bin/thrift --gen java:beans --gen java --gen py

/**
 * ICPC Live Communication thrift interface declarations.
 */

namespace java se.kth.livetech.communication.thrift

struct Node {
	1: string name,
	2: string address,
	3: i32 port,
}

struct NodeStatus {
	1: string name,
	2: i64 lastContact,
	3: i32 ping,
}

struct ContestEvent {
	1: i64 time,
	2: string type,
	3: map<string, string> attributes,
}

struct PropertyEvent {
	1: string key,
	2: string value,
}

enum LogLevel {
	spam, debug, info, notice, warning, error, critical,
}

struct LogEvent {
	1: i64 time,
	2: LogLevel level,
	3: string message,
	4: optional string origin,
	5: optional string file,
	6: optional i32 line,
}

exception LiveException {
	1: string message,
}

service LiveService {
	// Node registration
	void attach(1:Node node),
	void detach(),

	// Nodes
	void addNode(1:Node node),
	void removeNode(1:Node node),
	list<Node> getNodes(),
	list<NodeStatus> getNodeStatus(),

	// Server time
	i64 time(),

	// Code
	binary getClass(1:string className),
	oneway void classUpdate(1:string className),

	// Resources
	binary getResource(1:string resourceName),
	oneway void resourceUpdate(1:string resourceName),

	// Properties
	map<string, string> getProperties(),
	string getProperty(1:string key),
	void setProperty(1:string key, 2:string value),

	// Updates
	oneway void propertyUpdate(1:PropertyEvent event),
	oneway void contestUpdate(1:ContestEvent event),

	// Logging
	oneway void logEvent(1:LogEvent event),
	oneway void logSubscribe(1:LogEvent template),
	oneway void logUnsubscribe(1:LogEvent template),
}


#void err() throws (1:Err err)
