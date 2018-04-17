package cn.jxust.bigdata.loganalyze.utils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*
 * redis���ӳ�
 */
public class MyJedisPool {
	private static JedisPool pool;
	static{
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(15);//����������ʵ��
		poolConfig.setMaxTotal(100);//�������������
		poolConfig.setMaxWaitMillis(3000);//��������û������ʱ�����ȴ��ȴ�ʱ��
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		
		pool=new JedisPool(poolConfig,"localhost", 6379);
		
	}
	
	public static JedisPool getPool(){
		return pool;
	}
}
