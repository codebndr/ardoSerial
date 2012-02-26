package eu.amaxilatis.ardoserial;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.*;

public class Main implements Runnable {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Main.class);

    static SerialPort serialPort;
    private static String port;
    private static JTextArea jTextArea;


    public Main(final JTextArea jTextArea) {
        this.jTextArea = jTextArea;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(final String port) {
        Main.port = port;
    }

    public static void main(final String[] args) {
        connect();
    }

    public static void connect() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.fatal(e);
        }
        serialPort = new SerialPort(port);
        jTextArea.append(serialPort.getPortName());
        try {
            serialPort.openPort();
            serialPort.setParams(9600, 8, 1, 0);
            //Preparing a mask. In a mask, we need to specify the types of events that we want to track.
            //Well, for example, we need to know what came some data, thus in the mask must have the
            //following value: MASK_RXCHAR. If we, for example, still need to know about changes in states
            //of lines CTS and DSR, the mask has to look like this: SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR
            //Set the prepared mask
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            //Add an interface through which we will receive information about events
            serialPort.addEventListener(new SerialPortReader(jTextArea));
        } catch (SerialPortException ex) {
            jTextArea.append(ex.getExceptionType());

        }
    }

    @Override
    public void run() {
        connect();
    }

    public void disconnect() {
        try {
            serialPort.closePort();
            jTextArea.setText("Port closed");
        } catch (SerialPortException e) {
            jTextArea.setText("Cannot close port");
        }
    }

    public void reconnect(final String newport) {
        disconnect();
        port = newport;
        connect();
    }

    static class SerialPortReader implements SerialPortEventListener {

        private final JTextArea jTextArea;

        public SerialPortReader(final JTextArea jTextArea1) {
            this.jTextArea = jTextArea1;
        }

        public void serialEvent(final SerialPortEvent event) {
//            final char ch = (char) event.getEventValue();
//            System.out.print(ch);
//            System.out.println(event.getEventType());
//            System.out.println(event.isRXCHAR());
//            System.out.println(event.getEventValue());
            //Object type SerialPortEvent carries information about which event occurred and a value.
            //For example, if the data came a method event.getEventValue() returns us the number of bytes in the input buffer.
            if (event.isRXCHAR()) {
//                                            System.out.println(event.getEventValue() );
//                if (event.getEventValue() == 10) {
                try {

                    byte buffer[] = serialPort.readBytes(1);
                    LOGGER.info("|" + (char) buffer[0] + "|");
                    jTextArea.append(String.valueOf((char) buffer[0]));
                } catch (SerialPortException ex) {
                    jTextArea.append(ex.getExceptionType());
                }
//                }
            } else {
                LOGGER.info(event.isBREAK());
                LOGGER.info(event.isCTS());
                LOGGER.info(event.isTXEMPTY());
                LOGGER.info(event.isRXFLAG());
            }


            //If the CTS line status has changed, then the method event.getEventValue() returns 1 if the line is ON and 0 if it is OFF.
//            else if (event.isCTS()) {
//                if (event.getEventValue() == 1) {
//                    System.out.println("CTS - ON");
//                } else {
//                    System.out.println("CTS - OFF");
//                }
//            } else if (event.isDSR()) {
//                if (event.getEventValue() == 1) {
//                    System.out.println("DSR - ON");
//                } else {
//                    System.out.println("DSR - OFF");
//                }
//        }
        }
    }
}