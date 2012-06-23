package eu.amaxilatis.codebender.graphics;

import eu.amaxilatis.codebender.CodeBenderApplet;
import eu.amaxilatis.codebender.ConnectionManager;
import eu.amaxilatis.codebender.MyActionListener;
import eu.amaxilatis.codebender.actions.SaveOutputPrivilegedAction;

import javax.swing.*;
import java.awt.*;
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
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PortOutputViewerFrame.class);

    /**
     * the textArea that contains output from the arduino.
     */
    private final transient JTextArea textArea;

    /**
     * a new command to the arduino.
     */
    private final transient JTextField sendField;
    private final transient JCheckBox followText;

    /**
     * Constructor that Generates a new JFrame to listen to the arduino output.
     */
    public PortOutputViewerFrame() {
        this.setLayout(new BorderLayout());
        this.setTitle("Codebender.cc - ArduinoSerialMonitor - v" + CodeBenderApplet.version + "." + CodeBenderApplet.buildNum + "b");
//        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/codebender.jpg").getFile()));
        textArea = new JTextArea();
        sendField = new JTextField("");

        final JButton send = new JButton("Send to Arduino");
        final JButton disconnect = new JButton("Disconnect & Close");

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
                LOGGER.info("windowClosing");
            }

            @Override
            public void windowClosed(final WindowEvent windowEvent) {
                ConnectionManager.getInstance().disconnect();
                LOGGER.info("windowClosed");
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

        final JPanel pan1 = new JPanel();
        pan1.setLayout(new FlowLayout());
        sendField.setColumns(25);
        pan1.add(sendField);
        pan1.add(send);
        pan1.add(disconnect);

        getContentPane().setLayout(new BorderLayout());

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0, 1));
        topPanel.add(pan1);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);


        final JScrollPane middlePanel = new JScrollPane(textArea);
        textArea.setRows(15);
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
        this.setMinimumSize(new Dimension(700, 400));
    }

    /**
     * Adds a text string to the JTextArea.
     *
     * @param text the String to add.
     */
    public void appendText(final String text) {

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
    public void setText(final String text) {
        textArea.setText(text);
    }


}
