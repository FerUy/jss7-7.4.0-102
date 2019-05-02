/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
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

package org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement;

import java.io.Serializable;

/**
 *
 Long-GroupId ::= TBCD-STRING (SIZE (4)) -- When Long-Group-Id is less than eight characters in length, the TBCD filler (1111)
 * -- is used to fill unused half octets. -- Refers to the Group Identification as specified in 3GPP TS 23.003 -- and 3GPP TS
 * 43.068/ 43.069
 *
 *
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface LongGroupId extends Serializable {

    String getLongGroupId();

}
