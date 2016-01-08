package org.tapestry.objects;

public class Preference {

	private int siteId;
	private int sosButton;
	private String sosReceiver;
	private String elderAbuseButton;
	private String elderAbuseContent;
	private String selfHarmButton;
	private String selfHarmContent;
	private String crisisLinesButton;
	private String crisisLinesContent;	
	private String apptNotiReceiver;
	private String reportNotiReceiver;
	
	public Preference(){}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getSosButton() {
		return sosButton;
	}

	public void setSosButton(int sosButton) {
		this.sosButton = sosButton;
	}

	public String getSosReceiver() {
		return sosReceiver;
	}

	public void setSosReceiver(String sosReceiver) {
		this.sosReceiver = sosReceiver;
	}

	public String getElderAbuseButton() {
		return elderAbuseButton;
	}

	public void setElderAbuseButton(String elderAbuseButton) {
		this.elderAbuseButton = elderAbuseButton;
	}

	public String getElderAbuseContent() {
		return elderAbuseContent;
	}

	public void setElderAbuseContent(String elderAbuseContent) {
		this.elderAbuseContent = elderAbuseContent;
	}

	public String getSelfHarmButton() {
		return selfHarmButton;
	}

	public void setSelfHarmButton(String selfHarmButton) {
		this.selfHarmButton = selfHarmButton;
	}

	public String getSelfHarmContent() {
		return selfHarmContent;
	}

	public void setSelfHarmContent(String selfHarmContent) {
		this.selfHarmContent = selfHarmContent;
	}

	public String getCrisisLinesButton() {
		return crisisLinesButton;
	}

	public void setCrisisLinesButton(String crisisLinesButton) {
		this.crisisLinesButton = crisisLinesButton;
	}

	public String getCrisisLinesContent() {
		return crisisLinesContent;
	}

	public void setCrisisLinesContent(String crisisLinesContent) {
		this.crisisLinesContent = crisisLinesContent;
	}

	public String getApptNotiReceiver() {
		return apptNotiReceiver;
	}

	public void setApptNotiReceiver(String apptNotiReceiver) {
		this.apptNotiReceiver = apptNotiReceiver;
	}

	public String getReportNotiReceiver() {
		return reportNotiReceiver;
	}

	public void setReportNotiReceiver(String reportNotiReceiver) {
		this.reportNotiReceiver = reportNotiReceiver;
	}		
	
}
