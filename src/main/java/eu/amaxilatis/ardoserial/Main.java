package eu.amaxilatis.ardoserial;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import javax.swing.JTextArea;

/**
 * This is the Class Responsible for connecting to the arduino.
 * Uses the jSSC library provided by http://code.google.com/p/java-simple-serial-connector/
 */
public class Main implements Runnable {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Main.class);
    /**
     * the serial port connection.
     */
    private static SerialPort serialPort;
    /**
     * the name of the serial port.
     */
    private static String port;
    /**
     * the text area to append output of the serial port.
     */
    private final transient JTextArea jTextArea;
    /**
     * Initialization sleep time.
     */
    private static final long SLEEP_TIME = 5000;

    /**
     * basic constructor.
     * appends all output to a JTextArea.
     *
     * @param jTextArea the JTextArea to append to.
     */
    public Main(final JTextArea jTextArea) {
        this.jTextArea = jTextArea;
    }

    /**
     * returns the name of the port used.
     *
     * @return the port name
     */
    public final String getPort() {
        return port;
    }

    /**
     * sets the port name to the given string.
     *
     * @param port the new port name.
     */
    public final void setPort(final String port) {
        Main.port = port;
    }

    /**
     * testing main function.
     *
     * @param args input args
     */
    public final void main(final String[] args) {
        connect();
    }

    /**
     * connects to the previously set port.
     */
    public final void connect() {
        String[] portNames = SerialPortList.getPortNames();
        LOGGER.info("found ports: " + portNames.length);
        if (portNames.length > 0) {
            for (String portname : portNames) {
                LOGGER.info(portname);
            }
        }

        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            LOGGER.fatal(e);
        }
        serialPort = new SerialPort(port);
        jTextArea.append(serialPort.getPortName());
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            //Preparing a mask. In a mask, we need to specify the types of events that we want to track.
            //Well, for example, we need to know what came some data, thus in the mask must have the
            //following value: MASK_RXCHAR. If we, for example, still need to know about changes in states
            //of lines CTS and DSR, the mask has to look like this:
            // SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR
            //Set the prepared mask
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            //Add an interface through which we will receive information about events
            serialPort.addEventListener(new SerialPortReader(jTextArea));
        } catch (SerialPortException ex) {
            jTextArea.append(ex.getExceptionType());

        }
    }

    @Override
    public final void run() {
        connect();
    }

    /**
     * called to disconnect form the port.
     */
    public final void disconnect() {
        if (serialPort.isOpened()) {
            try {
                serialPort.closePort();
                jTextArea.setText("Port closed");
            } catch (final SerialPortException e) {
                jTextArea.setText("Cannot close port");
            }
        }
    }

    /**
     * called to disconnect from the previous port if connected and reconnect to a new one.
     *
     * @param newport the new port to connect to.
     */
    public final void reconnect(final String newport) {
        disconnect();
        port = newport;
        connect();
    }

    /**
     * A class that handles the serial port output.
     */
    static class SerialPortReader implements SerialPortEventListener {

        /**
         * the JTextArea to append output.
         */
        private final transient JTextArea jTextArea;

        /**
         * basic constructor.
         *
         * @param jTextArea1 a JTextArea object.
         */
        public SerialPortReader(final JTextArea jTextArea1) {
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

                    final byte buffer[] = serialPort.readBytes(1);
                    LOGGER.info("|" + (char) buffer[0] + "|");
                    jTextArea.append(String.valueOf((char) buffer[0]));
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
}
