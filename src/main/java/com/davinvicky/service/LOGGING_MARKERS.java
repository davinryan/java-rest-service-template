package nz.co.acc.myacc.services.invoicing;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Markers for sending log output to different files.
 */
enum LOGGING_MARKERS {
    PORTLET;

    public Marker getMarker() {
        return MarkerFactory.getMarker(this.name());
    }
}
