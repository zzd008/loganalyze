package cn.jxust.bigdata.loganalyze.main;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import clojure.main;
import cn.jxust.bigdata.loganalyze.bolt.DealBolt;
import cn.jxust.bigdata.loganalyze.bolt.FilterBolt;
import cn.jxust.bigdata.loganalyze.spout.RandomSpout;

/*
 * 驱动类
 */
public class LogAnalyzeTopologyMain {
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("random-spout", new RandomSpout(), 1);
		builder.setBolt("filter-bolt", new FilterBolt(), 3).shuffleGrouping("random-spout");
		builder.setBolt("deal-bolt", new DealBolt(),3).fieldsGrouping("filter-bolt", new Fields("type"));
		
		Config config=new Config();
		config.setNumWorkers(3);
		config.setNumAckers(2);//开启ack-fail机制
		
		if(args!=null&&args.length>0){
			StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());//集群运行 传入的是topology的名字
		}else{
			LocalCluster localCluster = new LocalCluster();
			localCluster.submitTopology("logmonitor_topology", config, builder.createTopology());//本地运行
		}
		
	}
}
