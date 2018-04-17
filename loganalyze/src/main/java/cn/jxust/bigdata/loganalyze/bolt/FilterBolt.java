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
 * ����ԭʼ��־��Ϣ�������ɶ��󣬽�������������־��Ϣ���´���
 */
public class FilterBolt extends BaseBasicBolt{

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String msg = input.getString(0);
		//������־��Ϣ�ɶ���
		LogMessage message = LogAnalyzeHandler.parser(msg);
		
		//�Ƿ�������Ҫ���������
		if(message==null||!LogAnalyzeHandler.isContainValidType(message.getType())){
			System.out.println("--------------��־��Ϣ�����ϣ��Ѷ���----------");
			return;
		}
		collector.emit(new Values(message.getType(),message));
		//���ڸ���job��
		LogAnalyzeHandler.scheduleLoad();
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("type","logMessage"));
	}

}
