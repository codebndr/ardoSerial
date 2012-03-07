package eu.amaxilatis.ardoserial.graphics;

import eu.amaxilatis.ardoserial.ConnectionManager;
import eu.amaxilatis.ardoserial.MyApplet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
import java.io.File;

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
    private transient JTextArea textArea;
    /**
     * a new command to the arduino.
     */
    private transient JTextField sendField;

    private ConnectionManager connectionManager;
    private MyApplet applet;

    public PortOutputViewerFrame() {

    }

    public PortOutputViewerFrame(final MyApplet applet) {
        this.applet = applet;

        this.setLayout(new BorderLayout());
        textArea = new JTextArea();
        sendField = new JTextField("");
        final JButton send = new JButton("Send to Arduino");

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                connectionManager.send(sendField.getText());
            }
        });


        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
                LOGGER.info("windowClosing");
                connectionManager.disconnect();
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                LOGGER.info("windowClosing");
                connectionManager.disconnect();
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {
                //nothing
            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {
                //nothing
            }
        });

        final JPanel pan1 = new JPanel();
        pan1.setLayout(new FlowLayout());
        sendField.setColumns(25);
        pan1.add(sendField);
        pan1.add(send);

        getContentPane().setLayout(new BorderLayout());

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(0, 1));
        topPanel.add(pan1);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        topPanel.add(ArduinoStatusImage.getArduinoStatus());


        final JScrollPane middlePanel = new JScrollPane(textArea);
        textArea.setRows(15);
        this.getContentPane().add(middlePanel, BorderLayout.CENTER);


        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        final JCheckBox followText = new JCheckBox("Autoscroll");
        bottomPanel.add(followText);
        final JButton save2file = new JButton("Save output...");
        save2file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                applet.saveText(textArea.getText());
            }
        });
        bottomPanel.add(save2file);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        this.setVisible(true);
        this.setMinimumSize(new Dimension(700, 400));
    }


    public void appendText(final String text) {

        textArea.append(text);

        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void setText(final String text) {
        textArea.setText(text);
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
}
