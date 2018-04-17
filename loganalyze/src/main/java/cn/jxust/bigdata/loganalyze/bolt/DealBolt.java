package cn.jxust.bigdata.loganalyze.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import cn.jxust.bigdata.loganalyze.bean.LogMessage;
import cn.jxust.bigdata.loganalyze.utils.LogAnalyzeHandler;

public class DealBolt extends BaseBasicBolt{

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		LogMessage msg=(LogMessage) input.getValue(1);
		//根据job处理日志
		LogAnalyzeHandler.deal(msg);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

}
