package cn.jxust.bigdata.loganalyze.bean;

/*
 * Job的判断条件类
 */
public class LogAnalyzeJobDetail {
    private int id;
    private int jobId;//所属的job的id
    private String field;//字段
    private String value;//字段值
    private int compare;//比较办法 1是包含 2是等于

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCompare() {
        return compare;
    }

    public void setCompare(int compare) {
        this.compare = compare;
    }

    @Override
    public String toString() {
        return "LogAnalyzeJobDetail{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", field='" + field + '\'' +
                ", value='" + value + '\'' +
                ", compare=" + compare +
                '}';
    }
}
