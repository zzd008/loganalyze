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
 * ����һ���Ӽ���һ������
 */
public class OneMinuteTask implements Runnable{

	@Override
	public void run() {
		//��Ϊredis�е�keyҲ����ҵ��ָ���ǰ�����ȥ����ģ����Եڶ���ʱ������µ�pvkey����ͬ���������ڵڶ����ǰһ����Ҳ����23:59�ִ�Ļ��Ǿ����ݣ�������24��ʱҪ�������
		Calendar calendar = Calendar.getInstance();
        //24:00��ʱ�����������
        if (calendar.get(Calendar.MINUTE) == 0 && calendar.get(Calendar.HOUR) == 24) {
            CacheMaps.setPvMap(new HashMap<String, Long>());
            CacheMaps.setUvMap(new HashMap<String, Long>());
            System.out.println("-----------��ջ���-----------");
        }
		
		//��redis�л�ȡ����job��Ӧ�ĵ�����ָ��
		String date=DateUtils.getDate().split(" ")[0];//��ȡ����-��-��
		List<Record> allRecords = getAllRecords(date);
		
		//�ӻ������õ��ϴε�ȫ��ֵ�������µ�ȫ��ֵ��ȥ�ϴε�ȫ��ֵ��������
		List<Record> incrRecordList = dealRecords(allRecords);		

		//��������Ϣ����mysql
		new LogAnalyzeDao().saveOneMinuteRecord(incrRecordList);
		System.out.println("----------�����������������--------");
	}
	
	//���� ���µļ�ȥ�ϴε�
	public List<Record> dealRecords(List<Record> list){
		List<Record> incrRecordList=new ArrayList<Record>();//Ҫ���ص�����list
		//����ÿһ��ָ�������
		for(Record record:list){
			Long pv=CacheMaps.getPv(record);
			Long uv=CacheMaps.getUv(record);
			System.out.println("---------����  kv:"+pv+" uv:"+uv+"------------");
			incrRecordList.add(new Record(record.getIndexName(), pv, uv, record.getExecuteTime()));
		}
		
		return incrRecordList;
	}
	
	
	//��ȡ����job�µ�ָ��
	public List<Record> getAllRecords(String date){
		List<Record> recordList=new ArrayList<Record>();
		//��ȡ����job
		List<LogAnalyzeJob> jobList = new LogAnalyzeDao().getJobList();
		//��redis�еõ�ÿ��job��Ӧ��ָ��
		for(LogAnalyzeJob job:jobList){
			String pvKey = "log:" + job.getJobId()+":"+job.getJobName() + ":pv:" + date;
            String uvKey = "log:" +  job.getJobId()+":"+ job.getJobName() + ":uv:" + date;
            
            JedisPool pool = MyJedisPool.getPool();//��ȡ���ӳ�
            Jedis jedis =pool.getResource();//�����ӳ��л�ȡһ������
            
            if(jedis.get(pvKey)!=null&&jedis.scard(uvKey)!=null){//redis�б��뺬�и�job��pv��uvָ��
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
