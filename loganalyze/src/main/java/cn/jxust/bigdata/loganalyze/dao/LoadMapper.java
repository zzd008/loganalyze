package cn.jxust.bigdata.loganalyze.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import cn.jxust.bigdata.loganalyze.bean.*;
import cn.jxust.bigdata.loganalyzesynchro.bean.Record;

/*
 * 操作数据库的接口
 */
public interface LoadMapper {
	@Select(value="select jobId,jobName,jobType from log_analyze_job where status= 1")
	List<LogAnalyzeJob> getJobList();//获取所有job列表
	
	@Select(value="select a1.jobId,a2.field,a2.value,a2.compare from log_analyze_job a1,log_analyze_job_condition a2 where a1.jobId=a2.jobId")
	List<LogAnalyzeJobDetail> getJobDetailList();//获取所有jobdetail列表
	
	@Insert(value="insert into log_analyze_job_minute_append values(#{indexName},#{pv},#{uv},#{executeTime},#{createTime})")
	void saveOneMinuteRecord(Record record);//存分钟增量
	
	//注意使用参数时不能用#{startTime} 要用0 1
	@Select(value="select indexName,sum(pv) as pv,sum(uv) as uv from log_analyze_job_minute_append where executeTime between #{0} and #{1}  group by indexName ")
	List<Record> getHalfHourRecord(String startTime,String endTime);//从分钟增量表中查出半小时的增量
	
	@Insert(value="insert into log_analyze_job_halfhour_append values(#{indexName},#{pv},#{uv},#{executeTime},#{createTime})")
	void saveHalfHourRecord(Record record);//存半小时增量

}
