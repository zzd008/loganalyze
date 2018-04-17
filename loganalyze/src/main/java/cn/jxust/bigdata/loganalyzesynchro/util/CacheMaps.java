package cn.jxust.bigdata.loganalyzesynchro.util;

import java.util.HashMap;
import java.util.Map;

import cn.jxust.bigdata.loganalyzesynchro.bean.Record;

/*
 * ������Ϣ������ϴ�ÿ��job��pv��uv
 */
public class CacheMaps {
	//keyΪjobName valueΪpv/uv
	private static Map<String,Long> pvMap=new HashMap<String,Long>();
	private static Map<String,Long> uvMap=new HashMap<String,Long>();
	
	//��������pv
	public static Long getPv(Record record){
		Long pv = pvMap.get(record.getIndexName());//�����pv
		if(pv==null){//��һ�λ�����pv����Ϊ0��������һ�μ���ľ��ǳ�ʼpvȫ��
			pv=(long) 0;
		}
		long pv1=record.getPv();//��ǰpv
		pvMap.put(record.getIndexName(), pv1);//�����µ�pvֵ���ǵ�������
		return pv1-pv;//���
	}
	
	//��������uv
	public static Long getUv(Record record){
		Long uv = uvMap.get(record.getIndexName());//�����uv
		if(uv==null){//��һ�λ�����uv����Ϊ0��������һ�μ���ľ��ǳ�ʼuvȫ��
			uv=(long) 0;
		}
		long uv1=record.getUv();//��ǰuv
		uvMap.put(record.getIndexName(), uv1);//�����µ�uvֵ���ǵ�������
		return uv1-uv;//���
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
