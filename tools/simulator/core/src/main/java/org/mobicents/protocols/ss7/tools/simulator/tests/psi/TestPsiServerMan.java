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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mobicents.protocols.ss7.isup.impl.message.parameter.LocationNumberImpl;
import org.mobicents.protocols.ss7.isup.message.parameter.LocationNumber;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPParameterFactory;
import org.mobicents.protocols.ss7.map.api.MAPProvider;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorCode;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;

import org.mobicents.protocols.ss7.map.MAPParameterFactoryImpl;

import org.mobicents.protocols.ss7.map.api.errors.UnknownSubscriberDiagnostic;
import org.mobicents.protocols.ss7.map.api.primitives.ExternalSignalInfo;
import org.mobicents.protocols.ss7.map.api.primitives.NAEAPreferredCI;
import org.mobicents.protocols.ss7.map.api.primitives.NetworkResource;
import org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan;
import org.mobicents.protocols.ss7.map.api.primitives.AddressNature;
import org.mobicents.protocols.ss7.map.api.primitives.LMSI;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.mobicents.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.mobicents.protocols.ss7.map.api.primitives.IMEI;

import org.mobicents.protocols.ss7.map.api.service.callhandling.AllowedServices;
import org.mobicents.protocols.ss7.map.api.service.callhandling.CCBSIndicators;
import org.mobicents.protocols.ss7.map.api.service.callhandling.CUGCheckInfo;
import org.mobicents.protocols.ss7.map.api.service.callhandling.ExtendedRoutingInfo;
import org.mobicents.protocols.ss7.map.api.service.callhandling.IstCommandRequest;
import org.mobicents.protocols.ss7.map.api.service.callhandling.IstCommandResponse;
import org.mobicents.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.mobicents.protocols.ss7.map.api.service.callhandling.MAPServiceCallHandling;
import org.mobicents.protocols.ss7.map.api.service.callhandling.MAPServiceCallHandlingListener;
import org.mobicents.protocols.ss7.map.api.service.callhandling.ProvideRoamingNumberRequest;
import org.mobicents.protocols.ss7.map.api.service.callhandling.ProvideRoamingNumberResponse;
import org.mobicents.protocols.ss7.map.api.service.callhandling.RoutingInfo;
import org.mobicents.protocols.ss7.map.api.service.callhandling.SendRoutingInformationRequest;
import org.mobicents.protocols.ss7.map.api.service.callhandling.SendRoutingInformationResponse;
import org.mobicents.protocols.ss7.map.api.service.callhandling.UnavailabilityCause;
import org.mobicents.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPServiceMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.ForwardCheckSSIndicationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;

import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;

import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;

import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.DomainType;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSNetworkCapability;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSRadioAccessCapability;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PDPContextInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberStateChoice;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.ExtBasicServiceCode;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;

import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.OfferedCamel4CSIs;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.SupportedCamelPhases;
import org.mobicents.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.mobicents.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.mobicents.protocols.ss7.map.api.service.sms.MAPServiceSms;
import org.mobicents.protocols.ss7.map.api.service.sms.MAPServiceSmsListener;
import org.mobicents.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;
import org.mobicents.protocols.ss7.map.api.service.sms.ForwardShortMessageRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.ForwardShortMessageResponse;
import org.mobicents.protocols.ss7.map.api.service.sms.AlertServiceCentreRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.AlertServiceCentreResponse;
import org.mobicents.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.ReportSMDeliveryStatusResponse;
import org.mobicents.protocols.ss7.map.api.service.sms.MoForwardShortMessageRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.MoForwardShortMessageResponse;
import org.mobicents.protocols.ss7.map.api.service.sms.MtForwardShortMessageRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.MtForwardShortMessageResponse;
import org.mobicents.protocols.ss7.map.api.service.sms.InformServiceCentreRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.ReadyForSMRequest;
import org.mobicents.protocols.ss7.map.api.service.sms.ReadyForSMResponse;
import org.mobicents.protocols.ss7.map.api.service.sms.NoteSubscriberPresentRequest;

import org.mobicents.protocols.ss7.map.api.service.supplementary.SSCode;
import org.mobicents.protocols.ss7.map.errors.MAPErrorMessageAbsentSubscriberImpl;
import org.mobicents.protocols.ss7.map.errors.MAPErrorMessageUnknownSubscriberImpl;
import org.mobicents.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.mobicents.protocols.ss7.map.primitives.IMSIImpl;
import org.mobicents.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.mobicents.protocols.ss7.map.primitives.LMSIImpl;

import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.GeographicalInformationImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.GeodeticInformationImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.MSNetworkCapabilityImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.MSRadioAccessCapabilityImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.RouteingNumberImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationEPSImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.EUtranCgiImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.TAIdImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationGPRSImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.LocationNumberMapImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.RAIdentityImpl;

import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationNumberMap;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.NumberPortabilityStatus;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RouteingNumber;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.NotReachableReason;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.UserCSGInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RAIdentity;

import org.mobicents.protocols.ss7.map.service.mobility.subscriberManagement.LSAIdentityImpl;

import org.mobicents.protocols.ss7.tcap.asn.ProblemImpl;
import org.mobicents.protocols.ss7.tcap.asn.comp.InvokeProblemType;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;
import org.mobicents.protocols.ss7.tools.simulator.Stoppable;
import org.mobicents.protocols.ss7.tools.simulator.common.AddressNatureType;
import org.mobicents.protocols.ss7.tools.simulator.common.TesterBase;
import org.mobicents.protocols.ss7.tools.simulator.level3.MapMan;
import org.mobicents.protocols.ss7.tools.simulator.level3.NumberingPlanMapType;
import org.mobicents.protocols.ss7.tools.simulator.management.TesterHost;
import org.mobicents.protocols.ss7.tools.simulator.tests.sms.SRIReaction;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class TestPsiServerMan extends TesterBase implements TestPsiServerManMBean, Stoppable, MAPServiceMobilityListener, MAPServiceSmsListener, MAPServiceCallHandlingListener {

  private static Logger logger = Logger.getLogger(TestPsiServerMan.class);

  public static String SOURCE_NAME = "TestPsiServerMan";
  private final String name;
  private MapMan mapMan;
  private boolean isStarted = false;
  private int countMapSriForSmReq = 0;
  private int countMapSriForSmResp = 0;
  private int countMapSriReq = 0;
  private int countMapSriResp = 0;
  private int countMapPsiReq = 0;
  private int countMapPsiResp = 0;
  private String currentRequestDef = "";
  private boolean needSendSend = false;
  private boolean needSendClose = false;
  private int countErrSent = 0;
  private MAPProvider mapProvider;
  private MAPServiceMobility mapServiceMobility;
  private MAPServiceSms mapServiceSms;
  private MAPServiceCallHandling mapServiceCallHandling;
  private MAPParameterFactory mapParameterFactory;

  public TestPsiServerMan(String name) {
    super(SOURCE_NAME);
    this.name = name;
    this.isStarted = false;
  }

  public boolean start() {

    this.mapProvider = this.mapMan.getMAPStack().getMAPProvider();
    this.mapServiceSms = this.mapProvider.getMAPServiceSms();
    this.mapServiceCallHandling = this.mapProvider.getMAPServiceCallHandling();
    this.mapServiceMobility = mapProvider.getMAPServiceMobility();
    this.mapParameterFactory = mapProvider.getMAPParameterFactory();

    mapServiceSms.acivate();
    mapServiceCallHandling.acivate();
    mapServiceMobility.acivate();
    mapServiceSms.addMAPServiceListener(this);
    mapServiceCallHandling.addMAPServiceListener(this);
    mapServiceMobility.addMAPServiceListener(this);
    mapProvider.addMAPDialogListener(this);

    this.testerHost.sendNotif(SOURCE_NAME, "PSI Server has been started", "", Level.INFO);
    isStarted = true;
    this.countMapPsiReq = 0;
    this.countMapPsiResp = 0;
    return true;
  }

  public void setTesterHost(TesterHost testerHost) {
    this.testerHost = testerHost;
  }

  public void setMapMan(MapMan val) {
    this.mapMan = val;
  }

  @Override
  public String getState() {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append(SOURCE_NAME);
    sb.append(": ");
    sb.append("<br>Count: countMapSriForSmReq-");
    sb.append(countMapSriForSmReq);
    sb.append(", countMapSriForSmResp-");
    sb.append(countMapSriForSmResp);
    sb.append("<br>Count: countMapSriReq-");
    sb.append(countMapSriReq);
    sb.append(", countMapSriResp-");
    sb.append(countMapSriResp);
    sb.append("<br>Count: countMapPsiReq-");
    sb.append(countMapPsiReq);
    sb.append(", countMapPsiResp-");
    sb.append(countMapPsiResp);
    sb.append("</html>");
    return sb.toString();
  }

  @Override
  public void execute() {
  }

  @Override
  public void stop() {
    isStarted = false;
    mapProvider.getMAPServiceLsm().deactivate();
    mapProvider.getMAPServiceMobility().deactivate();
    mapProvider.getMAPServiceSms().removeMAPServiceListener(this);
    mapProvider.getMAPServiceMobility().removeMAPServiceListener(this);
    mapProvider.removeMAPDialogListener(this);
    this.testerHost.sendNotif(SOURCE_NAME, "PSI Client has been stopped", "", Level.INFO);
  }

  //**************************//
  //*** SRIforSMS methods ***//
  //************************//
  @Override
  public String performSendRoutingInfoForSMResponse() {
    if (!isStarted) {
      return "The tester is not started";
    }

    return sendRoutingInfoForSMResponse();
  }

  public String sendRoutingInfoForSMResponse() {

    return "sendRoutingInfoForSMResponse called automatically";
  }

  public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInforForSMRequest) {

    MAPErrorMessage mapErrorMessage = null;
    logger.debug("\nonSendRoutingInfoForSMRequest");
    if (!isStarted)
      return;

    this.countMapSriForSmReq++;

    MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
    MAPDialogSms curDialog = sendRoutingInforForSMRequest.getMAPDialog();
    long invokeId = sendRoutingInforForSMRequest.getInvokeId();

    if (!this.testerHost.getConfigurationData().getTestSmsClientConfigurationData().isOneNotificationFor100Dialogs()) {
      String srIforSMReqData = this.createSRIforSMData(sendRoutingInforForSMRequest);
      this.testerHost.sendNotif(SOURCE_NAME, "Rcvd: sriForSMReq", srIforSMReqData, Level.DEBUG);
    }

    // Generate MAP errors for specific MSISDNs
    String msisdnAddress = sendRoutingInforForSMRequest.getMsisdn().getAddress();
    if (msisdnAddress.equalsIgnoreCase("99998888")) {
      InvokeProblemType invokeProblemType = InvokeProblemType.UnrecognizedOperation;
      Problem problem = new ProblemImpl();
      problem.setInvokeProblemType(invokeProblemType);
      try {
        curDialog.sendRejectComponent(invokeId, problem);
        curDialog.close(false);
      } catch (MAPException e) {
        e.printStackTrace();
      }
      logger.debug("\nRejectComponent sent");
      this.testerHost.sendNotif(SOURCE_NAME, "Sent: RejectComponent",
          createSRIforSMRespData(curDialog.getLocalDialogId(), null, null), Level.INFO);
      return;
    }
    if (msisdnAddress.equalsIgnoreCase("99990000")) {
      MAPErrorMessage mapErrorMessageAbsentSubscriber = new MAPErrorMessageAbsentSubscriberImpl();
      try {
        curDialog.sendErrorComponent(invokeId, mapErrorMessageAbsentSubscriber);
        curDialog.close(false);
      } catch (MAPException e) {
        e.printStackTrace();
      }
      logger.debug("\nErrorComponent sent");
      this.testerHost.sendNotif(SOURCE_NAME, "Sent: ErrorComponent",
          createSRIforSMRespData(curDialog.getLocalDialogId(), null, null), Level.INFO);
      return;
    }

    IMSI imsi = new IMSIImpl("124356871012345");

    String nnnAddress = "5982123007";
    ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(AddressNature.international_number,
            NumberingPlan.ISDN, nnnAddress);

    byte[] lmsiByte = {50, 57, 49, 53};
    LMSI lmsi = new LMSIImpl(lmsiByte);
    MAPExtensionContainer mapExtensionContainer = null;
    AdditionalNumber additionalNumber = null;
    boolean mwdSet = false;
    LocationInfoWithLMSI locationInfoWithLMSI  = mapProvider.getMAPParameterFactory().createLocationInfoWithLMSI(networkNodeNumber, lmsi, mapExtensionContainer, false, additionalNumber);
    logger.info("LocationInfoWithLMSI for onSendRoutingInfoForSMRequest: NNN="
            +locationInfoWithLMSI.getNetworkNodeNumber().getAddress()+ ", IMSI="+imsi.getData()+ ", LMSI="+lmsi.getData().toString());

    try {

      curDialog.addSendRoutingInfoForSMResponse(invokeId, imsi, locationInfoWithLMSI, mapExtensionContainer, mwdSet);
      curDialog.close(false);

      logger.debug("\nSendRoutingForSMResponse sent");
      this.countMapSriForSmResp++;

      this.testerHost.sendNotif(SOURCE_NAME, "Sent: SendRoutingForSMResponse",
              createSRIforSMRespData(curDialog.getLocalDialogId(), imsi, locationInfoWithLMSI), Level.INFO);

    } catch (MAPException e) {
      this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking addSendRoutingInfoForSMResponse() : " + e.getMessage(), e, Level.ERROR);
    }
  }

  private String createSRIforSMData(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequest) {
    StringBuilder sb = new StringBuilder();
    sb.append("dialogId=");
    sb.append(sendRoutingInfoForSMRequest.getMAPDialog().getLocalDialogId());
    sb.append(",\nsriReq=");
    sb.append(sendRoutingInfoForSMRequest);
    sb.append(",\nRemoteAddress=");
    sb.append(sendRoutingInfoForSMRequest.getMAPDialog().getRemoteAddress());
    sb.append(",\nLocalAddress=");
    sb.append(sendRoutingInfoForSMRequest.getMAPDialog().getLocalAddress());

    return sb.toString();
  }

  private String createSRIforSMRespData(long dialogId, IMSI imsi, LocationInfoWithLMSI locationInfoWithLMSI) {
    StringBuilder sb = new StringBuilder();
    sb.append("dialogId=");
    sb.append(dialogId);
    sb.append(",\n imsi=");
    sb.append(imsi);
    sb.append(",\n locationInfo=");
    sb.append(locationInfoWithLMSI);
    sb.append(",\n");
    return sb.toString();
  }

  //**************************//
  //*** SRI methods ***//
  //************************//
  @Override
  public String performSendRoutingInformationResponse() {
    if (!isStarted) {
      return "The tester is not started";
    }

    return sendRoutingInformationResponse();
  }

  public String sendRoutingInformationResponse() {

    return "sendRoutingInfoResponse called automatically";
  }

  public void onSendRoutingInformationRequest(SendRoutingInformationRequest sendRoutingInformationRequest) {

    MAPErrorMessage mapErrorMessage = null;
    logger.debug("\nonSendRoutingInfoRequest");
    if (!isStarted)
      return;

    this.countMapSriReq++;

    MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
    MAPDialogCallHandling curDialog = sendRoutingInformationRequest.getMAPDialog();
    long invokeId = sendRoutingInformationRequest.getInvokeId();

    String sriReqData = this.createSRIData(sendRoutingInformationRequest);
    this.testerHost.sendNotif(SOURCE_NAME, "Rcvd: sriReq", sriReqData, Level.DEBUG);

    // Generate MAP errors for specific MSISDNs
    String msisdnAddress = sendRoutingInformationRequest.getMsisdn().getAddress();
    if (msisdnAddress.equalsIgnoreCase("99998888")) {
      InvokeProblemType invokeProblemType = InvokeProblemType.ResourceLimitation;
      Problem problem = new ProblemImpl();
      problem.setInvokeProblemType(invokeProblemType);
      try {
        curDialog.sendRejectComponent(invokeId, problem);
        curDialog.close(false);
      } catch (MAPException e) {
        e.printStackTrace();
      }
      logger.debug("\nRejectComponent sent");
      this.testerHost.sendNotif(SOURCE_NAME, "Sent: RejectComponent",
          createSRIRespData(curDialog.getLocalDialogId(), null, null), Level.INFO);
      return;
    }
    if (msisdnAddress.equalsIgnoreCase("99990000")) {
      MAPErrorMessage mapErrorMessage1 = new MAPErrorMessageUnknownSubscriberImpl();
      try {
        curDialog.sendErrorComponent(invokeId, mapErrorMessage1);
        curDialog.close(false);
      } catch (MAPException e) {
        e.printStackTrace();
      }
      logger.debug("\nErrorComponent sent");
      this.testerHost.sendNotif(SOURCE_NAME, "Sent: ErrorComponent",
          createSRIRespData(curDialog.getLocalDialogId(), null, null), Level.INFO);
      return;
    }

    IMSI imsi = new IMSIImpl("321654987051423");

    String vmscAddressDigits = "5982123007";
    ISDNAddressString vmscAddress = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, vmscAddressDigits);

    ISDNAddressString msisdn = sendRoutingInformationRequest.getMsisdn();

    logger.info("onSendRoutingInfoRequest: VMSC Address=" +vmscAddress.getAddress()+ ", IMSI="+imsi.getData()+ "invokeID="+invokeId);

    ExtendedRoutingInfo extRoutingInfo = null;
    CUGCheckInfo cugCheckInfo = null;
    boolean cugSubscriptionFlag = false;
    SubscriberInfo subscriberInfo = null;
    ArrayList<SSCode> ssList = null;
    ExtBasicServiceCode basicService = null;
    boolean forwardingInterrogationRequired = false;
    MAPExtensionContainer extensionContainer = null;
    NAEAPreferredCI naeaPreferredCI = null;
    CCBSIndicators ccbsIndicators = null;
    NumberPortabilityStatus nrPortabilityStatus = null;
    Integer istAlertTimer = null;
    SupportedCamelPhases supportedCamelPhases = null;
    OfferedCamel4CSIs offeredCamel4CSIs = null;
    RoutingInfo routingInfo2 = null;
    ArrayList<SSCode> ssList2 = null;
    ExtBasicServiceCode basicService2 = null;
    AllowedServices allowedServices = null;
    UnavailabilityCause unavailabilityCause = null;
    boolean releaseResourcesSupported = false;
    ExternalSignalInfo gsmBearerCapability = null;

    try {

      curDialog.addSendRoutingInformationResponse(invokeId, imsi, extRoutingInfo, cugCheckInfo, cugSubscriptionFlag, subscriberInfo, ssList,
          basicService, forwardingInterrogationRequired, vmscAddress, extensionContainer, naeaPreferredCI, ccbsIndicators,
          msisdn, nrPortabilityStatus, istAlertTimer, supportedCamelPhases, offeredCamel4CSIs, routingInfo2, ssList2, basicService2,
          allowedServices, unavailabilityCause, releaseResourcesSupported, gsmBearerCapability);

      curDialog.close(false);

      logger.debug("\nSendRoutingInformationResponse sent");
      this.countMapSriResp++;

      this.testerHost.sendNotif(SOURCE_NAME, "Sent: SendRoutingInformationResponse",
          createSRIRespData(curDialog.getLocalDialogId(), imsi, vmscAddress), Level.INFO);

    } catch (MAPException e) {
      this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking addSendRoutingInformationResponse() : " + e.getMessage(), e, Level.ERROR);
    }
  }

  private String createSRIData(SendRoutingInformationRequest sendRoutingInformationRequest) {
    StringBuilder sb = new StringBuilder();
    sb.append("dialogId=");
    sb.append(sendRoutingInformationRequest.getMAPDialog().getLocalDialogId());
    sb.append(",\nsriReq=");
    sb.append(sendRoutingInformationRequest);
    sb.append(",\nRemoteAddress=");
    sb.append(sendRoutingInformationRequest.getMAPDialog().getRemoteAddress());
    sb.append(",\nLocalAddress=");
    sb.append(sendRoutingInformationRequest.getMAPDialog().getLocalAddress());

    return sb.toString();
  }

  private String createSRIRespData(long dialogId, IMSI imsi, ISDNAddressString vmscAddress) {
    StringBuilder sb = new StringBuilder();
    sb.append("dialogId=");
    sb.append(dialogId);
    sb.append(",\n imsi=");
    sb.append(imsi);
    sb.append(",\n vmscAddress=");
    sb.append(vmscAddress);
    sb.append(",\n");
    return sb.toString();
  }

  //********************//
  //*** PSI methods ***//
  //******************//

  @Override
  public String performProvideSubscriberInfoResponse() {
    if (!isStarted) {
      return "The tester is not started";
    }

    return sendProvideSubscriberInfoResponse();
  }

  public String sendProvideSubscriberInfoResponse() {

    return "sendProvideSubscriberInfoResponse called automatically";
  }

  public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {

    MAPErrorMessage mapErrorMessage = null;
    logger.debug("\nonProvideSubscriberInfoRequest");
    if (!isStarted)
      return;

    this.countMapPsiReq++;

    MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
    MAPDialogMobility curDialog = provideSubscriberInfoRequest.getMAPDialog();
    long invokeId = provideSubscriberInfoRequest.getInvokeId();
    RequestedInfo requestedInfo = provideSubscriberInfoRequest.getRequestedInfo();

    String psiReqData = this.createPSIReqData(provideSubscriberInfoRequest);
    this.testerHost.sendNotif(SOURCE_NAME, "Rcvd: psiReq", psiReqData, Level.DEBUG);

    Random rand = new Random();

    try {

      PSIReaction psiReaction = this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getPsiReaction();

      switch (psiReaction.intValue()) {

        case PSIReaction.VAL_RETURN_SUCCESS:
          String msisdnStr = "59899077937";
          ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, msisdnStr);
          SubscriberInfo subscriberInfo = null;
          IMSI imsi = null;
          LocationInformation locationInformation = null;
          LocationInformationEPS locationInformationEPS = null;
          LocationInformationGPRS locationInformationGPRS = null;
          int mcc, mnc, lac, ci;
          CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = null;
          CellGlobalIdOrServiceAreaIdFixedLength cgiOrSai;
          Boolean saiPresent = null;
          LocationNumber locationNumber = null;
          String mscAddress = "5982123007";
          String vlrAddress = "59899000231";
          String sgsnAddress = "5982133021";
          ISDNAddressString mscNumber, vlrNumber, sgsnNumber;
          LocationNumberMap locationNumberMap = null;
          GeographicalInformation geographicalInformation;
          GeodeticInformation geodeticInformation;
          EUtranCgi eUtranCgi;
          TAId taId;
          LSAIdentity selectedLSAIdentity = null;
          RAIdentity routeingAreaIdentity = null;
          RouteingNumber routeingNumber = null;
          Boolean currentLocationRetrieved = false;
          UserCSGInformation userCSGInformation = null;
          SubscriberStateChoice subscriberStateChoice = null;
          PSSubscriberStateChoice psSubscriberStateChoice = null;
          SubscriberState subscriberState = null;
          PSSubscriberState psSubscriberState = null;
          NotReachableReason notReachableReason = null;
          ArrayList<PDPContextInfo> pdpContextInfoList = null;//new ArrayList<PDPContextInfo>();
          MNPInfoRes mnpInfoRes = null;
          NumberPortabilityStatus numberPortabilityStatus = null;
          MSClassmark2 msClassmark2 = null;
          GPRSMSClass gprsMSClass = null;
          IMEI imei = null;
          MAPExtensionContainer extensionContainer = null;

          if (requestedInfo.getLocationInformation()) {
            if (requestedInfo.getCurrentLocation()) {
              currentLocationRetrieved = true;
            }
            if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
              mcc = 748;
              mnc = 2;
              lac = 53201;
              ci = 23479;

              mscNumber = new ISDNAddressStringImpl(AddressNature.international_number,NumberingPlan.ISDN, mscAddress);
              vlrNumber = new ISDNAddressStringImpl(AddressNature.international_number,NumberingPlan.ISDN, vlrAddress);
              sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number,NumberingPlan.ISDN, sgsnAddress);

              int aol = 1;
              if (this.countMapPsiReq % 2 == 0) {
                saiPresent = false; // set saiPresent to false if this ATI request is even since test started
              } else {
                saiPresent = true; // set saiPresent to true if this ATI request is odd since test started
              }

              TypeOfShape geographicalTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
              Double geographicalLatitude = -23.291032;
              Double geographicalLongitude = 109.977810;
              Double geographicalUncertainty = 20.0;
              Integer geoOption = rand.nextInt(3) + 1;
              if (geoOption == 1) {
                geographicalLatitude = 0.0;
                geographicalLongitude = 0.0;
                geographicalUncertainty = 0.0;
              }

              TypeOfShape geodeticTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
              Double geodeticLatitude = -24.010010;
              Double geodeticLongitude = 110.00987;
              Double geodeticlUncertainty = 10.0;
              if (geoOption == 1) {
                geodeticLatitude = 0.0;
                geodeticLongitude = 0.0;
                geodeticlUncertainty = 0.0;
              }
              int geodeticConfidence = 1;
              int screeningAndPresentationIndicators = 3;

              byte[] lteCgi = hexStringToByteArray("37320100014e4a");
              eUtranCgi = new EUtranCgiImpl(lteCgi);
              byte[] trackinAreaId = hexStringToByteArray("3732013935");
              taId = new TAIdImpl(trackinAreaId);
              byte[] mmeNom = {77, 77, 69, 55, 52, 56, 48, 48, 48, 49};
              DiameterIdentity mmeName = new DiameterIdentityImpl(mmeNom);

              int natureOfAddressIndicator = 4;
              String locationNumberAddressDigits= "819203961904";
              int numberingPlanIndicator = 1;
              int internalNetworkNumberIndicator = 1;
              int addressRepresentationRestrictedIndicator = 1;
              int screeningIndicator = 3;
              locationNumber = new LocationNumberImpl(natureOfAddressIndicator, locationNumberAddressDigits, numberingPlanIndicator,
                  internalNetworkNumberIndicator, addressRepresentationRestrictedIndicator, screeningIndicator);
              try {
                locationNumberMap = new LocationNumberMapImpl(locationNumber);
              } catch (MAPException e) {
                e.printStackTrace();
              }

              try {
                MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();
                CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength = mapFactory.createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, ci);
                cellGlobalIdOrServiceAreaIdOrLAI = mapFactory.createCellGlobalIdOrServiceAreaIdOrLAI(cellGlobalIdOrServiceAreaIdFixedLength);
                geographicalInformation = new GeographicalInformationImpl(geographicalTypeOfShape, geographicalLatitude, geographicalLongitude, geographicalUncertainty);
                geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, geodeticTypeOfShape, geodeticLatitude, geodeticLongitude, geodeticlUncertainty, geodeticConfidence);
                locationInformationEPS = new LocationInformationEPSImpl(eUtranCgi, taId, extensionContainer, geographicalInformation,
                    geodeticInformation, currentLocationRetrieved, aol, mmeName);
                locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(aol, geographicalInformation,
                    vlrNumber,locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, extensionContainer, selectedLSAIdentity, mscNumber, geodeticInformation, currentLocationRetrieved,
                    saiPresent, locationInformationEPS, userCSGInformation);
              } catch (MAPException e) {
                this.testerHost.sendNotif(SOURCE_NAME, "Exception when creating subscriber info for PSI response while onProvideSubscriberInfoRequest: " + e.getMessage(), e, Level.ERROR);
              }

            } else {
              // requestedInfo.getRequestedDomain() == DomainType.psDomain
              mcc = 748;
              mnc = 23;
              lac = 32006;
              ci = 38222;
              int aol = 14571;
              if (this.countMapPsiReq % 2 == 0)
                saiPresent = true; // set saiPresent to true if this ATI request is even since test started
              else
                saiPresent = false; // set saiPresent to false if this ATI request is odd since test started
              if (requestedInfo.getCurrentLocation()) {
                currentLocationRetrieved = false;
              }
              cgiOrSai = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, ci);
              cellGlobalIdOrServiceAreaIdOrLAI = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdOrLAI(cgiOrSai);
              byte[] raId = hexStringToByteArray("47f810393532");
              routeingAreaIdentity = new RAIdentityImpl(raId);
              sgsnNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                  NumberingPlan.ISDN, sgsnAddress);
              byte[] lsaId = {49, 51, 49};
              selectedLSAIdentity = new LSAIdentityImpl(lsaId);

              TypeOfShape geographicalTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
              Double geographicalLatitude = 48.000100;
              Double geographicalLongitude = -121.400101;
              Double geographicalUncertainty = 10.0;
              Integer geoOption = rand.nextInt(3) + 1;
              if (geoOption == 1) {
                geographicalLatitude = 0.0;
                geographicalLongitude = 0.0;
                geographicalUncertainty = 0.0;
              }

              TypeOfShape geodeticTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
              Double geodeticLatitude = 24.010010;
              Double geodeticLongitude = -99.001797;
              Double geodeticlUncertainty = 5.0;
              int geodeticConfidence = 2;
              int screeningAndPresentationIndicators = 2;
              if (geoOption == 1) {
                geodeticLatitude = 0.0;
                geodeticLongitude = 0.0;
                geodeticlUncertainty = 0.0;
              }

              geographicalInformation = new GeographicalInformationImpl(geographicalTypeOfShape, geographicalLatitude, geographicalLongitude, geographicalUncertainty);
              geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, geodeticTypeOfShape, geodeticLatitude, geodeticLongitude, geodeticlUncertainty, geodeticConfidence);

              locationInformationGPRS = new LocationInformationGPRSImpl(cellGlobalIdOrServiceAreaIdOrLAI, routeingAreaIdentity, geographicalInformation, sgsnNumber,
                  selectedLSAIdentity, extensionContainer, saiPresent, geodeticInformation, currentLocationRetrieved, aol);
            }

            if (requestedInfo.getMnpRequestedInfo()) {
              //RouteingNumber routeingNumber = mapProvider.getMAPParameterFactory().createRouteingNumber("5555555888");
              routeingNumber = new RouteingNumberImpl("598123");
              imsi = new IMSIImpl("748026871012345");
              numberPortabilityStatus = NumberPortabilityStatus.ownNumberNotPortedOut;
              mnpInfoRes = mapProvider.getMAPParameterFactory().createMNPInfoRes(routeingNumber, imsi, msisdn, numberPortabilityStatus, extensionContainer);
            }

            if (requestedInfo.getImei()) {
              if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
                imei = mapProvider.getMAPParameterFactory().createIMEI("011714004661050");
              } else {
                imei = mapProvider.getMAPParameterFactory().createIMEI("011714004661051");
              }
            }

            if (requestedInfo.getMsClassmark()) {
              if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
                byte[] classmark = {48, 48, 51};
                msClassmark2 = mapProvider.getMAPParameterFactory().createMSClassmark2(classmark);
              } else {
                byte[] mSNetworkCapabilityB = hexStringToByteArray("3130303032303331");
                MSNetworkCapability mSNetworkCapability = new MSNetworkCapabilityImpl(mSNetworkCapabilityB);
                byte[] mSRadioAccessCapabilityB = hexStringToByteArray("31303030323033313730383134");
                MSRadioAccessCapability mSRadioAccessCapability = new MSRadioAccessCapabilityImpl(mSRadioAccessCapabilityB);
                gprsMSClass = mapProvider.getMAPParameterFactory().createGPRSMSClass(mSNetworkCapability, mSRadioAccessCapability);
              }
            }

            Integer stateOption = rand.nextInt(17) + 1;
            switch (stateOption) {
              case 1:
              case 2:
              case 3:
              case 4:
              case 5:
              case 6:
                subscriberStateChoice = SubscriberStateChoice.assumedIdle;
                psSubscriberStateChoice = PSSubscriberStateChoice.psAttachedReachableForPaging;
                break;
              case 7:
              case 8:
              case 9:
              case 10:
              case 11:
              case 12:
                subscriberStateChoice = SubscriberStateChoice.camelBusy;
                psSubscriberStateChoice = PSSubscriberStateChoice.psAttachedReachableForPaging;
                break;
              case 13:
                subscriberStateChoice = SubscriberStateChoice.netDetNotReachable;
                notReachableReason = NotReachableReason.imsiDetached;
                psSubscriberStateChoice = PSSubscriberStateChoice.netDetNotReachable;
                int ageOfLocationInformation = 1575;
                currentLocationRetrieved = false;
                geographicalInformation = null;
                geodeticInformation = null;
                locationInformationEPS = null;
                mscNumber = null;
                imei = null;
                msClassmark2 = null;
                gprsMSClass = null;
                mnpInfoRes = null;
                if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
                  vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, vlrAddress);
                  locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(ageOfLocationInformation, geographicalInformation,
                      vlrNumber, locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, extensionContainer, selectedLSAIdentity, mscNumber, geodeticInformation,
                      currentLocationRetrieved, saiPresent, locationInformationEPS, userCSGInformation);
                } else {
                  sgsnNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, sgsnAddress);
                  locationInformationGPRS = mapProvider.getMAPParameterFactory().createLocationInformationGPRS(cellGlobalIdOrServiceAreaIdOrLAI,
                      routeingAreaIdentity, geographicalInformation, sgsnNumber, selectedLSAIdentity, extensionContainer, saiPresent, geodeticInformation,
                      currentLocationRetrieved, ageOfLocationInformation);
                }
                break;
              case 14:
              case 15:
                subscriberStateChoice = SubscriberStateChoice.notProvidedFromVLR;
                psSubscriberStateChoice = PSSubscriberStateChoice.notProvidedFromSGSNorMME;
                break;
              case 16:
                subscriberStateChoice = SubscriberStateChoice.netDetNotReachable;
                notReachableReason = NotReachableReason.restrictedArea;
                psSubscriberStateChoice = PSSubscriberStateChoice.netDetNotReachable;
                ageOfLocationInformation = 300;
                currentLocationRetrieved = false;
                geographicalInformation = null;
                geodeticInformation = null;
                locationInformationEPS = null;
                mscNumber = null;
                imei = null;
                msClassmark2 = null;
                gprsMSClass = null;
                mnpInfoRes = null;
                if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
                  vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, vlrAddress);
                  locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(ageOfLocationInformation, geographicalInformation,
                      vlrNumber, locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, extensionContainer, selectedLSAIdentity, mscNumber, geodeticInformation,
                      currentLocationRetrieved, saiPresent, locationInformationEPS, userCSGInformation);
                } else  {
                  sgsnNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, sgsnAddress);
                  locationInformationGPRS = mapProvider.getMAPParameterFactory().createLocationInformationGPRS(cellGlobalIdOrServiceAreaIdOrLAI,
                      routeingAreaIdentity, geographicalInformation, sgsnNumber, selectedLSAIdentity, extensionContainer, saiPresent, geodeticInformation,
                      currentLocationRetrieved, ageOfLocationInformation);
                }
                break;
              case 17:
                subscriberStateChoice = SubscriberStateChoice.netDetNotReachable;
                notReachableReason = NotReachableReason.msPurged;
                psSubscriberStateChoice = PSSubscriberStateChoice.psAttachedNotReachableForPaging;
                ageOfLocationInformation = 221;
                currentLocationRetrieved = false;
                geographicalInformation = null;
                geodeticInformation = null;
                locationInformationEPS = null;
                mscNumber = null;
                imei = null;
                msClassmark2 = null;
                gprsMSClass = null;
                mnpInfoRes = null;
                if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
                  vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, vlrAddress);
                  locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(ageOfLocationInformation, geographicalInformation,
                      vlrNumber, locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, extensionContainer, selectedLSAIdentity, mscNumber, geodeticInformation,
                      currentLocationRetrieved, saiPresent, locationInformationEPS, userCSGInformation);
                } else {
                  sgsnNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, sgsnAddress);
                  locationInformationGPRS = mapProvider.getMAPParameterFactory().createLocationInformationGPRS(cellGlobalIdOrServiceAreaIdOrLAI,
                      routeingAreaIdentity, geographicalInformation, sgsnNumber, selectedLSAIdentity, extensionContainer, saiPresent, geodeticInformation,
                      currentLocationRetrieved, ageOfLocationInformation);
                }
                break;
            }
            if (requestedInfo.getSubscriberState()) {
              if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
                subscriberState = mapProvider.getMAPParameterFactory().createSubscriberState(subscriberStateChoice, notReachableReason);
              } else {
                psSubscriberState = mapProvider.getMAPParameterFactory().createPSSubscriberState(psSubscriberStateChoice, notReachableReason, pdpContextInfoList);
              }
            }

            try {

              subscriberInfo = mapProvider.getMAPParameterFactory().createSubscriberInfo(locationInformation, subscriberState, extensionContainer,
                  locationInformationGPRS, psSubscriberState, imei, msClassmark2, gprsMSClass, mnpInfoRes);

              curDialog.addProvideSubscriberInfoResponse(invokeId, subscriberInfo, extensionContainer);
              curDialog.close(false);

              logger.debug("\nProvideSubscriberInfoResponse sent");
              this.countMapPsiResp++;

              this.testerHost.sendNotif(SOURCE_NAME, "Sent: ProvideSubscriberInfoResponse",
                  createPSIRespData(curDialog.getLocalDialogId(), subscriberInfo, extensionContainer), Level.INFO);

            } catch (MAPException e) {
              this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking addProvideSubscriberInfoResponse() : " + e.getMessage(), e, Level.ERROR);
            }

          }
          break;

        case PSIReaction.VAL_UNEXPECTED_DATA_VALUE:
          mapErrorMessage = mapProvider.getMAPErrorMessageFactory().createMAPErrorMessageExtensionContainer(
              (long) MAPErrorCode.unexpectedDataValue, null);
          curDialog.sendErrorComponent(invokeId, mapErrorMessage);
          this.countErrSent++;
          psiReqData = this.createErrorData(curDialog.getLocalDialogId(), (int) invokeId, mapErrorMessage);
          this.testerHost.sendNotif(SOURCE_NAME, "Sent: unexpectedDataVal", psiReqData, Level.DEBUG);
          break;

        case PSIReaction.VAL_ERROR_SYSTEM_FAILURE:
          mapErrorMessage = mapProvider.getMAPErrorMessageFactory().createMAPErrorMessageSystemFailure(
              (long) curDialog.getApplicationContext().getApplicationContextVersion().getVersion(), NetworkResource.hlr, null, null);
          curDialog.sendErrorComponent(invokeId, mapErrorMessage);
          this.countErrSent++;
          psiReqData = this.createErrorData(curDialog.getLocalDialogId(), (int) invokeId, mapErrorMessage);
          this.testerHost.sendNotif(SOURCE_NAME, "Sent: errSysFail", psiReqData, Level.DEBUG);
          break;

        case PSIReaction.VAL_DATA_MISSING:
          mapErrorMessage = mapProvider.getMAPErrorMessageFactory().createMAPErrorMessageExtensionContainer(
              (long) MAPErrorCode.dataMissing, null);
          curDialog.sendErrorComponent(invokeId, mapErrorMessage);
          this.countErrSent++;
          psiReqData = this.createErrorData(curDialog.getLocalDialogId(), (int) invokeId, mapErrorMessage);
          this.testerHost.sendNotif(SOURCE_NAME, "Sent: errDataMissing", psiReqData, Level.DEBUG);
          break;

        case PSIReaction.VAL_ERROR_UNKNOWN_SUBSCRIBER:
          mapErrorMessage = mapProvider.getMAPErrorMessageFactory().createMAPErrorMessageUnknownSubscriber(null,
              UnknownSubscriberDiagnostic.imsiUnknown);
          curDialog.sendErrorComponent(invokeId, mapErrorMessage);
          this.countErrSent++;
          psiReqData = this.createErrorData(curDialog.getLocalDialogId(), (int) invokeId, mapErrorMessage);
          this.testerHost.sendNotif(SOURCE_NAME, "Sent: errUnknSubs", psiReqData, Level.DEBUG);
          break;
      }

    } catch (Exception e) {
      this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking addAnyTimeInterrogationResponse() : " + e.getMessage(), e, Level.ERROR);
    }

  }

  private String createPSIReqData(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {
    StringBuilder sb = new StringBuilder();
    sb.append("dialogId=");
    sb.append(provideSubscriberInfoRequest.getMAPDialog().getLocalDialogId());
    sb.append(",\nPSI Operation Code=");
    sb.append(provideSubscriberInfoRequest.getOperationCode());
    sb.append(",\nRemoteAddress=");
    sb.append(provideSubscriberInfoRequest.getMAPDialog().getRemoteAddress());
    sb.append(",\nLocalAddress=");
    sb.append(provideSubscriberInfoRequest.getMAPDialog().getLocalAddress());
    sb.append(",\nPSI Request=");
    sb.append(provideSubscriberInfoRequest);

    return sb.toString();
  }

  private String createPSIRespData(long dialogId, SubscriberInfo subscriberInfo, MAPExtensionContainer mapExtensionContainer) {
    StringBuilder sb = new StringBuilder();
    sb.append("dialogId=");
    sb.append(dialogId);
    if (subscriberInfo.getLocationInformation() != null) {
      if (subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
        if (subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
          try {
            sb.append(",\nMCC=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
            sb.append(",\nMNC=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
            sb.append(",\nLAC=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
            sb.append(",\nCI=");
            sb.append(subscriberInfo.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());
          } catch (MAPException e) {
            e.printStackTrace();
          }
        }
        if (subscriberInfo.getLocationInformation().getLocationNumber() != null) {
          sb.append(",\nLocation number=");
          try {
            sb.append(",\nLocation number address digits=");
            sb.append(subscriberInfo.getLocationInformation().getLocationNumber().getLocationNumber().getAddress());
            sb.append(",\nLocation number NAI=");
            sb.append(subscriberInfo.getLocationInformation().getLocationNumber().getLocationNumber().getNatureOfAddressIndicator());
            sb.append(",\nLocation number code=");
            sb.append(subscriberInfo.getLocationInformation().getLocationNumber().getLocationNumber().getCode());
            sb.append(",\nLocation number NPI=");
            sb.append(subscriberInfo.getLocationInformation().getLocationNumber().getLocationNumber().getNumberingPlanIndicator());
            sb.append(",\nLocation number AddressRepresentationRestrictedIndicator=");
            sb.append(subscriberInfo.getLocationInformation().getLocationNumber().getLocationNumber().getAddressRepresentationRestrictedIndicator());
            sb.append(",\nLocation number InternalNetworkNumberIndicator=");
            sb.append(subscriberInfo.getLocationInformation().getLocationNumber().getLocationNumber().getInternalNetworkNumberIndicator());
            sb.append(",\nLocation number ScreeningIndicator=");
            sb.append(subscriberInfo.getLocationInformation().getLocationNumber().getLocationNumber().getScreeningIndicator());
          } catch (MAPException e) {
            e.printStackTrace();
          }
        }
      }
      if (subscriberInfo.getLocationInformation().getMscNumber() != null) {
        sb.append(",\nMSC number=");
        sb.append(subscriberInfo.getLocationInformation().getMscNumber().getAddress());
      }
      if (subscriberInfo.getLocationInformation().getVlrNumber() != null) {
        sb.append(",\nVLR number=");
        sb.append(subscriberInfo.getLocationInformation().getVlrNumber().getAddress());
      }
      sb.append(",\nAOL=");
      sb.append(subscriberInfo.getLocationInformation().getAgeOfLocationInformation());
      sb.append(",\nSAI present=");
      sb.append(subscriberInfo.getLocationInformation().getSaiPresent());
      if (subscriberInfo.getLocationInformation().getGeographicalInformation() != null) {
        sb.append(",\nGeographicalLatitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getLatitude());
        sb.append(",\nGeographical Longitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getLongitude());
        sb.append(",\nGeographicalUncertainty=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getUncertainty());
        sb.append(",\nGeographical Type of Shape=");
        sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getTypeOfShape());
      }
      if (subscriberInfo.getLocationInformation().getGeographicalInformation() != null) {
        sb.append(",\nGeodetic Latitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getLatitude());
        sb.append(",\nGeodetic Longitude=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getLongitude());
        sb.append(",\nGeodetic Uncertainty=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getUncertainty());
        sb.append(",\nGeodetic Confidence=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getConfidence());
        sb.append(",\nGeodetic Type of Shape=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getTypeOfShape());
        sb.append(",\nGeodetic Screening and Presentation Indicators=");
        sb.append(subscriberInfo.getLocationInformation().getGeodeticInformation().getScreeningAndPresentationIndicators());
      }
      sb.append(",\nCurrent Location Retrieved=");
      sb.append(subscriberInfo.getLocationInformation().getCurrentLocationRetrieved());
      if (subscriberInfo.getLocationInformation().getLocationInformationEPS() != null) {
        if (subscriberInfo.getLocationInformation().getLocationInformationEPS().getMmeName() != null) {
          sb.append(",\nMME name=");
          sb.append(new String(subscriberInfo.getLocationInformation().getLocationInformationEPS().getMmeName().getData()));
        }
        if (subscriberInfo.getLocationInformation().getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
          sb.append(",\nE-UTRAN CGI=");
          sb.append(new String(subscriberInfo.getLocationInformation().getLocationInformationEPS().getEUtranCellGlobalIdentity().getData()));
        }
        if (subscriberInfo.getLocationInformation().getLocationInformationEPS().getTrackingAreaIdentity() != null) {
          sb.append(",\nEPS Traking Identity=");
          sb.append(new String(subscriberInfo.getLocationInformation().getLocationInformationEPS().getTrackingAreaIdentity().getData()));
        }
        if (subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeographicalInformation() != null) {
          sb.append(",\nEPS GeographicalLatitude=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getLatitude());
          sb.append(",\nEPS Geographical Longitude=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getLongitude());
          sb.append(",\nEPS GeographicalUncertainty=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getUncertainty());
          sb.append(",\nEPS Geographical Type of Shape Code=");
          sb.append(subscriberInfo.getLocationInformation().getGeographicalInformation().getTypeOfShape().getCode());
        }
        if (subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation() != null) {
          sb.append(",\nEPS Geodetic Latitude=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getLatitude());
          sb.append(",\nEPS Geodetic Longitude=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getLongitude());
          sb.append(",\nEPS Geodetic Uncertainty=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getUncertainty());
          sb.append(",\nEPS Geodetic Confidence=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getConfidence());
          sb.append(",\nEPS Geodetic Type of Shape=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getTypeOfShape());
          sb.append(",\nEPS Geodetic Screening and Presentation Indicators=");
          sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getScreeningAndPresentationIndicators());
        }
        sb.append(",\nEPS Current Location Retrieved=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved());
        sb.append(",\nEPS AOL=");
        sb.append(subscriberInfo.getLocationInformation().getLocationInformationEPS().getAgeOfLocationInformation());
      }
    }
    if (subscriberInfo.getLocationInformationGPRS() != null) {
      if (subscriberInfo.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
        if (subscriberInfo.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
          try {
            sb.append(",\nMCC=");
            sb.append(subscriberInfo.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
            sb.append(",\nMNC=");
            sb.append(subscriberInfo.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
            sb.append(",\nLAC=");
            sb.append(subscriberInfo.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
            sb.append(",\nCI=");
            sb.append(subscriberInfo.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());
          } catch (MAPException e) {
            e.printStackTrace();
          }
        }
      }
      if (subscriberInfo.getLocationInformationGPRS().getGeographicalInformation() != null) {
        sb.append(",\nGPRS GeographicalLatitude=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeographicalInformation().getLatitude());
        sb.append(",\nGPRS Geographical Longitude=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeographicalInformation().getLongitude());
        sb.append(",\nGPRS GeographicalUncertainty=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeographicalInformation().getUncertainty());
        sb.append(",\nGPRS Geographical Type of Shape=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeographicalInformation().getTypeOfShape());
      }
      if (subscriberInfo.getLocationInformationGPRS().getGeodeticInformation() != null) {
        sb.append(",\nGPRS Geodetic Latitude=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeodeticInformation().getLatitude());
        sb.append(",\nGPRS Geodetic Longitude=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeodeticInformation().getLongitude());
        sb.append(",\nGPRS Geodetic Uncertainty=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeodeticInformation().getUncertainty());
        sb.append(",\nGPRS Geodetic Confidence=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeodeticInformation().getConfidence());
        sb.append(",\nGPRS Geodetic Type of Shape=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeodeticInformation().getTypeOfShape());
        sb.append(",\nGPRS Geodetic Screening and Presentation Indicators=");
        sb.append(subscriberInfo.getLocationInformationGPRS().getGeodeticInformation().getScreeningAndPresentationIndicators());
      }
    }
    sb.append(",\nMNP info result=");
    if (subscriberInfo.getMNPInfoRes() != null) {
      sb.append(",\nMNP info result number portability status=");
      sb.append(subscriberInfo.getMNPInfoRes().getNumberPortabilityStatus().getType());
      sb.append(",\nMNP info result MSISDN=");
      sb.append(subscriberInfo.getMNPInfoRes().getMSISDN().getAddress());
      sb.append(",\nMNP info result IMSI=");
      sb.append(subscriberInfo.getMNPInfoRes().getIMSI().getData());
      sb.append(",\nMNP info result MSISDN=");
      sb.append(subscriberInfo.getMNPInfoRes().getMSISDN().getAddress());
      sb.append(",\nMNP info result Routeing Number=");
      sb.append(subscriberInfo.getMNPInfoRes().getRouteingNumber().getRouteingNumber());
    }

    sb.append(",\nMS Classmark2=");
    if (subscriberInfo.getMSClassmark2() != null)
      sb.append(new String(subscriberInfo.getMSClassmark2().getData()));
    sb.append(",\nGPRS MS Class:");
    if (subscriberInfo.getGPRSMSClass() != null) {
      sb.append(",\nNetwork Capability:");
      if (subscriberInfo.getGPRSMSClass().getMSNetworkCapability() != null)
        sb.append(new String(subscriberInfo.getGPRSMSClass().getMSNetworkCapability().getData()));
      sb.append(",\nMS Radio Access Capability:");
      if (subscriberInfo.getGPRSMSClass().getMSRadioAccessCapability() != null)
        sb.append(new String(subscriberInfo.getGPRSMSClass().getMSRadioAccessCapability().getData()));
    }
    return sb.toString();
  }

  private String createErrorData(long dialogId, int invokeId, MAPErrorMessage mapErrorMessage) {
    StringBuilder sb = new StringBuilder();
    sb.append("dialogId=");
    sb.append(dialogId);
    sb.append(",\n invokeId=");
    sb.append(invokeId);
    sb.append(",\n mapErrorMessage=");
    sb.append(mapErrorMessage);
    sb.append(",\n");
    return sb.toString();
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
          + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }

  // *** GETTER & SETTERS **//
  //***********************//

  @Override
  public AddressNatureType getAddressNature() {
    return new AddressNatureType(this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().getAddressNature().getIndicator());
  }

  @Override
  public void setAddressNature(AddressNatureType val) {
    this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().setAddressNature(AddressNature.getInstance(val.intValue()));
    this.testerHost.markStore();
  }

  @Override
  public String getNumberingPlan() {
    return this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().getNumberingPlan();
  }

  @Override
  public void setNumberingPlan(String numPlan) {
    this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().setNumberingPlan(numPlan);
    this.testerHost.markStore();
  }

  @Override
  public NumberingPlanMapType getNumberingPlanType() {
    return new NumberingPlanMapType(this.testerHost.getConfigurationData().getTestLcsServerConfigurationData().getNumberingPlanType().getIndicator());
  }

  @Override
  public void setNumberingPlanType(NumberingPlanMapType val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setNumberingPlanType(NumberingPlan.getInstance(val.intValue()));
    this.testerHost.markStore();
  }

  @Override
  public void putAddressNature(String val) {
    AddressNatureType x = AddressNatureType.createInstance(val);
    if (x != null)
      this.setAddressNature(x);
  }

  @Override
  public void putNumberingPlanType(String val) {
    NumberingPlanMapType x = NumberingPlanMapType.createInstance(val);
    if (x != null)
      this.setNumberingPlanType(x);
  }


  /*******************************/

  @Override
  public String getNetworkNodeNumber() {
    return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getNetworkNodeNumber());
  }

  @Override
  public void setNetworkNodeNumber(String val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setNetworkNodeNumber(val);
    this.testerHost.markStore();
  }

  @Override
  public String getVmscAddress() {
    return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getVmscAddress());
  }

  @Override
  public void setVmscAddress(String val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setVmscAddress(val);
    this.testerHost.markStore();
  }

  @Override
  public String getImsi() {
    return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getImsi());
  }

  @Override
  public void setImsi(String val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setLmsi(val);
    this.testerHost.markStore();
  }

  @Override
  public String getLmsi() {
    return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getLmsi());
  }

  @Override
  public void setLmsi(String val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setImsi(val);
    this.testerHost.markStore();
  }

  @Override
  public int getMcc() {
    return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getMcc());
  }

  @Override
  public void setMcc(int val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setMcc(val);
    this.testerHost.markStore();
  }

  @Override
  public int getMnc() {
    return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getMnc());
  }

  @Override
  public void setMnc(int val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setMnc(val);
    this.testerHost.markStore();
  }

  @Override
  public int getLac() {
    return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getLac());
  }

  @Override
  public void setLac(int val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setLac(val);
    this.testerHost.markStore();
  }

  @Override
  public int getCi() {
    return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getCi());
  }

  @Override
  public void setCi(int val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setCi(val);
    this.testerHost.markStore();
  }

  @Override
  public int getAol() {
    return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getAol());
  }

  @Override
  public void setAol(int val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setAol(val);
    this.testerHost.markStore();
  }

  @Override
  public boolean isSaiPresent() {
    return new Boolean(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().isSaiPresent());
  }

  @Override
  public void setSaiPresent(boolean val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setSaiPresent(val);
    this.testerHost.markStore();
  }

  @Override
  public double getGeographicalLatitude() {
    return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeographicalLatitude());
  }

  @Override
  public void setGeographicalLatitude(double val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeographicalLatitude(val);
    this.testerHost.markStore();
  }

  @Override
  public double getGeographicalLongitude() {
    return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeographicalLongitude());
  }

  @Override
  public void setGeographicalLongitude(double val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeographicalLongitude(val);
    this.testerHost.markStore();
  }

  @Override
  public double getGeographicalUncertainty() {
    return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeographicalUncertainty());
  }

  @Override
  public void setGeographicalUncertainty(double val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeographicalLongitude(val);
    this.testerHost.markStore();
  }

  @Override
  public int getScreeningAndPresentationIndicators() {
    return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getScreeningAndPresentationIndicators());
  }

  @Override
  public void setScreeningAndPresentationIndicators(int val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setScreeningAndPresentationIndicators(val);
    this.testerHost.markStore();
  }

  @Override
  public double getGeodeticLatitude() {
    return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticLatitude());
  }

  @Override
  public void setGeodeticLatitude(double val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticLatitude(val);
    this.testerHost.markStore();
  }

  @Override
  public double getGeodeticLongitude() {
    return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticLongitude());
  }

  @Override
  public void setGeodeticLongitude(double val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticLongitude(val);
    this.testerHost.markStore();
  }

  @Override
  public double getGeodeticUncertainty() {
    return new Double(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticUncertainty());
  }

  @Override
  public void setGeodeticUncertainty(double val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticUncertainty(val);
    this.testerHost.markStore();
  }

  @Override
  public int getGeodeticConfidence() {
    return new Integer(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getGeodeticConfidence());
  }

  @Override
  public void setGeodeticConfidence(int val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setGeodeticConfidence(val);
    this.testerHost.markStore();
  }

  @Override
  public boolean isCurrentLocationRetrieved() {
    return new Boolean(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().isCurrentLocationRetrieved());
  }

  @Override
  public void setCurrentLocationRetrieved(boolean val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setCurrentLocationRetrieved(val);
    this.testerHost.markStore();
  }

  @Override
  public String getImei() {
    return new String(this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getImei());
  }

  @Override
  public void setImei(String val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setImei(val);
    this.testerHost.markStore();
  }


  /*******************************/


  @Override
  public SRIReaction getSRIReaction() {
    return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getSriForSMReaction();
  }

  @Override
  public String getSRIReaction_Value() {
    return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getSriForSMReaction().toString();
  }

  @Override
  public void setSRIReaction(SRIReaction val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setSriForSMReaction(val);
    this.testerHost.markStore();
  }

  @Override
  public void putSRIReaction(String val) {
    SRIReaction x = SRIReaction.createInstance(val);
    if (x != null)
      this.setSRIReaction(x);
  }

  @Override
  public PSIReaction getPSIReaction() {
    return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getPsiReaction();
  }

  @Override
  public String getPSIReaction_Value() {
    return this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().getPsiReaction().toString();
  }

  @Override
  public void setPSIReaction(PSIReaction val) {
    this.testerHost.getConfigurationData().getTestPsiServerConfigurationData().setPsiReaction(val);
    this.testerHost.markStore();
  }

  @Override
  public void putPSIReaction(String val) {
    PSIReaction x = PSIReaction.createInstance(val);
    if (x != null)
      this.setPSIReaction(x);
  }

  @Override
  public String getCurrentRequestDef() {
    return "LastDialog: " + currentRequestDef;
  }

  @Override
  public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem, boolean isLocalOriginated) {
    super.onRejectComponent(mapDialog, invokeId, problem, isLocalOriginated);
    if (isLocalOriginated)
      needSendClose = true;
  }

  @Override
  public void onDialogDelimiter(MAPDialog mapDialog) {
    try {
      if (needSendSend) {
        needSendSend = false;
        mapDialog.send();
        return;
      }
    } catch (Exception e) {
      this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking send() : " + e.getMessage(), e, Level.ERROR);
      return;
    }
    try {
      if (needSendClose) {
        needSendClose = false;
        mapDialog.close(false);
        return;
      }
    } catch (Exception e) {
      this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking close() : " + e.getMessage(), e, Level.ERROR);
      return;
    }
  }


  //** Not needed MAPServiceSms methods **//
  @Override
  public void onForwardShortMessageRequest(ForwardShortMessageRequest forwardShortMessageRequest) {

  }

  @Override
  public void onForwardShortMessageResponse(ForwardShortMessageResponse forwardShortMessageResponse) {

  }

  @Override
  public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwardShortMessageRequest) {

  }

  @Override
  public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponse) {

  }

  @Override
  public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequest) {

  }

  @Override
  public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponse) {

  }

  @Override
  public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponse) {

  }

  @Override
  public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequest) {

  }

  @Override
  public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponse) {

  }

  @Override
  public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequest) {

  }

  @Override
  public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequest) {

  }

  @Override
  public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreResponse) {

  }

  @Override
  public void onReadyForSMRequest(ReadyForSMRequest readyForSMRequest) {

  }

  @Override
  public void onReadyForSMResponse(ReadyForSMResponse readyForSMResponse) {

  }

  @Override
  public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest noteSubscriberPresentRequest) {

  }

  //** Not needed MAPServiceMobility methods **//
  @Override
  public void onUpdateLocationRequest(UpdateLocationRequest updateLocationRequest) {

  }

  @Override
  public void onUpdateLocationResponse(UpdateLocationResponse updateLocationResponse) {

  }

  @Override
  public void onCancelLocationRequest(CancelLocationRequest cancelLocationRequest) {

  }

  @Override
  public void onCancelLocationResponse(CancelLocationResponse cancelLocationResponse) {

  }

  @Override
  public void onSendIdentificationRequest(SendIdentificationRequest sendIdentificationRequest) {

  }

  @Override
  public void onSendIdentificationResponse(SendIdentificationResponse sendIdentificationResponse) {

  }

  @Override
  public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest updateGprsLocationRequest) {

  }

  @Override
  public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse updateGprsLocationResponse) {

  }

  @Override
  public void onPurgeMSRequest(PurgeMSRequest purgeMSRequest) {

  }

  @Override
  public void onPurgeMSResponse(PurgeMSResponse purgeMSResponse) {

  }

  @Override
  public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest sendAuthenticationInfoRequest) {

  }

  @Override
  public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse sendAuthenticationInfoResponse) {

  }

  @Override
  public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest authenticationFailureReportRequest) {

  }

  @Override
  public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse authenticationFailureReportResponse) {

  }

  @Override
  public void onResetRequest(ResetRequest resetRequest) {

  }

  @Override
  public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest forwardCheckSSIndicationRequest) {

  }

  @Override
  public void onRestoreDataRequest(RestoreDataRequest restoreDataRequest) {

  }

  @Override
  public void onRestoreDataResponse(RestoreDataResponse restoreDataResponse) {

  }

  @Override
  public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest anyTimeInterrogationRequest) {

  }

  @Override
  public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse anyTimeInterrogationResponse) {

  }

  @Override
  public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse provideSubscriberInfoResponse) {

  }

  @Override
  public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest insertSubscriberDataRequest) {

  }

  @Override
  public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse insertSubscriberDataResponse) {

  }

  @Override
  public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest deleteSubscriberDataRequest) {

  }

  @Override
  public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse deleteSubscriberDataResponse) {

  }

  @Override
  public void onCheckImeiRequest(CheckImeiRequest checkImeiRequest) {

  }

  @Override
  public void onCheckImeiResponse(CheckImeiResponse checkImeiResponse) {

  }

  @Override
  public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility activateTraceModeRequest_mobility) {

  }

  @Override
  public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility activateTraceModeResponse_mobility) {

  }

  @Override
  public void onSendRoutingInformationResponse(SendRoutingInformationResponse response) {

  }

  @Override
  public void onProvideRoamingNumberRequest(ProvideRoamingNumberRequest request) {

  }

  @Override
  public void onProvideRoamingNumberResponse(ProvideRoamingNumberResponse response) {

  }

  @Override
  public void onIstCommandRequest(IstCommandRequest request) {

  }

  @Override
  public void onIstCommandResponse(IstCommandResponse response) {

  }

}
