package eu.amaxilatis.ardoserial;

import eu.amaxilatis.ardoserial.serialPorts.SerialPortList;
import org.apache.log4j.BasicConfigurator;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
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
    private transient Main arduinoConnection;
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

        arduinoConnection = new Main(textArea);
        serialPortThread = new Thread(arduinoConnection);
        serialPortThread.start();
        arduinoConnection.setPort(portSelection.getSelectedItem().toString());

    }

    /**
     * Build the default user interface.
     */
    private void createGUI() {
        LOGGER.info("MyApplet called CreateGUI");

        final JButton port = new JButton("Set Port");
        port.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textArea.setText("");
                arduinoConnection.reconnect(portSelection.getSelectedItem().toString());
            }
        });

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(portSelection, 0);
        topPanel.add(port, 1);

        final JButton disconnect = new JButton("disconnect");
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textArea.setText("");

                arduinoConnection.disconnect();
            }
        });

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 2));
        final JButton about = new JButton("about");
        final JButton send = new JButton("Send to Arduino");
        bottomPanel.add(sendField, 0);
        bottomPanel.add(send, 1);
        bottomPanel.add(about, 2);
        bottomPanel.add(disconnect, 3);


//        textArea.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(final DocumentEvent documentEvent) {
//                textArea.selectAll();
//                final int prevText = textArea.getSelectionEnd();
//                textArea.select(prevText, prevText);
//            }
//
//            @Override
//            public void removeUpdate(final DocumentEvent documentEvent) {
//                textArea.selectAll();
//                final int prevText = textArea.getSelectionEnd();
//                textArea.select(prevText, prevText);
//            }
//
//            @Override
//            public void changedUpdate(final DocumentEvent documentEvent) {
//                textArea.selectAll();
//                final int prevText = textArea.getSelectionEnd();
//                textArea.select(prevText, prevText);
//            }
//        });

        final JScrollPane scrollPane = new JScrollPane(textArea);
        getContentPane().setLayout(new BorderLayout());
//        add(menu);
//        add(port);
//        add(about);
//        add(port);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        LOGGER.info("booting up");


    }


}
