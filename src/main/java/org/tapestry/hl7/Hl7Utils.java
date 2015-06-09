package org.tapestry.hl7;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.tapestry.objects.Appointment;
import org.tapestry.objects.HL7Report;
import org.tapestry.objects.Patient;
import org.tapestry.objects.User;
import org.tapestry.report.ScoresInReport;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v23.datatype.TX;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v23.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.OBR;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;
@Component
public class Hl7Utils {
	@Resource(name="keyLocation")
    private Properties keyProps;
	
	public static String populateORUMessage(HL7Report report) throws HL7Exception, Exception{
		User logginUser = report.getUser();
		
		ORU_R01 message = new ORU_R01();
		//format obr date
		Timestamp currentTime = new Timestamp(new java.util.Date().getTime());			
		String orbDate = currentTime.toString().substring(0,10);
		orbDate = orbDate.replace("-", "");			
		// Populate the MSH Segment
		ca.uhn.hl7v2.model.v23.segment.MSH mshSegment = message.getMSH();
		
		mshSegment.getFieldSeparator().setValue("|");
		mshSegment.getEncodingCharacters().setValue("^~\\&");
		mshSegment.getSendingApplication().getNamespaceID().setValue("Tapestry Reports");
		mshSegment.getSendingFacility().getNamespaceID().setValue("CML");
	
		mshSegment.getMessageType().getCm_msg1_MessageType().setValue("ORU");
		mshSegment.getMessageType().getCm_msg2_TriggerEvent().setValue("R01");
		mshSegment.getProcessingID().getPt1_ProcessingID().setValue("1");
		mshSegment.getVersionID().setValue("2.3");	
		mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentTime);
				 
		ca.uhn.hl7v2.model.v23.segment.PID pid = message.getRESPONSE().getPATIENT().getPID();

		Patient p = report.getPatient();
		int patientId = p.getPatientID();
		String sex = p.getGender().substring(0,1);
				 
		pid.getSex().setValue(sex);//sex	
		pid.getPid1_SetIDPatientID().setValue(String.valueOf(patientId));//patientId	

		pid.getPatientName().getFamilyName().setValue(p.getLastName());//last name		
		pid.getPatientName().getGivenName().setValue(p.getFirstName());//first name
		pid.getDateOfBirth().getTimeOfAnEvent().setValue(p.getBod());// birth date	

		pid.getPid8_Sex().setValue(p.getGender());//sex
		pid.getPatientAddress(0).getStreetAddress().setValue(p.getStreetAddress());
		pid.getPatientAddress(0).getCity().setValue(p.getCity());				 
		pid.getPatientAddress(0).getCountry().setValue("Canada");
		pid.getPatientAddress(0).getStateOrProvince().setValue(p.getProvice());
		pid.getPatientAddress(0).getZipOrPostalCode().setValue(p.getPostalCode());
		
		//PV1 Segment
		ca.uhn.hl7v2.model.v23.segment.PV1 pv = message.getRESPONSE().getPATIENT().getVISIT().getPV1();
		
		Appointment a = report.getAppointment();
		pv.getPatientClass().setValue("U");
		pv.getAdmissionType().setValue("Follow up Visit");
		pv.getAttendingDoctor(0).getIDNumber().setValue("Dr " + p.getMrpLastName()); //mrp
		pv.getAdmitDateTime().getDegreeOfPrecision().setValue(a.getDate() +"|" + a.getTime());
		
		//ORC Segment
		ca.uhn.hl7v2.model.v23.segment.ORC orc = message.getRESPONSE().getORDER_OBSERVATION().getORC();
		orc.getOrc1_OrderControl().setValue("NW");
		orc.getOrc2_PlacerOrderNumber(0).getUniversalID().setValue("TR" + patientId);
		orc.getOrc5_OrderStatus().setValue("F");
		orc.getOrc12_OrderingProvider(0).getAssigningAuthority().getUniversalID().setValue("Tapestry");//provider organization
		orc.getOrc12_OrderingProvider(0).getFamilyName().setValue("Tapestry FamilyN");//family name
		orc.getOrc12_OrderingProvider(0).getGivenName().setValue("Tapestry FirstN");//first name
		orc.getOrc15_OrderEffectiveDateTime().getTimeOfAnEvent().setValue(orbDate);	
	
		/*
		 * The OBR segment is contained within a group called ORDER_OBSERVATION, 
		 * which is itself in a group called PATIENT_RESULT. These groups are
		 * reached using named accessors.
		 */
		//patient goals goals survey Q8
		List<String> lGoals = report.getPatientGoals();
		ORU_R01_ORDER_OBSERVATION orderObservation = message.getRESPONSE().getORDER_OBSERVATION(0);			
		fillOBR(1,orderObservation,"PATIENT GOALS",patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPPG", "PATIENT GOALS", message,lGoals.get(0), "1");
		fillOBX(orderObservation, "TPG1", "PATIENT GOAL1", message,lGoals.get(1), "2");
		fillOBX(orderObservation, "TPG2", "PATIENT GOAL2", message,lGoals.get(2), "3");
		fillOBX(orderObservation, "TPG3", "PATIENT GOAL3", message,lGoals.get(3), "4");

		//Key information                
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(1);		
		List<String> list = report.getAlerts();
		String alerts = getStringFromList(list);  
		fillOBR(2,orderObservation, "KEY INFORMATION", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPKO", "KEY OBSERVATION", message,alerts, "1");
	         
		//Social Context
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(2);  
		String keyObservation = a.getKeyObservation();
		fillOBR(3,orderObservation, "SOCIAL CONTEXT", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPSC", "SOCIAL CONTEXT", message,keyObservation, "1");
		        
		//memory screen and advance directives  
		list = report.getAdditionalInfos(); 
		List<String> sList = new ArrayList<String>();		
		for (int i = 0; i<list.size(); i++)
		{
			if (list.get(i).equals("1"))
				sList.add("YES");
			else
				sList.add("NO");
		}
		
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(3);
		fillOBR(4,orderObservation, "MEMORY SCREEN", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TMS1", "MEMORY SCREEN QUESTION1", message,sList.get(0), "1");
		fillOBX(orderObservation, "TMS2", "MEMORY SCREEN QUESTION2", message,sList.get(1), "2");
			
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(4);
		fillOBR(5,orderObservation, "ADVANCE DIRECTIVES", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TAD1", "ADVANCE DIRECTIVE QUESTION1", message,sList.get(2), "1");
		fillOBX(orderObservation, "TAD2", "ADVANCE DIRECTIVE QUESTION2", message,sList.get(3), "2");
		fillOBX(orderObservation, "TAD3", "ADVANCE DIRECTIVE QUESTION3", message,sList.get(3), "3");
		
		//Summary of tapestry tools
		//function status
		ScoresInReport scores = report.getScores();
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(5);
		fillOBR(6,orderObservation, "FUNCTION STATUS", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TFS1", "FUNCTION STATUS TUG SCORE", message,scores.getTimeUpGoTest(), "1");
		fillOBX(orderObservation, "TFS2", "FUNCTION STATUS EFS SCORE", message,scores.getEdmontonFrailScale(), "2");
		
		//nutrition status
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(6); 
		fillOBR(7,orderObservation, "NUTRITIONAL STATUS", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPNS", "NUTRITIONAL STATUS", message,String.valueOf(scores.getNutritionScreen()), "1");
	
		//social supports
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(7);
		fillOBR(8,orderObservation, "SOCIAL SUPPORT", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TSS1", "SOCIAL SUPPORT SATISFACTION SCORE", message,String.valueOf(scores.getSocialSatisfication()), "1");
		fillOBX(orderObservation, "TSS2", "SOCIAL SUPPORT NETWORK SCORE", message,String.valueOf(scores.getSocialNetwork()), "2");
						
		//mobility
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(8);
		fillOBR(9,orderObservation, "MOBILITY", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TMB1", "MOBILITY WALKING 2KM", message, scores.getMobilityWalking2(), "1");
		fillOBX(orderObservation, "TMB2", "MOBILITY WALKING 0.5KM", message, scores.getMobilityWalkingHalf(), "2");
		fillOBX(orderObservation, "TMB3", "MOBILITY CLIMBING STAIRS", message, scores.getMobilityClimbing(), "3");		
				         
		//physical activity
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(9);
		fillOBR(10,orderObservation, "PHYSICAL ACTIVITY", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPA1", "PHYSICAL ACTIVITY AEROBIC SCORE", message, String.valueOf(scores.getAerobicMessage()), "1");
		fillOBX(orderObservation, "TPA2", "PHYSICAL ACTIVITY SF SCORE", message, String.valueOf(scores.getpAStrengthAndFlexibility()), "2");			
		//end of summary tools	
		             
		//Life Goals from Q3
		List<String> lLifeGoals = report.getLifeGoals();
		String lgs = getStringFromList(lLifeGoals);
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(10);
		fillOBR(11,orderObservation, "LIFE GOALS", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPLG", "LIFE GOALS", message, lgs, "1");		
	
		//Health Goals from Q2
		List<String> lHealthGoals = report.getHealthGoals();
		String hgs = getStringFromList(lHealthGoals);
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(11);
		fillOBR(12,orderObservation, "HEALTH GOALS", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPHG", "HEALTH GOALS", message, hgs, "1");
		
		//Tapestry questions
		List<String> qList = report.getDailyActivities();
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(12);
		fillOBR(13,orderObservation, "TAPESTRY QUESTIONS", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TPQ1", "TAPESTRY QUESTION1", message, qList.get(0), "1");
		fillOBX(orderObservation, "TPQ2", "TAPESTRY QUESTION2", message, qList.get(1), "2");
		fillOBX(orderObservation, "TPQ3", "TAPESTRY QUESTION3", message, qList.get(2), "3");
		fillOBX(orderObservation, "TPQ4", "TAPESTRY QUESTION4", message, qList.get(3), "4");
		fillOBX(orderObservation, "TPQ5", "TAPESTRY QUESTION5", message, qList.get(4), "5");
		fillOBX(orderObservation, "TPQ6", "TAPESTRY QUESTION6", message, qList.get(5), "6");
		
		//volunteer infos
		List<String> volunteerInfos = report.getVolunteerInformations();
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(13);
		fillOBR(14,orderObservation, "VOLUNTEER INFORMATIONS", patientId, orbDate, logginUser);
		fillOBX(orderObservation, "TVI1", "VOLUNTEER 1", message, volunteerInfos.get(0), "1");
		fillOBX(orderObservation, "TVI2", "VOLUNTEER 2", message, volunteerInfos.get(1), "2");
		fillOBX(orderObservation, "TVI3", "VOLUNTEER NOTES", message, volunteerInfos.get(2), "3");
	
		Parser parser = new PipeParser(); 
		return parser.encode(message);		 
	}
	
	 public static void fillOBX(ORU_R01_ORDER_OBSERVATION orderObservation, String type, String desc, 
			 ORU_R01 message, String val, String index)throws HL7Exception
	 {
		//Populate OBX	
		 int ind = Integer.parseInt(index);
		 ORU_R01_OBSERVATION observation = orderObservation.getOBSERVATION(ind-1);	 
		 OBX obx = observation.getOBX();
		 obx.getSetIDOBX().setValue(index);
		 obx.getValueType().setValue("ST");
		 obx.getObservationIdentifier().getIdentifier().setValue(type);
		 obx.getObservationIdentifier().getText().setValue(desc);
		 
		 TX tx = new TX(message);
         tx.setValue(val);
         
         Varies value = obx.getObservationValue(0);         
         value.setData(tx); 
	 }
	 
	 public static void fillOBR(int index, ORU_R01_ORDER_OBSERVATION orderObservation,String obrName, 
			 int patientId, String obrDate, User provider) throws HL7Exception
	 {
		//populate OBR
		 OBR obr = orderObservation.getOBR();
		 obr.getSetIDObservationRequest().setValue(String.valueOf(index));
		 obr.getObr6_RequestedDateTime().getTimeOfAnEvent().setValue(obrDate);
		 obr.getObr7_ObservationDateTime().getTimeOfAnEvent().setValue(obrDate);
		 obr.getPlacerOrderNumber(0).getEi1_EntityIdentifier().setValue("TR" + patientId);
		 obr.getOrderingProvider(0).getAssigningAuthority().getUniversalID().setValue("Tapestry");
		 obr.getUniversalServiceIdentifier().getText().setValue(obrName);
		 //provider Id number--- provider billing#,  Oscar will map it to that provider(s) inbox.
		 obr.getOrderingProvider(0).getIDNumber().setValue("0001918");
		 obr.getOrderingProvider(0).getFamilyName().setValue(provider.getLastName());//family name
		 obr.getOrderingProvider(0).getGivenName().setValue(provider.getFirstName());//first name
			 
	 }
	 
	 public static boolean save(String message, String appendix) throws HL7Exception
	 {				 
	        String fileName = "TR" + appendix + ".hl7";
	        String saveDir = "webapps/hl7/";	        		 	        
	   
	        try {
	        	File directory = new File(saveDir);
	        	if (!directory.exists())
	        		directory.mkdir();	        	
	        	
	        	FileWriter fw = new FileWriter(saveDir + fileName, false);
	        	BufferedWriter out = new BufferedWriter(fw);
	        	out.write(message);
	        	out.close();
	        } catch (IOException e) {
	        	System.out.println("exception:====  " +e.getMessage());
				return false;
	        }		        
	        return true;
		}
	 
	 /*
	  *	Encrypts the message so it can be transfered securely
	  */
	 public static File encryptFile(InputStream is, SecretKey skey, String fileName) throws Exception{
		 fileName = fileName+".enc";
		 //Encode file
		 try{
			 OutputStream fos = new FileOutputStream(fileName);
			 byte[] buf = new byte[1024];

			 SecretKeySpec skeySpec = new SecretKeySpec(skey.getEncoded(), "AES");
			 Cipher cipher = Cipher.getInstance("AES");
			 cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			 fos = new CipherOutputStream(fos, cipher);

			 // Read in the cleartext bytes and write to out to encrypt
			 int numRead = 0;
			 while ((numRead = is.read(buf)) >= 0) {
				 fos.write(buf, 0, numRead);
			 }
			 fos.close();

		 }catch(Exception e){
			 throw e;
		 }	
		 return(new File(fileName));				
	 }
	 
	 /*
	  *	Creates the secret key used to encrypt the message
	  */
	public static SecretKey createSecretKey() throws Exception {
		// Create key for ecryption
		try{
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			return(kgen.generateKey());
		}catch(Exception e){
			//return(null);
			throw e;		
		}
	}
	
	public  void test() throws Exception{		
		ClassPathResource propertyFile = new ClassPathResource("keyLocation.properties");
	    InputStream inputStream = this.getClass().getClassLoader()  
	              .getResourceAsStream("/keyLocation.properties");  	      
	      List keyPairInfo = new ArrayList();   

	      keyPairInfo = parseKeyFile(inputStream);
	}
	
	ArrayList parseKeyFile(InputStream input) throws Exception{
		String serviceName = null;
		String privateKey = null;
		String publicKey = null;
		PrivateKey privKey = null;
		PublicKey pubKey = null;

		Base64 base64 = new Base64();
		ArrayList keyInfo = new ArrayList();

		try{
	//		InputStream input = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(input));

	        String line = null;		
			int lineCount = 0;
			while ((line = br.readLine()) != null) {
			
				if (lineCount == 0)
					serviceName = line;				
				else if (lineCount == 1)
					privateKey = line;
				else if (lineCount == 2)
					publicKey = line;
				
			    lineCount++;
			}
			System.out.println("ServiceName "+serviceName);
			System.out.println("privateKey "+privateKey);
			System.out.println("publicKey "+publicKey);
			
			Security.addProvider(new BouncyCastleProvider());
			//create private key from string          
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(base64.decode(privateKey.getBytes("ASCII")));
      		KeyFactory privKeyFactory = KeyFactory.getInstance("RSA");
			privKey = privKeyFactory.generatePrivate(privKeySpec);

			//create public key from string
//			java.security.Security.addProvider(
//			         new org.bouncycastle.jce.provider.BouncyCastleProvider()
//			);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(base64.decode(publicKey.getBytes("ASCII")));
			KeyFactory pubKeyFactory = KeyFactory.getInstance("RSA");
			pubKey = pubKeyFactory.generatePublic(pubKeySpec);

			keyInfo.add(serviceName);
			keyInfo.add(privKey);
			keyInfo.add(pubKey);
		}catch(Exception e){
			System.out.println("Could not get key info from file : "+e);
            e.printStackTrace();
           
			throw e;
		}
		return(keyInfo);
	}
		
	public PublicKey generateKeys() throws Exception{
		try{
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			PublicKey publicKey = kp.getPublic();
			PrivateKey privateKey = kp.getPrivate();
			return publicKey;
		}
		catch(Exception e){
			System.out.println("Could not get key info from file ' "+e);
			e.printStackTrace();
			throw e;
		}
	}
	
	private static String getStringFromList(List<String> list)
	{
		StringBuilder sb = new StringBuilder();
		for (String str : list ) 
		{
		    sb.append(str.toString());
		    sb.append(";");
		}
		
		return sb.toString();
	}
	
}
