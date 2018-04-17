package cn.jxust.bigdata.loganalyzesynchro.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jxust.bigdata.loganalyzesynchro.task.HalfHourTask;
import cn.jxust.bigdata.loganalyzesynchro.task.OneMinuteTask;

/*
 * 同步程序主类
 */
public class SynchroMain {
	public static void main(String[] args) {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
		
		//每分钟计算一下增量
		ses.scheduleAtFixedRate(new OneMinuteTask(), 0, 60, TimeUnit.SECONDS);//每隔一分钟开启一个线程去执行任务
		
		//每半个小时计算一下增量
//		ses.scheduleAtFixedRate(new HalfHourTask(), 0, 30, TimeUnit.MINUTES);//可以这样每半个小时执行一次，但是在24整点的时候要去做判断，去清理缓存map
		ses.scheduleAtFixedRate(new HalfHourTask(), 0, 60, TimeUnit.SECONDS);//建议每一分钟去执行一下任务，在任务中判断是否是半点，而且是从数据库中读取分钟的增量
		
		//计算全量
		
		
	}
}
