package se.kth.livetech.communication;

import java.util.Set;

import redis.clients.jedis.JedisPubSub;
import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;
import se.kth.livetech.properties.IProperty;

public class RedisClient extends JedisPubSub implements NodeUpdateListener {
	
	private String redisHost = "localhost";
	private int redisPort = 6379;
	private RedisConnection redis;
	private LiveState localState;
	private NodeId localNode;
	private Runnable fetcher;
	
	public Runnable getFetcher() {
		return fetcher;
	}

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

		this.fetcher = new Runnable() {
			@Override
			public void run() {
				while(true){
					synchronized(this){
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					for(String s: redis.keys("live.*")) {//TODO: check prefix
						onMessage("property", s); //emulate received messages for all keys
					}
				}
			}
		};
		new Thread(fetcher).start();
		
		redis.subscribe(this, "property", "contest");

		for(String s: redis.keys("live.*")) {//TODO: check prefix
			onMessage("property", s); //emulate received messages for all keys
		}
		Set<String> contests = redis.sMembers("contests");
		for(String contest : contests) {
			Set<String> events = redis.sMembers(contest + ".events");
			for(String event : events) {
				onMessage("contest", contest + "." + event);
			}
		}
	}

	@Override
	public void attrsUpdated(AttrsUpdateEvent e) {
		// Called when local contest changed
		final ContestId contestId = new ContestId("contest", 0); // TODO: contest id
		assert(!contestId.name.contains("."));
		final String contestIdString = contestId.name +"."+ Long.toString(contestId.starttime);
		String eventId = e.getProperty("event-id");
		
		final String contestkey = "contest." + contestIdString;
		final String basekey = contestkey + "." + eventId;
		boolean publish = false;
		if (!e.getType().equals(redis.get(basekey + ".type"))) {
			publish = true;
			redis.set(basekey + ".type", e.getType());
		}
		for (String name : e.getProperties()) {
			String key = basekey + "." + name;
			String value = e.getProperty(name);
			if (!value.equals(redis.get(key))) {
				redis.set(key, value);
				redis.sAdd(basekey + ".fields", name);
				publish = true;
			}
		}
		if(publish) {
			redis.sAdd(contestkey + ".events", eventId);
			redis.sAdd("contests", contestkey);
			redis.publish("contest", basekey);
		}
	}

	@Override
	public void propertyChanged(IProperty changed) {
		String propertyName = changed.getName();
		if(changed.isSet()){
			this.redis.set(propertyName, changed.getOwnValue());
		}
		else {
			this.redis.delete(propertyName); //TODO: check
		}
		if(changed.isLinked()) {
			this.redis.set(propertyName + "#link", changed.getLink());
		}
		else {
			this.redis.delete(propertyName + "#link"); //TODO: check
		}
		String message = propertyName;
		this.redis.publish("property", message);
	}

	@Override
	public NodeId getId() {
		return localNode;
	}

	@Override
	public void onMessage(String channel, String message) {
		if ("property".equals(channel)) {
			// Called when Redis publish a property update.
			String propertyName = message;
			IProperty property = this.localState.getHierarchy().getProperty(propertyName);
			String value = this.redis.get(propertyName);
			if(value != null){
				property.setValue(value);
			}
			else{
				property.clearValue();
			}
			String link = this.redis.get(propertyName + "#link");
			if(link != null) {
				property.setLink(link);
			}
			else{
				property.clearLink();
			}
		} else if("contest".equals(channel)) {
			// Called when Redis publish a contest update.
			String[] keys = message.split("\\.", 4);
			assert(keys.length==4);
			assert(keys[0].equals("contest"));
			ContestId contestId = new ContestId(keys[1], Long.valueOf(keys[2]));
			Set<String> fields = redis.sMembers(message + ".fields");
			String type = redis.get(message + ".type");
			AttrsUpdateEventImpl e = new AttrsUpdateEventImpl(0, type);
			for (String field : fields) {
				e.setProperty(field, redis.get(message + "." + field));
			}
			localState.getContest(contestId).attrsUpdated(e);
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
