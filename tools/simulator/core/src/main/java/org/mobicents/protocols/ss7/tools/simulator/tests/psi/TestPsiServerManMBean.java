/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2018, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.protocols.ss7.tools.simulator.tests.psi;

import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.mobicents.protocols.ss7.tools.simulator.common.AddressNatureType;
import org.mobicents.protocols.ss7.tools.simulator.level3.NumberingPlanMapType;
import org.mobicents.protocols.ss7.tools.simulator.tests.sms.SRIReaction;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public interface TestPsiServerManMBean {

  //Operations
  String performSendRoutingInfoForSMResponse();

  String performSendRoutingInformationResponse();

  void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest);

  String performProvideSubscriberInfoResponse();

  //Attributes

  AddressNatureType getAddressNature();

  String getNumberingPlan();

  NumberingPlanMapType getNumberingPlanType();

  void setAddressNature(AddressNatureType addressNatureType);

  void setNumberingPlan(String numberingPlan);

  void setNumberingPlanType(NumberingPlanMapType numberingPlanMapType);

  void putAddressNature(String addressNature);

  void putNumberingPlanType(String NumberingPlanType);

  /********************/

  String getNetworkNodeNumber();

  void setNetworkNodeNumber(String networkNodeNumber);

  String getVmscAddress();

  void setVmscAddress(String vmscAddress);

  String getImsi();

  void setImsi(String imsi);

  String getLmsi();

  void setLmsi(String lmsi);

  int getMcc();

  void setMcc(int mcc);

  int getMnc();

  void setMnc(int mnc);

  int getLac();

  void setLac(int lac);

  int getCi();

  void setCi(int ci);

  int getAol();

  void setAol(int aol);

  boolean isSaiPresent();

  void setSaiPresent(boolean saiPresent);

  double getGeographicalLatitude();

  void setGeographicalLatitude(double geographicalLatitude);

  double getGeographicalLongitude();

  void setGeographicalLongitude(double geographicalLongitude);

  double getGeographicalUncertainty();

  void setGeographicalUncertainty(double geographicalUncertainty) ;

  int getScreeningAndPresentationIndicators();

  void setScreeningAndPresentationIndicators(int screeningAndPresentationIndicators);

  double getGeodeticLatitude();

  void setGeodeticLatitude(double geodeticLatitude);

  double getGeodeticLongitude();

  void setGeodeticLongitude(double geodeticLongitude);

  double getGeodeticUncertainty();

  void setGeodeticUncertainty(double geodeticUncertainty);

  int getGeodeticConfidence();

  void setGeodeticConfidence(int geodeticConfidence);

  boolean isCurrentLocationRetrieved();

  void setCurrentLocationRetrieved(boolean currentLocationRetrieved);

  String getImei();

  void setImei(String imei);

  /*************************/

  SRIReaction getSRIReaction();

  String getSRIReaction_Value();

  void setSRIReaction(SRIReaction val);

  void putSRIReaction(String val);

  PSIReaction getPSIReaction();

  String getPSIReaction_Value();

  void setPSIReaction(PSIReaction val);

  void putPSIReaction(String val);

  String getCurrentRequestDef();

}
