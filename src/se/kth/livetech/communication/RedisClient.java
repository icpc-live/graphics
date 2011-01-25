package se.kth.livetech.communication;

import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.properties.IProperty;

import redis.clients.jedis.JedisPubSub;

public class RedisClient extends JedisPubSub implements NodeUpdateListener {
	
	private String redisHost = "localhost";
	private int redisPort = 6379;
	private RedisConnection redis;
	private LiveState localState;
	private NodeId localNode;
	
	public RedisClient(LiveState localState, NodeId localNode, String redisHost) {
		this.redisHost = redisHost;
		this.localState = localState;
		this.localNode = localNode;
	}
	
	public RedisClient(LiveState localState, NodeId localNode, String redisHost, int redisPort) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.localState = localState;
		this.localNode = localNode;
	}
	
	public void connect() {
		redis = new RedisConnection(redisHost, redisPort);
		localState.addListeners(this);
		redis.subscribe(this, "property", "contest");
	}

	@Override
	public void attrsUpdated(AttrsUpdateEvent e) {
		// TODO Called when local contest changed
		
	}

	@Override
	public void propertyChanged(IProperty changed) {
		// TODO Called when local property changed
		
	}

	@Override
	public NodeId getId() {
		return localNode;
	}

	@Override
	public void onMessage(String channel, String message) {
		if ("property".equals(channel)) {
			// TODO Called when Redis publish a property update.
		} else if("contest".equals(channel)) {
			// TODO Called when Reids publish a contest update.
		}
	}

	@Override
	public void onPMessage(String pattern, String channel, String message) {}

	@Override
	public void onPSubscribe(String channel, int subscribedChannels) {}

	@Override
	public void onPUnsubscribe(String channel, int subscribedChannels) {}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {}
}
