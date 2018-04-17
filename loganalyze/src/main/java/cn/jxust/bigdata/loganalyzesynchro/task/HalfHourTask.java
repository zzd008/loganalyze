package cn.jxust.bigdata.loganalyzesynchro.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.jxust.bigdata.loganalyze.dao.LogAnalyzeDao;
import cn.jxust.bigdata.loganalyze.utils.DateUtils;
import cn.jxust.bigdata.loganalyzesynchro.bean.Record;

/*
 * ��Сʱ����һ����������
 */
public class HalfHourTask implements Runnable{

	@Override
	public void run() {
		Calendar calendar = Calendar.getInstance();
		if(calendar.get(Calendar.MINUTE)%30==0){
			Date executeTime = calendar.getTime();//��ǰʱ��

			String endTime = DateUtils.getDataTime(calendar);
			String startTime = DateUtils.before30Minute(calendar);
			
//			System.out.println(startTime+"   "+endTime);
			
			LogAnalyzeDao dao = new LogAnalyzeDao();
			//�ӷ����������м������Сʱ������
			List<Record> halfHourRecord = dao.getHalfHourRecord(startTime, endTime);
			
			//����ִ��ʱ��
			for(Record record:halfHourRecord){
				record.setExecuteTime(executeTime);
				record.setCreateTime(executeTime);
			}
			//���
			dao.saveHalfHourRecord(halfHourRecord);
			System.out.println("----------��Сʱ���������-----------------");
		}
		
	}

}
