package eu.amaxilatis.ardoserial.util;

import eu.amaxilatis.ardoserial.ConnectionManager;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.JTextArea;

/**
 * A class that handles the serial port output.
 */
public class SerialPortReader implements SerialPortEventListener {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ConnectionManager.class);
    /**
     * the JTextArea to append output.
     */
    private final transient JTextArea jTextArea;
    private ConnectionManager connectionManager;

    /**
     * basic constructor.
     *
     * @param jTextArea1 a JTextArea object.
     */
    public SerialPortReader(final ConnectionManager m, final JTextArea jTextArea1) {
        this.connectionManager = m;
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
                LOGGER.info("|" + (char) buffer[0] + "|");
                jTextArea.append(String.valueOf((char) buffer[0]));
                jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
            } catch (SerialPortException ex) {
                jTextArea.append(ex.getExceptionType());
            }
        } else {
            LOGGER.info(event.isBREAK());
            LOGGER.info(event.isCTS());
            LOGGER.info(event.isTXEMPTY());
            LOGGER.info(event.isRXFLAG());
        }
    }

}