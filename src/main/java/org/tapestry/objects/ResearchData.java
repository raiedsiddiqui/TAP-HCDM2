package org.tapestry.objects;

public class ResearchData {
	private int patientId;
	private String researchId;
	//survey ED5Q quality of life
	private int qol1;
	private int qol2;
	private int qol3;
	private int qol4;
	private int qol5;
	private int qol6;
	//survey social life
	private String dSS1_role_TO;
	private String dSS2_under_TO;
	private String dSS3_useful_TO;
	private String dSS4_listen_TO;
	private String dSS5_happen_TO;
	private String dSS6_talk_TO;
	private String dSS7_satisfied_TO;
	private String dSS8_nofam_TO;
	private String dSS9_timesnotliving_TO;
	private String dSS10_timesphone_TO;
	private String dSS11_timesclubs_TO;
	private String dSS_notes_TO;
	
	//survey goals
	private String goals1Matter_TO;
	private String goals2Life_TO;
	private String goals3Health_TO;
	private String goals4List_TO;
	private String goals5FirstSpecific_TO;
	private String goals6FirstBaseline_TO;
	private String goals7FirstTaget_TO;
	private String goals5SecondSpecific_TO;
	private String goals6SecondBaseline_TO;
	private String goals7SecondTaget_TO;
	private String goals5ThirdSpecific_TO;
	private String goals6ThirdBaseline_TO;
	private String goals7ThirdTaget_TO;
	private String goals8Priority_TO;
	private String goalsDiscussion_notes_TO;
	
	//survey daily life active
	private String dla1;	
	private String dla2;
	private String dla3;
	private String dla4;
	private String dla5;
	private String dla6;
	private String dla7;
	private String dla7a;
	//RAPA
	private int rapa1;
	private int rapa2;
	private int rapa3;
	private int rapa4;
	private int rapa5;
	private int rapa6;
	private int rapa7;
	private int rapa8;
	private int rapa9;
	//memory
	private int mem1;
	private String mem2;
	private int mem3;
	private String mem4;
	//advance directive
	private int ad1;
	private int ad2;
	private int ad3;
	//general health
	private int gh1;
	private int gh2;
	private int gh3;
	private int gh4;
	private int gh5;
	private int gh6;
	private int gh7;
	private int gh8;
	private int gh9;
	private int gh10;
	private int gh11;
	//mobility
	private int mob1;
	private String mob2;
	private int mob21;
	private int mob22;
	private int mob23;
	private int mob24;
	private int mob25;
	private int mob26;
	private int mob3;
	private String mob4;
	private int mob41;
	private int mob42;
	private int mob43;
	private int mob44;
	private int mob45;
	private int mob46;
	private int mob5;
	private String mob6;
	private int mob61;
	private int mob62;
	private int mob63;
	private int mob64;
	private int mob65;
	private int mob66;
	//nutrition
	private int nut1;
	private int nut2;
	private int nut3;
	private int nut4;
	private int nut5;
	private int nut6;
	private int nut7;
	private int nut8;
	private int nut9;
	private int nut10;
	private int nut11;
	private int nut12;
	private int nut13;
	private int nut14;
	private int nut15;
	private int nut16;
	private int nut17;
	//3 month follow up
	private int fu1;
	private int fu2;
	private String fu3;
	private int fu5;
	private int fu6;
	private String fu7;
	private int fu8;
	private String fu9;
	private int fu11;
	private String fu12, fu13, fu14;
	private int fu15;
	private String fu16, fu17, fu18;
	private int fu19;
	private String fu20, fu21, fu22;
	
	public ResearchData(){
		
	}
	
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	
	public String getResearchId() {
		return researchId;
	}

	public void setResearchId(String researchId) {
		this.researchId = researchId;
	}

	public String getdSS1_role_TO() {
		return dSS1_role_TO;
	}
	public void setdSS1_role_TO(String dSS1_role_TO) {
		this.dSS1_role_TO = dSS1_role_TO;
	}
	public String getdSS2_under_TO() {
		return dSS2_under_TO;
	}
	public void setdSS2_under_TO(String dSS2_under_TO) {
		this.dSS2_under_TO = dSS2_under_TO;
	}
	public String getdSS3_useful_TO() {
		return dSS3_useful_TO;
	}
	public void setdSS3_useful_TO(String dSS3_useful_TO) {
		this.dSS3_useful_TO = dSS3_useful_TO;
	}
	public String getdSS4_listen_TO() {
		return dSS4_listen_TO;
	}
	public void setdSS4_listen_TO(String dSS4_listen_TO) {
		this.dSS4_listen_TO = dSS4_listen_TO;
	}
	public String getdSS5_happen_TO() {
		return dSS5_happen_TO;
	}
	public void setdSS5_happen_TO(String dSS5_happen_TO) {
		this.dSS5_happen_TO = dSS5_happen_TO;
	}
	public String getdSS6_talk_TO() {
		return dSS6_talk_TO;
	}
	public void setdSS6_talk_TO(String dSS6_talk_TO) {
		this.dSS6_talk_TO = dSS6_talk_TO;
	}
	public String getdSS7_satisfied_TO() {
		return dSS7_satisfied_TO;
	}
	public void setdSS7_satisfied_TO(String dSS7_satisfied_TO) {
		this.dSS7_satisfied_TO = dSS7_satisfied_TO;
	}
	public String getdSS8_nofam_TO() {
		return dSS8_nofam_TO;
	}
	public void setdSS8_nofam_TO(String dSS8_nofam_TO) {
		this.dSS8_nofam_TO = dSS8_nofam_TO;
	}
	public String getdSS9_timesnotliving_TO() {
		return dSS9_timesnotliving_TO;
	}
	public void setdSS9_timesnotliving_TO(String dSS9_timesnotliving_TO) {
		this.dSS9_timesnotliving_TO = dSS9_timesnotliving_TO;
	}
	public String getdSS10_timesphone_TO() {
		return dSS10_timesphone_TO;
	}
	public void setdSS10_timesphone_TO(String dSS10_timesphone_TO) {
		this.dSS10_timesphone_TO = dSS10_timesphone_TO;
	}
	public String getdSS11_timesclubs_TO() {
		return dSS11_timesclubs_TO;
	}
	public void setdSS11_timesclubs_TO(String dSS11_timesclubs_TO) {
		this.dSS11_timesclubs_TO = dSS11_timesclubs_TO;
	}
	public String getdSS_notes_TO() {
		return dSS_notes_TO;
	}
	public void setdSS_notes_TO(String dSS_notes_TO) {
		this.dSS_notes_TO = dSS_notes_TO;
	}
	public String getGoals1Matter_TO() {
		return goals1Matter_TO;
	}
	public void setGoals1Matter_TO(String goals1Matter_TO) {
		this.goals1Matter_TO = goals1Matter_TO;
	}
	public String getGoals2Life_TO() {
		return goals2Life_TO;
	}
	public void setGoals2Life_TO(String goals2Life_TO) {
		this.goals2Life_TO = goals2Life_TO;
	}
	public String getGoals3Health_TO() {
		return goals3Health_TO;
	}
	public void setGoals3Health_TO(String goals3Health_TO) {
		this.goals3Health_TO = goals3Health_TO;
	}
	public String getGoals4List_TO() {
		return goals4List_TO;
	}
	public void setGoals4List_TO(String goals4List_TO) {
		this.goals4List_TO = goals4List_TO;
	}
	public String getGoals5FirstSpecific_TO() {
		return goals5FirstSpecific_TO;
	}
	public void setGoals5FirstSpecific_TO(String goals5FirstSpecific_TO) {
		this.goals5FirstSpecific_TO = goals5FirstSpecific_TO;
	}
	public String getGoals6FirstBaseline_TO() {
		return goals6FirstBaseline_TO;
	}
	public void setGoals6FirstBaseline_TO(String goals6FirstBaseline_TO) {
		this.goals6FirstBaseline_TO = goals6FirstBaseline_TO;
	}
	public String getGoals7FirstTaget_TO() {
		return goals7FirstTaget_TO;
	}
	public void setGoals7FirstTaget_TO(String goals7FirstTaget_TO) {
		this.goals7FirstTaget_TO = goals7FirstTaget_TO;
	}
	public String getGoals5SecondSpecific_TO() {
		return goals5SecondSpecific_TO;
	}
	public void setGoals5SecondSpecific_TO(String goals5SecondSpecific_TO) {
		this.goals5SecondSpecific_TO = goals5SecondSpecific_TO;
	}
	public String getGoals6SecondBaseline_TO() {
		return goals6SecondBaseline_TO;
	}
	public void setGoals6SecondBaseline_TO(String goals6SecondBaseline_TO) {
		this.goals6SecondBaseline_TO = goals6SecondBaseline_TO;
	}
	public String getGoals7SecondTaget_TO() {
		return goals7SecondTaget_TO;
	}
	public void setGoals7SecondTaget_TO(String goals7SecondTaget_TO) {
		this.goals7SecondTaget_TO = goals7SecondTaget_TO;
	}
	public String getGoals5ThirdSpecific_TO() {
		return goals5ThirdSpecific_TO;
	}
	public void setGoals5ThirdSpecific_TO(String goals5ThirdSpecific_TO) {
		this.goals5ThirdSpecific_TO = goals5ThirdSpecific_TO;
	}
	public String getGoals6ThirdBaseline_TO() {
		return goals6ThirdBaseline_TO;
	}
	public void setGoals6ThirdBaseline_TO(String goals6ThirdBaseline_TO) {
		this.goals6ThirdBaseline_TO = goals6ThirdBaseline_TO;
	}
	public String getGoals7ThirdTaget_TO() {
		return goals7ThirdTaget_TO;
	}
	public void setGoals7ThirdTaget_TO(String goals7ThirdTaget_TO) {
		this.goals7ThirdTaget_TO = goals7ThirdTaget_TO;
	}
	public String getGoals8Priority_TO() {
		return goals8Priority_TO;
	}
	public void setGoals8Priority_TO(String goals8pPriority_TO) {
		this.goals8Priority_TO = goals8pPriority_TO;
	}
	public String getGoalsDiscussion_notes_TO() {
		return goalsDiscussion_notes_TO;
	}
	public void setGoalsDiscussion_notes_TO(String goalsDiscussion_notes_TO) {
		this.goalsDiscussion_notes_TO = goalsDiscussion_notes_TO;
	}
	public String getDla1() {
		return dla1;
	}

	public void setDla1(String dla1) {
		this.dla1 = dla1;
	}

	public String getDla2() {
		return dla2;
	}

	public void setDla2(String dla2) {
		this.dla2 = dla2;
	}

	public String getDla3() {
		return dla3;
	}

	public void setDla3(String dla3) {
		this.dla3 = dla3;
	}

	public String getDla4() {
		return dla4;
	}

	public void setDla4(String dla4) {
		this.dla4 = dla4;
	}

	public String getDla5() {
		return dla5;
	}

	public void setDla5(String dla5) {
		this.dla5 = dla5;
	}

	public String getDla6() {
		return dla6;
	}

	public void setDla6(String dla6) {
		this.dla6 = dla6;
	}

	public String getDla7() {
		return dla7;
	}

	public void setDla7(String dla7) {
		this.dla7 = dla7;
	}

	public String getDla7a() {
		return dla7a;
	}

	public void setDla7a(String dla7a) {
		this.dla7a = dla7a;
	}

	public int getQol1() {
		return qol1;
	}

	public void setQol1(int qol1) {
		this.qol1 = qol1;
	}

	public int getQol2() {
		return qol2;
	}

	public void setQol2(int qol2) {
		this.qol2 = qol2;
	}

	public int getQol3() {
		return qol3;
	}

	public void setQol3(int qol3) {
		this.qol3 = qol3;
	}

	public int getQol4() {
		return qol4;
	}

	public void setQol4(int qol4) {
		this.qol4 = qol4;
	}

	public int getQol5() {
		return qol5;
	}

	public void setQol5(int qol5) {
		this.qol5 = qol5;
	}

	public int getQol6() {
		return qol6;
	}

	public void setQol6(int qol6) {
		this.qol6 = qol6;
	}

	public int getRapa1() {
		return rapa1;
	}

	public void setRapa1(int rapa1) {
		this.rapa1 = rapa1;
	}

	public int getRapa2() {
		return rapa2;
	}

	public void setRapa2(int rapa2) {
		this.rapa2 = rapa2;
	}

	public int getRapa3() {
		return rapa3;
	}

	public void setRapa3(int rapa3) {
		this.rapa3 = rapa3;
	}

	public int getRapa4() {
		return rapa4;
	}

	public void setRapa4(int rapa4) {
		this.rapa4 = rapa4;
	}

	public int getRapa5() {
		return rapa5;
	}

	public void setRapa5(int rapa5) {
		this.rapa5 = rapa5;
	}

	public int getRapa6() {
		return rapa6;
	}

	public void setRapa6(int rapa6) {
		this.rapa6 = rapa6;
	}

	public int getRapa7() {
		return rapa7;
	}

	public void setRapa7(int rapa7) {
		this.rapa7 = rapa7;
	}

	public int getRapa8() {
		return rapa8;
	}

	public void setRapa8(int rapa8) {
		this.rapa8 = rapa8;
	}

	public int getRapa9() {
		return rapa9;
	}

	public void setRapa9(int rapa9) {
		this.rapa9 = rapa9;
	}

	public int getMem1() {
		return mem1;
	}

	public void setMem1(int mem1) {
		this.mem1 = mem1;
	}

	public String getMem2() {
		return mem2;
	}

	public void setMem2(String mem2) {
		this.mem2 = mem2;
	}

	public int getMem3() {
		return mem3;
	}

	public void setMem3(int mem3) {
		this.mem3 = mem3;
	}

	public String getMem4() {
		return mem4;
	}

	public void setMem4(String mem4) {
		this.mem4 = mem4;
	}

	public int getAd1() {
		return ad1;
	}

	public void setAd1(int ad1) {
		this.ad1 = ad1;
	}

	public int getAd2() {
		return ad2;
	}

	public void setAd2(int ad2) {
		this.ad2 = ad2;
	}

	public int getAd3() {
		return ad3;
	}

	public void setAd3(int ad3) {
		this.ad3 = ad3;
	}

	public int getGh1() {
		return gh1;
	}

	public void setGh1(int gh1) {
		this.gh1 = gh1;
	}

	public int getGh2() {
		return gh2;
	}

	public void setGh2(int gh2) {
		this.gh2 = gh2;
	}

	public int getGh3() {
		return gh3;
	}

	public void setGh3(int gh3) {
		this.gh3 = gh3;
	}

	public int getGh4() {
		return gh4;
	}

	public void setGh4(int gh4) {
		this.gh4 = gh4;
	}

	public int getGh5() {
		return gh5;
	}

	public void setGh5(int gh5) {
		this.gh5 = gh5;
	}

	public int getGh6() {
		return gh6;
	}

	public void setGh6(int gh6) {
		this.gh6 = gh6;
	}

	public int getGh7() {
		return gh7;
	}

	public void setGh7(int gh7) {
		this.gh7 = gh7;
	}

	public int getGh8() {
		return gh8;
	}

	public void setGh8(int gh8) {
		this.gh8 = gh8;
	}

	public int getGh9() {
		return gh9;
	}

	public void setGh9(int gh9) {
		this.gh9 = gh9;
	}

	public int getGh10() {
		return gh10;
	}

	public void setGh10(int gh10) {
		this.gh10 = gh10;
	}

	public int getGh11() {
		return gh11;
	}

	public void setGh11(int gh11) {
		this.gh11 = gh11;
	}

	public int getMob1() {
		return mob1;
	}

	public void setMob1(int mob1) {
		this.mob1 = mob1;
	}

	public String getMob2() {
		return mob2;
	}

	public void setMob2(String mob2) {
		this.mob2 = mob2;
	}

	public int getMob3() {
		return mob3;
	}

	public void setMob3(int mob3) {
		this.mob3 = mob3;
	}

	public String getMob4() {
		return mob4;
	}

	public void setMob4(String mob4) {
		this.mob4 = mob4;
	}

	public int getMob5() {
		return mob5;
	}

	public void setMob5(int mob5) {
		this.mob5 = mob5;
	}

	public String getMob6() {
		return mob6;
	}

	public void setMob6(String mob6) {
		this.mob6 = mob6;
	}	

	public int getMob21() {
		return mob21;
	}

	public void setMob21(int mob21) {
		this.mob21 = mob21;
	}

	public int getMob22() {
		return mob22;
	}

	public void setMob22(int mob22) {
		this.mob22 = mob22;
	}

	public int getMob23() {
		return mob23;
	}

	public void setMob23(int mob23) {
		this.mob23 = mob23;
	}

	public int getMob24() {
		return mob24;
	}

	public void setMob24(int mob24) {
		this.mob24 = mob24;
	}

	public int getMob25() {
		return mob25;
	}

	public void setMob25(int mob25) {
		this.mob25 = mob25;
	}

	public int getMob26() {
		return mob26;
	}

	public void setMob26(int mob26) {
		this.mob26 = mob26;
	}

	public int getMob41() {
		return mob41;
	}

	public void setMob41(int mob41) {
		this.mob41 = mob41;
	}

	public int getMob42() {
		return mob42;
	}

	public void setMob42(int mob42) {
		this.mob42 = mob42;
	}

	public int getMob43() {
		return mob43;
	}

	public void setMob43(int mob43) {
		this.mob43 = mob43;
	}

	public int getMob44() {
		return mob44;
	}

	public void setMob44(int mob44) {
		this.mob44 = mob44;
	}

	public int getMob45() {
		return mob45;
	}

	public void setMob45(int mob45) {
		this.mob45 = mob45;
	}

	public int getMob46() {
		return mob46;
	}

	public void setMob46(int mob46) {
		this.mob46 = mob46;
	}

	public int getMob61() {
		return mob61;
	}

	public void setMob61(int mob61) {
		this.mob61 = mob61;
	}

	public int getMob62() {
		return mob62;
	}

	public void setMob62(int mob62) {
		this.mob62 = mob62;
	}

	public int getMob63() {
		return mob63;
	}

	public void setMob63(int mob63) {
		this.mob63 = mob63;
	}

	public int getMob64() {
		return mob64;
	}

	public void setMob64(int mob64) {
		this.mob64 = mob64;
	}

	public int getMob65() {
		return mob65;
	}

	public void setMob65(int mob65) {
		this.mob65 = mob65;
	}

	public int getMob66() {
		return mob66;
	}

	public void setMob66(int mob66) {
		this.mob66 = mob66;
	}

	public int getNut1() {
		return nut1;
	}

	public void setNut1(int nut1) {
		this.nut1 = nut1;
	}

	public int getNut2() {
		return nut2;
	}

	public void setNut2(int nut2) {
		this.nut2 = nut2;
	}

	public int getNut3() {
		return nut3;
	}

	public void setNut3(int nut3) {
		this.nut3 = nut3;
	}

	public int getNut4() {
		return nut4;
	}

	public void setNut4(int nut4) {
		this.nut4 = nut4;
	}

	public int getNut5() {
		return nut5;
	}

	public void setNut5(int nut5) {
		this.nut5 = nut5;
	}

	public int getNut6() {
		return nut6;
	}

	public void setNut6(int nut6) {
		this.nut6 = nut6;
	}

	public int getNut7() {
		return nut7;
	}

	public void setNut7(int nut7) {
		this.nut7 = nut7;
	}

	public int getNut8() {
		return nut8;
	}

	public void setNut8(int nut8) {
		this.nut8 = nut8;
	}

	public int getNut9() {
		return nut9;
	}

	public void setNut9(int nut9) {
		this.nut9 = nut9;
	}

	public int getNut10() {
		return nut10;
	}

	public void setNut10(int nut10) {
		this.nut10 = nut10;
	}

	public int getNut11() {
		return nut11;
	}

	public void setNut11(int nut11) {
		this.nut11 = nut11;
	}

	public int getNut12() {
		return nut12;
	}

	public void setNut12(int nut12) {
		this.nut12 = nut12;
	}

	public int getNut13() {
		return nut13;
	}

	public void setNut13(int nut13) {
		this.nut13 = nut13;
	}

	public int getNut14() {
		return nut14;
	}

	public void setNut14(int nut14) {
		this.nut14 = nut14;
	}

	public int getNut15() {
		return nut15;
	}

	public void setNut15(int nut15) {
		this.nut15 = nut15;
	}

	public int getNut16() {
		return nut16;
	}

	public void setNut16(int nut16) {
		this.nut16 = nut16;
	}

	public int getNut17() {
		return nut17;
	}

	public void setNut17(int nut17) {
		this.nut17 = nut17;
	}

	public int getFu1() {
		return fu1;
	}

	public void setFu1(int fu1) {
		this.fu1 = fu1;
	}

	public int getFu2() {
		return fu2;
	}

	public void setFu2(int fu2) {
		this.fu2 = fu2;
	}

	public String getFu3() {
		return fu3;
	}

	public void setFu3(String fu3) {
		this.fu3 = fu3;
	}

	public int getFu5() {
		return fu5;
	}

	public void setFu5(int fu5) {
		this.fu5 = fu5;
	}

	public int getFu6() {
		return fu6;
	}

	public void setFu6(int fu6) {
		this.fu6 = fu6;
	}

	public String getFu7() {
		return fu7;
	}

	public void setFu7(String fu7) {
		this.fu7 = fu7;
	}

	public int getFu8() {
		return fu8;
	}

	public void setFu8(int fu8) {
		this.fu8 = fu8;
	}

	public String getFu9() {
		return fu9;
	}

	public void setFu9(String fu9) {
		this.fu9 = fu9;
	}

	public int getFu11() {
		return fu11;
	}

	public void setFu11(int fu11) {
		this.fu11 = fu11;
	}

	public String getFu12() {
		return fu12;
	}

	public void setFu12(String fu12) {
		this.fu12 = fu12;
	}

	public String getFu13() {
		return fu13;
	}

	public void setFu13(String fu13) {
		this.fu13 = fu13;
	}

	public String getFu14() {
		return fu14;
	}

	public void setFu14(String fu14) {
		this.fu14 = fu14;
	}

	public int getFu15() {
		return fu15;
	}

	public void setFu15(int fu15) {
		this.fu15 = fu15;
	}

	public String getFu16() {
		return fu16;
	}

	public void setFu16(String fu16) {
		this.fu16 = fu16;
	}

	public String getFu17() {
		return fu17;
	}

	public void setFu17(String fu17) {
		this.fu17 = fu17;
	}

	public String getFu18() {
		return fu18;
	}

	public void setFu18(String fu18) {
		this.fu18 = fu18;
	}

	public int getFu19() {
		return fu19;
	}

	public void setFu19(int fu19) {
		this.fu19 = fu19;
	}

	public String getFu20() {
		return fu20;
	}

	public void setFu20(String fu20) {
		this.fu20 = fu20;
	}

	public String getFu21() {
		return fu21;
	}

	public void setFu21(String fu21) {
		this.fu21 = fu21;
	}

	public String getFu22() {
		return fu22;
	}

	public void setFu22(String fu22) {
		this.fu22 = fu22;
	}
	
	
}
