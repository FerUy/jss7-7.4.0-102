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

package org.mobicents.protocols.ss7.tools.simulator.tests.lcs;

import org.mobicents.protocols.ss7.tools.simulator.common.EnumeratedBase;

import java.util.Hashtable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>*
 */
public class PSLReaction extends EnumeratedBase {

    private static final long serialVersionUID = 83L;

    public static final int VAL_RETURN_SUCCESS = 1;
    public static final int VAL_ERROR_SYSTEM_FAILURE = 2;
    public static final int VAL_DATA_MISSING = 3;
    public static final int VAL_UNEXPECTED_DATA_VALUE = 4;
    public static final int VAL_FACILITY_NOT_SUPPORTED = 5;
    public static final int VAL_UNIDENTIFIED_SUBSCRIBER = 6;
    public static final int VAL_ILLEGAL_SUBSCRIBER = 7;
    public static final int VAL_ILLEGAL_EQUIPMENT = 8;
    public static final int VAL_ABSENT_SUBSCRIBER = 9;
    public static final int VAL_UNAUTHORIZED_REQUESTING_NETWORK = 10;
    public static final int VAL_UNAUTHORIZED_LCS_CLIENT = 11;
    public static final int VAL_POSITION_METHOD_FAILURE = 12;


    private static Hashtable<String, Integer> stringMap = new Hashtable<String, Integer>();
    private static Hashtable<Integer, String> intMap = new Hashtable<Integer, String>();

    static {
        intMap.put(VAL_RETURN_SUCCESS, "Return success");
        intMap.put(VAL_ERROR_SYSTEM_FAILURE, "Return error system failure");
        intMap.put(VAL_DATA_MISSING, "Return error data missing");
        intMap.put(VAL_UNEXPECTED_DATA_VALUE, "Return error unexpected data value");
        intMap.put(VAL_FACILITY_NOT_SUPPORTED, "Return error facility not supported");
        intMap.put(VAL_UNIDENTIFIED_SUBSCRIBER, "Return error unidentified subscriber");
        intMap.put(VAL_ILLEGAL_SUBSCRIBER, "Return error illegal subscriber");
        intMap.put(VAL_ILLEGAL_EQUIPMENT, "Return error illegal equipment");
        intMap.put(VAL_ABSENT_SUBSCRIBER, "Return error absent subscriber");
        intMap.put(VAL_UNAUTHORIZED_REQUESTING_NETWORK, "Return error unauthorized requesting network");
        intMap.put(VAL_UNAUTHORIZED_LCS_CLIENT, "Return error unauthorized LCS client");
        intMap.put(VAL_POSITION_METHOD_FAILURE, "Return error position method failure");

        stringMap.put("Return success", VAL_RETURN_SUCCESS);
        stringMap.put("Return error system failure", VAL_ERROR_SYSTEM_FAILURE);
        stringMap.put("Return error data missing", VAL_DATA_MISSING);
        stringMap.put("Return error unexpected data value", VAL_UNEXPECTED_DATA_VALUE);
        stringMap.put("Return error facility not supported", VAL_FACILITY_NOT_SUPPORTED);
        stringMap.put("Return error unidentified subscriber", VAL_UNIDENTIFIED_SUBSCRIBER);
        stringMap.put("Return error illegal subscriber", VAL_ILLEGAL_SUBSCRIBER);
        stringMap.put("Return error illegal equipment", VAL_ILLEGAL_EQUIPMENT);
        stringMap.put("Return error absent subscriber", VAL_ABSENT_SUBSCRIBER);
        stringMap.put("Return error unauthorized requesting network", VAL_UNAUTHORIZED_REQUESTING_NETWORK);
        stringMap.put("Return error unauthorized LCS client", VAL_UNAUTHORIZED_LCS_CLIENT);
        stringMap.put("Return error position method failure", VAL_POSITION_METHOD_FAILURE);
    }

    public PSLReaction() {
    }

    public PSLReaction(int val) throws java.lang.IllegalArgumentException {
        super(val);
    }

    public PSLReaction(Integer val) throws java.lang.IllegalArgumentException {
        super(val);
    }

    public PSLReaction(String val) throws java.lang.IllegalArgumentException {
        super(val);
    }

    public static PSLReaction createInstance(String s) {
        Integer instance = doCreateInstance(s, stringMap, intMap);
        if (instance == null)
            return new PSLReaction(VAL_RETURN_SUCCESS);
        else
            return new PSLReaction(instance);
    }

    @Override
    protected Hashtable<Integer, String> getIntTable() {
        return intMap;
    }

    @Override
    protected Hashtable<String, Integer> getStringTable() {
        return stringMap;
    }

}
