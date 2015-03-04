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
		ORU_R01 message = new ORU_R01();
		//format obr date
		Timestamp currentTime = new Timestamp(new java.util.Date().getTime());			
		String orbDate = currentTime.toString().substring(0,10);
		orbDate = orbDate.replace("-", "");		
		User user = report.getUser();
		
		// Populate the MSH Segment
		ca.uhn.hl7v2.model.v23.segment.MSH mshSegment = message.getMSH();
		
		mshSegment.getFieldSeparator().setValue("|");
		mshSegment.getEncodingCharacters().setValue("^~\\&");
		mshSegment.getSendingApplication().getNamespaceID().setValue("Tapestry Reports");
	//	mshSegment.getSendingFacility().getNamespaceID().setValue("CML");
		mshSegment.getMessageType().getCm_msg1_MessageType().setValue("ORU");
		mshSegment.getMessageType().getCm_msg2_TriggerEvent().setValue("R01");
		mshSegment.getProcessingID().getPt1_ProcessingID().setValue("1");
		mshSegment.getVersionID().setValue("2.3");	
		mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentTime);
				 
		ca.uhn.hl7v2.model.v23.segment.PID pid = message.getRESPONSE().getPATIENT().getPID();
		pid.getAlternatePatientID().getID().setValue("1");
				 
		Patient p = report.getPatient();
		int patientId = p.getPatientID();
		String sex = p.getGender().substring(0,1);
				 
		pid.getSex().setValue(sex);//sex	
		pid.getPatientIDInternalID(0).getID().setValue(String.valueOf(patientId));//patientId	
		pid.getPatientName().getFamilyName().setValue(p.getLastName());//last name		
		pid.getPatientName().getGivenName().setValue(p.getFirstName());//first name
		pid.getDateOfBirth().getTimeOfAnEvent().setValue(p.getBod());// birth date	
		pid.getDateOfBirth().getDegreeOfPrecision().setValue(p.getBod()); // birth date	
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
		pv.getAttendingDoctor(0).getIDNumber().setValue(p.getMrpFirstName() + " " + p.getMrpLastName()); //mrp
		pv.getAdmitDateTime().getDegreeOfPrecision().setValue(a.getDate() +"|" + a.getTime());
		
		//ORC Segment
		ca.uhn.hl7v2.model.v23.segment.ORC orc = message.getRESPONSE().getORDER_OBSERVATION().getORC();
		orc.getOrc1_OrderControl().setValue("NW");
		orc.getOrc2_PlacerOrderNumber(0).getUniversalID().setValue("TR" + patientId);
		orc.getOrc5_OrderStatus().setValue("F");
		orc.getOrc12_OrderingProvider(0).getAssigningAuthority().getUniversalID().setValue("Tapestry");//provider organization
//		orc.getOrc12_OrderingProvider(0).getIDNumber().setValue("05808");//provider Id number
		orc.getOrc12_OrderingProvider(0).getFamilyName().setValue("Tapestry FamilyN");//family name
		orc.getOrc12_OrderingProvider(0).getGivenName().setValue("Tapestry FirstN");//first name
		orc.getOrc15_OrderEffectiveDateTime().getTimeOfAnEvent().setValue(orbDate);	
	
		/*
		 * The OBR segment is contained within a group called ORDER_OBSERVATION, 
		 * which is itself in a group called PATIENT_RESULT. These groups are
		 * reached using named accessors.
		 */
		//patient goals goals survey Q8
		List<String> goalsList = report.getPatientGoals();
		ORU_R01_ORDER_OBSERVATION orderObservation = message.getRESPONSE().getORDER_OBSERVATION(0);		
		fillOBXAndOBRField(1, goalsList.get(0), orderObservation, message, "TPLG", patientId, orbDate, "x33001", "PATIENT GOALS");
	//	fillOBXAndOBRField(1, goalsList.get(0), orderObservation, message, "TPLG", patientId, orbDate, "6652-2", "PATIENT GOALS");
		      
		//For case Review with IP-TEAM                 
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(1);
		
		List<String> list = new ArrayList<String>();
		list = report.getAlerts(); 
		String[] alerts = list.toArray(new String[0]);
		fillOBXAndOBRField(2, alerts, orderObservation, message, "TPCR", alerts.length, patientId, orbDate, "x33002", "KEY OBSERVATION"); 
		         
		//Social Context
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(2);  
		String keyObservation = a.getKeyObservation();
//		String keyObservation = p.getKeyObservation();
		fillOBXAndOBRField(3, keyObservation, orderObservation, message, "TPSC", patientId, orbDate, "x33003", "SOCIAL CONTEXT"); 
		         
		//volunteer follow up plan
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(3);   
		String[] plans;
//		
		if (a.getPlans() != null && a.getPlans() != "") 
			plans = a.getPlans().split(";");     
		else
			plans = new String[]{""};
		fillOBXAndOBRField(4, plans, orderObservation, message, "TPVP", plans.length, patientId, orbDate, "x33004", "VOLUNTEER PLANS"); 
				         
		//memory screen        
		list = report.getAdditionalInfos(); 
		if (list.size() >= 2)
		{
			orderObservation = message.getRESPONSE().getORDER_OBSERVATION(4);
			String[] memorys = new String[]{list.get(0), list.get(1)};
			fillOBXAndOBRField(5, memorys, orderObservation, message, "TPMS", 2, patientId, orbDate, "x33005", "MEMORY SCREEN"); 
		}          
		//advance directives
		if (list.size() >=5)
		{
			orderObservation = message.getRESPONSE().getORDER_OBSERVATION(5);
			String[] aDirectives = {list.get(2), list.get(3), list.get(4)};
			fillOBXAndOBRField(6, aDirectives, orderObservation, message, "TPAD", 3, patientId, orbDate, "x33006", "ADVANCED DIRECTIVES");
		}               
		//Summary of tapestry tools
		//function status
		ScoresInReport scores = report.getScores();
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(6);
		String[] functionStatus= new String[]{scores.getClockDrawingTest(), scores.getTimeUpGoTest(), scores.getEdmontonFrailScale()};
		fillOBXAndOBRField(7, functionStatus, orderObservation, message, "TPFS", 3, patientId, orbDate, "x33007", "FUNCTIONAL STATUS");
		
		//nutritional status
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(7);     
		fillOBXAndOBRField(8, String.valueOf(scores.getNutritionScreen()), orderObservation, message, "TPNS", patientId, orbDate, "x33008", "NUTRITIONAL STATUS");
		
		//social supports
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(8);
		String[] socialSupport= new String[]{String.valueOf(scores.getSocialSatisfication()), String.valueOf(scores.getSocialNetwork())};
		fillOBXAndOBRField(9, socialSupport, orderObservation, message, "TPSS", 2, patientId, orbDate, "x33009", "SOCIAL SUPPORT");
		
		//mobility
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(9);
		String[] mobility= new String[]{scores.getMobilityWalking2(),scores.getMobilityWalkingHalf(), scores.getMobilityClimbing()};
		fillOBXAndOBRField(10, mobility, orderObservation, message, "TPMB", 3, patientId, orbDate, "x330010", "MOBILITY");
		         
		//physical activity
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(10);
//		String[] physicalActivity= new String[]{String.valueOf(scores.getpAAerobic()),String.valueOf(scores.getpAStrengthAndFlexibility())};
		String[] physicalActivity= new String[]{String.valueOf(scores.getAerobicMessage()),String.valueOf(scores.getpAStrengthAndFlexibility())};
		fillOBXAndOBRField(11, physicalActivity, orderObservation, message, "TPPA", 2, patientId, orbDate, "x330011", "PHYSICAL ACTIVITY");
		//end of summary tools
		                  
		//Three goals from Goals survey Q3
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(11);
		
		int goalsSize = goalsList.size();
		String[] threeGoals = new String[goalsSize -1] ;
		for (int i = 1; i < goalsSize; i++)
			threeGoals[i-1] = goalsList.get(i);
		fillOBXAndOBRField(12, threeGoals, orderObservation, message, "TPHG", goalsSize-1, patientId, orbDate, "x330012", "HEALTHY GOALS"); 
		
		//Tapesty questions
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(12);
		String[] questions = report.getDailyActivities().toArray(new String[0]);  
		fillOBXAndOBRField(13, questions, orderObservation, message, "TPTQ", questions.length, patientId,  orbDate, "x330013", "TAPESTRY QUESTIONS");
		         
		//volunteer infor
		orderObservation = message.getRESPONSE().getORDER_OBSERVATION(13);
		String[] volunteerInfos = report.getVolunteerInformations().toArray(new String[0]);;
		fillOBXAndOBRField(14, volunteerInfos, orderObservation, message, "TPVI", volunteerInfos.length, patientId,  orbDate, "x330014", "VOLUNTEER INFORMATION");

		Parser parser = new PipeParser(); 
		return parser.encode(message);		 
	}

	 public static void fillOBXAndOBRField(int index, String str, ORU_R01_ORDER_OBSERVATION orderObservation, 
			 ORU_R01 message, String tagName, int patientId, String obrDate, String lonicCode, String lonicDes) throws HL7Exception
	{
		 //populate OBR
		 fillOBR(index, orderObservation, tagName, patientId, obrDate);
		 
		 //Populate OBX
		 ORU_R01_OBSERVATION observation = orderObservation.getOBSERVATION(0);		 
		 OBX obx = observation.getOBX();
		 obx.getSetIDOBX().setValue("1");
		 obx.getValueType().setValue("ST");
		 obx.getObservationIdentifier().getIdentifier().setValue(lonicCode);		 
		 obx.getObservationIdentifier().getText().setValue(lonicDes);
		 
		 TX tx = new TX(message);
         tx.setValue(str);
         
         Varies value = obx.getObservationValue(0);         
         value.setData(tx); 
	 }
	 
	 public static void fillOBXAndOBRField(int index, String[] str, ORU_R01_ORDER_OBSERVATION orderObservation, 
			 ORU_R01 message, String tagName, int numberOfOBX, int patientId, String obrDate, String lonicCode, String lonicDes) throws HL7Exception
	{
		//populate OBR
		 fillOBR(index, orderObservation, tagName , patientId, obrDate);
		 
		//Populate OBX
		 ORU_R01_OBSERVATION observation;
		 OBX obx;
		 for (int i = 1; i< numberOfOBX +1; i++)
		 {
			 observation = orderObservation.getOBSERVATION(i-1);		 
			 obx = observation.getOBX();
			 obx.getSetIDOBX().setValue(String.valueOf(i));
			 obx.getValueType().setValue("ST");
			 obx.getObservationIdentifier().getIdentifier().setValue(lonicCode);
			 obx.getObservationIdentifier().getText().setValue(lonicDes);
			 TX tx = new TX(message);
	         tx.setValue(str[i-1]);
	         
	         Varies value = obx.getObservationValue(0);
	         value.setData(tx);
		 }
	}
	 
	 public static void fillOBR(int index, ORU_R01_ORDER_OBSERVATION orderObservation,String tagName, 
			 int patientId, String obrDate) throws HL7Exception
	 {
		//populate OBR
		 OBR obr = orderObservation.getOBR();
		 obr.getSetIDObservationRequest().setValue(String.valueOf(index));
		 obr.getObr6_RequestedDateTime().getTimeOfAnEvent().setValue(obrDate);
		 obr.getObr7_ObservationDateTime().getTimeOfAnEvent().setValue(obrDate);
		 obr.getPlacerOrderNumber(0).getEi1_EntityIdentifier().setValue("TR" + patientId);
		 obr.getOrderingProvider(0).getAssigningAuthority().getUniversalID().setValue("Tapestry");
		 obr.getUniversalServiceIdentifier().getText().setValue("OBR Name");
		 //provider Id number--- provider billing#,  Oscar will map it to that provider(s) inbox.
		 obr.getOrderingProvider(0).getIDNumber().setValue("0001918");
		 obr.getOrderingProvider(0).getFamilyName().setValue("Tapestry");//family name
		 obr.getOrderingProvider(0).getGivenName().setValue("tester");//first name
		 obr.getPlacerField1().setValue(tagName);		 
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
	
}
