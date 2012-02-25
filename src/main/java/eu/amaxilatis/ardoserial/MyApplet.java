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
    private JTextArea textArea;
    private JButton port;
    private JButton about;
    private TextField portField;
    private JButton send;
    private TextField sendField;
    private JButton disconnect;
    private Main m;

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
            System.err.println("createGUI didn't successfully complete");
        }


        m = new Main("/dev/ttyACM0", textArea);
        m.run();
    }


    private void createGUI() {
        port = new JButton("Set Port");
        port.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                textArea.setText("");
                m.reconnect(portField.getText());
            }
        });
        portField = new TextField("/dev/ttyACM1");
        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(portField, 0);
        topPanel.add(port, 1);

        send = new JButton("Send to Arduino");
        sendField = new TextField("...");
        about = new JButton("about");
        disconnect = new JButton("disconnect");
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                textArea.setText("");

                m.disconnect();
            }
        });

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 2));
        bottomPanel.add(portField, 0);
        bottomPanel.add(port, 1);
        bottomPanel.add(about, 2);
        bottomPanel.add(disconnect, 3);

        textArea = new JTextArea();
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                int x;
                textArea.selectAll();
                x = textArea.getSelectionEnd();
                textArea.select(x, x);
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                int x;
                textArea.selectAll();
                x = textArea.getSelectionEnd();
                textArea.select(x, x);
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                int x;
                textArea.selectAll();
                x = textArea.getSelectionEnd();
                textArea.select(x, x);
            }
        });

        JScrollPane sp = new JScrollPane(textArea);


        //
        getContentPane().setLayout(new BorderLayout());


//        add(menu);
//        add(port);
//        add(about);
//        add(port);
        this.getContentPane().add(topPanel, BorderLayout.NORTH);
        this.getContentPane().add(sp, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        System.out.println("moving");


    }


}
