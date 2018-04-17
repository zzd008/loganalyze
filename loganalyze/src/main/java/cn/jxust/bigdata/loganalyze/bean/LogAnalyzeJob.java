package cn.jxust.bigdata.loganalyze.bean;

/*
 *Job类
 */
public class LogAnalyzeJob {
    private String jobId ;//job id 
    private String jobName;//job名字
    private int jobType; //类型，1:浏览日志、2:点击日志、3:搜索日志、4:购买日志
    private int bussinessId;//所属业务线
    private int status;//是否有效

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

    public int getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(int bussinessId) {
        this.bussinessId = bussinessId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LogAnalyzeJob{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", jobType=" + jobType +
                ", bussinessId=" + bussinessId +
                ", status=" + status +
                '}';
    }
}
