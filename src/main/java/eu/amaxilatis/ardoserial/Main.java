package eu.amaxilatis.ardoserial;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.*;

public class Main implements Runnable {

    static SerialPort serialPort;
    private int portname;
    private static JTextArea jTextArea;


    public Main(int port, JTextArea jTextArea) {
        portname = port;
        this.jTextArea = jTextArea;
    }

    public static void main(String[] args) {
        connect(1);
    }

    public static void connect(int pport) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        serialPort = new SerialPort("/dev/ttyACM" + pport);
        jTextArea.append(serialPort.getPortName());
        try {
            serialPort.openPort();
            serialPort.setParams(9600, 8, 1, 0);
            //Preparing a mask. In a mask, we need to specify the types of events that we want to track.
            //Well, for example, we need to know what came some data, thus in the mask must have the
            //following value: MASK_RXCHAR. If we, for example, still need to know about changes in states
            //of lines CTS and DSR, the mask has to look like this: SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR
            int mask = SerialPort.MASK_RXCHAR;
            //Set the prepared mask
            serialPort.setEventsMask(mask);
            //Add an interface through which we will receive information about events
            serialPort.addEventListener(new SerialPortReader(jTextArea));
        } catch (SerialPortException ex) {
            jTextArea.append(ex.getExceptionType());

        }
    }

    @Override
    public void run() {
        connect(portname);
    }

    public void disconnect() {
        try {
            serialPort.closePort();
            jTextArea.setText("Port closed");
        } catch (SerialPortException e) {
            jTextArea.setText("Cannot close port");
        }
    }

    static class SerialPortReader implements SerialPortEventListener {

        private JTextArea jTextArea;

        public SerialPortReader(final JTextArea jTextArea1) {
            this.jTextArea = jTextArea1;
        }

        public void serialEvent(SerialPortEvent event) {
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
                    System.out.print("|" + (char) buffer[0] + "|");
                    jTextArea.append(String.valueOf((char) buffer[0]));
                } catch (SerialPortException ex) {
                    jTextArea.append(ex.getExceptionType());
                }
//                }
            } else {
                System.out.println(event.isBREAK());
                System.out.println(event.isCTS());
                System.out.println(event.isTXEMPTY());
                System.out.println(event.isRXFLAG());
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