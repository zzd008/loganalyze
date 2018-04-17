package cn.jxust.bigdata.loganalyze.utils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
 * redis连接池
 */
public class MyJedisPool {
	private static JedisPool pool;
	static{
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(15);//设置最大空闲实例
		poolConfig.setMaxTotal(100);//设置最大连接数
		poolConfig.setMaxWaitMillis(3000);//当池子中没有连接时，最大等待等待时间
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		
		pool=new JedisPool(poolConfig,"localhost", 6379);
		
	}
	
	public static JedisPool getPool(){
		return pool;
	}
}
