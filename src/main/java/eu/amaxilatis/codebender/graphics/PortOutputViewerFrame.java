package eu.amaxilatis.codebender.graphics;

import eu.amaxilatis.codebender.CodeBenderApplet;
import eu.amaxilatis.codebender.actions.SaveOutputPrivilegedAction;
import eu.amaxilatis.codebender.util.ConnectionManager;
import eu.amaxilatis.codebender.util.MyActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.security.AccessController;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/4/12
 * Time: 12:31 PM
 */
public class PortOutputViewerFrame extends JFrame {

    /**
     * the textArea that contains output from the arduino.
     */
    private final transient JTextArea textArea;

    /**
     * a new command to the arduino.
     */
    private final transient JTextField sendField;
    /**
     * enable follow text on the frame.
     */
    private final transient JCheckBox followText;
    /**
     * Size parameter for text area.
     */
    private static final int FIELD_COLUMNS = 15;
    /**
     * Size parameter for text area.
     */
    private static final int FIELD_ROWS = 15;
    /**
     * Size parameter for jframe.
     */
    private static final int WIDTH_S = 700;
    /**
     * Size parameter for jframe.
     */
    private static final int LENGTH_S = 400;
    private static String port;
    private JComboBox baudratesList;
    private JButton send;
    private JButton disconnect;

    /**
     * Constructor that Generates a new JFrame to listen to the arduino output.
     */
    public PortOutputViewerFrame(final CodeBenderApplet applet, final String port) {
        this.port = port;
        this.setLayout(new BorderLayout());
        if (applet != null) {
            this.setTitle("Codebender.cc - ArduinoSerialMonitor - Version:"
                    + applet.getVersion());
        } else {
            this.setTitle("Codebender.cc - ArduinoSerialMonitor - Version:");
        }
        textArea = new JTextArea();
        sendField = new JTextField("");

        send = new JButton("Send to Arduino");
        disconnect = new JButton("Disconnect & Close");

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                ConnectionManager.getInstance().send(sendField.getText());
            }
        });

        disconnect.addActionListener(new MyActionListener(this));

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(final WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                ConnectionManager.getInstance().disconnect();
                System.out.println("windowClosing");
            }

            @Override
            public void windowClosed(final WindowEvent windowEvent) {
                ConnectionManager.getInstance().disconnect();
                System.out.println("windowClosed");
            }

            @Override
            public void windowIconified(final WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowDeiconified(final WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowActivated(final WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowDeactivated(final WindowEvent windowEvent) {
                //nothing
            }
        });


        final JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new FlowLayout());

        String list = ConnectionManager.getInstance().getBaudrates();
        baudratesList = new JComboBox(list.split(","));

        ConnectionManager.getInstance().setjTextArea(this);

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final int rate = Integer.parseInt((String) baudratesList.getSelectedItem());
                System.out.println("connecting to  " + port + " @ " + rate);

                ConnectionManager.getInstance().setPort(port, rate);
                ConnectionManager.getInstance().connect();
                enableInterface();
            }
        });


        sendPanel.add(connectButton);
        sendPanel.add(baudratesList);
        sendField.setColumns(FIELD_COLUMNS);
        sendPanel.add(sendField);
        sendPanel.add(send);
        sendPanel.add(disconnect);

        getContentPane().setLayout(new BorderLayout());

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0, 1));
        topPanel.add(sendPanel);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);


        final JScrollPane middlePanel = new JScrollPane(textArea);
        textArea.setRows(FIELD_ROWS);
        this.getContentPane().add(middlePanel, BorderLayout.CENTER);


        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        followText = new JCheckBox("Autoscroll");
        followText.setEnabled(true);
        followText.setSelected(true);
        bottomPanel.add(followText);

        final JButton save2file = new JButton("Save output...");
        save2file.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                AccessController.doPrivileged(new SaveOutputPrivilegedAction(textArea.getText()));
            }
        });
        bottomPanel.add(save2file);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        this.setVisible(true);
        this.setMinimumSize(new Dimension(WIDTH_S, LENGTH_S));
        disableInterface();
    }

    /**
     * Adds a text string to the JTextArea.
     *
     * @param text the String to add.
     */
    public final void appendText(final String text) {

        textArea.append(text);
        if (followText.isSelected()) {
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    /**
     * Sets the JTextArea to the text given.
     *
     * @param text the String to use.
     */
    public final void setText(final String text) {
        textArea.setText(text);
    }

    public final void enableInterface() {
        sendField.setEnabled(true);
        send.setEnabled(true);
        disconnect.setEnabled(true);
        textArea.setEnabled(true);
        textArea.setText("");
    }

    public final void disableInterface() {
        sendField.setEnabled(false);
        send.setEnabled(false);
        disconnect.setEnabled(false);
        textArea.setEnabled(false);
        textArea.setText("No Connection avaialable please select a Baudrate and click connect");
    }

    public static void main(String[] args) {
        PortOutputViewerFrame pf = new PortOutputViewerFrame(null, "/dev/ttyACM0");
        pf.show();
    }


}
