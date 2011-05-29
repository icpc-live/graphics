package se.kth.livetech.communication;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.exceptions.JedisException;
import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;
import se.kth.livetech.properties.IProperty;

public class RedisClient extends JedisPubSub implements NodeUpdateListener {
	
	JedisShardInfo redisShardInfo;
	private Jedis redis;
	private LiveState localState;
	private NodeId localNode;
	
	public Runnable getFetcher() {
		return null;
	}

	public RedisClient(LiveState localState, NodeId localNode, String redisHost) {
		this.localState = localState;
		this.localNode = localNode;
		this.redisShardInfo = new JedisShardInfo(redisHost, 6379);
	}
	
	public RedisClient(LiveState localState, NodeId localNode, String redisHost, int redisPort) {
		this.localState = localState;
		this.localNode = localNode;
		this.redisShardInfo = new JedisShardInfo(redisHost, redisPort);
	}
	
	public void connect() {
		redis = new Jedis(redisShardInfo);
		redis.connect();
		
		localState.addListeners(this);
		
		spawnSubscriptionThread();
		
		for(String s: redis.keys("live.*")) {//TODO: check prefix
			onMessage("property", s); //emulate received messages for all keys
		}
		Set<String> contests = redis.smembers("contests");
		for(String contest : contests) {
			Set<String> events = redis.smembers(String.format("%s.events", contest));
			for(String event : events) {
				onMessage("contest", String.format("%s.%s", contest, event));
			}
		}
	}

	private Thread spawnSubscriptionThread() {
		Thread t = new Thread() {
			@Override
			public void run() {
				Jedis j = new Jedis(redisShardInfo);
				while(true){
					try {
						if (!j.isConnected())
							j.connect();
						System.out.println("RedisConnection - Starting subscription of updates");
						j.subscribe(RedisClient.this, "property", "contest");
						j.disconnect();
					} catch (Exception e) {
						System.out.println("RedisConnection - Error: " + e.getMessage());
					}

					if (j.isConnected()) {
						try {
                            j.quit();
                        } catch (Exception e2) {
                        }
                        j.disconnect();							
					}
					
					try {
						Thread.sleep(3000); // wait a little while before retrying
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.setDaemon(true);
		t.start();
		return t;
	}
	
	// Called when local contest changed
	public void attrsUpdated(ContestId contestId, AttrsUpdateEvent e) {
		assert(!contestId.name.contains("."));
		
		if (!redis.isConnected())
			redis.connect();
		
		String eventId = e.getProperty("event-id");
		
		final String contestKey = String.format("contest.%s.%d", contestId.name, contestId.starttime);
		final String eventKey = String.format("%s.%s", contestKey, eventId);
		
		String eventTypeKey = String.format("%s.type", eventKey);
		String eventType = redis.get(eventTypeKey);
		
		boolean publish = false;

		if (!e.getType().equals(eventType)) {
			redis.set(eventTypeKey, e.getType());
			publish = true;
		}
		
		for (String name : e.getProperties()) {
			String key = String.format("%s.%s", eventKey, name);
			String value = e.getProperty(name);
			if (!value.equals(redis.get(key))) {
				redis.set(key, value);
				redis.sadd(String.format("%s.fields", eventKey), name);
				publish = true;
			}
		}
		
		if (publish) {
			redis.sadd(String.format("%s.events", contestKey), eventId);
			redis.sadd("contests", contestKey);
			redis.publish("contest", eventKey);
		}
	}

	@Override
	public void propertyChanged(IProperty changed) {
		if (!redis.isConnected())
			redis.connect();
		
		String propertyName = changed.getName();
		
		if(changed.isSet()){
			redis.set(propertyName, changed.getOwnValue());
		}
		else {
			redis.del(propertyName); //TODO: check
		}
		if(changed.isLinked()) {
			redis.set(propertyName + "#link", changed.getLink());
		}
		else {
			redis.del(propertyName + "#link"); //TODO: check
		}
		String message = propertyName;
		redis.publish("property", message);
	}

	@Override
	public NodeId getId() {
		return localNode;
	}

	public synchronized void onMessage(Jedis j, String channel, String message) {
		if ("property".equals(channel)) {
			// Called when Redis publish a property update.
			String propertyName = message;
			IProperty property = this.localState.getHierarchy().getProperty(propertyName);
			String value = j.get(propertyName);
			if(value != null){
				property.setValue(value);
			}
			else{
				property.clearValue();
			}
			String link = j.get(propertyName + "#link");
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
			Set<String> fields = j.smembers(message + ".fields");
			String type = j.get(message + ".type");
			AttrsUpdateEventImpl e = new AttrsUpdateEventImpl(0, type);
			for (String field : fields) {
				e.setProperty(field, j.get(message + "." + field));
			}
			localState.getContest(contestId).attrsUpdated(e);
		}
	}

	@Override
	public void onMessage(String channel, String message) {
		onMessage(redis, channel, message);
	}	
	
	@Override
	public void onPMessage(String pattern, String channel, String message) {}

	@Override
	public void onPSubscribe(String channel, int subscribedChannels) {}

	@Override
	public void onPUnsubscribe(String channel, int subscribedChannels) {}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		Jedis j = new Jedis(redisShardInfo);
		j.connect();
		for(String s: j.keys("live.*")) {//TODO: check prefix
			onMessage(j, "property", s); //emulate received messages for all keys
		}
		j.disconnect();
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {}

	@Override
	public AttrsUpdateListener getAttrsUpdateListener(final ContestId contestId) {
		return new AttrsUpdateListener() {
			@Override
			public void attrsUpdated(AttrsUpdateEvent e) {
				// TODO Auto-generated method stub
				RedisClient.this.attrsUpdated(contestId, e);
			}
		};
	}
}
