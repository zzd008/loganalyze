package cn.jxust.bigdata.loganalyze.bolt;

import com.google.gson.Gson;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import cn.jxust.bigdata.loganalyze.bean.LogMessage;
import cn.jxust.bigdata.loganalyze.utils.LogAnalyzeHandler;

/*
 * 解析原始日志信息并构建成对象，将符合条件的日志信息向下传递
 */
public class FilterBolt extends BaseBasicBolt{

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String msg = input.getString(0);
		//解析日志信息成对象
		LogMessage message = LogAnalyzeHandler.parser(msg);
		
		//是否是我们要处理的数据
		if(message==null||!LogAnalyzeHandler.isContainValidType(message.getType())){
			System.out.println("--------------日志信息不符合，已丢弃----------");
			return;
		}
		collector.emit(new Values(message.getType(),message));
		//定期更新job库
		LogAnalyzeHandler.scheduleLoad();
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("type","logMessage"));
	}

}
