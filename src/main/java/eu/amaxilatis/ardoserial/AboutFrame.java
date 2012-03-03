package eu.amaxilatis.ardoserial;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.GridLayout;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 3:12 PM
 */
public class AboutFrame extends JFrame {
    public AboutFrame() {
        this.setLayout(new GridLayout(5, 1));
        this.add(new JLabel("This is an Applet Designed to provide"));
        this.add(new JLabel("connection and communication to you arduino!"));
        this.add(new JLabel("designed by d.amaxilatis (d.amaxilatis@gmail.com)"));
        this.setVisible(true);
        this.pack();
    }
}

