/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */

package org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation;

import java.io.Serializable;

import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;

/**
<code>
RequestedInfo ::= SEQUENCE {
  locationInformation               [0] NULL OPTIONAL,
  subscriberState                   [1] NULL OPTIONAL,
  extensionContainer                [2] ExtensionContainer OPTIONAL,
  ...,
  currentLocation                   [3] NULL OPTIONAL,
  requestedDomain                   [4] DomainType OPTIONAL,
  imei                              [6] NULL OPTIONAL,
  ms-classmark                      [5] NULL OPTIONAL,
  mnpRequestedInfo                  [7] NULL OPTIONAL,
 locationInformationEPS-Supported   [11] NULL OPTIONAL,
 t-adsData                          [8] NULL OPTIONAL,
 requestedNodes                     [9] requestedNodes OPTIONAL,
 servingNodeIndication              [10] NULL OPTIONAL,
 localTimeZoneRequest               [12] NULL OPTIONAL
}

-- currentLocation and locationInformationEPS-Supported shall be absent if locationInformation is absent
-- t-adsData shall be absent in messages sent to the VLR
-- requestedNodes shall be absent if requestedDomain is "cs-Domain"
-- servingNodeIndication shall be absent if locationInformation is absent;
-- servingNodeIndication shall be absent if current location is present;
-- servingNodeIndication indicates by its presence that only the serving node's
-- address (MME-Name or SGSN-Number or VLR-Number) is requested.
</code>
 *
 *
 * @author abhayani
 *
 */
public interface RequestedInfo extends Serializable {
    boolean getLocationInformation();

    boolean getSubscriberState();

    MAPExtensionContainer getExtensionContainer();

    boolean getCurrentLocation();

    DomainType getRequestedDomain();

    boolean getImei();

    boolean getMsClassmark();

    boolean getMnpRequestedInfo();

    boolean getLocationInformationEPSSupported();

    boolean getTadsData();

    RequestedServingNode getRequestedNodes();

    boolean getServingNodeIndication();

    boolean getLocalTimeZoneRequest();
}
