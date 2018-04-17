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
 * 点击流项目的核心类
 */
public class LogAnalyzeHandler {
	//定时加载配置文件的标识
    private static boolean reloaded = false;
    //用来保存job信息，key为jobType，value为该类别下所有的job 
    private static Map<String, List<LogAnalyzeJob>> jobMap;
    //用来保存job的判断条件，key为jobId,value为list，list中封装了很多判断条件
    private static Map<String, List<LogAnalyzeJobDetail>> jobDetailMap; 
	
	static{
		jobMap = loadJobMap();
		jobDetailMap = loadJobDetailMap();
	}
	
	//加载jobMap
	private static synchronized Map<String, List<LogAnalyzeJob>> loadJobMap() {
		Map<String, List<LogAnalyzeJob>> map = new HashMap<String, List<LogAnalyzeJob>>();
		
        List<LogAnalyzeJob> logAnalyzeJobList = new LogAnalyzeDao().getJobList();
        for (LogAnalyzeJob logAnalyzeJob : logAnalyzeJobList) {
            int jobType = logAnalyzeJob.getJobType();
            if (isValidType(jobType)) {//不可用的不进行加载
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
	
	//加载jobDetailMap
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

	
	//解析日志信息成对象，实际生产中kfk-spout中发过来的日志信息是字符串要进行分割解析的
	public static LogMessage parser(String msg){
		return new Gson().fromJson(msg, LogMessage.class);//这里接收的是json串，直接解析成对象
	}
	
	//判断job任务中的type是否是合法的类型：1：浏览日志、2：点击日志、3：搜索日志、4：购买日志   job任务及其规则条件有各个业务线人员输入指定
	public static boolean isValidType(int type){
		if (type == LogAnalyzeConstant.BUY || type == LogAnalyzeConstant.CLICK
                || type == LogAnalyzeConstant.VIEW || type == LogAnalyzeConstant.SEARCH) {
            return true;
        }
		return false;
	}
	
	//判断日志信息的type在job任务中是否存在
	public static boolean isContainValidType(int type){
		if (jobMap.containsKey(type+"")) {//jobmap中必须包含该type
			return true;
		}
		return false;
	}
	
	
	
	//根据job处理日志数据 计算pv uv 转化率
	public static  void deal(LogMessage msg){
		if(jobMap==null||jobDetailMap==null){
			reLoad();
		}
		
		//判断日志信息是否符合job条件
		int type=msg.getType();//获取日志所属的type
		List<LogAnalyzeJob> jobList = jobMap.get(type+"");//根据type获取job集合
		for(LogAnalyzeJob job:jobList){//遍历集合
			boolean flag=false;//是否匹配
			List<LogAnalyzeJobDetail> jobDetailList = jobDetailMap.get(job.getJobId());//获取job对应的条件列表
			for(LogAnalyzeJobDetail condition:jobDetailList){//要满足一个job下的所有条件
				String value=msg.getCompareFieldValue(condition.getField());//获取要比较的字段的值
				
				if (condition.getCompare()== 1){//包含
					if(value.contains(condition.getValue())){
						flag = true;
					}else{
						flag=false;
					}
				}else if(condition.getCompare()== 2){//等于
					if(value.equals(condition.getValue())){
						flag = true;
					}else{
						flag=false;
					}
				}else{
					
				}
				
                if (!flag) {//不满足条件就立即跳出
                    break;
                }
				
			}
			//满足条件，存入redis
			if(flag){
				// pv uv 在redis中是string，key为：log:{jobId}:{jobName}:pv:{时间}，value=pv数量/用户名
				 String pvKey = "log:" + job.getJobId()+":"+job.getJobName() + ":pv:" + DateUtils.getDate().split(" ")[0];//只存年月日  按天计算指标
	             String uvKey = "log:" +  job.getJobId()+ ":"+job.getJobName() + ":uv:" + DateUtils.getDate().split(" ")[0];
	             JedisPool pool = MyJedisPool.getPool();//获取连接池
	             Jedis jedis =pool.getResource();//从连接池中获取一个连接
	             //给pv+1
	             jedis.incr(pvKey);
	             //设置uv，uv需要去重，使用set
	             jedis.sadd(uvKey, msg.getUserName());
	             pool.returnResource(jedis);//释放连接
	             System.out.println("-----------存入redis成功！！-------------");
	             
	             //计算转化率
	             String viewKey="log:" + job.getJobId()+":"+job.getJobName() + ":conversion:view" + DateUtils.getDate().split(" ")[0];//浏览用户
	             String orderKey="log:" + job.getJobId()+":"+job.getJobName() + ":conversion:order" + DateUtils.getDate().split(" ")[0];//下单用户
	             String buyKey="log:" + job.getJobId()+":"+job.getJobName() + ":conversion:buy" + DateUtils.getDate().split(" ")[0];//购买用户
	             
	             //使用set去重
	             jedis.sadd(viewKey, msg.getUserName());//用户进了商品页面就算浏览
	             if(msg.getRequestUrl().contains("/order")){//用户进入下单页面
	            	 jedis.sadd(orderKey, msg.getUserName());
	             }
	             if(msg.getRequestUrl().contains("/buy")){//用户进入购买页面
	            	 jedis.sadd(buyKey, msg.getUserName());
	             }
	             
			}else{
				System.out.println("-------------不满足job条件！------------------");
//				System.out.println(msg.getReferrerUrl()+"           "+msg.getRequestUrl());
			}
			
		}
	}
	
	
	//定期更新job
	public static void scheduleLoad() {
		String date = DateUtils.getDate();//获取当前时间
		int now = Integer.parseInt(date.split(":")[1]);//获取当前的分钟 
		if(now%10==0){//每10分钟重新更新规则一次
			reLoad();
		}else{
			reloaded=true;//非reload时间要将reloaded置为true，不然进不去reLoad()方法
		}
		
	}
	
	//从数据库重新加载job
	private static synchronized void reLoad() {//多线程操作共享变量要加锁
		if(reloaded){
			jobMap = loadJobMap();
			jobDetailMap = loadJobDetailMap();
			
			reloaded=false;//当一个线程操作完毕之后将reloaded置为false，不让其他线程再操作
			System.out.println("------------重新加载job任务----------------------");
		}
	}
	
	
}
