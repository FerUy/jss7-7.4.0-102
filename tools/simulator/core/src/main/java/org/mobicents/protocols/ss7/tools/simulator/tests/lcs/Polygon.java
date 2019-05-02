package org.mobicents.protocols.ss7.tools.simulator.tests.lcs;

import java.io.Serializable;

public interface Polygon extends Serializable {

    byte[] getData();

    int getNumberOfPoints();

    EllipsoidPoint getEllipsoidPoint(int position);

}
