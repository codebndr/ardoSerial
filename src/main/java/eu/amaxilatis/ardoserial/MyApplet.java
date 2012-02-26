package eu.amaxilatis.ardoserial;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 2/24/12
 * Time: 10:07 PM
 */
public class MyApplet extends JApplet {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MyApplet.class);

    private final JTextArea textArea;

    private final TextField portField;
    private final TextField sendField;
    private Main arduinoConnection;

    public MyApplet() throws HeadlessException {
        portField = new TextField("/dev/ttyACM1");


        textArea = new JTextArea();

        sendField = new TextField("...");


    }

    @Override
    public void init() {


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
        arduinoConnection.setPort("/dev/ttyACM0");
        arduinoConnection.run();
    }


    private void createGUI() {
        final JButton port = new JButton("Set Port");
        port.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                textArea.setText("");
                arduinoConnection.reconnect(portField.getText());
            }
        });

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(portField, 0);
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


        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent documentEvent) {
                textArea.selectAll();
                final int prevText = textArea.getSelectionEnd();
                textArea.select(prevText, prevText);
            }

            @Override
            public void removeUpdate(final DocumentEvent documentEvent) {
                textArea.selectAll();
                final int prevText = textArea.getSelectionEnd();
                textArea.select(prevText, prevText);
            }

            @Override
            public void changedUpdate(final DocumentEvent documentEvent) {
                textArea.selectAll();
                final int prevText = textArea.getSelectionEnd();
                textArea.select(prevText, prevText);
            }
        });

        final JScrollPane sp = new JScrollPane(textArea);
        getContentPane().setLayout(new BorderLayout());
//        add(menu);
//        add(port);
//        add(about);
//        add(port);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(sp, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        LOGGER.info("booting up");


    }


}
