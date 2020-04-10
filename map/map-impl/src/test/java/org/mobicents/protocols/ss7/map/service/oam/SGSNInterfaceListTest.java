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

package org.mobicents.protocols.ss7.map.service.oam;

import static org.testng.Assert.*;

import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.mobicents.protocols.asn.Tag;
import org.testng.annotations.Test;

/**
*
* @author sergey vetyutnev
*
*/
public class SGSNInterfaceListTest {

    private byte[] getEncodedData() {
        return new byte[] { 3, 3, 5, (byte) 182, (byte) 160 };
    }

    @Test(groups = { "functional.decode", "service.oam" })
    public void testDecode() throws Exception {

        byte[] rawData = getEncodedData();
        AsnInputStream asn = new AsnInputStream(rawData);

        int tag = asn.readTag();
        SGSNInterfaceListImpl asc = new SGSNInterfaceListImpl();
        asc.decodeAll(asn);

        assertEquals(tag, Tag.STRING_BIT);
        assertEquals(asn.getTagClass(), Tag.CLASS_UNIVERSAL);

        assertTrue(asc.getGb());
        assertFalse(asc.getIu());
        assertTrue(asc.getGn());
        assertTrue(asc.getMapGr());
        assertFalse(asc.getMapGd());
        assertTrue(asc.getMapGf());
        assertTrue(asc.getGs());
        assertFalse(asc.getGe());
        assertTrue(asc.getS3());
        assertFalse(asc.getS4());
        assertTrue(asc.getS6d());

    }

    @Test(groups = { "functional.encode", "service.oam" })
    public void testEncode() throws Exception {

        SGSNInterfaceListImpl asc = new SGSNInterfaceListImpl(true, false, true, true, false, true, true, false, true, false, true);
//        boolean gb, boolean iu, boolean gn, boolean mapGr, boolean mapGd,
//        boolean mapGf, boolean gs, boolean ge, boolean s3, boolean s4,
//        boolean s6d

        AsnOutputStream asnOS = new AsnOutputStream();
        asc.encodeAll(asnOS);

        byte[] encodedData = asnOS.toByteArray();
        byte[] rawData = getEncodedData();
        assertTrue(Arrays.equals(rawData, encodedData));

    }

}
