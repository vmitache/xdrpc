package com.os.rpc.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.SerializingTranscoder;
import java.util.logging.*;

import javax.servlet.ServletConfig;

/**
 * The Cache. Mainly an interface to memcached.
 */
public class ServerCache {
	private static final Logger __logger = Logger.getLogger("Cache");

	private static CacheImpl __cacheImpl = new LocalCacheImpl();

	/**
	 * Add an element only if it doesn't exist.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 * @param expiration
	 *          Ex: 10s, 3mn, 8h
	 */
	public static void add(String key, Object value, String expiration) {
		__cacheImpl.add(key, value, Time.parseDuration(expiration));
	}

	/**
	 * Add an element only if it doesn't exist, and return only when the element
	 * is effectivly cached.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 * @param expiration
	 *          Ex: 10s, 3mn, 8h
	 * @return If the element an eventually been cached
	 */
	public static boolean safeAdd(String key, Object value, String expiration) {
		return __cacheImpl.safeAdd(key, value, Time.parseDuration(expiration));
	}

	/**
	 * Add an element only if it doesn't exist and store it indefinitly.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 */
	public static void add(String key, Object value) {
		__cacheImpl.add(key, value, Time.parseDuration(null));
	}

	/**
	 * Set an element.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 * @param expiration
	 *          Ex: 10s, 3mn, 8h
	 */
	public static void set(String key, Object value, String expiration) {
		__cacheImpl.set(key, value, Time.parseDuration(expiration));
	}

	/**
	 * Set an element and return only when the element is effectivly cached.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 * @param expiration
	 *          Ex: 10s, 3mn, 8h
	 * @return If the element an eventually been cached
	 */
	public static boolean safeSet(String key, Object value, String expiration) {
		return __cacheImpl.safeAdd(key, value, Time.parseDuration(expiration));
	}

	/**
	 * Set an element and store it indefinitly.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 */
	public static void set(String key, Object value) {
		__cacheImpl.set(key, value, Time.parseDuration(null));
	}

	/**
	 * Replace an element only if it already exists.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 * @param expiration
	 *          Ex: 10s, 3mn, 8h
	 */
	public static void replace(String key, Object value, String expiration) {
		__cacheImpl.replace(key, value, Time.parseDuration(expiration));
	}

	/**
	 * Replace an element only if it already exists and return only when the
	 * element is effectivly cached.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 * @param expiration
	 *          Ex: 10s, 3mn, 8h
	 * @return If the element an eventually been cached
	 */
	public static boolean safeReplace(String key, Object value, String expiration) {
		return __cacheImpl.safeReplace(key, value, Time.parseDuration(expiration));
	}

	/**
	 * Replace an element only if it already exists and store it indefinitly.
	 * 
	 * @param key
	 *          Element key
	 * @param value
	 *          Element value
	 */
	public static void replace(String key, Object value) {
		__cacheImpl.replace(key, value, Time.parseDuration(null));
	}

	/**
	 * Increment the element value (must be a Number).
	 * 
	 * @param key
	 *          Element key
	 * @param by
	 *          The incr value
	 * @return The new value
	 */
	public static long incr(String key, int by) {
		return __cacheImpl.incr(key, by);
	}

	/**
	 * Increment the element value (must be a Number) by 1.
	 * 
	 * @param key
	 *          Element key
	 * @return The new value
	 */
	public static long incr(String key) {
		return __cacheImpl.incr(key, 1);
	}

	/**
	 * Decrement the element value (must be a Number).
	 * 
	 * @param key
	 *          Element key
	 * @param by
	 *          The decr value
	 * @return The new value
	 */
	public static long decr(String key, int by) {
		return __cacheImpl.decr(key, by);
	}

	/**
	 * Decrement the element value (must be a Number) by 1.
	 * 
	 * @param key
	 *          Element key
	 * @return The new value
	 */
	public static long decr(String key) {
		return __cacheImpl.decr(key, 1);
	}

	/**
	 * Retrieve an object.
	 * 
	 * @param key
	 *          The element key
	 * @return The element value or null
	 */
	public static Object get(String key) {
		return __cacheImpl.get(key);
	}

	/**
	 * Bulk retrieve.
	 * 
	 * @param key
	 *          List of keys
	 * @return Map of keys & values
	 */
	public static Map<String, Object> get(String... key) {
		return __cacheImpl.get(key);
	}

	/**
	 * Delete an element from the cache.
	 * 
	 * @param key
	 *          The element key *
	 */
	public static void delete(String key) {
		__cacheImpl.delete(key);
	}

	/**
	 * Delete an element from the cache and return only when the element is
	 * effectivly removed.
	 * 
	 * @param key
	 *          The element key
	 * @return If the element an eventually been deleted
	 */
	public static boolean safeDelete(String key) {
		return __cacheImpl.safeDelete(key);
	}

	/**
	 * Clear all data from cache.
	 */
	public static void clear() {
		__cacheImpl.clear();
	}

	/**
	 * Convenient clazz to get a value a class type;
	 * 
	 * @param <T>
	 *          The needed type
	 * @param key
	 *          The element key
	 * @param clazz
	 *          The type class
	 * @return The element value or null
	 */
	public static <T> T get(String key, Class<T> clazz) {
		return (T) __cacheImpl.get(key);
	}

	/**
	 * Init the cache system.
	 */
	public static void init(ServletConfig pConfig) {
		if ("true".equals(pConfig.getInitParameter("memcached"))) {
			try {
				__cacheImpl = new MemcachedImpl(pConfig);
				__logger.info("Connected to memcached");
			} catch (Exception e) {
				__logger.log(Level.WARNING, "Error while connecting to memcached", e);
				__logger.info("Fallback to local cache");
				__cacheImpl = new LocalCacheImpl();
			}
		} else {
			__cacheImpl = new LocalCacheImpl();
		}
	}

	/**
	 * Stop the cache system.
	 */
	public static void stop() {
		__cacheImpl.stop();
	}

	static interface CacheImpl {
		public void add(String key, Object value, int expiration);

		public boolean safeAdd(String key, Object value, int expiration);

		public void set(String key, Object value, int expiration);

		public boolean safeSet(String key, Object value, int expiration);

		public void replace(String key, Object value, int expiration);

		public boolean safeReplace(String key, Object value, int expiration);

		public Object get(String key);

		public Map<String, Object> get(String[] keys);

		public long incr(String key, int by);

		public long decr(String key, int by);

		public void clear();

		public void delete(String key);

		public boolean safeDelete(String key);

		public void stop();
	}

	static class MemcachedImpl implements CacheImpl {
		MemcachedClient m_client;

		public MemcachedImpl(ServletConfig pConfig) throws IOException {
			System.setProperty("net.spy.log.LoggerImpl", "net.spy.log.Log4JLogger");
			if (pConfig.getInitParameter("memcached.host") != null) {
				m_client = new MemcachedClient(AddrUtil.getAddresses(pConfig.getInitParameter("memcached.host")));
				m_client.setTranscoder(new SerializingTranscoder() {
					@Override
					protected Object deserialize(byte[] data) {
						try {
							return new ObjectInputStream(new ByteArrayInputStream(data)) {
								@Override
								protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
									return ServerCache.class.getClassLoader().loadClass(desc.getName());
								}
							}.readObject();
						} catch (Exception e) {
							__logger.log(Level.WARNING, "Could not deserialize", e);
						}
						return null;
					}

					@Override
					protected byte[] serialize(Object object) {
						try {
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							new ObjectOutputStream(bos).writeObject(object);
							return bos.toByteArray();
						} catch (IOException e) {
							__logger.log(Level.WARNING, "Could not serialize", e);
						}
						return null;
					}
				});
			} else if (pConfig.getInitParameter("memcached.1.host") != null) {
				int nb = 1;
				String addresses = "";
				while (pConfig.getInitParameter("memcached." + nb + ".host") != null) {
					addresses += pConfig.getInitParameter("memcached." + nb + ".host") + " ";
					nb++;
				}
				m_client = new MemcachedClient(AddrUtil.getAddresses(addresses));
			} else {
				throw new IOException(("Bad configuration for memcached"));
			}
		}

		public void add(String key, Object value, int expiration) {
			m_client.add(key, expiration, value);
		}

		public Object get(String key) {
			Future<Object> future = m_client.asyncGet(key);
			try {
				return future.get(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				future.cancel(false);
			}
			return null;
		}

		public void clear() {
			m_client.flush();
		}

		public void delete(String key) {
			m_client.delete(key);
		}

		public Map<String, Object> get(String[] keys) {
			Future<Map<String, Object>> future = m_client.asyncGetBulk(keys);
			try {
				return future.get(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				future.cancel(false);
			}
			return new HashMap<String, Object>();
		}

		public long incr(String key, int by) {
			return m_client.incr(key, by);
		}

		public long decr(String key, int by) {
			return m_client.decr(key, by);
		}

		public void replace(String key, Object value, int expiration) {
			m_client.replace(key, expiration, value);
		}

		public boolean safeAdd(String key, Object value, int expiration) {
			Future<Boolean> future = m_client.add(key, expiration, value);
			try {
				return future.get(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				future.cancel(false);
			}
			return false;
		}

		public boolean safeDelete(String key) {
			Future<Boolean> future = m_client.delete(key);
			try {
				return future.get(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				future.cancel(false);
			}
			return false;
		}

		public boolean safeReplace(String key, Object value, int expiration) {
			Future<Boolean> future = m_client.replace(key, expiration, value);
			try {
				return future.get(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				future.cancel(false);
			}
			return false;
		}

		public boolean safeSet(String key, Object value, int expiration) {
			Future<Boolean> future = m_client.set(key, expiration, value);
			try {
				return future.get(1, TimeUnit.SECONDS);
			} catch (Exception e) {
				future.cancel(false);
			}
			return false;
		}

		public void set(String key, Object value, int expiration) {
			m_client.set(key, expiration, value);
		}

		public void stop() {
			m_client.shutdown();
		}

	}

	static class LocalCacheImpl implements CacheImpl {

		private Map<String, CachedElement> m_cache = new HashMap<String, CachedElement>();

		public void add(String key, Object value, int expiration) {
			safeAdd(key, value, expiration);
		}

		public Object get(String key) {
			CachedElement cachedElement = m_cache.get(key);
			if (cachedElement != null && System.currentTimeMillis() >= cachedElement.getExpiration()) {
				m_cache.remove(key);
				return null;
			}
			return cachedElement == null ? null : cachedElement.getValue();
		}

		public void delete(String key) {
			safeDelete(key);
		}

		public Map<String, Object> get(String[] keys) {
			Map<String, Object> result = new HashMap<String, Object>();
			for (String key : keys) {
				result.put(key, get(key));
			}
			return result;
		}

		public synchronized long incr(String key, int by) {
			CachedElement cachedElement = m_cache.get(key);
			if (cachedElement == null) {
				return -1;
			}
			long newValue = (Long) cachedElement.getValue() + by;
			cachedElement.setValue(newValue);
			return newValue;
		}

		public synchronized long decr(String key, int by) {
			CachedElement cachedElement = m_cache.get(key);
			if (cachedElement == null) {
				return -1;
			}
			long newValue = (Long) cachedElement.getValue() - by;
			cachedElement.setValue(newValue);
			return newValue;
		}

		public void replace(String key, Object value, int expiration) {
			safeReplace(key, value, expiration);
		}

		public void set(String key, Object value, int expiration) {
			safeSet(key, value, expiration);
		}

		public boolean safeAdd(String key, Object value, int expiration) {
			Object v = get(key);
			if (v == null) {
				set(key, value, expiration);
				return true;
			}
			return false;
		}

		public boolean safeDelete(String key) {
			CachedElement cachedElement = m_cache.get(key);
			if (cachedElement != null) {
				m_cache.remove(key);
				return true;
			}
			return false;
		}

		public boolean safeReplace(String key, Object value, int expiration) {
			CachedElement cachedElement = m_cache.get(key);
			if (cachedElement == null) {
				return false;
			}
			cachedElement.setExpiration(expiration * 1000 + System.currentTimeMillis());
			cachedElement.setValue(value);
			return true;
		}

		public boolean safeSet(String key, Object value, int expiration) {
			m_cache.put(key, new CachedElement(key, value, expiration * 1000 + System.currentTimeMillis()));
			return true;
		}

		public void stop() {
		}

		public void clear() {
			m_cache.clear();
		}

		//
		class CachedElement {

			private String m_key;
			private Object m_value;
			private Long m_expiration;

			public CachedElement(String key, Object value, Long expiration) {
				this.m_key = key;
				this.m_value = value;
				this.m_expiration = expiration;
			}

			public String getKey() {
				return m_key;
			}

			public void setKey(String key) {
				this.m_key = key;
			}

			public Object getValue() {
				return m_value;
			}

			public void setValue(Object value) {
				this.m_value = value;
			}

			public Long getExpiration() {
				return m_expiration;
			}

			public void setExpiration(Long expiration) {
				this.m_expiration = expiration;
			}
		}
	}
}
