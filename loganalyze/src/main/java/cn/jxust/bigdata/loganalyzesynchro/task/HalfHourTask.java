package cn.jxust.bigdata.loganalyzesynchro.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.jxust.bigdata.loganalyze.dao.LogAnalyzeDao;
import cn.jxust.bigdata.loganalyze.utils.DateUtils;
import cn.jxust.bigdata.loganalyzesynchro.bean.Record;

/*
 * 半小时计算一次增量数据
 */
public class HalfHourTask implements Runnable{

	@Override
	public void run() {
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.MINUTE)%30==0){
			Date executeTime = calendar.getTime();//当前时间

			String endTime = DateUtils.getDataTime(calendar);
			String startTime = DateUtils.before30Minute(calendar);
			
//			System.out.println(startTime+"   "+endTime);
			
			LogAnalyzeDao dao = new LogAnalyzeDao();
			//从分钟增量表中计算出半小时的增量
			List<Record> halfHourRecord = dao.getHalfHourRecord(startTime, endTime);
			
			//设置执行时间
			for(Record record:halfHourRecord){
				record.setExecuteTime(executeTime);
				record.setCreateTime(executeTime);
			}
			//入库
			dao.saveHalfHourRecord(halfHourRecord);
			System.out.println("----------半小时增量已入库-----------------");
		}
		
	}

}
