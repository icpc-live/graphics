package se.kth.livetech.communication;

import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

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
			public void run() {
				Jedis j = getJedisInstance();
				j.subscribe(listener, channels);
				returnJedisInstance(j);
			}
		};
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Don't forget to return the resource when you are done!
	 * 
	 * @return A new Jedis instance.
	 */
	public Jedis getJedisInstance() {
		return pool.getResource();
	}

	/**
	 * Returns a resource so that it can be reused.
	 * 
	 * @param jedis
	 *            Instance to return.
	 */
	public void returnJedisInstance(Jedis jedis) {
		pool.returnResource(jedis);
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

	public static void main(String[] args) {
		RedisConnection client = new RedisConnection("localhost", 6379);
		Jedis j = client.getJedisInstance();
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
			public void onUnsubscribe(String arg0, int arg1) {
				System.out.println("UNSUBSCRIBED: " + arg0);
			}

			public void onSubscribe(String arg0, int arg1) {
				System.out.println("SUBSCRIBED: " + arg0);
			}

			public void onPUnsubscribe(String arg0, int arg1) {
				System.out.println("PUNSUBSCRIBED: " + arg0);
			}

			public void onPSubscribe(String arg0, int arg1) {
				System.out.println("PSUBSCRIBED: " + arg0);
			}

			public void onPMessage(String arg0, String arg1, String arg2) {
				System.out.println("PMESSAGE: " + arg2 + " on " + arg0 + " = "
						+ arg1);
			}

			public void onMessage(String arg0, String arg1) {
				System.out.println("MESSAGE: " + arg1 + " on " + arg0);
			}
		};
		client.subscribe(myListener, "CHA", "CHA1");
		client.returnJedisInstance(j);
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
