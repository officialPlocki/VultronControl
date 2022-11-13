package de.plocki.util;

import java.io.IOException;

public interface TicketInformation {

    long getUserID();

    boolean hasPrio();

    String getHelpReason();

    String aiCategorisation() throws IOException;

}
