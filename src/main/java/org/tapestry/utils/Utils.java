package org.tapestry.utils;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.math.*;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.tapestry.objects.Volunteer;

public class Utils {
	
	public static String timeFormat(String time){
		String hour = null;
		int iHour = 0;
		
		hour = time.substring(0,2);
		iHour = Integer.parseInt(hour);
		
		if (iHour >= 12)
			time = time + " PM";
		else 
			time = time + " AM";
		
		return time;
	}
	
	public static boolean isNullOrEmpty(String str){
		if (str != null && !str.equals(""))
			return false;
		else
			return true;
		
	}
	
	public static String getFormatDate(Date date){
		//convert current date to the format matched in DB				
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String strDate = sdf.format(date); 
		
		return strDate;		
	}
	
	public static String getDateByFormat(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String strDate = sdf.format(date); 
		
		return strDate;	
	}
	
	//return date with format yyyy-MM-dd'T'HH:mm:ss. 
	public static String getDateByCalendar(Calendar calendar){
		return DateFormatUtils.ISO_DATE_FORMAT.format(calendar);
	}
	
	public static String getDateOfWeek(String day){
		Calendar now = Calendar.getInstance();  
		int weekday = now.get(Calendar.DAY_OF_WEEK);  		
		Date date;
		
		if(day.endsWith("mon"))
		{
			if (weekday != Calendar.MONDAY)  
			{  
			    // calculate how much to add  
			    // the 2 is the difference between Saturday and Monday  
			    int days = (Calendar.SATURDAY - weekday + 2) % 7;  
			    now.add(Calendar.DAY_OF_YEAR, days);  
			}  
			// now get the date you want  
			date = now.getTime();  
		}		
		else if (day.equals("tue"))
		{
			if (weekday != Calendar.TUESDAY)  
			{  
			    int days = (Calendar.SATURDAY - weekday + 3) % 7;  
			    now.add(Calendar.DAY_OF_YEAR, days);  
			}  
			
			date = now.getTime();  
		}
		else if (day.equals("wed"))
		{
			if (weekday != Calendar.WEDNESDAY)  
			{  			    
			    int days = (Calendar.SATURDAY - weekday + 4) % 7;  
			    now.add(Calendar.DAY_OF_YEAR, days);  
			}  		
			date = now.getTime();  
		}
		else if (day.equals("thu"))
		{
			if (weekday != Calendar.THURSDAY)  
			{  			 
			    int days = (Calendar.SATURDAY - weekday + 5) % 7;  
			    now.add(Calendar.DAY_OF_YEAR, days);  
			}
			 date = now.getTime();  
		}
		else if (day.equals("fri"))
		{
			if (weekday != Calendar.FRIDAY)  
			{  
			    int days = (Calendar.SATURDAY - weekday + 6) % 7;  
			    now.add(Calendar.DAY_OF_YEAR, days);  
			} 
		 date = now.getTime();  
		}
		else
		 date = new Date();
		
		String strDate = Utils.getFormatDate(date);
		return strDate;
	}
	
	public static List<String> sortList(List<String> list){			
		if (list != null)
			Collections.sort(list);
		return  list;
	}
	
	public static void getPosition(String dayOfWeek, String attribute, String[] alist,
			boolean dayNull, ModelMap model)
	{
		List<String> list = new ArrayList<String>();	
		String[] positions = new String[4];
		
		for (String str: alist){
			if (str.startsWith(dayOfWeek))
				list.add(str.substring(1));
		}
		
		if (list.size() > 1)
		{
			positions = getPosition(list);			
			model.addAttribute(attribute, positions);
		}
		else
			dayNull = true;
	}

	/*
	public static String[] getPosition(List<String> list){	
		String[] positions = new String[4];		
		int size = list.size();
		positions[0] = list.get(0);		
		String current, next;
		int iCurrent, iNext;
						
		mainloop:
		for (int i = 0; i<size; i++){			
			current = list.get(i);
					
			if ((i + 1) == size)//only one dropdown has values
			{//value of end position which is displayed on dropdown 
				int j = Integer.valueOf(list.get(i));
			//	j++;  when an appointment's time is suppose to be 1/2 hour
				j = j+2; //when an appointment's time is suppose to be 1 hour
				positions[1] = String.valueOf(j);
				positions[2] = "null";
				positions[3] = "null";
			}
			else// both dropdown have values
			{
				next = list.get(i+1);						
				iCurrent = Integer.valueOf(current);
				iNext = Integer.valueOf(next);
					
				//separate two group of data for two dropdown to find start position and end position
				if ((iNext - iCurrent) > 1){	
					//value of end position which is displayed on dropdown 
			//		positions[1] = String.valueOf(iCurrent + 1); when an appointment's time is suppose to be 1/2 hour
					positions[1] = String.valueOf(iCurrent + 2);//when an appointment's time is suppose to be 1 hour
					positions[2] = list.get(i+1);				
					//value of end position which is displayed on dropdown 
			//		positions[3] = String.valueOf(Integer.valueOf(list.get(size-1)) +1 ); when an appointment's time is suppose to be 1/2 hour
					positions[3] = String.valueOf(Integer.valueOf(list.get(size-1)) +2 );//when an appointment's time is suppose to be 1 hour
					
					break mainloop;
				}
			}
		}
		return positions;
	} */
	
	public static boolean isConsecutive(List<String> list)
	{
		boolean consecutived = true;
		int current, next;
		int size = list.size();
		for (int i = 0; i<size; i++)
		{
			if (i < (size - 1))
			{
				current = Integer.valueOf(list.get(i));
				next = Integer.valueOf(list.get(i+1));
				
				if ((next - current) != 1)
					consecutived = false;
			}
		}
		
		return consecutived;
	}
	
	public static String[] getPosition(List<String> aList){
		boolean consecutived = true;
		int current, next;
		String pos1="";
		String pos2="";
		
		int size = aList.size();
		String[] positions = new String[4];		
		positions[0] = aList.get(0);		
		for (int i = 0; i<size; i++)
		{
			if (i < (size - 1))
			{
				current = Integer.valueOf(aList.get(i));
				next = Integer.valueOf(aList.get(i+1));
				
				if ((next - current) != 1)
				{
					consecutived = false;
					pos1 = String.valueOf(current + 2);//when an appointment's time is suppose to be 1 hour
					pos2 = String.valueOf(next);
					break;
				}
			}
		}
		
		String pos3 = String.valueOf(Integer.valueOf(aList.get(size-1)) + 2);//when an appointment's time is suppose to be 1 hour
		if (consecutived)
		{
			positions[1] = pos3;
			positions[2] = "null";
			positions[3] = "null";
		}
		else
		{
			positions[1] = pos1;
			positions[2] = pos2;
			positions[3] = pos3;
			
		}		
		return positions;		
	}
	
	
	
	public static List<String> getAvailablePeriod(String from, String to, List<String> list){
		StringBuffer sb;
		int start, end;
		String dayOfWeek;
				
		if ((from.length() == 1) || (to.length() == 1 ))//nothing selected 
			return list;
		else
		{
			//first letter in the string stands for the day of week
			dayOfWeek = from.substring(0, 1);		
			
			from = from.substring(1);
			to = to.substring(1);
				
			start = Integer.valueOf(from);
			end = Integer.valueOf(to);
				
			//endTime must great than startTime
			if (end > start)
			{
				for (int i = start; i < end-1; i++)//an appointment suppose to last 1 hour, if it is half hour, i<end instead of i<end-1
				{
					sb = new StringBuffer();		
					sb = sb.append(dayOfWeek);
					sb.append(String.valueOf(i));				
					list.add(sb.toString());
				}
			}					
			return list;
		}
	}
	


	/**
	 * get index of day of week by Date from calendar
	 * @param day
	 * @return
	 */
	public static int getDayOfWeekByDate(String day){
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");		
		int dayOfWeek = 999;
		try{
			if (dateValidate(day)){
				Date dDay = format.parse(day);
				//EE meaning "day of week, short version"
//				format=new SimpleDateFormat("EE"); 
//				String dayOfWeek=format.format(dDay);
				Calendar c = Calendar.getInstance();
				c.setTime(dDay);
				dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			}
			else
			{
				System.out.println("Invalid Date Formats!!!");
			}
		}
		catch(Exception e)
	    {
	        System.out.println("Invalid Date Formats!!!");
	    }		
		return dayOfWeek;
	}
	
	/**
	 * validate input Date's format
	 * @param d
	 * @return
	 */
	public static boolean dateValidate(String d){
		String dateArray[]= d.split("-");
	    int year=Integer.parseInt(dateArray[0]);
	    int month=Integer.parseInt(dateArray[1]);
	    int day=Integer.parseInt(dateArray[2]);
	   
	    boolean leapYear=false;

	    if((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0))
	    {
	         leapYear=true;
	    }

	    if(year>2099 || year<1900)
	        return false;

	    if(month<13)
	    {
	        if(month==1||month==3||month==5||month==7||month==8||month==10||month==12)
	        {
	            if(day>31)
	                return false;
	        }
	        else if(month==4||month==6||month==9||month==11)
	        {
	            if(day>30)
	                return false;
	        }
	        else if(leapYear==true && month==2)
	        {
	            if(day>29)
	              return false;
	        }
	        else if(leapYear==false && month==2)
	        {
	            if(day>28)
	              return false;
	        }
	        return true;    
	    }
	    else return false;
	 }
	
	//hard code plan definition by center admin
	public static List<String> getPlanDefinition(){
		List<String> plans = new ArrayList<String>();
		
		plans.add("");
		plans.add("Review Consent");
		plans.add("Shared MyOscar Info");
		plans.add("Booked Next Visit");
		plans.add("Left information about MyOcar for her/his to discuss with her/his daughter on her/his next visit");
		plans.add("Left information about Meals on Wheels service");
		
		return plans;
		
	}
	
	public static Map<String, String> getPlanDefinitionMap(){
		Map<String, String> plan = new HashMap<String, String>();
		List<String> planDef = getPlanDefinition();
		
		for ( int i = 1; i <= planDef.size(); i++)
			plan.put(String.valueOf(i), planDef.get(i-1));		
		
		return plan;
		
	}
	
	public static int getAgeByBirthDate(Calendar birthDate){
		int age = 0;
		
		Calendar today = Calendar.getInstance(); 
		int yearOfToday = today.get(Calendar.YEAR);
		
		int yearOfBirth = birthDate.get(Calendar.YEAR);

		if (yearOfBirth <= yearOfToday)
			age = yearOfToday - yearOfBirth;
			
		return age;
	}
	
	public static void download (HttpServletResponse response, String fileName, String contentType) throws Exception {
		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		FileInputStream in = new FileInputStream(fileName);
		ServletOutputStream out = response.getOutputStream();
		long buf_size = new File(fileName).length();
		byte[] outputByte = new byte[(int) buf_size];
		while (in.read(outputByte) != -1) {
			out.write(outputByte, 0, (int)buf_size);
		}
		in.close();
		out.flush();
		out.close();
	}
	
	//remove part of string
	public static String remove(String strSource, String strBeRemoved){
		int index = strSource.indexOf(strBeRemoved);
		if (index != (-1))
			strSource = strSource.substring(0, index); 
		return strSource;
	}
	
	public static boolean isNumeric(String str){
		return NumberUtils.isNumber(str);
	}
	
	//check if a string contains only digits
	public static boolean onlyDigitInString (String str)
	{		
		return str.matches("\\d+");
	}
	
	//replace newline character(^M which is a carriage-return character) 
	public static String replaceNewlineChar(String str, String replacement)
	{
		return str.replaceAll("\\p{Cntrl}", replacement);
	}
	
}
