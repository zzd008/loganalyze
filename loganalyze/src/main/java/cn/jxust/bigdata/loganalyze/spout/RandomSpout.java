package cn.jxust.bigdata.loganalyze.spout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.google.gson.Gson;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import cn.jxust.bigdata.loganalyze.bean.LogMessage;

/*
 * ����ģ������������ݣ�ʵ����������ʹ��kfkSpout
 */
public class RandomSpout extends BaseRichSpout{
	private SpoutOutputCollector collector;
	private List<LogMessage> list;
	private Map<String,String> map;
	
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector=collector;//!!!!!���� Ҫ��this
		this.map=new HashMap<String, String>();
		
		list=new ArrayList<LogMessage>();
		//����ģ�����ݣ�ֱ�ӹ����ɶ��󷢹�ȥ��ʵ���з��͵�����־�ַ�����bolt��Ҫȥ�����������ɶ���
		list.add(new LogMessage(1, "http://www.baidu.com", "http://www.jxuststore.com/product?id=101", "zzd"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd"));//��� 4 
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd1"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd2"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd3"));
		list.add(new LogMessage(2, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd3"));
		list.add(new LogMessage(1, "http://www.baidu.com", "http://www.jxuststore.com/product?id=101", "zhouzhida"));
		list.add(new LogMessage(1, "http://www.baidu.com", "http://www.jxuststore.com/product?id=102", "zhangsan"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/order", "zzd"));//�µ� 3 
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/order", "zzd1"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/order", "zzd2"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/buy", "zzd"));//���� 2
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/buy", "zzd1"));
	}

	@Override
	public void nextTuple() {
		Random random=new Random();
		LogMessage logMessage = list.get(random.nextInt(list.size()));
		String msg = new Gson().toJson(logMessage);
		collector.emit(new Values(msg));//��json��ʽ���͵�bolt��Ҳ����ֱ�ӷ��Ͷ���
		System.out.println("-------------���ͣ�"+msg+"-----------");
		String uId = UUID.randomUUID().toString().replaceAll("-", "");
		map.put(uId, msg);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//ģ�����ݼ���ת����
		/*for(LogMessage logMessage:list){
			String msg = new Gson().toJson(logMessage);
			collector.emit(new Values(msg));//��json��ʽ���͵�bolt��Ҳ����ֱ�ӷ��Ͷ���
			System.out.println("-------------���ͣ�"+msg+"-----------");
			String uId = UUID.randomUUID().toString().replaceAll("-", "");
			map.put(uId, msg);
		}
		
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("logMsg"));
	}
	
	/*
	 * ʹ��ack-fail���� 
	 */
	@Override
	public void ack(Object msgId) {
		map.remove(msgId.toString());
		System.out.println("----------�ɹ����ͣ���------------");
	}
	
	@Override
	public void fail(Object msgId) {
		System.out.println("----------�ɹ�ʧ�ܣ������·��ͣ���------------");
		collector.emit(new Values(map.get(msgId)));
	}
}
