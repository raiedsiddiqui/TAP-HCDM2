					
						<c:choose>
							<c:when test="${monDropPosition[0] eq 'null'}">
								<c:set var="monFrom1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="monFrom1" value="${monDropPosition[0]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${monDropPosition[1] eq 'null'}">
								<c:set var="monTo1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="monTo1" value="${monDropPosition[1]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${monDropPosition[2] eq 'null'}">
								<c:set var="monFrom2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="monFrom2" value="${monDropPosition[2]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${monDropPosition[3] eq 'null'}">
								<c:set var="monTo2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="monTo2" value="${monDropPosition[3]}"/>
							</c:otherwise>
						</c:choose>					
							<table>
								<tr>
									<td class="col-md-2"><h4>Monday <input type="checkbox" name="mondayNull" <c:if test="${mondayNull eq 'true'}">checked</c:if> value = "non"> N/A</h4></td>
									<td class="col-md-2"><h4>Tuesday <input type="checkbox" name="tuesdayNull" <c:if test="${tuesdayNull eq 'true'}">checked</c:if> value = "non"> N/A </h4></td>
									<td class="col-md-2"><h4>Wednesday <input type="checkbox" name="wednesdayNull" <c:if test="${wednesdayNull eq 'true'}">checked</c:if> value = "non"> N/A</h4></td>
									<td class="col-md-2"><h4>Thursday <input type="checkbox" name="thursdayNull" value = "non" <c:if test="${thursdayNull eq 'true'}">checked</c:if>> N/A</h4>	</td>
									<td class="col-md-2"><h4>Friday <input type="checkbox" name="fridayNull" value = "non" <c:if test="${fridayNull eq 'true'}">checked</c:if>> N/A</h4></td>
								</tr>
								<tr>
									<td class="col-md-2"><select name="monFrom1" >
										<option value="0" <c:if test="${monFrom1 eq '0'}">selected</c:if>>...</option>
										<option value="11" <c:if test="${monFrom1 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="12" <c:if test="${monFrom1 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="13" <c:if test="${monFrom1 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="14" <c:if test="${monFrom1 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="15" <c:if test="${monFrom1 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="16" <c:if test="${monFrom1 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="17" <c:if test="${monFrom1 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="18" <c:if test="${monFrom1 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="19" <c:if test="${monFrom1 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="110" <c:if test="${monFrom1 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="111" <c:if test="${monFrom1 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="112" <c:if test="${monFrom1 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="113" <c:if test="${monFrom1 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="114" <c:if test="${monFrom1 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="115" <c:if test="${monFrom1 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="116" <c:if test="${monFrom1 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="117" <c:if test="${monFrom1 eq '17'}">selected</c:if> >16:00 PM</option>
										<option value="118" <c:if test="${monFrom1 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="119" <c:if test="${monFrom1 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="120" <c:if test="${monFrom1 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="121" <c:if test="${monFrom1 eq '21'}">selected</c:if> >18:00 PM</option>
									</select> TO <select name="monTo1" >
										<option value="0" <c:if test="${monTo1 eq '0'}">selected</c:if>>...</option>
										<option value="11" <c:if test="${monTo1 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="12" <c:if test="${monTo1 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="13" <c:if test="${monTo1 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="14" <c:if test="${monTo1 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="15" <c:if test="${monTo1 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="16" <c:if test="${monTo1 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="17" <c:if test="${monTo1 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="18" <c:if test="${monTo1 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="19" <c:if test="${monTo1 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="110" <c:if test="${monTo1 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="111" <c:if test="${monTo1 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="112" <c:if test="${monTo1 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="113" <c:if test="${monTo1 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="114" <c:if test="${monTo1 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="115" <c:if test="${monTo1 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="116" <c:if test="${monTo1 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="117" <c:if test="${monTo1 eq '17'}">selected</c:if> >16:00 PM</option>
										<option value="118" <c:if test="${monTo1 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="119" <c:if test="${monTo1 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="120" <c:if test="${monTo1 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="121" <c:if test="${monTo1 eq '21'}">selected</c:if> >18:00 PM</option>
									</select>
									<select name="monFrom2" >
										<option value="0" <c:if test="${monFrom2 eq '0'}">selected</c:if>>...</option>
										<option value="11" <c:if test="${monFrom2 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="12" <c:if test="${monFrom2 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="13" <c:if test="${monFrom2 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="14" <c:if test="${monFrom2 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="15" <c:if test="${monFrom2 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="16" <c:if test="${monFrom2 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="17" <c:if test="${monFrom2 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="18" <c:if test="${monFrom2 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="19" <c:if test="${monFrom2 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="110" <c:if test="${monFrom2 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="111" <c:if test="${monFrom2 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="112" <c:if test="${monFrom2 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="113" <c:if test="${monFrom2 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="114" <c:if test="${monFrom2 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="115" <c:if test="${monFrom2 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="116" <c:if test="${monFrom2 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="117" <c:if test="${monFrom2 eq '17'}">selected</c:if> >16:00 PM</option>
										<option value="118" <c:if test="${monFrom2 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="119" <c:if test="${monFrom2 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="120" <c:if test="${monFrom2 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="121" <c:if test="${monFrom2 eq '21'}">selected</c:if> >18:00 PM</option>
									</select> TO <select name="monTo2" >
										<option value="0" <c:if test="${monTo2 eq '0'}">selected</c:if>>...</option>
										<option value="11" <c:if test="${monTo2 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="12" <c:if test="${monTo2 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="13" <c:if test="${monTo2 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="14" <c:if test="${monTo2 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="15" <c:if test="${monTo2 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="16" <c:if test="${monTo2 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="17" <c:if test="${monTo2 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="18" <c:if test="${monTo2 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="19" <c:if test="${monTo2 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="110" <c:if test="${monTo2 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="111" <c:if test="${monTo2 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="112" <c:if test="${monTo2 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="113" <c:if test="${monTo2 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="114" <c:if test="${monTo2 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="115" <c:if test="${monTo2 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="116" <c:if test="${monTo2 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="117" <c:if test="${monTo2 eq '17'}">selected</c:if> >16:00 PM</option>
										<option value="118" <c:if test="${monTo2 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="119" <c:if test="${monTo2 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="120" <c:if test="${monTo2 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="121" <c:if test="${monTo2 eq '21'}">selected</c:if> >18:00 PM</option>
									</select>
								</td>
								<c:choose>
							<c:when test="${tueDropPosition[0] eq 'null'}">
								<c:set var="tueFrom1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="tueFrom1" value="${tueDropPosition[0]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${tueDropPosition[1] eq 'null'}">
								<c:set var="tueTo1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="tueTo1" value="${tueDropPosition[1]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${tueDropPosition[2] eq 'null'}">
								<c:set var="tueFrom2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="tueFrom2" value="${tueDropPosition[2]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${tueDropPosition[3] eq 'null'}">
								<c:set var="tueTo2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="tueTo2" value="${tueDropPosition[3]}"/>
							</c:otherwise>
						</c:choose>
											<td>
											<select name="tueFrom1" >
										<option value="0" <c:if test="${tueFrom1 eq '0'}">selected</c:if>>...</option>
										<option value="21" <c:if test="${tueFrom1 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="22" <c:if test="${tueFrom1 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="23" <c:if test="${tueFrom1 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="24" <c:if test="${tueFrom1 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="25" <c:if test="${tueFrom1 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="26" <c:if test="${tueFrom1 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="27" <c:if test="${tueFrom1 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="28" <c:if test="${tueFrom1 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="29" <c:if test="${tueFrom1 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="210" <c:if test="${tueFrom1 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="211" <c:if test="${tueFrom1 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="212" <c:if test="${tueFrom1 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="213" <c:if test="${tueFrom1 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="214" <c:if test="${tueFrom1 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="215" <c:if test="${tueFrom1 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="216" <c:if test="${tueFrom1 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="217" <c:if test="${tueFrom1 eq '17'}">selected</c:if>>16:00 PM</option>
										<option value="218" <c:if test="${tueFrom1 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="219" <c:if test="${tueFrom1 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="220" <c:if test="${tueFrom1 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="221" <c:if test="${tueFrom1 eq '21'}">selected</c:if>>18:00 PM</option>
									</select> TO <select name="tueTo1" >
										<option value="0" <c:if test="${tueTo1 eq '0'}">selected</c:if>>...</option>
										<option value="21" <c:if test="${tueTo1 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="22" <c:if test="${tueTo1 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="23" <c:if test="${tueTo1 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="24" <c:if test="${tueTo1 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="25" <c:if test="${tueTo1 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="26" <c:if test="${tueTo1 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="27" <c:if test="${tueTo1 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="28" <c:if test="${tueTo1 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="29" <c:if test="${tueTo1 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="210" <c:if test="${tueTo1 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="211" <c:if test="${tueTo1 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="212" <c:if test="${tueTo1 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="213" <c:if test="${tueTo1 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="214" <c:if test="${tueTo1 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="215" <c:if test="${tueTo1 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="216" <c:if test="${tueTo1 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="217" <c:if test="${tueTo1 eq '17'}">selected</c:if>>16:00 PM</option>
										<option value="218" <c:if test="${tueTo1 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="219" <c:if test="${tueTo1 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="220" <c:if test="${tueTo1 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="221" <c:if test="${tueTo1 eq '21'}">selected</c:if>>18:00 PM</option>
									</select>
									<select name="tueFrom2" >
										<option value="0" <c:if test="${tueFrom2 eq '0'}">selected</c:if>>...</option>
										<option value="21" <c:if test="${tueFrom2 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="22" <c:if test="${tueFrom2 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="23" <c:if test="${tueFrom2 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="24" <c:if test="${tueFrom2 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="25" <c:if test="${tueFrom2 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="26" <c:if test="${tueFrom2 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="27" <c:if test="${tueFrom2 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="28" <c:if test="${tueFrom2 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="29" <c:if test="${tueFrom2 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="210" <c:if test="${tueFrom2 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="211" <c:if test="${tueFrom2 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="212" <c:if test="${tueFrom2 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="213" <c:if test="${tueFrom2 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="214" <c:if test="${tueFrom2 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="215" <c:if test="${tueFrom2 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="216" <c:if test="${tueFrom2 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="217" <c:if test="${tueFrom2 eq '17'}">selected</c:if>>16:00 PM</option>
										<option value="218" <c:if test="${tueFrom2 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="219" <c:if test="${tueFrom2 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="220" <c:if test="${tueFrom2 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="221" <c:if test="${tueFrom2 eq '21'}">selected</c:if>>18:00 PM</option>
									</select> TO <select name="tueTo2" >									
										<option value="0" <c:if test="${tueTo2 eq '0'}">selected</c:if>>...</option>
										<option value="21" <c:if test="${tueTo2 eq '1'}">selected</c:if>>08:00 AM</option>
										<option value="22" <c:if test="${tueTo2 eq '2'}">selected</c:if>>08:30 AM</option>
										<option value="23" <c:if test="${tueTo2 eq '3'}">selected</c:if>>09:00 AM</option>
										<option value="24" <c:if test="${tueTo2 eq '4'}">selected</c:if>>09:30 AM</option>
										<option value="25" <c:if test="${tueTo2 eq '5'}">selected</c:if>>10:00 AM</option>
										<option value="26" <c:if test="${tueTo2 eq '6'}">selected</c:if>>10:30 AM</option>
										<option value="27" <c:if test="${tueTo2 eq '7'}">selected</c:if>>11:00 AM</option>
										<option value="28" <c:if test="${tueTo2 eq '8'}">selected</c:if>>11:30 AM</option>
										<option value="29" <c:if test="${tueTo2 eq '9'}">selected</c:if>>12:00 PM</option>
										<option value="210" <c:if test="${tueTo2 eq '10'}">selected</c:if>>12:30 PM</option>
										<option value="211" <c:if test="${tueTo2 eq '11'}">selected</c:if>>13:00 PM</option>
										<option value="212" <c:if test="${tueTo2 eq '12'}">selected</c:if>>13:30 PM</option>
										<option value="213" <c:if test="${tueTo2 eq '13'}">selected</c:if>>14:00 PM</option>
										<option value="214" <c:if test="${tueTo2 eq '14'}">selected</c:if>>14:30 PM</option>
										<option value="215" <c:if test="${tueTo2 eq '15'}">selected</c:if>>15:00 PM</option>
										<option value="216" <c:if test="${tueTo2 eq '16'}">selected</c:if>>15:30 PM</option>
										<option value="217" <c:if test="${tueTo2 eq '17'}">selected</c:if>>16:00 PM</option>
										<option value="218" <c:if test="${tueTo2 eq '18'}">selected</c:if>>16:30 PM</option>
										<option value="219" <c:if test="${tueTo2 eq '19'}">selected</c:if>>17:00 PM</option>
										<option value="220" <c:if test="${tueTo2 eq '20'}">selected</c:if>>17:30 PM</option>
										<option value="221" <c:if test="${tueTo2 eq '21'}">selected</c:if>>18:00 PM</option>
									</select>
											</td>
											<c:choose>
							<c:when test="${wedDropPosition[0] eq 'null'}">
								<c:set var="wedFrom1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="wedFrom1" value="${wedDropPosition[0]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${wedDropPosition[1] eq 'null'}">
								<c:set var="wedTo1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="wedTo1" value="${wedDropPosition[1]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${wedDropPosition[2] eq 'null'}">
								<c:set var="wedFrom2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="wedFrom2" value="${wedDropPosition[2]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${wedDropPosition[3] eq 'null'}">
								<c:set var="wedTo2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="wedTo2" value="${wedDropPosition[3]}"/>
							</c:otherwise>
						</c:choose>
											<td>
											<select name="wedFrom1" >
											<option value="0" <c:if test="${wedFrom1 eq '0'}">selected</c:if>>...</option>
											<option value="31" <c:if test="${wedFrom1 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="32" <c:if test="${wedFrom1 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="33" <c:if test="${wedFrom1 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="34" <c:if test="${wedFrom1 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="35" <c:if test="${wedFrom1 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="36" <c:if test="${wedFrom1 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="37" <c:if test="${wedFrom1 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="38" <c:if test="${wedFrom1 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="39" <c:if test="${wedFrom1 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="310" <c:if test="${wedFrom1 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="311" <c:if test="${wedFrom1 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="312" <c:if test="${wedFrom1 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="313" <c:if test="${wedFrom1 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="314" <c:if test="${wedFrom1 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="315" <c:if test="${wedFrom1 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="316" <c:if test="${wedFrom1 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="317" <c:if test="${wedFrom1 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="318" <c:if test="${wedFrom1 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="319" <c:if test="${wedFrom1 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="320" <c:if test="${wedFrom1 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="321" <c:if test="${wedFrom1 eq '21'}">selected</c:if>>18:00 PM</option>
											</select> TO <select name="wedTo1" >
											<option value="0" <c:if test="${wedTo1 eq '0'}">selected</c:if>>...</option>
											<option value="31" <c:if test="${wedTo1 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="32" <c:if test="${wedTo1 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="33" <c:if test="${wedTo1 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="34" <c:if test="${wedTo1 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="35" <c:if test="${wedTo1 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="36" <c:if test="${wedTo1 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="37" <c:if test="${wedTo1 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="38" <c:if test="${wedTo1 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="39" <c:if test="${wedTo1 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="310" <c:if test="${wedTo1 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="311" <c:if test="${wedTo1 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="312" <c:if test="${wedTo1 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="313" <c:if test="${wedTo1 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="314" <c:if test="${wedTo1 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="315" <c:if test="${wedTo1 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="316" <c:if test="${wedTo1 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="317" <c:if test="${wedTo1 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="318" <c:if test="${wedTo1 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="319" <c:if test="${wedTo1 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="320" <c:if test="${wedTo1 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="321" <c:if test="${wedTo1 eq '21'}">selected</c:if>>18:00 PM</option>
										</select>
										<select name="wedFrom2" >
											<option value="0" <c:if test="${wedFrom2 eq '0'}">selected</c:if>>...</option>
											<option value="31" <c:if test="${wedFrom2 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="32" <c:if test="${wedFrom2 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="33" <c:if test="${wedFrom2 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="34" <c:if test="${wedFrom2 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="35" <c:if test="${wedFrom2 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="36" <c:if test="${wedFrom2 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="37" <c:if test="${wedFrom2 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="38" <c:if test="${wedFrom2 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="39" <c:if test="${wedFrom2 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="310" <c:if test="${wedFrom2 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="311" <c:if test="${wedFrom2 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="312" <c:if test="${wedFrom2 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="313" <c:if test="${wedFrom2 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="314" <c:if test="${wedFrom2 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="315" <c:if test="${wedFrom2 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="316" <c:if test="${wedFrom2 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="317" <c:if test="${wedFrom2 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="318" <c:if test="${wedFrom2 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="319" <c:if test="${wedFrom2 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="320" <c:if test="${wedFrom2 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="321" <c:if test="${wedFrom2 eq '21'}">selected</c:if>>18:00 PM</option>
										</select> TO <select name="wedTo2" >
											<option value="0" <c:if test="${wedTo2 eq '0'}">selected</c:if>>...</option>
											<option value="31" <c:if test="${wedTo2 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="32" <c:if test="${wedTo2 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="33" <c:if test="${wedTo2 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="34" <c:if test="${wedTo2 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="35" <c:if test="${wedTo2 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="36" <c:if test="${wedTo2 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="37" <c:if test="${wedTo2 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="38" <c:if test="${wedTo2 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="39" <c:if test="${wedTo2 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="310" <c:if test="${wedTo2 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="311" <c:if test="${wedTo2 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="312" <c:if test="${wedTo2 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="313" <c:if test="${wedTo2 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="314" <c:if test="${wedTo2 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="315" <c:if test="${wedTo2 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="316" <c:if test="${wedTo2 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="317" <c:if test="${wedTo2 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="318" <c:if test="${wedTo2 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="319" <c:if test="${wedTo2 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="320" <c:if test="${wedTo2 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="321" <c:if test="${wedTo2 eq '21'}">selected</c:if>>18:00 PM</option>
										</select>
											</td>
											<c:choose>
							<c:when test="${thuDropPosition[0] eq 'null'}">
								<c:set var="thuFrom1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="thuFrom1" value="${thuDropPosition[0]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${thuDropPosition[1] eq 'null'}">
								<c:set var="thuTo1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="thuTo1" value="${thuDropPosition[1]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${thuDropPosition[2] eq 'null'}">
								<c:set var="thuFrom2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="thuFrom2" value="${thuDropPosition[2]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${thuDropPosition[3] eq 'null'}">
								<c:set var="thuTo2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="thuTo2" value="${thuDropPosition[3]}"/>
							</c:otherwise>
						</c:choose>
											<td>
											<select name="thuFrom1" >
											<option value="0" <c:if test="${thuFrom1 eq '0'}">selected</c:if>>...</option>
											<option value="41" <c:if test="${thuFrom1 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="42" <c:if test="${thuFrom1 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="43" <c:if test="${thuFrom1 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="44" <c:if test="${thuFrom1 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="45" <c:if test="${thuFrom1 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="46" <c:if test="${thuFrom1 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="47" <c:if test="${thuFrom1 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="48" <c:if test="${thuFrom1 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="49" <c:if test="${thuFrom1 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="410" <c:if test="${thuFrom1 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="411" <c:if test="${thuFrom1 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="412" <c:if test="${thuFrom1 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="413" <c:if test="${thuFrom1 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="414" <c:if test="${thuFrom1 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="415" <c:if test="${thuFrom1 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="416" <c:if test="${thuFrom1 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="417" <c:if test="${thuFrom1 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="418" <c:if test="${thuFrom1 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="419" <c:if test="${thuFrom1 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="420" <c:if test="${thuFrom1 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="421" <c:if test="${thuFrom1 eq '21'}">selected</c:if>>18:00 PM</option>
										</select> TO <select name="thuTo1">
										<option value="0" <c:if test="${thuTo1 eq '0'}">selected</c:if>>...</option>
											<option value="41" <c:if test="${thuTo1 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="42" <c:if test="${thuTo1 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="43" <c:if test="${thuTo1 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="44" <c:if test="${thuTo1 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="45" <c:if test="${thuTo1 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="46" <c:if test="${thuTo1 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="47" <c:if test="${thuTo1 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="48" <c:if test="${thuTo1 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="49" <c:if test="${thuTo1 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="410" <c:if test="${thuTo1 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="411" <c:if test="${thuTo1 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="412" <c:if test="${thuTo1 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="413" <c:if test="${thuTo1 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="414" <c:if test="${thuTo1 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="415" <c:if test="${thuTo1 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="416" <c:if test="${thuTo1 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="417" <c:if test="${thuTo1 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="418" <c:if test="${thuTo1 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="419" <c:if test="${thuTo1 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="420" <c:if test="${thuTo1 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="421" <c:if test="${thuTo1 eq '21'}">selected</c:if>>18:00 PM</option>
										</select>
										<select name="thuFrom2">										
											<option value="0" <c:if test="${thuFrom2 eq '0'}">selected</c:if>>...</option>
											<option value="41" <c:if test="${thuFrom2 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="42" <c:if test="${thuFrom2 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="43" <c:if test="${thuFrom2 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="44" <c:if test="${thuFrom2 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="45" <c:if test="${thuFrom2 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="46" <c:if test="${thuFrom2 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="47" <c:if test="${thuFrom2 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="48" <c:if test="${thuFrom2 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="49" <c:if test="${thuFrom2 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="410" <c:if test="${thuFrom2 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="411" <c:if test="${thuFrom2 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="412" <c:if test="${thuFrom2 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="413" <c:if test="${thuFrom2 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="414" <c:if test="${thuFrom2 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="415" <c:if test="${thuFrom2 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="416" <c:if test="${thuFrom2 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="417" <c:if test="${thuFrom2 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="418" <c:if test="${thuFrom2 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="419" <c:if test="${thuFrom2 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="420" <c:if test="${thuFrom2 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="421" <c:if test="${thuFrom2 eq '21'}">selected</c:if>>18:00 PM</option>
										</select> TO <select name="thuTo2" >											
											<option value="0" <c:if test="${thuTo2 eq '0'}">selected</c:if>>...</option>
											<option value="41" <c:if test="${thuTo2 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="42" <c:if test="${thuTo2 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="43" <c:if test="${thuTo2 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="44" <c:if test="${thuTo2 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="45" <c:if test="${thuTo2 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="46" <c:if test="${thuTo2 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="47" <c:if test="${thuTo2 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="48" <c:if test="${thuTo2 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="49" <c:if test="${thuTo2 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="410" <c:if test="${thuTo2 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="411" <c:if test="${thuTo2 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="412" <c:if test="${thuTo2 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="413" <c:if test="${thuTo2 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="414" <c:if test="${thuTo2 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="415" <c:if test="${thuTo2 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="416" <c:if test="${thuTo2 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="417" <c:if test="${thuTo2 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="418" <c:if test="${thuTo2 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="419" <c:if test="${thuTo2 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="420" <c:if test="${thuTo2 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="421" <c:if test="${thuTo2 eq '21'}">selected</c:if>>18:00 PM</option>
										</select>
											</td>
											<c:choose>
							<c:when test="${friDropPosition[0] eq 'null'}">
								<c:set var="friFrom1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="friFrom1" value="${friDropPosition[0]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${friDropPosition[1] eq 'null'}">
								<c:set var="friTo1" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="friTo1" value="${friDropPosition[1]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${friDropPosition[2] eq 'null'}">
								<c:set var="friFrom2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="friFrom2" value="${friDropPosition[2]}"/>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${friDropPosition[3] eq 'null'}">
								<c:set var="friTo2" value="0"/>
							</c:when>
							<c:otherwise>
								<c:set var="friTo2" value="${friDropPosition[3]}"/>
							</c:otherwise>
						</c:choose>
											<td>
											<select name="friFrom1" >
											<option value="0" <c:if test="${friFrom1 eq '0'}">selected</c:if>>...</option>
											<option value="51" <c:if test="${friFrom1 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="52" <c:if test="${friFrom1 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="53" <c:if test="${friFrom1 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="54" <c:if test="${friFrom1 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="55" <c:if test="${friFrom1 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="56" <c:if test="${friFrom1 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="57" <c:if test="${friFrom1 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="58" <c:if test="${friFrom1 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="59" <c:if test="${friFrom1 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="510" <c:if test="${friFrom1 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="511" <c:if test="${friFrom1 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="512" <c:if test="${friFrom1 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="513" <c:if test="${friFrom1 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="514" <c:if test="${friFrom1 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="515" <c:if test="${friFrom1 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="516" <c:if test="${friFrom1 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="517" <c:if test="${friFrom1 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="518" <c:if test="${friFrom1 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="519" <c:if test="${friFrom1 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="520" <c:if test="${friFrom1 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="521" <c:if test="${friFrom1 eq '21'}">selected</c:if>>18:00 PM</option>
										</select> TO <select name="friTo1" >										
											<option value="0"  <c:if test="${friTo1 eq '0'}">selected</c:if>>...</option>
											<option value="51" <c:if test="${friTo1 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="52" <c:if test="${friTo1 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="53" <c:if test="${friTo1 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="54" <c:if test="${friTo1 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="55" <c:if test="${friTo1 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="56" <c:if test="${friTo1 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="57" <c:if test="${friTo1 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="58" <c:if test="${friTo1 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="59" <c:if test="${friTo1 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="510" <c:if test="${friTo1 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="511" <c:if test="${friTo1 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="512" <c:if test="${friTo1 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="513" <c:if test="${friTo1 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="514" <c:if test="${friTo1 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="515" <c:if test="${friTo1 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="516" <c:if test="${friTo1 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="517" <c:if test="${friTo1 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="518" <c:if test="${friTo1 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="519" <c:if test="${friTo1 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="520" <c:if test="${friTo1 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="521" <c:if test="${friTo1 eq '21'}">selected</c:if>>18:00 PM</option>
										</select>
										<select name="friFrom2" >
										<option value="0" <c:if test="${friFrom2 eq '0'}">selected</c:if>>...</option>
											<option value="51" <c:if test="${friFrom2 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="52" <c:if test="${friFrom2 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="53" <c:if test="${friFrom2 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="54" <c:if test="${friFrom2 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="55" <c:if test="${friFrom2 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="56" <c:if test="${friFrom2 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="57" <c:if test="${friFrom2 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="58" <c:if test="${friFrom2 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="59" <c:if test="${friFrom2 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="510" <c:if test="${friFrom2 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="511" <c:if test="${friFrom2 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="512" <c:if test="${friFrom2 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="513" <c:if test="${friFrom2 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="514" <c:if test="${friFrom2 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="515" <c:if test="${friFrom2 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="516" <c:if test="${friFrom2 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="517" <c:if test="${friFrom2 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="518" <c:if test="${friFrom2 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="519" <c:if test="${friFrom2 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="520" <c:if test="${friFrom2 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="521" <c:if test="${friFrom2 eq '21'}">selected</c:if>>18:00 PM</option>
										</select> TO <select name="friTo2" >
											<option value="0" <c:if test="${friTo2 eq '0'}">selected</c:if>>...</option>
											<option value="51" <c:if test="${friTo2 eq '1'}">selected</c:if>>08:00 AM</option>
											<option value="52" <c:if test="${friTo2 eq '2'}">selected</c:if>>08:30 AM</option>
											<option value="53" <c:if test="${friTo2 eq '3'}">selected</c:if>>09:00 AM</option>
											<option value="54" <c:if test="${friTo2 eq '4'}">selected</c:if>>09:30 AM</option>
											<option value="55" <c:if test="${friTo2 eq '5'}">selected</c:if>>10:00 AM</option>
											<option value="56" <c:if test="${friTo2 eq '6'}">selected</c:if>>10:30 AM</option>
											<option value="57" <c:if test="${friTo2 eq '7'}">selected</c:if>>11:00 AM</option>
											<option value="58" <c:if test="${friTo2 eq '8'}">selected</c:if>>11:30 AM</option>
											<option value="59" <c:if test="${friTo2 eq '9'}">selected</c:if>>12:00 PM</option>
											<option value="510" <c:if test="${friTo2 eq '10'}">selected</c:if>>12:30 PM</option>
											<option value="511" <c:if test="${friTo2 eq '11'}">selected</c:if>>13:00 PM</option>
											<option value="512" <c:if test="${friTo2 eq '12'}">selected</c:if>>13:30 PM</option>
											<option value="513" <c:if test="${friTo2 eq '13'}">selected</c:if>>14:00 PM</option>
											<option value="514" <c:if test="${friTo2 eq '14'}">selected</c:if>>14:30 PM</option>
											<option value="515" <c:if test="${friTo2 eq '15'}">selected</c:if>>15:00 PM</option>
											<option value="516" <c:if test="${friTo2 eq '16'}">selected</c:if>>15:30 PM</option>
											<option value="517" <c:if test="${friTo2 eq '17'}">selected</c:if>>16:00 PM</option>
											<option value="518" <c:if test="${friTo2 eq '18'}">selected</c:if>>16:30 PM</option>
											<option value="519" <c:if test="${friTo2 eq '19'}">selected</c:if>>17:00 PM</option>
											<option value="520" <c:if test="${friTo2 eq '20'}">selected</c:if>>17:30 PM</option>
											<option value="521" <c:if test="${friTo2 eq '21'}">selected</c:if>>18:00 PM</option>
										</select>
											</td>
										</tr>
									</table>
	