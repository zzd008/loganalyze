package cn.jxust.bigdata.loganalyzesynchro.bean;

import java.util.Date;

/*
 * ��¼��Ϣ
 */
public class Record {
	private String indexName;//ָ����
	private long pv;//pv��
	private long uv;//uv��
	private Date executeTime;//ִ��ʱ��
	private Date createTime=new Date();//����ʱ��
	
	public Record(String indexName, long pv, long uv, Date executeTime) {
		super();
		this.indexName = indexName;
		this.pv = pv;
		this.uv = uv;
		this.executeTime = executeTime;
	}

	//Ҫ�ṩ�޲ι�������mybatis����ʱ��
	public Record() {
		
	}
	
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public long getPv() {
		return pv;
	}
	public void setPv(long pv) {
		this.pv = pv;
	}
	public long getUv() {
		return uv;
	}
	public void setUv(long uv) {
		this.uv = uv;
	}
	public Date getExecuteTime() {
		return executeTime;
	}
	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	@Override
	public String toString() {
		return "Record [indexName=" + indexName + ", pv=" + pv + ", uv=" + uv + ", executeTime=" + executeTime
				+ ", createTime=" + createTime + "]";
	}
	
	
	
	
}
