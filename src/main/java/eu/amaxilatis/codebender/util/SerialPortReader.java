package eu.amaxilatis.codebender.util;

import eu.amaxilatis.codebender.ConnectionManager;
import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * A class that handles the serial port output.
 */
public class SerialPortReader implements SerialPortEventListener {

    /**
     * the JTextArea to append output.
     */
    private final transient PortOutputViewerFrame jTextArea;
    private final transient ConnectionManager connectionManager;

    /**
     * basic constructor.
     *
     * @param jTextArea1 a JTextArea object.
     */
    public SerialPortReader(final ConnectionManager manager, final PortOutputViewerFrame jTextArea1) {
        this.connectionManager = manager;
        this.jTextArea = jTextArea1;
    }

    /**
     * event handler for new serial port output.
     *
     * @param event a new SerialPortEvent.
     */
    public void serialEvent(final SerialPortEvent event) {

        //Object type SerialPortEvent carries information about which event occurred and a value.
        //ie, if the data came a method event.getEventValue() returns the number of bytes in the in buffer.
        if (event.isRXCHAR()) {
            try {

                final byte buffer[] = connectionManager.getSerialPort().readBytes(1);
//                LOGGER.info("|" + (char) buffer[0] + "|");
                jTextArea.appendText(String.valueOf((char) buffer[0]));
            } catch (SerialPortException ex) {
                jTextArea.appendText(ex.getExceptionType());
            }
        } else {
//            LOGGER.info(event.isBREAK());
//            LOGGER.info(event.isCTS());
//            LOGGER.info(event.isTXEMPTY());
//            LOGGER.info(event.isRXFLAG());
        }
    }

}