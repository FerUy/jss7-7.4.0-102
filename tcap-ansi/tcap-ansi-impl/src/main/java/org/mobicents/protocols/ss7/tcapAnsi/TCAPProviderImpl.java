/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2012, Telestax Inc and individual contributors
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

package org.mobicents.protocols.ss7.tcapAnsi;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javolution.util.FastMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.mobicents.protocols.ss7.sccp.RemoteSccpStatus;
import org.mobicents.protocols.ss7.sccp.SccpListener;
import org.mobicents.protocols.ss7.sccp.SccpProvider;
import org.mobicents.protocols.ss7.sccp.SignallingPointStatus;
import org.mobicents.protocols.ss7.sccp.message.MessageFactory;
import org.mobicents.protocols.ss7.sccp.message.SccpDataMessage;
import org.mobicents.protocols.ss7.sccp.message.SccpNoticeMessage;
import org.mobicents.protocols.ss7.sccp.parameter.ParameterFactory;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.tcapAnsi.api.ComponentPrimitiveFactory;
import org.mobicents.protocols.ss7.tcapAnsi.api.DialogPrimitiveFactory;
import org.mobicents.protocols.ss7.tcapAnsi.api.MessageType;
import org.mobicents.protocols.ss7.tcapAnsi.api.TCAPException;
import org.mobicents.protocols.ss7.tcapAnsi.api.TCAPProvider;
import org.mobicents.protocols.ss7.tcapAnsi.api.TCListener;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.ParseException;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.ProtocolVersion;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.Component;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.PAbortCause;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.Reject;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.RejectProblem;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.TCAbortMessage;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.TCQueryMessage;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.TCConversationMessage;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.TCResponseMessage;
import org.mobicents.protocols.ss7.tcapAnsi.api.asn.comp.TCUniMessage;
import org.mobicents.protocols.ss7.tcapAnsi.api.tc.dialog.Dialog;
import org.mobicents.protocols.ss7.tcapAnsi.api.tc.dialog.TRPseudoState;
import org.mobicents.protocols.ss7.tcapAnsi.api.tc.dialog.events.DraftParsedMessage;
import org.mobicents.protocols.ss7.tcapAnsi.asn.InvokeImpl;
import org.mobicents.protocols.ss7.tcapAnsi.asn.TCAbortMessageImpl;
import org.mobicents.protocols.ss7.tcapAnsi.asn.TCNoticeIndicationImpl;
import org.mobicents.protocols.ss7.tcapAnsi.asn.TCResponseMessageImpl;
import org.mobicents.protocols.ss7.tcapAnsi.asn.TCUnidentifiedMessage;
import org.mobicents.protocols.ss7.tcapAnsi.asn.TcapFactory;
import org.mobicents.protocols.ss7.tcapAnsi.asn.TransactionID;
import org.mobicents.protocols.ss7.tcapAnsi.asn.Utils;
import org.mobicents.protocols.ss7.tcapAnsi.tc.component.ComponentPrimitiveFactoryImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.DialogPrimitiveFactoryImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.DraftParsedMessageImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.TCQueryIndicationImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.TCConversationIndicationImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.TCResponseIndicationImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.TCPAbortIndicationImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.TCUniIndicationImpl;
import org.mobicents.protocols.ss7.tcapAnsi.tc.dialog.events.TCUserAbortIndicationImpl;

/**
 * @author amit bhayani
 * @author baranowb
 * @author sergey vetyutnev
 *
 */
public class TCAPProviderImpl implements TCAPProvider, SccpListener {

    private static final Logger logger = Logger.getLogger(TCAPProviderImpl.class); // listenres

    private transient List<TCListener> tcListeners = new CopyOnWriteArrayList<TCListener>();
    protected transient ScheduledExecutorService _EXECUTOR;
    // boundry for Uni directional dialogs :), tx id is always encoded
    // on 4 octets, so this is its max value
    // private static final long _4_OCTETS_LONG_FILL = 4294967295l;
    private transient ComponentPrimitiveFactory componentPrimitiveFactory;
    private transient DialogPrimitiveFactory dialogPrimitiveFactory;
    private transient SccpProvider sccpProvider;

    private transient MessageFactory messageFactory;
    private transient ParameterFactory parameterFactory;

    private transient TCAPStackImpl stack; // originating TX id ~=Dialog, its direct
    // mapping, but not described
    // explicitly...
    private transient FastMap<Long, DialogImpl> dialogs = new FastMap<Long, DialogImpl>();
    protected transient FastMap<PrevewDialogDataKey, PrevewDialogData> dialogPreviewList = new FastMap<PrevewDialogDataKey, PrevewDialogData>();

    private int seqControl = 0;
    private int ssn;
    private long curDialogId = 0;

    protected TCAPProviderImpl(SccpProvider sccpProvider, TCAPStackImpl stack, int ssn) {
        super();
        this.sccpProvider = sccpProvider;
        this.ssn = ssn;
        messageFactory = sccpProvider.getMessageFactory();
        parameterFactory = sccpProvider.getParameterFactory();
        this.stack = stack;

        this.componentPrimitiveFactory = new ComponentPrimitiveFactoryImpl(this);
        this.dialogPrimitiveFactory = new DialogPrimitiveFactoryImpl(this.componentPrimitiveFactory);
    }

    public boolean getPreviewMode() {
        return this.stack.getPreviewMode();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.ss7.tcap.api.TCAPStack#addTCListener(org.mobicents .protocols.ss7.tcap.api.TCListener)
     */

    public void addTCListener(TCListener lst) {
        if (this.tcListeners.contains(lst)) {
        } else {
            this.tcListeners.add(lst);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.ss7.tcap.api.TCAPStack#removeTCListener(org.mobicents .protocols.ss7.tcap.api.TCListener)
     */
    public void removeTCListener(TCListener lst) {
        this.tcListeners.remove(lst);

    }

    private boolean checkAvailableTxId(Long id) {
        if (!this.dialogs.containsKey(id))
            return true;
        else
            return false;
    }

    // some help methods... crude but will work for first impl.
    private Long getAvailableTxId() throws TCAPException {
        if (this.dialogs.size() >= this.stack.getMaxDialogs())
            throw new TCAPException("Current dialog count exceeds its maximum value");

        while (true) {
            if (this.curDialogId < this.stack.getDialogIdRangeStart())
                this.curDialogId = this.stack.getDialogIdRangeStart() - 1;
            if (++this.curDialogId > this.stack.getDialogIdRangeEnd())
                this.curDialogId = this.stack.getDialogIdRangeStart();
            Long id = this.curDialogId;
            if (checkAvailableTxId(id))
                return id;
        }
    }

    // get next Seq Control value available
    private synchronized int getNextSeqControl() {
        seqControl++;
        if (seqControl > 255) {
            seqControl = 0;

        }

        if (this.stack.getSlsRangeType() == SlsRangeType.Odd) {
            if (seqControl % 2 == 0)
                seqControl++;
        } else if (this.stack.getSlsRangeType() == SlsRangeType.Even) {
            if (seqControl %2 != 0)
                seqControl++;
        }

        return seqControl;
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.mobicents.protocols.ss7.tcap.api.TCAPProvider# getComopnentPrimitiveFactory()
     */
    public ComponentPrimitiveFactory getComponentPrimitiveFactory() {

        return this.componentPrimitiveFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.ss7.tcap.api.TCAPProvider#getDialogPrimitiveFactory ()
     */
    public DialogPrimitiveFactory getDialogPrimitiveFactory() {

        return this.dialogPrimitiveFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.ss7.tcap.api.TCAPProvider#getNewDialog(org.mobicents
     * .protocols.ss7.sccp.parameter.SccpAddress, org.mobicents.protocols.ss7.sccp.parameter.SccpAddress)
     */
    public Dialog getNewDialog(SccpAddress localAddress, SccpAddress remoteAddress) throws TCAPException {
        DialogImpl res = getNewDialog(localAddress, remoteAddress, getNextSeqControl(), null);
        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateAllLocalEstablishedDialogsCount();
            this.stack.getCounterProviderImpl().updateAllEstablishedDialogsCount();
        }
        this.setSsnToDialog(res, localAddress.getSubsystemNumber());
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.ss7.tcap.api.TCAPProvider#getNewDialog(org.mobicents
     * .protocols.ss7.sccp.parameter.SccpAddress, org.mobicents.protocols.ss7.sccp.parameter.SccpAddress, Long id)
     */
    public Dialog getNewDialog(SccpAddress localAddress, SccpAddress remoteAddress, Long id) throws TCAPException {
        DialogImpl res = getNewDialog(localAddress, remoteAddress, getNextSeqControl(), id);
        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateAllLocalEstablishedDialogsCount();
            this.stack.getCounterProviderImpl().updateAllEstablishedDialogsCount();
        }
        this.setSsnToDialog(res, localAddress.getSubsystemNumber());
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.protocols.ss7.tcap.api.TCAPProvider#getNewUnstructuredDialog
     * (org.mobicents.protocols.ss7.sccp.parameter.SccpAddress, org.mobicents.protocols.ss7.sccp.parameter.SccpAddress)
     */
    public Dialog getNewUnstructuredDialog(SccpAddress localAddress, SccpAddress remoteAddress) throws TCAPException {
        DialogImpl res = _getDialog(localAddress, remoteAddress, false, getNextSeqControl(), null);
        this.setSsnToDialog(res, localAddress.getSubsystemNumber());
        return res;
    }

    private DialogImpl getNewDialog(SccpAddress localAddress, SccpAddress remoteAddress, int seqControl, Long id) throws TCAPException {
        return _getDialog(localAddress, remoteAddress, true, seqControl, id);
    }

    private DialogImpl _getDialog(SccpAddress localAddress, SccpAddress remoteAddress, boolean structured, int seqControl, Long id)
            throws TCAPException {

        if (this.stack.getPreviewMode()) {
            throw new TCAPException("Can not create a Dialog in a PreviewMode");
        }

        if (localAddress == null) {
            throw new NullPointerException("LocalAddress must not be null");
        }

        synchronized (this.dialogs) {
            if (id == null) {
                id = this.getAvailableTxId();
            } else {
                if (!checkAvailableTxId(id)) {
                    throw new TCAPException("Suggested local TransactionId is already present in system: " + id);
                }
            }
            if (structured) {
                DialogImpl di = new DialogImpl(localAddress, remoteAddress, id, structured, this._EXECUTOR, this, seqControl, this.stack.getPreviewMode());

                this.dialogs.put(id, di);
                if (this.stack.getStatisticsEnabled()) {
                    this.stack.getCounterProviderImpl().updateMinDialogsCount(this.dialogs.size());
                    this.stack.getCounterProviderImpl().updateMaxDialogsCount(this.dialogs.size());
                }

                return di;
            } else {
                DialogImpl di = new DialogImpl(localAddress, remoteAddress, id, structured, this._EXECUTOR, this, seqControl, this.stack.getPreviewMode());
                return di;
            }
        }
    }

    private void setSsnToDialog(DialogImpl di, int ssn) {
        if (ssn != this.ssn) {
            if (ssn <= 0 || !this.stack.isExtraSsnPresent(ssn))
                ssn = this.ssn;
        }
        di.setLocalSsn(ssn);
    }

    @Override
    public int getCurrentDialogsCount() {
        return this.dialogs.size();
    }

    public void send(byte[] data, boolean returnMessageOnError, SccpAddress destinationAddress, SccpAddress originatingAddress,
            int seqControl, int networkId, int localSsn) throws IOException {
        if (this.stack.getPreviewMode())
            return;

        SccpDataMessage msg = messageFactory.createDataMessageClass1(destinationAddress, originatingAddress, data, seqControl,
                localSsn, returnMessageOnError, null, null);
        msg.setNetworkId(networkId);
        sccpProvider.send(msg);
    }

    public int getMaxUserDataLength(SccpAddress calledPartyAddress, SccpAddress callingPartyAddress, int networkId) {
        return this.sccpProvider.getMaxUserDataLength(calledPartyAddress, callingPartyAddress, networkId);
    }

    public void deliver(DialogImpl dialogImpl, TCQueryIndicationImpl msg) {

        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateTcQueryReceivedCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onTCQuery(msg);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering data to transport layer.", e);
            }
        }

    }

    public void deliver(DialogImpl dialogImpl, TCConversationIndicationImpl tcContinueIndication) {

        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateTcConversationReceivedCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onTCConversation(tcContinueIndication);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering data to transport layer.", e);
            }
        }

    }

    public void deliver(DialogImpl dialogImpl, TCResponseIndicationImpl tcEndIndication) {

        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateTcResponseReceivedCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onTCResponse(tcEndIndication);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering data to transport layer.", e);
            }
        }
    }

    public void deliver(DialogImpl dialogImpl, TCPAbortIndicationImpl tcAbortIndication) {

        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateTcPAbortReceivedCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onTCPAbort(tcAbortIndication);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering data to transport layer.", e);
            }
        }

    }

    public void deliver(DialogImpl dialogImpl, TCUserAbortIndicationImpl tcAbortIndication) {

        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateTcUserAbortReceivedCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onTCUserAbort(tcAbortIndication);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering data to transport layer.", e);
            }
        }

    }

    public void deliver(DialogImpl dialogImpl, TCUniIndicationImpl tcUniIndication) {

        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateTcUniReceivedCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onTCUni(tcUniIndication);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering data to transport layer.", e);
            }
        }
    }

    public void deliver(DialogImpl dialogImpl, TCNoticeIndicationImpl tcNoticeIndication) {
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onTCNotice(tcNoticeIndication);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering data to transport layer.", e);
            }
        }
    }

    public void release(DialogImpl d) {
        Long did = d.getLocalDialogId();

        if (!d.getPreviewMode()) {
            synchronized (this.dialogs) {
                this.dialogs.remove(did);
                if (this.stack.getStatisticsEnabled()) {
                    this.stack.getCounterProviderImpl().updateMinDialogsCount(this.dialogs.size());
                    this.stack.getCounterProviderImpl().updateMaxDialogsCount(this.dialogs.size());
                }
            }

            this.doRelease(d);
        }
    }

    private void doRelease(DialogImpl d) {

        if (d.isStructured() && this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateDialogReleaseCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onDialogReleased(d);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering dialog release.", e);
            }
        }
    }

    /**
     * @param d
     */
    public void timeout(DialogImpl d) {

        if (this.stack.getStatisticsEnabled()) {
            this.stack.getCounterProviderImpl().updateDialogTimeoutCount();
        }
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onDialogTimeout(d);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering dialog release.", e);
            }
        }
    }

    public TCAPStackImpl getStack() {
        return this.stack;
    }

    // ///////////////////////////////////////////
    // Some methods invoked by operation FSM //
    // //////////////////////////////////////////
    public Future createOperationTimer(Runnable operationTimerTask, long invokeTimeout) {

        return this._EXECUTOR.schedule(operationTimerTask, invokeTimeout, TimeUnit.MILLISECONDS);
    }

    public void operationTimedOut(InvokeImpl tcInvokeRequestImpl) {
        try {
            for (TCListener lst : this.tcListeners) {
                lst.onInvokeTimeout(tcInvokeRequestImpl);
            }
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Received exception while delivering Begin.", e);
            }
        }
    }

    void start() {
        logger.info("Starting TCAP Provider");

        this._EXECUTOR = Executors.newScheduledThreadPool(4);
        this.sccpProvider.registerSccpListener(ssn, this);
        logger.info("Registered SCCP listener with ssn " + ssn);

        List<Integer> extraSsns = this.stack.getExtraSsns();
        if (extraSsns != null) {
            for (Integer I1 : extraSsns) {
                if (I1 != null) {
                    int extraSsn = I1;
                    this.sccpProvider.registerSccpListener(extraSsn, this);
                    logger.info("Registered SCCP listener with extra ssn " + extraSsn);
                }
            }
        }
    }

    void stop() {
        this._EXECUTOR.shutdown();
        this.sccpProvider.deregisterSccpListener(ssn);

        List<Integer> extraSsns = this.stack.getExtraSsns();
        if (extraSsns != null) {
            for (Integer I1 : extraSsns) {
                if (I1 != null) {
                    int extraSsn = I1;
                    this.sccpProvider.deregisterSccpListener(extraSsn);
                }
            }
        }

        this.dialogs.clear();
        this.dialogPreviewList.clear();
    }

    protected void sendProviderAbort(PAbortCause pAbortCause, byte[] remoteTransactionId, SccpAddress remoteAddress,
            SccpAddress localAddress, int seqControl, int networkId) {
        if (this.stack.getPreviewMode())
            return;

        TCAbortMessageImpl msg = (TCAbortMessageImpl) TcapFactory.createTCAbortMessage();
        msg.setDestinationTransactionId(remoteTransactionId);
        msg.setPAbortCause(pAbortCause);

        AsnOutputStream aos = new AsnOutputStream();
        try {
            msg.encode(aos);
            if (this.stack.getStatisticsEnabled()) {
                this.stack.getCounterProviderImpl().updateTcPAbortSentCount();
            }
            this.send(aos.toByteArray(), false, remoteAddress, localAddress, seqControl, networkId, localAddress.getSubsystemNumber());
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Failed to send message: ", e);
            }
        }
    }

    protected void sendRejectAsProviderAbort(PAbortCause pAbortCause, byte[] remoteTransactionId, SccpAddress remoteAddress,
            SccpAddress localAddress, int seqControl, int networkId) {
        if (this.stack.getPreviewMode())
            return;

        RejectProblem rp = RejectProblem.getFromPAbortCause(pAbortCause);
        if (rp == null)
            rp = RejectProblem.transactionBadlyStructuredTransPortion;

        TCResponseMessageImpl msg = (TCResponseMessageImpl) TcapFactory.createTCResponseMessage();
        msg.setDestinationTransactionId(remoteTransactionId);
        Component[] cc = new Component[1];
        Reject r = TcapFactory.createComponentReject();
        r.setProblem(rp);
        cc[0] = r;
        msg.setComponent(cc);

        AsnOutputStream aos = new AsnOutputStream();
        try {
            msg.encode(aos);
            if (this.stack.getStatisticsEnabled()) {
                this.stack.getCounterProviderImpl().updateTcPAbortSentCount();
            }
            this.send(aos.toByteArray(), false, remoteAddress, localAddress, seqControl, networkId, localAddress.getSubsystemNumber());
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Failed to send message: ", e);
            }
        }
    }

    public void onCoordRequest(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public void onCoordResponse(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public void onMessage(SccpDataMessage message) {

        try {
            byte[] data = message.getData();
            SccpAddress localAddress = message.getCalledPartyAddress();
            SccpAddress remoteAddress = message.getCallingPartyAddress();

            // asnData - it should pass
            AsnInputStream ais = new AsnInputStream(data);

            // this should have TC message tag :)
            int tag = ais.readTag();

            if (ais.getTagClass() != Tag.CLASS_PRIVATE) {
                unrecognizedPackageType(message, localAddress, remoteAddress, ais, tag, message.getNetworkId());
                return;
            }

            switch (tag) {
            case TCConversationMessage._TAG_CONVERSATION_WITH_PERM:
            case TCConversationMessage._TAG_CONVERSATION_WITHOUT_PERM:
                TCConversationMessage tcm = null;
                try {
                    tcm = TcapFactory.createTCConversationMessage(ais);
                } catch (ParseException e) {
                    logger.error("ParseException when parsing TCConversationMessage: " + e.toString(), e);

                    // parsing OriginatingTransactionId
                    ais = new AsnInputStream(data);
                    tag = ais.readTag();
                    TCUnidentifiedMessage tcUnidentified = new TCUnidentifiedMessage();
                    tcUnidentified.decode(ais);
                    if (tcUnidentified.getOriginatingTransactionId() != null) {
                        boolean isDP = false;
                        if (tcUnidentified.isDialogPortionExists()) {
                            isDP = true;
                        } else {
                            Dialog ddi = null;
                            if (tcUnidentified.getDestinationTransactionId() != null) {
                                long dialogId = Utils.decodeTransactionId(tcUnidentified.getDestinationTransactionId());
                                ddi = this.dialogs.get(dialogId);
                            }
                            if (ddi != null && ddi.getProtocolVersion() != null)
                                isDP = true;
                        }
                        if (isDP) {
                            if (e.getPAbortCauseType() != null) {
                                this.sendProviderAbort(e.getPAbortCauseType(), tcUnidentified.getOriginatingTransactionId(), remoteAddress, localAddress,
                                        message.getSls(), message.getNetworkId());
                            } else {
                                this.sendProviderAbort(PAbortCause.BadlyStructuredDialoguePortion, tcUnidentified.getOriginatingTransactionId(), remoteAddress,
                                        localAddress, message.getSls(), message.getNetworkId());
                            }
                        } else {
                            if (e.getPAbortCauseType() != null) {
                                this.sendRejectAsProviderAbort(e.getPAbortCauseType(), tcUnidentified.getOriginatingTransactionId(), remoteAddress,
                                        localAddress, message.getSls(), message.getNetworkId());
                            } else {
                                this.sendRejectAsProviderAbort(PAbortCause.BadlyStructuredTransactionPortion, tcUnidentified.getOriginatingTransactionId(),
                                        remoteAddress, localAddress, message.getSls(), message.getNetworkId());
                            }
                        }
                    }
                    return;
                }

                long dialogId = Utils.decodeTransactionId(tcm.getDestinationTransactionId());
                DialogImpl di;
                if (this.stack.getPreviewMode()) {
                    PrevewDialogDataKey ky1 = new PrevewDialogDataKey(message.getIncomingDpc(),
                            (message.getCalledPartyAddress().getGlobalTitle() != null ? message.getCalledPartyAddress().getGlobalTitle().getDigits() : null),
                            message.getCalledPartyAddress().getSubsystemNumber(), dialogId);
                    long dId = Utils.decodeTransactionId(tcm.getOriginatingTransactionId());
                    PrevewDialogDataKey ky2 = new PrevewDialogDataKey(message.getIncomingOpc(),
                            (message.getCallingPartyAddress().getGlobalTitle() != null ? message.getCallingPartyAddress().getGlobalTitle().getDigits() : null),
                            message.getCallingPartyAddress().getSubsystemNumber(), dId);
                    di = (DialogImpl) this.getPreviewDialog(ky1, ky2, localAddress, remoteAddress, seqControl);
                    setSsnToDialog(di, message.getCalledPartyAddress().getSubsystemNumber());
                } else {
                    di = this.dialogs.get(dialogId);
                }
                if (di == null) {
                    logger.warn("TC-Conversation: No dialog/transaction for id: " + dialogId);
                    if (tcm.getDialogPortion() != null) {
                        this.sendProviderAbort(PAbortCause.UnassignedRespondingTransactionID, tcm.getOriginatingTransactionId(), remoteAddress, localAddress,
                                message.getSls(), message.getNetworkId());
                    } else {
                        this.sendRejectAsProviderAbort(PAbortCause.UnassignedRespondingTransactionID, tcm.getOriginatingTransactionId(), remoteAddress,
                                localAddress, message.getSls(), message.getNetworkId());
                    }
                } else {
                    di.processConversation(tcm, localAddress, remoteAddress, tag == TCConversationMessage._TAG_CONVERSATION_WITH_PERM);
                }

                break;

            case TCQueryMessage._TAG_QUERY_WITH_PERM:
            case TCQueryMessage._TAG_QUERY_WITHOUT_PERM:
                TCQueryMessage tcb = null;
                try {
                    tcb = TcapFactory.createTCQueryMessage(ais);
                } catch (ParseException e) {
                    logger.error("ParseException when parsing TCQueryMessage: " + e.toString(), e);

                    // parsing OriginatingTransactionId
                    ais = new AsnInputStream(data);
                    tag = ais.readTag();
                    TCUnidentifiedMessage tcUnidentified = new TCUnidentifiedMessage();
                    tcUnidentified.decode(ais);
                    if (tcUnidentified.getOriginatingTransactionId() != null) {
                        if (tcUnidentified.isDialogPortionExists()) {
                            if (e.getPAbortCauseType() != null) {
                                this.sendProviderAbort(e.getPAbortCauseType(), tcUnidentified.getOriginatingTransactionId(), remoteAddress, localAddress,
                                        message.getSls(), message.getNetworkId());
                            } else {
                                this.sendProviderAbort(PAbortCause.BadlyStructuredDialoguePortion, tcUnidentified.getOriginatingTransactionId(), remoteAddress,
                                        localAddress, message.getSls(), message.getNetworkId());
                            }
                        } else {
                            if (e.getPAbortCauseType() != null) {
                                this.sendRejectAsProviderAbort(e.getPAbortCauseType(), tcUnidentified.getOriginatingTransactionId(), remoteAddress,
                                        localAddress, message.getSls(), message.getNetworkId());
                            } else {
                                this.sendRejectAsProviderAbort(PAbortCause.BadlyStructuredTransactionPortion, tcUnidentified.getOriginatingTransactionId(),
                                        remoteAddress, localAddress, message.getSls(), message.getNetworkId());
                            }
                        }
                    }
                    return;
                }

                di = null;
                try {
                    if (this.stack.getPreviewMode()) {
                        long dId = Utils.decodeTransactionId(tcb.getOriginatingTransactionId());
                        PrevewDialogDataKey ky = new PrevewDialogDataKey(message.getIncomingOpc(),
                                (message.getCallingPartyAddress().getGlobalTitle() != null ? message.getCallingPartyAddress().getGlobalTitle().getDigits()
                                        : null), message.getCallingPartyAddress().getSubsystemNumber(), dId);
                        di = (DialogImpl) this.createPreviewDialog(ky, localAddress, remoteAddress, seqControl);
                        setSsnToDialog(di, message.getCalledPartyAddress().getSubsystemNumber());
                    } else {
                        di = (DialogImpl) this.getNewDialog(localAddress, remoteAddress, message.getSls(), null);
                        setSsnToDialog(di, message.getCalledPartyAddress().getSubsystemNumber());
                    }

                } catch (TCAPException e) {
                    if (tcb.getDialogPortion() != null) {
                        this.sendProviderAbort(PAbortCause.ResourceUnavailable, tcb.getOriginatingTransactionId(), remoteAddress, localAddress,
                                message.getSls(), message.getNetworkId());
                    } else {
                        this.sendRejectAsProviderAbort(PAbortCause.ResourceUnavailable, tcb.getOriginatingTransactionId(), remoteAddress, localAddress,
                                message.getSls(), message.getNetworkId());
                    }
                    logger.error("Can not add a new dialog when receiving TCBeginMessage: " + e.getMessage(), e);
                    return;
                }

                if (tcb.getDialogPortion() != null) {
                    if (tcb.getDialogPortion().getProtocolVersion() != null) {
                        di.setProtocolVersion(tcb.getDialogPortion().getProtocolVersion());
                    } else {
                        ProtocolVersion pv = TcapFactory.createProtocolVersionEmpty();
                        di.setProtocolVersion(pv);
                    }
                }

                if (this.stack.getStatisticsEnabled()) {
                    this.stack.getCounterProviderImpl().updateAllRemoteEstablishedDialogsCount();
                    this.stack.getCounterProviderImpl().updateAllEstablishedDialogsCount();
                }
                di.setNetworkId(message.getNetworkId());
                di.processQuery(tcb, localAddress, remoteAddress, tag == TCQueryMessage._TAG_QUERY_WITH_PERM);

                if (this.stack.getPreviewMode()) {
                    di.getPrevewDialogData().setLastACN(di.getApplicationContextName());
                    di.getPrevewDialogData().setOperationsSentB(di.operationsSent);
                    di.getPrevewDialogData().setOperationsSentA(di.operationsSentA);
                }

                break;

            case TCResponseMessage._TAG_RESPONSE:
                TCResponseMessage teb = null;
                try {
                    teb = TcapFactory.createTCResponseMessage(ais);
                } catch (ParseException e) {
                    logger.error("ParseException when parsing TCResponseMessage: " + e.toString(), e);
                    return;
                }

                dialogId = Utils.decodeTransactionId(teb.getDestinationTransactionId());
                if (this.stack.getPreviewMode()) {
                    PrevewDialogDataKey ky = new PrevewDialogDataKey(message.getIncomingDpc(),
                            (message.getCalledPartyAddress().getGlobalTitle() != null ? message.getCalledPartyAddress().getGlobalTitle().getDigits() : null),
                            message.getCalledPartyAddress().getSubsystemNumber(), dialogId);
                    di = (DialogImpl) this.getPreviewDialog(ky, null, localAddress, remoteAddress, seqControl);
                    setSsnToDialog(di, message.getCalledPartyAddress().getSubsystemNumber());
                } else {
                    di = this.dialogs.get(dialogId);
                }
                if (di == null) {
                    logger.warn("TC-Response: No dialog/transaction for id: " + dialogId);
                } else {
                    di.processResponse(teb, localAddress, remoteAddress);

                    if (this.stack.getPreviewMode()) {
                        this.removePreviewDialog(di);
                    }
                }
                break;

            case TCAbortMessage._TAG_ABORT:
                TCAbortMessage tub = null;
                try {
                    tub = TcapFactory.createTCAbortMessage(ais);
                } catch (ParseException e) {
                    logger.error("ParseException when parsing TCAbortMessage: " + e.toString(), e);
                    return;
                }

                dialogId = Utils.decodeTransactionId(tub.getDestinationTransactionId());
                if (this.stack.getPreviewMode()) {
                    long dId = Utils.decodeTransactionId(tub.getDestinationTransactionId());
                    PrevewDialogDataKey ky = new PrevewDialogDataKey(message.getIncomingDpc(),
                            (message.getCalledPartyAddress().getGlobalTitle() != null ? message.getCalledPartyAddress().getGlobalTitle().getDigits() : null),
                            message.getCalledPartyAddress().getSubsystemNumber(), dId);
                    di = (DialogImpl) this.getPreviewDialog(ky, null, localAddress, remoteAddress, seqControl);
                    setSsnToDialog(di, message.getCalledPartyAddress().getSubsystemNumber());
                } else {
                    di = this.dialogs.get(dialogId);
                }
                if (di == null) {
                    logger.warn("TC-ABORT: No dialog/transaction for id: " + dialogId);
                } else {
                    di.processAbort(tub, localAddress, remoteAddress);

                    if (this.stack.getPreviewMode()) {
                        this.removePreviewDialog(di);
                    }
                }
                break;

            case TCUniMessage._TAG_UNI:
                TCUniMessage tcuni;
                try {
                    tcuni = TcapFactory.createTCUniMessage(ais);
                } catch (ParseException e) {
                    logger.error("ParseException when parsing TCUniMessage: " + e.toString(), e);
                    return;
                }

                DialogImpl uniDialog = (DialogImpl) this.getNewUnstructuredDialog(localAddress, remoteAddress);
                setSsnToDialog(uniDialog, message.getCalledPartyAddress().getSubsystemNumber());
                uniDialog.processUni(tcuni, localAddress, remoteAddress);
                break;

            default:
                unrecognizedPackageType(message, localAddress, remoteAddress, ais, tag, message.getNetworkId());
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Error while decoding Rx SccpMessage=%s", message), e);
        }
    }

    private void unrecognizedPackageType(SccpDataMessage message, SccpAddress localAddress, SccpAddress remoteAddress, AsnInputStream ais, int tag,
            int networkId) {
        if (this.stack.getPreviewMode()) {
            return;
        }

        logger.error(String.format("Rx unidentified messageType=%s. SccpMessage=%s", tag, message));
        TCUnidentifiedMessage tcUnidentified = new TCUnidentifiedMessage();
        try {
            tcUnidentified.decode(ais);
        } catch (ParseException e) {
            // we do nothing
        }

        if (tcUnidentified.getOriginatingTransactionId() != null) {
            byte[] otid = tcUnidentified.getOriginatingTransactionId();

            if (tcUnidentified.getDestinationTransactionId() != null) {
                Long dtid = Utils.decodeTransactionId(tcUnidentified.getDestinationTransactionId());
                this.sendProviderAbort(PAbortCause.UnrecognizedPackageType, otid, remoteAddress, localAddress, message.getSls(), networkId);
            } else {
                this.sendProviderAbort(PAbortCause.UnrecognizedPackageType, otid, remoteAddress, localAddress, message.getSls(), networkId);
            }
        } else {
            this.sendProviderAbort(PAbortCause.UnrecognizedPackageType, new byte[4], remoteAddress, localAddress, message.getSls(), networkId);
        }
    }

    public void onNotice(SccpNoticeMessage msg) {

        if (this.stack.getPreviewMode()) {
            return;
        }

        DialogImpl dialog = null;

        try {
            byte[] data = msg.getData();
            AsnInputStream ais = new AsnInputStream(data);

            // this should have TC message tag :)
            int tag = ais.readTag();

            TCUnidentifiedMessage tcUnidentified = new TCUnidentifiedMessage();
            tcUnidentified.decode(ais);

            if (tcUnidentified.getOriginatingTransactionId() != null) {
                long otid = Utils.decodeTransactionId(tcUnidentified.getOriginatingTransactionId());
                dialog = this.dialogs.get(otid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Error while decoding Rx SccpNoticeMessage=%s", msg), e);
        }

        TCNoticeIndicationImpl ind = new TCNoticeIndicationImpl();
        ind.setRemoteAddress(msg.getCallingPartyAddress());
        ind.setLocalAddress(msg.getCalledPartyAddress());
        ind.setDialog(dialog);
        ind.setReportCause(msg.getReturnCause().getValue());

        if (dialog != null) {
            try {
                dialog.dialogLock.lock();

                this.deliver(dialog, ind);

                if (dialog.getState() != TRPseudoState.Active) {
                    dialog.release();
                }
            } finally {
                dialog.dialogLock.unlock();
            }
        } else {
            this.deliver(dialog, ind);
        }
    }

    public void onPcState(int arg0, SignallingPointStatus arg1, int arg2, RemoteSccpStatus arg3) {
        // TODO Auto-generated method stub

    }

    public void onState(int arg0, int arg1, boolean arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    private Dialog createPreviewDialog(PrevewDialogDataKey ky, SccpAddress localAddress, SccpAddress remoteAddress,
            int seqControl) throws TCAPException {
        synchronized (this.dialogPreviewList) {
            if (this.dialogPreviewList.size() >= this.stack.getMaxDialogs())
                throw new TCAPException("Current dialog count exceeds its maximum value");

            // checking if a Dialog is current already exists
            PrevewDialogData pddx = this.dialogPreviewList.get(ky);
            if (pddx != null) {
                this.removePreviewDialog(pddx);
                throw new TCAPException("Dialog with trId=" + ky.origTxId + " is already exists - we ignore it and drops curent dialog");
            }

            Long dialogId = this.getAvailableTxIdPreview();
            PrevewDialogData pdd = new PrevewDialogData(this, dialogId);
            this.dialogPreviewList.put(ky, pdd);
            DialogImpl di = new DialogImpl(localAddress, remoteAddress, seqControl, this._EXECUTOR, this, pdd, false);
            pdd.setPrevewDialogDataKey1(ky);

            pdd.startIdleTimer();

            return di;
        }
    }

    private Long getAvailableTxIdPreview() throws TCAPException {
        while (true) {
            if (this.curDialogId < this.stack.getDialogIdRangeStart())
                this.curDialogId = this.stack.getDialogIdRangeStart() - 1;
            if (++this.curDialogId > this.stack.getDialogIdRangeEnd())
                this.curDialogId = this.stack.getDialogIdRangeStart();
            Long id = this.curDialogId;
            return id;
        }
    }

    protected Dialog getPreviewDialog(PrevewDialogDataKey ky1, PrevewDialogDataKey ky2, SccpAddress localAddress,
            SccpAddress remoteAddress, int seqControl) {
        synchronized (this.dialogPreviewList) {
            PrevewDialogData pdd = this.dialogPreviewList.get(ky1);
            DialogImpl di = null;
            boolean sideB = false;
            if (pdd != null) {
                sideB = pdd.getPrevewDialogDataKey1().equals(ky1);
                di = new DialogImpl(localAddress, remoteAddress, seqControl, this._EXECUTOR, this, pdd, sideB);
            } else {
                if (ky2 != null)
                    pdd = this.dialogPreviewList.get(ky2);
                if (pdd != null) {
                    sideB = pdd.getPrevewDialogDataKey1().equals(ky1);
                    di = new DialogImpl(localAddress, remoteAddress, seqControl, this._EXECUTOR, this, pdd, sideB);
                } else {
                    return null;
                }
            }

            pdd.restartIdleTimer();

            if (pdd.getPrevewDialogDataKey2() == null && ky2 != null) {
                if (pdd.getPrevewDialogDataKey1().equals(ky1))
                    pdd.setPrevewDialogDataKey2(ky2);
                else
                    pdd.setPrevewDialogDataKey2(ky1);
                this.dialogPreviewList.put(pdd.getPrevewDialogDataKey2(), pdd);
            }

            return di;
        }
    }

    protected void removePreviewDialog(DialogImpl di) {
        synchronized (this.dialogPreviewList) {
            PrevewDialogData pdd = this.dialogPreviewList.get(di.prevewDialogData.getPrevewDialogDataKey1());
            if (pdd == null && di.prevewDialogData.getPrevewDialogDataKey2() != null) {
                pdd = this.dialogPreviewList.get(di.prevewDialogData.getPrevewDialogDataKey2());
            }

            if (pdd != null)
                removePreviewDialog(pdd);
        }

        this.doRelease(di);
    }

    protected void removePreviewDialog(PrevewDialogData pdd) {
        synchronized (this.dialogPreviewList) {
            this.dialogPreviewList.remove(pdd.getPrevewDialogDataKey1());
            if (pdd.getPrevewDialogDataKey2() != null) {
                this.dialogPreviewList.remove(pdd.getPrevewDialogDataKey2());
            }
        }
        pdd.stopIdleTimer();

        // TODO ??? : create Dialog and invoke "this.doRelease(di);"
    }

    @Override
    public DraftParsedMessage parseMessageDraft(byte[] data) {
        try {
            DraftParsedMessageImpl res = new DraftParsedMessageImpl();

            AsnInputStream ais = new AsnInputStream(data);

            int tag = ais.readTag();

            if (ais.getTagClass() != Tag.CLASS_PRIVATE) {
                res.setParsingErrorReason("Message tag class must be CLASS_PRIVATE");
                return res;
            }

            switch (tag) {
                case TCConversationMessage._TAG_CONVERSATION_WITH_PERM:
                case TCConversationMessage._TAG_CONVERSATION_WITHOUT_PERM:
                    AsnInputStream localAis = ais.readSequenceStream();

                    // transaction portion
                    TransactionID tid = TcapFactory.readTransactionID(localAis);
                    if (tid.getFirstElem() == null || tid.getSecondElem() == null) {
                        res.setParsingErrorReason("TCTCConversationMessage decoding error: a message does not contain both both origination and resination transactionId");
                        return res;
                    }
                    byte[] originatingTransactionId = tid.getFirstElem();
                    res.setOriginationDialogId(Utils.decodeTransactionId(originatingTransactionId));

                    byte[] destinationTransactionId = tid.getSecondElem();
                    res.setDestinationDialogId(Utils.decodeTransactionId(destinationTransactionId));

                    if (tag == TCConversationMessage._TAG_CONVERSATION_WITH_PERM)
                        res.setMessageType(MessageType.ConversationWithPerm);
                    else
                        res.setMessageType(MessageType.ConversationWithoutPerm);
                    break;

                case TCQueryMessage._TAG_QUERY_WITH_PERM:
                case TCQueryMessage._TAG_QUERY_WITHOUT_PERM:
                    localAis = ais.readSequenceStream();

                    // transaction portion
                    tid = TcapFactory.readTransactionID(localAis);
                    if (tid.getFirstElem() == null || tid.getSecondElem() != null) {
                        res.setParsingErrorReason("TCQueryMessage decoding error: transactionId must contain only one transactionId");
                        return res;
                    }
                    originatingTransactionId = tid.getFirstElem();
                    res.setOriginationDialogId(Utils.decodeTransactionId(originatingTransactionId));

                    if (tag == TCQueryMessage._TAG_QUERY_WITH_PERM)
                        res.setMessageType(MessageType.QueryWithPerm);
                    else
                        res.setMessageType(MessageType.QueryWithoutPerm);
                    break;

                case TCResponseMessage._TAG_RESPONSE:
                    localAis = ais.readSequenceStream();

                    // transaction portion
                    tid = TcapFactory.readTransactionID(localAis);
                    if (tid.getFirstElem() == null || tid.getSecondElem() != null) {
                        res.setParsingErrorReason("TCResponseMessage decoding error: transactionId must contain only one transactionId");
                        return res;
                    }
                    destinationTransactionId = tid.getFirstElem();
                    res.setDestinationDialogId(Utils.decodeTransactionId(destinationTransactionId));

                    res.setMessageType(MessageType.Response);
                    break;

                case TCAbortMessage._TAG_ABORT:
                    localAis = ais.readSequenceStream();

                    // transaction portion
                    tid = TcapFactory.readTransactionID(localAis);
                    if (tid.getFirstElem() == null || tid.getSecondElem() != null) {
                        res.setParsingErrorReason("TCAbortMessage decoding error: transactionId must contain only one transactionId");
                        return res;
                    }
                    destinationTransactionId = tid.getFirstElem();
                    res.setDestinationDialogId(Utils.decodeTransactionId(destinationTransactionId));

                    res.setMessageType(MessageType.Abort);
                    break;

                case TCUniMessage._TAG_UNI:
                    res.setMessageType(MessageType.Unidirectional);
                    break;

                default:
                    res.setParsingErrorReason("Unrecognized message tag");
                    break;
            }

            return res;
        }
        catch (Exception e) {
            DraftParsedMessageImpl res = new DraftParsedMessageImpl();
            res.setParsingErrorReason("Exception when message parsing: " + e.getMessage());
            return res;
        }
    }

    protected class PrevewDialogDataKey {
        public int dpc;
        public String sccpDigits;
        public int ssn;
        public long origTxId;

        public PrevewDialogDataKey(int dpc, String sccpDigits, int ssn, long txId) {
            this.dpc = dpc;
            this.sccpDigits = sccpDigits;
            this.ssn = ssn;
            this.origTxId = txId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (!(obj instanceof PrevewDialogDataKey))
                return false;
            PrevewDialogDataKey b = (PrevewDialogDataKey) obj;

            if (this.sccpDigits != null) {
                // sccpDigits + ssn
                if (!this.sccpDigits.equals(b.sccpDigits))
                    return false;
            } else {
                // dpc + ssn
                if (this.dpc != b.dpc)
                    return false;
            }
            if (this.ssn != b.ssn)
                return false;
            if (this.origTxId != b.origTxId)
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            if (this.sccpDigits != null) {
                result = prime * result + ((sccpDigits == null) ? 0 : sccpDigits.hashCode());
            } else {
                result = prime * result + this.dpc;
            }
            result = prime * result + this.ssn;
            result = prime * result + (int) (this.origTxId + (this.origTxId >> 32));
            return result;
        }
    }
}
