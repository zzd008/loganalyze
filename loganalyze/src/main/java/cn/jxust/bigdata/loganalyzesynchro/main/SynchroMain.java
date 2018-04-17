package cn.jxust.bigdata.loganalyzesynchro.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.jxust.bigdata.loganalyzesynchro.task.HalfHourTask;
import cn.jxust.bigdata.loganalyzesynchro.task.OneMinuteTask;

/*
 * ͬ����������
 */
public class SynchroMain {
	public static void main(String[] args) {
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
		
		//ÿ���Ӽ���һ������
		ses.scheduleAtFixedRate(new OneMinuteTask(), 0, 60, TimeUnit.SECONDS);//ÿ��һ���ӿ���һ���߳�ȥִ������
		
		//ÿ���Сʱ����һ������
//		ses.scheduleAtFixedRate(new HalfHourTask(), 0, 30, TimeUnit.MINUTES);//��������ÿ���Сʱִ��һ�Σ�������24�����ʱ��Ҫȥ���жϣ�ȥ������map
		ses.scheduleAtFixedRate(new HalfHourTask(), 0, 60, TimeUnit.SECONDS);//����ÿһ����ȥִ��һ���������������ж��Ƿ��ǰ�㣬�����Ǵ����ݿ��ж�ȡ���ӵ�����
		
		//����ȫ��
		
		
	}
}
