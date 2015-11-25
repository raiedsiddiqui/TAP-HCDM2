package org.tapestry.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tapestry.utils.Utils;

public class CalculationManager {	
	
	public static int getScoreByQuestionsList(List<String> list){
		String ans ="";
		int score = 0;
		int iAns = 0;		
		String regex = "\\d+";
		
		for (int i=0; i< list.size(); i++){
			ans = list.get(i);
			//remove those answers with "-" or not digit
			if (!Utils.isNullOrEmpty(ans) && !ans.contains("-") && (ans.matches(regex)))
			{
				iAns = Integer.parseInt(ans.trim());
				score = score + iAns;
			}			
		}
		return score;
	}
	
	public static int getSocialSupportNetworkScore(List<String> qList)
	{
		int score = 0;
		int iAnswer = 0;
		
		for (int i = 0; i < qList.size(); i++)
		{
			try{
				iAnswer = Integer.valueOf(qList.get(i).toString());				
			}catch(NumberFormatException ex)
			{
				System.out.println("input is string or empty, not digit");
				iAnswer = 0;
			}
			
			if (iAnswer <= 1)
				score = score + 1;
			else if (iAnswer == 2)
				score = score + 2;
			else if (iAnswer >= 3)
				score = score + 3;								
		}		
		
		return score;	
	}
	
	public static int getGeneralHealthyScaleScore(List<String> qList){
		int score = 0;
		int iAnswer = 0;
		
		for (int i = 0; i < qList.size(); i++)
		{
			try{
				iAnswer = Integer.valueOf(qList.get(i).toString());				
			}catch(NumberFormatException ex)
			{
				System.out.println("input is string or empty, not digit");
				iAnswer = 0;
			}
			
			if (i == 0 || i == 1 || i ==3 || i ==4)
				score = score + countPoints(iAnswer,new int[]{0,1,2}, 3);				
			else if (i == 2)
				score = score + countPoints(iAnswer, new int[]{0,0,0,1,2}, 5);				
			else if (i > 4 && i <= 9)			
				score = score + countPoints(iAnswer, new int[]{0,1},2);						
			else if (i == 10)
				score = score + countPoints(iAnswer, new int[]{0,1,2,2,2}, 5);			
		}		
		
		return score;		
	}
	
	static int countPoints(int a, int[] b, int n){
		int result = 0;
		
		if (n == 2)
		{
			switch (a) {
				case 1: result = result + b[0];
						break;
				case 2: result = result + b[1];
						break;								
				default:break;
			}	
		} 
		else if (n == 3)
		{
			switch (a) {
				case 1: result = result + b[0];
						break;
				case 2: result = result + b[1];
						break;
				case 3: result = result + b[2];
						break;				
				default:break;
			}	
		}
		else if (n == 4)
		{
			switch (a) {
				case 1: result = result + b[0];
						break;
				case 2: result = result + b[1];
						break;
				case 3: result = result + b[2];
						break;	
				case 4: result = result + b[3];
						break;			
				default:break;
			}		
		}
		else if (n == 5)
		{
			switch (a) {
				case 1: result = result + b[0];
						break;
				case 2: result = result + b[1];
						break;
				case 3: result = result + b[2];
						break;	
				case 4: result = result + b[3];
						break;
				case 5: result = result + b[4];
						break;
				default:break;
			}		
		}
		else if (n == 8)
		{
			switch (a) {
				case 1: result = result + b[0];
						break;
				case 2: result = result + b[1];
						break;
				case 3: result = result + b[2];
						break;	
				case 4: result = result + b[3];
						break;
				case 5: result = result + b[4];
						break;
				case 6: result = result + b[5];
						break;	
				case 7: result = result + b[6];
						break;
				case 8: result = result + b[7];
						break;
				default:break;
			}
		}
	
		return result;
	}
	
	public static int getNutritionScore(List<String> qList){
		int score = 0;
		int iAnswer = 0;
		
		for (int i = 0; i < qList.size(); i++)
		{
			try{
				iAnswer = Integer.valueOf(qList.get(i).toString());				
			}catch(NumberFormatException ex)
			{
				System.out.println("input is string or empty, not digit");
				iAnswer = 0;
			}
			
			if (i == 0)
				score = score + countPoints(iAnswer,new int[]{4,0,0,1,2,0,1,2}, 8);		
			else if (i == 1)
				score = score + countPoints(iAnswer,new int[]{4,4,0}, 3);
			else if (i == 2)
				score = score + countPoints(iAnswer,new int[]{0,4,0}, 3);
			else if (i == 3 || i == 16)
				score = score + countPoints(iAnswer,new int[]{4,2,1,0}, 4);
			else if (i == 4 || i == 12)
				score = score + countPoints(iAnswer,new int[]{4,2,0}, 3);
			else if (i == 5 || i == 11)
				score = score + countPoints(iAnswer,new int[]{4,3,2,0}, 4);
			else if (i == 6 || i == 8 || i == 9)
				score = score + countPoints(iAnswer,new int[]{4,3,2,1,0}, 5);
			else if (i == 7 || i == 10)
				score = score + countPoints(iAnswer,new int[]{4,3,1,0}, 4);
			else if (i == 13)
				score = score + countPoints(iAnswer,new int[]{0,2,3,4}, 4);
			else if (i == 15)
				score = score + countPoints(iAnswer,new int[]{4,2,0,4,0}, 5);
		}
		return score;			
	}
	
	public static String getAerobicMsg(int score)
	{
		String strScore;
		if (score == 0)
			strScore = String.valueOf(score) + " (sedentary)";
		else if (score == 1)
			strScore = String.valueOf(score) + " (under-active)";
		else if (score == 2)
			strScore = String.valueOf(score) + " (under-active regular-light activities)";
		else if (score == 3 || score == 4)
			strScore = String.valueOf(score) + " (under-active regular)";
		else if (score == 5 || score == 6)
			strScore = String.valueOf(score) + " (active)";
		else 
			strScore = "";
		
		return strScore;
	}
	
	public static int getAScoreForRAPA(List<String> qList){
		int score = 0;		
		
		for (int i = 0; i < 7; i++)
		{	
			if (qList.get(i).equals("1") && i > score)
				score = i;
		}		
		return score;
	}
	
	public static int getSFScoreForRAPA(List<String> qList){
		int score = 0;				
		int size = qList.size();
		
		if (("1".equals(qList.get(size - 2))) && ("2".equals(qList.get(size - 1))))
			score = 1;
		else if (("1".equals(qList.get(size - 2))) && ("1".equals(qList.get(size - 1))))
			score = 3;
		else if (("2".equals(qList.get(size - 2))) && ("1".equals(qList.get(size - 1))))
			score = 2;
		else if (("2".equals(qList.get(size - 2))) && ("2".equals(qList.get(size - 1))))
			score = 0;
		
		return score;
	}	
	
	public static ScoresInReport getMobilityScore(Map<String, String> qMap, ScoresInReport score){		
		String key;
		String value;
		String a2aValue = "";
		String a2bValue = ""; 
		String a3aValue = "";
		String a3bValue ="";
		String a4aValue = ""; 
		String a4bValue = "";
		StringBuffer sb = new StringBuffer();
		
		Iterator iterator = qMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry mapEntry = (Map.Entry) iterator.next();
			key = mapEntry.getKey().toString();
			value = mapEntry.getValue().toString();			
			
			if ("a2a".equalsIgnoreCase(key))
				a2aValue = value;
			
			if ("a2b".equalsIgnoreCase(key))
				a2bValue = value;
			
			if ("a3a".equalsIgnoreCase(key))
				a3aValue = value;
			
			if ("a3b".equalsIgnoreCase(key))
				a3bValue = value;
			
			if ("a4a".equalsIgnoreCase(key))
				a4aValue = value;
			
			if ("a4b".equalsIgnoreCase(key))
				a4bValue = value;		
		}
	
		//score for walking in 2.0 km
		if (a2aValue.equals("1"))
		{
			sb.append("no limitation ");
			sb = getModificationInfo(sb, a2bValue);			
		}
		else if (a2aValue.equals("2"))
		{
			sb.append("preclinical limitation ");
			sb = getModificationInfo(sb, a2bValue);
		}
		else if (a2aValue.equals("3"))
		{
			sb.append("minor manifest limitation ");
			sb = getModificationInfo(sb, a2bValue);
		}
		else 
		{
			sb.append("major manifest limitation ");
			sb = getModificationInfo(sb, a2bValue);
		} 
		
		if (!Utils.isNullOrEmpty(sb.toString()))
			score.setMobilityWalking2(sb.toString());
		//score for walking in 0.5 km
		sb = new StringBuffer();
		if (a3aValue.equals("1"))
		{
			sb.append("no limitation ");
			sb = getModificationInfo(sb, a3bValue);	
		}
		else if (a3aValue.equals("2"))
		{
			sb.append("preclinical limitation ");
			sb = getModificationInfo(sb, a3bValue);
		}
		else if (a3aValue.equals("3"))
		{
			sb.append("minor manifest limitation ");
			sb = getModificationInfo(sb, a3bValue);
		}
		else 
		{
			sb.append("major manifest limitation ");
			sb = getModificationInfo(sb, a3bValue);
		} 
		
		if (!Utils.isNullOrEmpty(sb.toString()))
			score.setMobilityWalkingHalf(sb.toString());
		
		//score for climbing one flight of starirs
		sb = new StringBuffer();
		if (a4aValue.equals("1"))
		{
			sb.append("no limitation ");
			sb = getModificationInfo(sb, a4bValue);	
		}
		else if (a4aValue.equals("2"))
		{
			sb.append("preclinical limitation ");
			sb = getModificationInfo(sb, a4bValue);
		}
		else if (a4aValue.equals("3"))
		{
			sb.append("minor manifest limitation ");
			sb = getModificationInfo(sb, a4bValue);
		}
		else
		{
			sb.append("major manifest limitation ");
			sb = getModificationInfo(sb, a4bValue);
		} 
		
		if (!Utils.isNullOrEmpty(sb.toString()))
			score.setMobilityClimbing(sb.toString());
		
		return score;
	}
	
	public static List<String> getPatientGoals(List<String> qList){
		List<String> goals = new ArrayList<String>();		
		goals.add(qList.get(0));
	//	separate 4th question by '<br>' to a list
		goals.addAll(Arrays.asList(qList.get(3).split("<br>")));
		
		//remove 'GOAL 1/2/3' from answer of 4th Question
		String answer;
		for (int i = 1; i<goals.size(); i++)
		{
			answer = goals.get(i);
			if (answer.startsWith("GOAL"))
				answer = answer.substring(6);			
			if (answer.startsWith(" Select Goal "))
				answer = "n/a";						
			goals.set(i, answer);
		}		
		return goals;
	}
	
	public static List<String> getLifeOrHealthGoals(List<String> qList, int index)
	{
		//health goals are from Q2, index = 1; life goals are from Q3, index = 2		
		return new ArrayList<String>(Arrays.asList(qList.get(index).split("-------<br>")));
	}
	
	
	public static List<String> setPatientGoalsMsg(String answer, List<String> goals)
	{
		String seperator = "<br>";
		List<String> myList = new ArrayList<String>();
		if (answer.contains(seperator))
		{
			myList = new ArrayList<String>(Arrays.asList(answer.split(seperator)));
			goals.addAll(myList);
		}
		
		return goals;
	}
	
	private static StringBuffer getModificationInfo(StringBuffer sb, String bAnswer){
		if("1".equals(bAnswer))
			sb.append("not using any modifications");
		else 
			sb.append("using modifications");
		
		return sb;
	}	
}
