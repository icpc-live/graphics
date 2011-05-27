package se.kth.livetech.communication;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;

/**
 * RedisConnection is a gateway to the Redis database.
 * 
 * @author Hakan Terelius <hakante@kth.se>
 */
public class RedisConnection {

	/**
	 * Jedis is not threadsafe. You shouldn't use the same instance from
	 * different threads because you'll have strange errors.
	 */
	private JedisPool pool;

	/**
	 * @param host
	 *            Redis host
	 */
	public RedisConnection(String host) {
		this(host, 6379);
	}
	
	/**
	 * @param host
	 *            Redis host
	 * @param port
	 *            Redis port, default 6379
	 */
	public RedisConnection(String host, int port) {
		pool = new JedisPool(host, port);
	}

	/**
	 * Subscribes to a pub/sub channel. Notice that this creates a new thread!
	 * 
	 * @param channels
	 *            Channel names to subscribe to
	 * @param listener
	 *            Listener for updates
	 */
	public void subscribe(final JedisPubSub listener, final String... channels) {
		Thread t = new Thread() {
			@Override
			public void run() {
				while(true){
					Jedis j = null;
					try {
						j = pool.getResource();
						System.out.println("RedisConnection - Starting subscription to " + channels.length + " channels.");
						j.subscribe(listener, channels);
						pool.returnResource(j);
					} catch (JedisException e) {
						System.out.println("RedisConnection - Jedis Error: " + e.getMessage());
						System.out.println("RedisConnection - Returning broken resource to pool");
						pool.returnBrokenResource(j);
					} catch (Exception e) {
						e.printStackTrace();
						if (j != null) {
							System.out.println("RedisConnection - Returning orphaned resource to pool.");
							pool.returnResource(j);
						}
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
	}



	public String get(String key) {
		Jedis j = pool.getResource();
		String val = j.get(key);
		pool.returnResource(j);
		return val;
	}
	
	public void delete(String key) {
		Jedis j = pool.getResource();
		j.del(key);
		pool.returnResource(j);
	}
	
	public Set<String> keys(String pattern) {
		Jedis j = pool.getResource();
		Set<String> keys = j.keys(pattern);
		pool.returnResource(j);
		return keys;
	}
	
	public int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	public void set(String key, String value) {
		Jedis j = pool.getResource();
		j.set(key, value);
		pool.returnResource(j);
	}
	
	public void sAdd(String key, String value) {
		Jedis j = pool.getResource();
		j.sadd(key, value);
		pool.returnResource(j);
	}
	
	public Set<String> sMembers(String key) {
		Jedis j = pool.getResource();
		Set<String> members = j.smembers(key);
		pool.returnResource(j);
		return members;
	}

	public void setPublish(String key, String value, String channel) {
		Jedis j = pool.getResource();
		j.set(key, value);
		j.publish(channel, key);
		pool.returnResource(j);
	}

	public void publish(String channel, String msg) {
		Jedis j = pool.getResource();
		j.publish(channel, msg);
		pool.returnResource(j);
	}

	private JedisPool getPool() {
		return pool;
	}
	
	public static void main(String[] args) {
		RedisConnection client = new RedisConnection("localhost", 6379);
		Jedis j = client.getPool().getResource();
		if (!j.exists("counter")) {
			j.set("counter", "0");
		} else {
			j.incr("counter");
		}
		int nextVal = Integer.parseInt(j.get("counter"));
		j.set("myKey." + nextVal, "A value");
		for (String key : j.keys("*")) {
			System.out.println("KEY: " + key + ", VALUE " + j.get(key));
		}
		JedisPubSub myListener = new JedisPubSub() {
			@Override
			public void onUnsubscribe(String arg0, int arg1) {
				System.out.println("UNSUBSCRIBED: " + arg0);
			}

			@Override
			public void onSubscribe(String arg0, int arg1) {
				System.out.println("SUBSCRIBED: " + arg0);
			}

			@Override
			public void onPUnsubscribe(String arg0, int arg1) {
				System.out.println("PUNSUBSCRIBED: " + arg0);
			}

			@Override
			public void onPSubscribe(String arg0, int arg1) {
				System.out.println("PSUBSCRIBED: " + arg0);
			}

			@Override
			public void onPMessage(String arg0, String arg1, String arg2) {
				System.out.println("PMESSAGE: " + arg2 + " on " + arg0 + " = "
						+ arg1);
			}

			@Override
			public void onMessage(String arg0, String arg1) {
				System.out.println("MESSAGE: " + arg1 + " on " + arg0);
			}
		};
		client.subscribe(myListener, "CHA", "CHA1");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.publish("CHA1", "HEJ");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		myListener.unsubscribe();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
}
