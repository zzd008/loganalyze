package cn.jxust.bigdata.loganalyze.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import cn.jxust.bigdata.loganalyze.bean.LogAnalyzeJob;
import cn.jxust.bigdata.loganalyze.bean.LogAnalyzeJobDetail;
import cn.jxust.bigdata.loganalyze.bean.LogMessage;
import cn.jxust.bigdata.loganalyze.constant.LogAnalyzeConstant;
import cn.jxust.bigdata.loganalyze.dao.LogAnalyzeDao;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/*
 * �������Ŀ�ĺ�����
 */
public class LogAnalyzeHandler {
	//��ʱ���������ļ��ı�ʶ
    private static boolean reloaded = false;
    //��������job��Ϣ��keyΪjobType��valueΪ����������е�job 
    private static Map<String, List<LogAnalyzeJob>> jobMap;
    //��������job���ж�������keyΪjobId,valueΪlist��list�з�װ�˺ܶ��ж�����
    private static Map<String, List<LogAnalyzeJobDetail>> jobDetailMap; 
	
	static{
		jobMap = loadJobMap();
		jobDetailMap = loadJobDetailMap();
	}
	
	//����jobMap
	private static synchronized Map<String, List<LogAnalyzeJob>> loadJobMap() {
		Map<String, List<LogAnalyzeJob>> map = new HashMap<String, List<LogAnalyzeJob>>();
		
        List<LogAnalyzeJob> logAnalyzeJobList = new LogAnalyzeDao().getJobList();
        for (LogAnalyzeJob logAnalyzeJob : logAnalyzeJobList) {
            int jobType = logAnalyzeJob.getJobType();
            if (isValidType(jobType)) {//�����õĲ����м���
                List<LogAnalyzeJob> jobList = map.get(jobType+"");
                if (jobList == null || jobList.size() == 0) {
                    jobList = new ArrayList<>();
                    map.put(jobType + "", jobList);
                }
                jobList.add(logAnalyzeJob);
            }
        }
        return map;
	}
	
	//����jobDetailMap
	private static synchronized Map<String, List<LogAnalyzeJobDetail>> loadJobDetailMap() {
		Map<String, List<LogAnalyzeJobDetail>> map = new HashMap<String, List<LogAnalyzeJobDetail>>();
		
        List<LogAnalyzeJobDetail> logAnalyzeJobDetailList = new LogAnalyzeDao().getJobDetailList();
        for (LogAnalyzeJobDetail logAnalyzeJobDetail : logAnalyzeJobDetailList) {
            int jobId = logAnalyzeJobDetail.getJobId();
            List<LogAnalyzeJobDetail> jobDetails = map.get(jobId + "");
            if (jobDetails == null || jobDetails.size() == 0) {
                jobDetails = new ArrayList<>();
                map.put(jobId + "", jobDetails);
            }
            jobDetails.add(logAnalyzeJobDetail);
        }
        return map;
	}

	
	//������־��Ϣ�ɶ���ʵ��������kfk-spout�з���������־��Ϣ���ַ���Ҫ���зָ������
	public static LogMessage parser(String msg){
		return new Gson().fromJson(msg, LogMessage.class);//������յ���json����ֱ�ӽ����ɶ���
	}
	
	//�ж�job�����е�type�Ƿ��ǺϷ������ͣ�1�������־��2�������־��3��������־��4��������־   job��������������и���ҵ������Ա����ָ��
	public static boolean isValidType(int type){
		if (type == LogAnalyzeConstant.BUY || type == LogAnalyzeConstant.CLICK
                || type == LogAnalyzeConstant.VIEW || type == LogAnalyzeConstant.SEARCH) {
            return true;
        }
		return false;
	}
	
	//�ж���־��Ϣ��type��job�������Ƿ����
	public static boolean isContainValidType(int type){
		if (jobMap.containsKey(type+"")) {//jobmap�б��������type
			return true;
		}
		return false;
	}
	
	
	
	//����job������־���� ����pv uv ת����
	public static  void deal(LogMessage msg){
		if(jobMap==null||jobDetailMap==null){
			reLoad();
		}
		
		//�ж���־��Ϣ�Ƿ����job����
		int type=msg.getType();//��ȡ��־������type
		List<LogAnalyzeJob> jobList = jobMap.get(type+"");//����type��ȡjob����
		for(LogAnalyzeJob job:jobList){//��������
			boolean flag=false;//�Ƿ�ƥ��
			List<LogAnalyzeJobDetail> jobDetailList = jobDetailMap.get(job.getJobId());//��ȡjob��Ӧ�������б�
			for(LogAnalyzeJobDetail condition:jobDetailList){//Ҫ����һ��job�µ���������
				String value=msg.getCompareFieldValue(condition.getField());//��ȡҪ�Ƚϵ��ֶε�ֵ
				
				if (condition.getCompare()== 1){//����
					if(value.contains(condition.getValue())){
						flag = true;
					}else{
						flag=false;
					}
				}else if(condition.getCompare()== 2){//����
					if(value.equals(condition.getValue())){
						flag = true;
					}else{
						flag=false;
					}
				}else{
					
				}
				
                if (!flag) {//��������������������
                    break;
                }
				
			}
			//��������������redis
			if(flag){
				// pv uv ��redis����string��keyΪ��log:{jobId}:{jobName}:pv:{ʱ��}��value=pv����/�û���
				 String pvKey = "log:" + job.getJobId()+":"+job.getJobName() + ":pv:" + DateUtils.getDate().split(" ")[0];//ֻ��������  �������ָ��
	             String uvKey = "log:" +  job.getJobId()+ ":"+job.getJobName() + ":uv:" + DateUtils.getDate().split(" ")[0];
	             JedisPool pool = MyJedisPool.getPool();//��ȡ���ӳ�
	             Jedis jedis =pool.getResource();//�����ӳ��л�ȡһ������
	             //��pv+1
	             jedis.incr(pvKey);
	             //����uv��uv��Ҫȥ�أ�ʹ��set
	             jedis.sadd(uvKey, msg.getUserName());
	             pool.returnResource(jedis);//�ͷ�����
	             System.out.println("-----------����redis�ɹ�����-------------");
	             
	             //����ת����
	             String viewKey="log:" + job.getJobId()+":"+job.getJobName() + ":conversion:view" + DateUtils.getDate().split(" ")[0];//����û�
	             String orderKey="log:" + job.getJobId()+":"+job.getJobName() + ":conversion:order" + DateUtils.getDate().split(" ")[0];//�µ��û�
	             String buyKey="log:" + job.getJobId()+":"+job.getJobName() + ":conversion:buy" + DateUtils.getDate().split(" ")[0];//�����û�
	             
	             //ʹ��setȥ��
	             jedis.sadd(viewKey, msg.getUserName());//�û�������Ʒҳ��������
	             if(msg.getRequestUrl().contains("/order")){//�û������µ�ҳ��
	            	 jedis.sadd(orderKey, msg.getUserName());
	             }
	             if(msg.getRequestUrl().contains("/buy")){//�û����빺��ҳ��
	            	 jedis.sadd(buyKey, msg.getUserName());
	             }
	             
			}else{
				System.out.println("-------------������job������------------------");
//				System.out.println(msg.getReferrerUrl()+"           "+msg.getRequestUrl());
			}
			
		}
	}
	
	
	//���ڸ���job
	public static void scheduleLoad() {
		String date = DateUtils.getDate();//��ȡ��ǰʱ��
		int now = Integer.parseInt(date.split(":")[1]);//��ȡ��ǰ�ķ��� 
		if(now%10==0){//ÿ10�������¸��¹���һ��
			reLoad();
		}else{
			reloaded=true;//��reloadʱ��Ҫ��reloaded��Ϊtrue����Ȼ����ȥreLoad()����
		}
		
	}
	
	//�����ݿ����¼���job
	private static synchronized void reLoad() {//���̲߳����������Ҫ����
		if(reloaded){
			jobMap = loadJobMap();
			jobDetailMap = loadJobDetailMap();
			
			reloaded=false;//��һ���̲߳������֮��reloaded��Ϊfalse�����������߳��ٲ���
			System.out.println("------------���¼���job����----------------------");
		}
	}
	
	
}
