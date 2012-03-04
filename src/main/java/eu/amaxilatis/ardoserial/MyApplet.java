package eu.amaxilatis.ardoserial;

import eu.amaxilatis.ardoserial.graphics.ArduinoStatusImage;
import eu.amaxilatis.ardoserial.graphics.PortOutputViewerFrame;
import eu.amaxilatis.ardoserial.util.SerialPortList;
import jssc.SerialPort;
import org.apache.log4j.BasicConfigurator;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JApplet class.
 * Provides user interface to connecto to an arduino using a usb connection.
 */
public class MyApplet extends JApplet {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MyApplet.class);
    /**
     * the arduino port.
     */
    private final transient JComboBox portSelection;
    /**
     * the arduino port.
     */
    private final transient JComboBox portBaudrate;
    /**
     * a connection handler.
     */
    private transient ConnectionManager arduinoConnection;
    private Thread serialPortThread;
    private final String[] rates = new String[12];
    private String[] detectedPorts;


    @Override
    public final void destroy() {
        LOGGER.info("MyApplet called Destroy");
        arduinoConnection.disconnect();
    }

    /**
     * default constructor.
     *
     * @throws HeadlessException an exception.
     */
    public MyApplet() {
        BasicConfigurator.configure();
        portSelection = new JComboBox();
        portBaudrate = new JComboBox();
        initBaudrates(portBaudrate);
        detectedPorts = SerialPortList.getPortNames();
        for (final String detectedPort : detectedPorts) {
            portSelection.addItem(detectedPort);
        }


        rates[0] = new String(String.valueOf(SerialPort.BAUDRATE_110));
        rates[1] = new String(String.valueOf(SerialPort.BAUDRATE_300));
        rates[2] = new String(String.valueOf(SerialPort.BAUDRATE_600));
        rates[3] = new String(String.valueOf(SerialPort.BAUDRATE_1200));
        rates[4] = new String(String.valueOf(SerialPort.BAUDRATE_4800));
        rates[5] = new String(String.valueOf(SerialPort.BAUDRATE_9600));
        rates[6] = new String(String.valueOf(SerialPort.BAUDRATE_14400));
        rates[7] = new String(String.valueOf(SerialPort.BAUDRATE_19200));
        rates[8] = new String(String.valueOf(SerialPort.BAUDRATE_38400));
        rates[9] = new String(String.valueOf(SerialPort.BAUDRATE_115200));
        rates[10] = new String(String.valueOf(SerialPort.BAUDRATE_128000));
        rates[11] = new String(String.valueOf(SerialPort.BAUDRATE_256000));

    }

    private void initBaudrates(final JComboBox portBaudrate) {

        for (final String rate : rates) {
            portBaudrate.addItem(rate);
        }
        portBaudrate.setSelectedIndex(5);
    }

    public String getRates() {
        return rates.toString();
    }

    public String[] getDetectedPorts() {

        return detectedPorts;
    }

    @Override
    public final void init() {
        LOGGER.info("MyApplet called Init");

        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            LOGGER.error("createGUI didn't successfully complete");
        }
    }

    /**
     * Build the default user interface.
     */
    private void createGUI() {
        LOGGER.info("MyApplet called CreateGUI");

        this.setBackground(Color.white);

        final JButton port = new JButton("Connect");
//        final JButton disconnect = new JButton("Disconnect");

        port.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                arduinoConnection = new ConnectionManager(new PortOutputViewerFrame());
                serialPortThread = new Thread(arduinoConnection);
                serialPortThread.start();
                arduinoConnection.setPort(portSelection.getSelectedItem().toString(), portBaudrate.getSelectedItem().toString());
                arduinoConnection.connect();
//                arduinoConnection.reconnect(portSelection.getSelectedItem().toString());
            }
        });


//        disconnect.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(final ActionEvent actionEvent) {
////                textArea.setText("");
//
//                arduinoConnection.disconnect();
//            }
//        });


        ArduinoStatusImage.setDisconnected();
        setLayout(new FlowLayout());
        add(portSelection);
        add(portBaudrate);
        add(port);


        LOGGER.info("booting up");


    }

    public void overrideConnect() {
        arduinoConnection = new ConnectionManager(new PortOutputViewerFrame());
        serialPortThread = new Thread(arduinoConnection);
        serialPortThread.start();
        arduinoConnection.setPort(portSelection.getSelectedItem().toString(), portBaudrate.getSelectedItem().toString());
        arduinoConnection.connect();
    }


}
