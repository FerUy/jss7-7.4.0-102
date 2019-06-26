/*
 * TeleStax, Open Source Cloud Communications  Copyright 2012.
 * and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

package org.mobicents.protocols.ss7.tools.simulator.tests.ati;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mobicents.protocols.ss7.isup.impl.message.parameter.LocationNumberImpl;
import org.mobicents.protocols.ss7.isup.message.parameter.LocationNumber;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPDialogListener;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPProvider;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorCode;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.errors.UnknownSubscriberDiagnostic;
import org.mobicents.protocols.ss7.map.api.primitives.AddressNature;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.mobicents.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.mobicents.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.mobicents.protocols.ss7.map.api.primitives.IMEI;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.primitives.NetworkResource;
import org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
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
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.DomainType;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationNumberMap;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSNetworkCapability;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.MSRadioAccessCapability;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.NotReachableReason;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.NumberPortabilityStatus;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PDPContextInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberStateChoice;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RAIdentity;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.RouteingNumber;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.UserCSGInformation;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.mobicents.protocols.ss7.map.errors.MAPErrorMessageUnknownSubscriberImpl;
import org.mobicents.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.mobicents.protocols.ss7.map.primitives.IMSIImpl;
import org.mobicents.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.EUtranCgiImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.GeodeticInformationImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.GeographicalInformationImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationEPSImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.LocationNumberMapImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.MSNetworkCapabilityImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.MSRadioAccessCapabilityImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.RAIdentityImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.RouteingNumberImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberInformation.TAIdImpl;
import org.mobicents.protocols.ss7.map.service.mobility.subscriberManagement.LSAIdentityImpl;
import org.mobicents.protocols.ss7.tcap.asn.ProblemImpl;
import org.mobicents.protocols.ss7.tcap.asn.comp.InvokeProblemType;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;
import org.mobicents.protocols.ss7.tools.simulator.Stoppable;
import org.mobicents.protocols.ss7.tools.simulator.common.TesterBase;
import org.mobicents.protocols.ss7.tools.simulator.level3.MapMan;
import org.mobicents.protocols.ss7.tools.simulator.management.TesterHost;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 *  @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *  @author sergey vetyutnev
 *
 */
public class TestAtiServerMan extends TesterBase implements TestAtiServerManMBean, Stoppable, MAPDialogListener, MAPServiceMobilityListener {

    public static String SOURCE_NAME = "TestAtiServer";

    private static Logger logger = Logger.getLogger(TestAtiServerMan.class);

    private final String name;
    private MapMan mapMan;

    private boolean isStarted = false;
    private int countAtiReq = 0;
    private int countAtiResp = 0;
    private String currentRequestDef = "";
    private boolean needSendSend = false;
    private boolean needSendClose = false;
    private int countErrSent = 0;

    public TestAtiServerMan() {
        super(SOURCE_NAME);
        this.name = "???";
    }

    public TestAtiServerMan(String name) {
        super(SOURCE_NAME);
        this.name = name;
    }

    public void setTesterHost(TesterHost testerHost) {
        this.testerHost = testerHost;
    }

    public void setMapMan(MapMan val) {
        this.mapMan = val;
    }

    @Override
    public String getCurrentRequestDef() {
        return "LastDialog: " + currentRequestDef;
    }

    @Override
    public ATIReaction getATIReaction() {
        return this.testerHost.getConfigurationData().getTestAtiServerConfigurationData().getATIReaction();
    }

    @Override
    public String getATIReaction_Value() {
        return this.testerHost.getConfigurationData().getTestAtiServerConfigurationData().getATIReaction().toString();
    }

    @Override
    public void setATIReaction(ATIReaction val) {
        this.testerHost.getConfigurationData().getTestAtiServerConfigurationData().setATIReaction(val);
        this.testerHost.markStore();
    }

    @Override
    public void putATIReaction(String val) {
        ATIReaction x = ATIReaction.createInstance(val);
        if (x != null)
            this.setATIReaction(x);
    }

    @Override
    public String getState() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(SOURCE_NAME);
        sb.append(": ");
        sb.append("<br>Count: countAtiReq-");
        sb.append(countAtiReq);
        sb.append(", countAtiResp-");
        sb.append(countAtiResp);
        sb.append(", countErrSent-");
        sb.append(countErrSent);
        sb.append("</html>");
        return sb.toString();
    }

    public boolean start() {
        this.countAtiReq = 0;
        this.countAtiResp = 0;
        this.countErrSent = 0;

        MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
        mapProvider.getMAPServiceMobility().acivate();
        mapProvider.getMAPServiceMobility().addMAPServiceListener(this);
        mapProvider.addMAPDialogListener(this);
        this.testerHost.sendNotif(SOURCE_NAME, "ATI Server has been started", "", Level.INFO);
        isStarted = true;

        return true;
    }

    @Override
    public void stop() {
        MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
        isStarted = false;
        mapProvider.getMAPServiceMobility().deactivate();
        mapProvider.getMAPServiceMobility().removeMAPServiceListener(this);
        mapProvider.removeMAPDialogListener(this);
        this.testerHost.sendNotif(SOURCE_NAME, "ATI Server has been stopped", "", Level.INFO);
    }

    @Override
    public void execute() {
    }

    @Override
    public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest ind) {
        if (!isStarted)
            return;

        this.countAtiReq++;

        MAPProvider mapProvider = this.mapMan.getMAPStack().getMAPProvider();
        MAPDialogMobility curDialog = ind.getMAPDialog();
        long invokeId = ind.getInvokeId();
        String uData = this.createAtiData(ind);

        this.testerHost.sendNotif(SOURCE_NAME, "Rcvd: atiReq", uData, Level.DEBUG);

        // Generate MAP errors for specific MSISDNs
        String msisdnAddress = ind.getSubscriberIdentity().getMSISDN().getAddress();
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
                createAtiRespData(curDialog.getLocalDialogId()), Level.INFO);
            return;
        }
        if (msisdnAddress.equalsIgnoreCase("99990000")) {
            MAPErrorMessage mapErrorMessageUnknownSubscriber = new MAPErrorMessageUnknownSubscriberImpl();
            try {
                curDialog.sendErrorComponent(invokeId, mapErrorMessageUnknownSubscriber);
                curDialog.close(false);
            } catch (MAPException e) {
                e.printStackTrace();
            }
            logger.debug("\nErrorComponent sent");
            this.testerHost.sendNotif(SOURCE_NAME, "Sent: ErrorComponent",
                createAtiRespData(curDialog.getLocalDialogId()), Level.INFO);
            return;
        }

        Random rand = new Random();

        RequestedInfo ri = ind.getRequestedInfo();
        try {
            ATIReaction atiReaction = this.testerHost.getConfigurationData().getTestAtiServerConfigurationData().getATIReaction();

            switch (atiReaction.intValue()) {
                case ATIReaction.VAL_RETURN_SUCCESS:
                    LocationInformation locationInformation = null;
                    LocationInformationEPS locationInformationEPS = null;
                    LocationInformationGPRS locationInformationGPRS = null;
                    Boolean currentLocationRetrieved = false;
                    Boolean saiPresent = null;
                    int mcc, mnc, lac, cellId;
                    CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = null;
                    CellGlobalIdOrServiceAreaIdFixedLength cgiOrSai;
                    LocationNumber locationNumber = null;
                    LocationNumberMap locationNumberMap = null;
                    String mscAddress = "5982123007";
                    String vlrAddress = "59899000231";
                    String sgsnAddress = "5982133021";
                    ISDNAddressString mscNumber, vlrNumber, sgsnNumber;
                    GeographicalInformation geographicalInformation;
                    GeodeticInformation geodeticInformation;
                    EUtranCgi eUtranCgi;
                    TAId taId;
                    LSAIdentity selectedLSAIdentity = null;
                    RAIdentity routeingAreaIdentity = null;
                    RouteingNumber routeingNumber = null;
                    currentLocationRetrieved = false;
                    UserCSGInformation userCSGInformation = null;
                    SubscriberStateChoice subscriberStateChoice = null;
                    PSSubscriberStateChoice psSubscriberStateChoice = null;
                    SubscriberState subscriberState = null;
                    PSSubscriberState psSubscriberState = null;
                    NotReachableReason notReachableReason = null;
                    ArrayList<PDPContextInfo> pdpContextInfoList = null;//new ArrayList<PDPContextInfo>();;
                    MNPInfoRes mnpInfoRes = null;
                    NumberPortabilityStatus numberPortabilityStatus = null;
                    MSClassmark2 msClassmark2 = null;
                    GPRSMSClass gprsMSClass = null;
                    IMEI imei = null;
                    MAPExtensionContainer extensionContainer = null;
                    if (ri.getLocationInformation()) {
                        if (ri.getCurrentLocation()) {
                            currentLocationRetrieved = true;
                        }
                        int ageOfLocationInformation = 5;
                        if (this.countAtiReq % 2 == 0)
                            saiPresent = false; // set saiPresent to false if this ATI request is even since test started
                        else
                            saiPresent = true; // set saiPresent to true if this ATI request is odd since test started
                        TypeOfShape geographicalTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
                        Double geographicalLatitude = -44.721023;
                        Double geographicalLongitude = 105.993418;
                        Double geographicalUncertainty = 10.0;
                        if (msisdnAddress.equalsIgnoreCase("77778888")) {
                            geographicalLatitude = 0.0;
                            geographicalLongitude = 0.0;
                            geographicalUncertainty = 0.0;
                        }
                        geographicalInformation = new GeographicalInformationImpl(geographicalTypeOfShape, geographicalLatitude, geographicalLongitude, geographicalUncertainty);
                        //GeographicalInformation geographicalInformation = mapProvider.getMAPParameterFactory().createGeographicalInformation(geographicalLatitude, geographicalLongitude, geographicalUncertainty);
                        TypeOfShape geodeticTypeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
                        Double geodeticLatitude = -45.002109;
                        Double geodeticLongitude = 110.100079;
                        Double geodeticlUncertainty = 5.0;
                        if (msisdnAddress.equalsIgnoreCase("77778888")) {
                            geodeticLatitude = 0.0;
                            geodeticLongitude = 0.0;
                            geodeticlUncertainty = 0.0;
                        }
                        int geodeticConfidence = 1;
                        int screeningAndPresentationIndicators = 3;
                        geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, geodeticTypeOfShape, geodeticLatitude, geodeticLongitude, geodeticlUncertainty, geodeticConfidence);
                        if (ri.getRequestedDomain() == null || ri.getRequestedDomain() == DomainType.csDomain) {
                            mscNumber = new ISDNAddressStringImpl(AddressNature.international_number,NumberingPlan.ISDN, mscAddress);
                            vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                                NumberingPlan.ISDN, vlrAddress);
                            int natureOfAddressIndicator = 4;
                            String locationNumberAddressDigits= "819203961904";
                            int numberingPlanIndicator = 1;
                            int internalNetworkNumberIndicator = 1;
                            int addressRepresentationRestrictedIndicator = 1;
                            int screeningIndicator = 3;
                            locationNumber = new LocationNumberImpl(natureOfAddressIndicator, locationNumberAddressDigits, numberingPlanIndicator,
                                internalNetworkNumberIndicator, addressRepresentationRestrictedIndicator, screeningIndicator);
                            locationNumberMap = null;
                            try {
                                locationNumberMap = new LocationNumberMapImpl(locationNumber);
                            } catch (MAPException e) {
                                e.printStackTrace();
                            }
                            byte[] lteCgi = hexStringToByteArray("37320100014e4a");
                            eUtranCgi = new EUtranCgiImpl(lteCgi);
                            byte[] trackinAreaId = hexStringToByteArray("3732013935");
                            taId = new TAIdImpl(trackinAreaId);
                            //byte[] mmeNom = {77, 77, 69, 55, 52, 56, 48, 48, 48, 49};
                            //DiameterIdentity mmeName = new DiameterIdentityImpl(mmeNom);
                            String mmneNameStr = "mmec03.mmeer3000.mme.epc.mnc002.mcc748.3gppnetwork.org";
                            byte[] mme = mmneNameStr.getBytes();
                            DiameterIdentity mmeName = new DiameterIdentityImpl(mme);
                            byte[] lsaId = {49, 51, 50};
                            LSAIdentity selectedLSAId = new LSAIdentityImpl(lsaId);
                            userCSGInformation = null;

                            mcc = 748;
                            mnc = 21;
                            lac = 32005;
                            cellId = 38221;
                            cgiOrSai = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, cellId);
                            cellGlobalIdOrServiceAreaIdOrLAI = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdOrLAI(cgiOrSai);
                            locationInformationEPS = new LocationInformationEPSImpl(eUtranCgi, taId, extensionContainer, geographicalInformation,
                                geodeticInformation, currentLocationRetrieved, ageOfLocationInformation, mmeName);
                            locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(ageOfLocationInformation, geographicalInformation,
                                vlrNumber, locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, extensionContainer, selectedLSAId, mscNumber, geodeticInformation,
                                currentLocationRetrieved, saiPresent, locationInformationEPS, userCSGInformation);
                        } else {
                            mcc = 748;
                            mnc = 23;
                            lac = 32006;
                            cellId = 38222;
                            if (this.countAtiReq % 2 == 0)
                                saiPresent = true; // set saiPresent to true if this ATI request is even since test started
                            else
                                saiPresent = false; // set saiPresent to false if this ATI request is odd since test started
                            cgiOrSai = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, cellId);
                            cellGlobalIdOrServiceAreaIdOrLAI = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdOrLAI(cgiOrSai);
                            byte[] raId = hexStringToByteArray("47f810393532");
                            routeingAreaIdentity = new RAIdentityImpl(raId);
                            sgsnNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                                NumberingPlan.ISDN, sgsnAddress);
                            byte[] lsaId = {49, 51, 49};
                            selectedLSAIdentity = new LSAIdentityImpl(lsaId);
                            locationInformationGPRS = mapProvider.getMAPParameterFactory().createLocationInformationGPRS(cellGlobalIdOrServiceAreaIdOrLAI,
                                routeingAreaIdentity, geographicalInformation, sgsnNumber, selectedLSAIdentity, extensionContainer, saiPresent, geodeticInformation,
                                currentLocationRetrieved, ageOfLocationInformation);
                        }
                    }

                    if (ri.getMnpRequestedInfo()) {
                        //RouteingNumber routeingNumber = mapProvider.getMAPParameterFactory().createRouteingNumber("5555555888");
                        routeingNumber = new RouteingNumberImpl("598123");
                        IMSI imsi = new IMSIImpl("748026871012345");
                        String msisdnStr = "59899077937";
                        ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
                            NumberingPlan.ISDN, msisdnStr);
                        numberPortabilityStatus = NumberPortabilityStatus.ownNumberNotPortedOut;
                        mnpInfoRes = mapProvider.getMAPParameterFactory().createMNPInfoRes(routeingNumber, imsi, msisdn, numberPortabilityStatus, extensionContainer);
                    }

                    if (ri.getImei()) {
                        if (ri.getRequestedDomain() == null || ri.getRequestedDomain() == DomainType.csDomain) {
                            imei = mapProvider.getMAPParameterFactory().createIMEI("011714004661050");
                        } else {
                            imei = mapProvider.getMAPParameterFactory().createIMEI("011714004661051");
                        }
                    }

                    if (ri.getMsClassmark()) {
                        if (ri.getRequestedDomain() == null || ri.getRequestedDomain() == DomainType.csDomain) {
                            byte[] classmark = {57, 58, 82};
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
                            if (ri.getRequestedDomain() == null || ri.getRequestedDomain() == DomainType.csDomain) {
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
                            if (ri.getRequestedDomain() == null || ri.getRequestedDomain() == DomainType.csDomain) {
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
                            if (ri.getRequestedDomain() == null || ri.getRequestedDomain() == DomainType.csDomain) {
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
                    if (ri.getSubscriberState()) {
                        if (ri.getRequestedDomain() == null || ri.getRequestedDomain() == DomainType.csDomain) {
                            subscriberState = mapProvider.getMAPParameterFactory().createSubscriberState(subscriberStateChoice, notReachableReason);
                        } else {
                            psSubscriberState = mapProvider.getMAPParameterFactory().createPSSubscriberState(psSubscriberStateChoice, notReachableReason, pdpContextInfoList);
                        }
                    }

                    SubscriberInfo subscriberInfo = mapProvider.getMAPParameterFactory().createSubscriberInfo(locationInformation, subscriberState, extensionContainer,
                        locationInformationGPRS, psSubscriberState, imei, msClassmark2, gprsMSClass, mnpInfoRes);

                    curDialog.addAnyTimeInterrogationResponse(invokeId, subscriberInfo, extensionContainer);

                    this.countAtiResp++;
                    uData = this.createAtiRespData(curDialog.getLocalDialogId());
                    this.testerHost.sendNotif(SOURCE_NAME, "Sent: atiResp", uData, Level.DEBUG);
                    break;

                case ATIReaction.VAL_ERROR_UNKNOWN_SUBSCRIBER:
                    MAPErrorMessage mapErrorMessage = null;
                    // MAPUserAbortChoice mapUserAbortChoice = new MAPUserAbortChoiceImpl();
                    // mapUserAbortChoice.setProcedureCancellationReason(ProcedureCancellationReason.handoverCancellation);
                    // curDialog.abort(mapUserAbortChoice);
                    // return;
                    mapErrorMessage = mapProvider.getMAPErrorMessageFactory().createMAPErrorMessageUnknownSubscriber(null,
                        UnknownSubscriberDiagnostic.imsiUnknown);

                    curDialog.sendErrorComponent(invokeId, mapErrorMessage);

                    this.countErrSent++;
                    uData = this.createErrorData(curDialog.getLocalDialogId(), (int) invokeId, mapErrorMessage);
                    this.testerHost.sendNotif(SOURCE_NAME, "Sent: errUnknSubs", uData, Level.DEBUG);
                    break;

                case ATIReaction.VAL_DATA_MISSING:
                    mapErrorMessage = mapProvider.getMAPErrorMessageFactory().createMAPErrorMessageExtensionContainer(
                        (long) MAPErrorCode.dataMissing, null);
                    curDialog.sendErrorComponent(invokeId, mapErrorMessage);

                    this.countErrSent++;
                    uData = this.createErrorData(curDialog.getLocalDialogId(), (int) invokeId, mapErrorMessage);
                    this.testerHost.sendNotif(SOURCE_NAME, "Sent: errDataMissing", uData, Level.DEBUG);
                    break;

                case ATIReaction.VAL_ERROR_SYSTEM_FAILURE:
                    mapErrorMessage = mapProvider.getMAPErrorMessageFactory().createMAPErrorMessageSystemFailure(
                        (long) curDialog.getApplicationContext().getApplicationContextVersion().getVersion(), NetworkResource.hlr, null, null);
                    curDialog.sendErrorComponent(invokeId, mapErrorMessage);

                    this.countErrSent++;
                    uData = this.createErrorData(curDialog.getLocalDialogId(), (int) invokeId, mapErrorMessage);
                    this.testerHost.sendNotif(SOURCE_NAME, "Sent: errSysFail", uData, Level.DEBUG);
                    break;
            }

            this.needSendClose = true;

        } catch (MAPException e) {
            this.testerHost.sendNotif(SOURCE_NAME, "Exception when invoking addAnyTimeInterrogationResponse() : " + e.getMessage(), e, Level.ERROR);
        }
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

    private String createAtiData(AnyTimeInterrogationRequest ind) {
        StringBuilder sb = new StringBuilder();
        sb.append("dialogId=");
        sb.append(ind.getMAPDialog().getLocalDialogId());
        sb.append(",\natiReq=");
        sb.append(ind);

        sb.append(",\nRemoteAddress=");
        sb.append(ind.getMAPDialog().getRemoteAddress());
        sb.append(",\nLocalAddress=");
        sb.append(ind.getMAPDialog().getLocalAddress());

        return sb.toString();
    }

    private String createAtiRespData(long dialogId) {
        StringBuilder sb = new StringBuilder();
        sb.append("dialogId=");
        sb.append(dialogId);
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

    @Override
    public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCancelLocationRequest(CancelLocationRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCancelLocationResponse(CancelLocationResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckImeiRequest(CheckImeiRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckImeiResponse(CheckImeiResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPurgeMSRequest(PurgeMSRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPurgeMSResponse(PurgeMSResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResetRequest(ResetRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRestoreDataRequest(RestoreDataRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRestoreDataResponse(RestoreDataResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendIdentificationRequest(SendIdentificationRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSendIdentificationResponse(SendIdentificationResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdateLocationRequest(UpdateLocationRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdateLocationResponse(UpdateLocationResponse arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse arg0) {
        // TODO Auto-generated method stub

    }

}