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
 * 这里模拟随机发送数据，实际生产中是使用kfkSpout
 */
public class RandomSpout extends BaseRichSpout{
	private SpoutOutputCollector collector;
	private List<LogMessage> list;
	private Map<String,String> map;
	
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector=collector;//!!!!!啊啊 要加this
		this.map=new HashMap<String, String>();
		
		list=new ArrayList<LogMessage>();
		//这里模拟数据，直接构建成对象发过去，实际中发送的是日志字符串，bolt中要去解析来构建成对象
		list.add(new LogMessage(1, "http://www.baidu.com", "http://www.jxuststore.com/product?id=101", "zzd"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd"));//浏览 4 
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd1"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd2"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd3"));
		list.add(new LogMessage(2, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101", "zzd3"));
		list.add(new LogMessage(1, "http://www.baidu.com", "http://www.jxuststore.com/product?id=101", "zhouzhida"));
		list.add(new LogMessage(1, "http://www.baidu.com", "http://www.jxuststore.com/product?id=102", "zhangsan"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/order", "zzd"));//下单 3 
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/order", "zzd1"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/order", "zzd2"));
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/buy", "zzd"));//购买 2
		list.add(new LogMessage(1, "http://www.jxuststore.com", "http://www.jxuststore.com/product?id=101/buy", "zzd1"));
	}

	@Override
	public void nextTuple() {
		Random random=new Random();
		LogMessage logMessage = list.get(random.nextInt(list.size()));
		String msg = new Gson().toJson(logMessage);
		collector.emit(new Values(msg));//以json格式发送到bolt，也可以直接发送对象
		System.out.println("-------------发送："+msg+"-----------");
		String uId = UUID.randomUUID().toString().replaceAll("-", "");
		map.put(uId, msg);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//模拟数据计算转化率
		/*for(LogMessage logMessage:list){
			String msg = new Gson().toJson(logMessage);
			collector.emit(new Values(msg));//以json格式发送到bolt，也可以直接发送对象
			System.out.println("-------------发送："+msg+"-----------");
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
	 * 使用ack-fail机制 
	 */
	@Override
	public void ack(Object msgId) {
		map.remove(msgId.toString());
		System.out.println("----------成功发送！！------------");
	}
	
	@Override
	public void fail(Object msgId) {
		System.out.println("----------成功失败！！重新发送！！------------");
		collector.emit(new Values(map.get(msgId)));
	}
}
