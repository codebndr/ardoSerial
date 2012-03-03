package eu.amaxilatis.ardoserial;

import eu.amaxilatis.ardoserial.graphics.ArduinoStatusImage;
import eu.amaxilatis.ardoserial.util.SerialPortList;
import org.apache.log4j.BasicConfigurator;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.TextField;
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
     * the textArea that contains output from the arduino.
     */
    private final transient JTextArea textArea;
    /**
     * the arduino port.
     */
    private final transient JComboBox portSelection;
    /**
     * a new command to the arduino.
     */
    private final transient TextField sendField;
    /**
     * a connection handler.
     */
    private transient ConnectionManager arduinoConnection;
    private Thread serialPortThread;


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
        for (final String detectedPort : SerialPortList.getPortNames()) {
            portSelection.addItem(detectedPort);
        }

        textArea = new JTextArea();
        sendField = new TextField("...");
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

        arduinoConnection = new ConnectionManager(textArea);
        serialPortThread = new Thread(arduinoConnection);
        serialPortThread.start();
        arduinoConnection.setPort(portSelection.getSelectedItem().toString());

    }

    /**
     * Build the default user interface.
     */
    private void createGUI() {
        LOGGER.info("MyApplet called CreateGUI");

        this.setBackground(Color.white);

        final JButton port = new JButton("Connect");
        final JButton disconnect = new JButton("Disconnect");

        port.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textArea.setText("");
                arduinoConnection.reconnect(portSelection.getSelectedItem().toString());
            }
        });

        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textArea.setText("");

                arduinoConnection.disconnect();
            }
        });

        final JPanel topPanel = new JPanel();
        ArduinoStatusImage.setDisconnected();
//        topPanel.setLayout(new GridLayout(1, 3));
        topPanel.setLayout(new FlowLayout());
        topPanel.add(portSelection);
        topPanel.add(port);
        topPanel.add(disconnect);
        topPanel.add(ArduinoStatusImage.getArduinoStatus());


        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(0, 1));
        final JButton about = new JButton("about");
        about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new AboutFrame();
            }
        });
        final JButton send = new JButton("Send to Arduino");

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                arduinoConnection.send(sendField.getText());
            }
        });
        final JPanel pan1 = new JPanel();
        pan1.setLayout(new FlowLayout());
        sendField.setColumns(25);
        pan1.add(sendField);
        pan1.add(send);
        final JPanel pan2 = new JPanel();
        pan2.setLayout(new FlowLayout());
        pan2.add(about);

        bottomPanel.add(pan1);
        bottomPanel.add(pan2);


        final JScrollPane scrollPane = new JScrollPane(textArea);
        getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        LOGGER.info("booting up");


    }


}
