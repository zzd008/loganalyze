package cn.jxust.bigdata.loganalyze.dao;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import cn.jxust.bigdata.loganalyze.bean.*;
import cn.jxust.bigdata.loganalyzesynchro.bean.Record;

/*
 * dao层
 */
public class LogAnalyzeDao {
	private LoadMapper loadMapper;
	
	public LogAnalyzeDao(){
		this.loadMapper=MyBatisSqlSessionFactory.openSession(true).getMapper(LoadMapper.class);//获取代理对象
	}
	
	//获取所有job列表
	public List<LogAnalyzeJob> getJobList(){
		return loadMapper.getJobList();
	}
	
	//获取所有jobdetail列表
	public List<LogAnalyzeJobDetail> getJobDetailList(){
		return loadMapper.getJobDetailList();
	}
	
	//将每分钟计算的增量信息存到数据库中
	public void saveOneMinuteRecord(List<Record> list){
		for(Record record:list){
			loadMapper.saveOneMinuteRecord(record);
		}
	}
	
	//获取半个小时的增量
	public List<Record> getHalfHourRecord(String startTime,String endTime){
		return loadMapper.getHalfHourRecord(startTime,endTime);
	}
	
	//存储半个小时的增量
	public void saveHalfHourRecord(List<Record> list){
		for(Record record:list){
			loadMapper.saveHalfHourRecord(record);
		}
	}
	
	
	public static void main(String[] args) {
//		System.out.println(Calendar.getInstance());
//		System.out.println(Calendar.getInstance().getTime());
		
//		System.out.println(new LogAnalyzeDao().getJobList());
//		System.out.println(new LogAnalyzeDao().getJobDetailList());
		
		System.out.println(new LogAnalyzeDao().getHalfHourRecord("2018-03-11 14:42:32", "2018-03-11 15:12:32"));
		
	}
}

