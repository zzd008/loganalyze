package cn.jxust.bigdata.loganalyzesynchro.util;

import java.util.HashMap;
import java.util.Map;

import cn.jxust.bigdata.loganalyzesynchro.bean.Record;

/*
 * 缓存信息，存放上次每个job的pv、uv
 */
public class CacheMaps {
	//key为jobName value为pv/uv
	private static Map<String,Long> pvMap=new HashMap<String,Long>();
	private static Map<String,Long> uvMap=new HashMap<String,Long>();
	
	//计算增量pv
	public static Long getPv(Record record){
		Long pv = pvMap.get(record.getIndexName());//缓存的pv
		if(pv==null){//第一次缓存中pv设置为0，这样第一次计算的就是初始pv全量
			pv=(long) 0;
		}
		long pv1=record.getPv();//当前pv
		pvMap.put(record.getIndexName(), pv1);//将最新的pv值覆盖到缓存中
		return pv1-pv;//相减
	}
	
	//计算增量uv
	public static Long getUv(Record record){
		Long uv = uvMap.get(record.getIndexName());//缓存的uv
		if(uv==null){//第一次缓存中uv设置为0，这样第一次计算的就是初始uv全量
			uv=(long) 0;
		}
		long uv1=record.getUv();//当前uv
		uvMap.put(record.getIndexName(), uv1);//将最新的uv值覆盖到缓存中
		return uv1-uv;//相减
	}
	
	public static Map<String, Long> getPvMap() {
		return pvMap;
	}
	public static void setPvMap(Map<String, Long> pvMap) {
		CacheMaps.pvMap = pvMap;
	}
	public static Map<String, Long> getUvMap() {
		return uvMap;
	}
	public static void setUvMap(Map<String, Long> uvMap) {
		CacheMaps.uvMap = uvMap;
	}
	
	
	
	
	
	
}
