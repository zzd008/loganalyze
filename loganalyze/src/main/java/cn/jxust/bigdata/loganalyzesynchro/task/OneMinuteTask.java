package cn.jxust.bigdata.loganalyzesynchro.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jxust.bigdata.loganalyze.bean.LogAnalyzeJob;
import cn.jxust.bigdata.loganalyze.dao.LogAnalyzeDao;
import cn.jxust.bigdata.loganalyze.utils.DateUtils;
import cn.jxust.bigdata.loganalyze.utils.MyJedisPool;
import cn.jxust.bigdata.loganalyzesynchro.bean.Record;
import cn.jxust.bigdata.loganalyzesynchro.util.CacheMaps;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/*
 * 任务：一分钟计算一次增量
 */
public class OneMinuteTask implements Runnable{

	@Override
	public void run() {
		//因为redis中的key也就是业务指标是按照天去计算的，所以第二天时会产生新的pvkey，而同步程序中在第二天的前一分钟也就是23:59分存的还是旧数据，所以在24点时要将它清空
		Calendar calendar = Calendar.getInstance();
        //24:00分时，将缓存清空
        if (calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.HOUR) == 24) {
            CacheMaps.setPvMap(new HashMap<String, Long>());
            CacheMaps.setUvMap(new HashMap<String, Long>());
            System.out.println("-----------清空缓存-----------");
        }
		
		//从redis中获取所有job对应的的最新指标
		String date=DateUtils.getDate().split(" ")[0];//获取当年-月-日
		List<Record> allRecords = getAllRecords(date);
		
		//从缓存中拿到上次的全量值，用最新的全量值减去上次的全量值就是增量
		List<Record> incrRecordList = dealRecords(allRecords);		

		//将增量信息存入mysql
		new LogAnalyzeDao().saveOneMinuteRecord(incrRecordList);
		System.out.println("----------分钟增量数据已入库--------");
	}
	
	//计算 最新的减去上次的
	public List<Record> dealRecords(List<Record> list){
		List<Record> incrRecordList=new ArrayList<Record>();//要返回的增量list
		//计算每一个指标的增量
		for(Record record:list){
			Long pv=CacheMaps.getPv(record);
			Long uv=CacheMaps.getUv(record);
			System.out.println("---------增量  kv:"+pv+" uv:"+uv+"------------");
			incrRecordList.add(new Record(record.getIndexName(), pv, uv, record.getExecuteTime()));
		}
		
		return incrRecordList;
	}
	
	
	//获取所有job下的指标
	public List<Record> getAllRecords(String date){
		List<Record> recordList=new ArrayList<Record>();
		//获取所有job
		List<LogAnalyzeJob> jobList = new LogAnalyzeDao().getJobList();
		//在redis中得到每个job对应的指标
		for(LogAnalyzeJob job:jobList){
			String pvKey = "log:" + job.getJobId()+":"+job.getJobName() + ":pv:" + date;
            String uvKey = "log:" +  job.getJobId()+":"+ job.getJobName() + ":uv:" + date;
            
            JedisPool pool = MyJedisPool.getPool();//获取连接池
            Jedis jedis =pool.getResource();//从连接池中获取一个连接
            
            if(jedis.get(pvKey)!=null&&jedis.scard(uvKey)!=null){//redis中必须含有该job的pv、uv指标
            	String pv = jedis.get(pvKey);
                Long uv = jedis.scard(uvKey);
//                System.out.println(pv+"  "+uv);
	            Record record = new Record(job.getJobName(), Long.parseLong(pv), uv, new Date());
	            recordList.add(record);
            }
		}

		return recordList;
	}
	
}
