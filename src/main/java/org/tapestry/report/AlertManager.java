package org.tapestry.report;

import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.tapestry.utils.Utils;

public class AlertManager {
	public static List<String> getNutritionAlerts(int scores, List<String> alerts, List<String> qList){
		String ans ="";
		
		if (scores < 50)
			alerts.add(AlertsInReport.NUTRITION_ALERT1);
				
		
		if ((qList != null)&&(qList.size()>0))
		{
			//weight alert-- the first question in nutrition survey
			ans = qList.get(0);			
			ans = ans.trim();		
			
			if (!Utils.isNullOrEmpty(ans)) {
				if (ans.equals("2"))				
					alerts.add(AlertsInReport.NUTRITION_ALERT2A);			
				else if (ans.equals("3"))
					alerts.add(AlertsInReport.NUTRITION_ALERT2B);
				else if (ans.equals("6"))
					alerts.add(AlertsInReport.NUTRITION_ALERT2C);
			}
			
			//weight changed alert --- second question
			ans = qList.get(2);
			ans = ans.trim();
			if (!Utils.isNullOrEmpty(ans) && ans.equals("3"))
				alerts.add(AlertsInReport.NUTRITION_ALERT2);
			
			//meal alert -- forth question in nutrition survey		
			ans = qList.get(3);
			ans = ans.trim();
			if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
				alerts.add(AlertsInReport.NUTRITION_ALERT3);
			
			//Appetitive alert --- sixth question in nutrition survey
			ans = qList.get(5);
			ans = ans.trim();
			if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
				alerts.add(AlertsInReport.NUTRITION_ALERT4);
			
			//cough, choke and pain alert --- eleventh question in nutrition survey
			ans = qList.get(10);
			ans = ans.trim();
			if (!Utils.isNullOrEmpty(ans) && ans.equals("4"))
				alerts.add(AlertsInReport.NUTRITION_ALERT5);
		}			
			
		
		return alerts;
	}
	
	public static List<String> getMobilityAlerts(Map<String, String> mMobilitySurvey, List<String> alerts){		
		String key = "";
		String value = "";
		String a2aValue = "";
		String a3aValue = "";
		String a4aValue = ""; 
		
		Iterator iterator = mMobilitySurvey.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			key = mapEntry.getKey().toString();
			value = mapEntry.getValue().toString();
			
			if ("a2a".equalsIgnoreCase(key))
				a2aValue = value;

			if ("a3a".equalsIgnoreCase(key))
				a3aValue = value;
				
			if ("a4a".equalsIgnoreCase(key))
				a4aValue = value;
		}
	
		if ("4".equals(a2aValue) || "5".equals(a2aValue))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT1);
		if ("4".equals(a3aValue) || "5".equals(a3aValue))
			alerts.add(AlertsInReport.MOBILITY_WALKING_ALERT2);
		if ("4".equals(a4aValue) || "5".equals(a4aValue))
			alerts.add(AlertsInReport.MOBILITY_CLIMBING_ALERT);
		return alerts;
	}
	
	public static List<String> getGeneralHealthyAlerts(int score, List<String> alerts, List<String> qList){
		if (score >= 7)
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT1);
				
		if ("2".equals(qList.get(5)))
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT2);
		
		if ("2".equals(qList.get(6)))
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT3);
		
		if ("3".equals(qList.get(10)) )
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT5);
		
		if ("4".equals(qList.get(10)))
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT6);
		
		if ("2".equals(qList.get(8)) )
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT7);
		
		if ("2".equals(qList.get(9)))
			alerts.add(AlertsInReport.EDMONTON_FRAIL_SCALE_ALERT8);
		
		return alerts;
	}
	
	public static List<String> getSocialLifeAlerts(int score, List<String> alerts){
		if (score < 10)
			alerts.add(AlertsInReport.DUKE_INDEX_OF_SOCIAL_SUPPORT_ALERT);
		
		return alerts;
	}
}
