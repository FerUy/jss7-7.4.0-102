package org.mobicents.protocols.ss7.tools.simulator.tests.lcs;

public class EllipsoidPoint {

    double latitude;
    double longitude;

    public EllipsoidPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}

