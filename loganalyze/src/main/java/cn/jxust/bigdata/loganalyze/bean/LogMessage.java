package cn.jxust.bigdata.loganalyze.bean;

import java.io.Serializable;

public class LogMessage implements Serializable{
	private int type;//1�������־��2�������־��3��������־��4��������־
    private String hrefTag;//��ǩ��ʶ
    private String hrefContent;//��ǩ��Ӧ�ı�ʶ����Ҫ���a��ǩ֮�������
    private String referrerUrl;//��Դ��ַ
    private String requestUrl;//�������ַ
    private String clickTime;//���ʱ��
    private String appName;//���������
    private String appVersion;//������汾
    private String language;//���������
    private String platform;//����ϵͳ
    private String screen;//��Ļ�ߴ�
    private String coordinate;//�����ʱ������
    private String systemId; //�����������ϵͳ���
    private String userName;//�û�����
    
    //���췽�� �����ʹ��type��referrerUrl��requestUrl��userName���ĸ��ֶ�
	public LogMessage(int type, String referrerUrl, String requestUrl, String userName) {
		super();
		this.type = type;
		this.referrerUrl = referrerUrl;
		this.requestUrl = requestUrl;
		this.userName = userName;
	}
	
	//�����ֶ�����ȡ��Ӧ��ֵ
	public String getCompareFieldValue(String field) {
        if ("hrefTag".equalsIgnoreCase(field)) {
            return hrefTag;
        } else if ("referrerUrl".equalsIgnoreCase(field)) {
            return referrerUrl;
        } else if ("requestUrl".equalsIgnoreCase(field)) {
            return requestUrl;
        }
        return "";
    }
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getHrefTag() {
		return hrefTag;
	}
	public void setHrefTag(String hrefTag) {
		this.hrefTag = hrefTag;
	}
	public String getHrefContent() {
		return hrefContent;
	}
	public void setHrefContent(String hrefContent) {
		this.hrefContent = hrefContent;
	}
	public String getReferrerUrl() {
		return referrerUrl;
	}
	public void setReferrerUrl(String referrerUrl) {
		this.referrerUrl = referrerUrl;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getClickTime() {
		return clickTime;
	}
	public void setClickTime(String clickTime) {
		this.clickTime = clickTime;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getScreen() {
		return screen;
	}
	public void setScreen(String screen) {
		this.screen = screen;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
	@Override
	public String toString() {
		return "LogMessage [type=" + type + ", hrefTag=" + hrefTag + ", hrefContent=" + hrefContent + ", referrerUrl="
				+ referrerUrl + ", requestUrl=" + requestUrl + ", clickTime=" + clickTime + ", appName=" + appName
				+ ", appVersion=" + appVersion + ", language=" + language + ", platform=" + platform + ", screen="
				+ screen + ", coordinate=" + coordinate + ", systemId=" + systemId + ", userName=" + userName + "]";
	}
    
    
    
    
}
