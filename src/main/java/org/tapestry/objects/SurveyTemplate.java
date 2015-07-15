package org.tapestry.objects;

/**
* Survey Template bean
* Represents a survey template.
* @author Darryl Hui
* @version 1.0
*/
public class SurveyTemplate {

	private int surveyID;
	private String title;
	private String type;
	private int priority;
	private byte[] contents;
	private String description;
	private String createdDate;
	private boolean showDelete;
	private int site;
	private String siteName;
	private boolean defaultSurvey;

	public SurveyTemplate(){
		//Default constructor
	}
	
	public static SurveyTemplate getTransfer(SurveyTemplate surveyTemplate){
		if (surveyTemplate == null) 
			return(null);

		SurveyTemplate surveyTemplateTransfer = new SurveyTemplate();

		surveyTemplateTransfer.setContents(surveyTemplate.getContents());
		surveyTemplateTransfer.setSurveyID(surveyTemplate.getSurveyID());
		surveyTemplateTransfer.setTitle(surveyTemplate.getTitle());
		surveyTemplateTransfer.setType(surveyTemplate.getType());
		surveyTemplateTransfer.setSite(surveyTemplate.getSite());
		surveyTemplateTransfer.setDescription(surveyTemplate.getDescription());
		surveyTemplateTransfer.setCreatedDate(surveyTemplate.getCreatedDate());
		surveyTemplateTransfer.setSiteName(surveyTemplate.getSiteName());
		surveyTemplateTransfer.setDefaultSurvey(surveyTemplate.isDefaultSurvey());

		return(surveyTemplateTransfer);
	}

	//Accessor methods
	/**
	*@return survey_ID The numeric ID of the survey
	*/
	public int getSurveyID(){
		return surveyID;
	}

	/**
	*@return title The survey's title
	*/
	public String getTitle(){
		return title;
	}

	/**
	*@return type The survey's type. One of:
	*<ul>
	*<li>MUMPS</li>
	*</ul>
	*/
	public String getType(){
		return type;
	}
	
	/**
	 * @return the survey contents
	 */
	public byte[] getContents(){
		return contents;
	}
	
	/**
	 * @return the survey priority
	 */
	public int getPriority(){
		return priority;
	}
	
	/**
	 * @return the survey description
	 */
	public String getDescription(){
		return description;
	}

	//Mutator methods
	public void setSurveyID(int id){
		this.surveyID = id;
	}

	/**
	*@param name The new title for the survey
	*/
	public void setTitle(String title){
		this.title = title;
	}

	/**
	* @param type The new type for the survey ("MUMPS")
	*/
	public void setType(String type){
		this.type = type;
	}
	
	/**
	 * @param contents The new contents for the survey
	 */
	public void setContents(byte[] contents){
		this.contents = contents;
	}
	
	/**
	 * @param priority The new priority for the survey
	 */
	public void setPriority(int priority){
		this.priority = priority;
	}
	
	/**
	 * @param desc The new description for the survey
	 */
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public boolean isShowDelete() {
		return showDelete;
	}

	public void setShowDelete(boolean showDelete) {
		this.showDelete = showDelete;
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public boolean isDefaultSurvey() {
		return defaultSurvey;
	}

	public void setDefaultSurvey(boolean defaultSurvey) {
		this.defaultSurvey = defaultSurvey;
	}
	
}