package eu.amaxilatis.ardoserial.graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 3:21 PM
 */
public class ArduinoStatusImage {

    private static final JLabel arduinoStatus = new JLabel();

    public static JLabel getArduinoStatus() {
        return arduinoStatus;
    }

    public ArduinoStatusImage() {

    }

    public static void setConnected() {
        arduinoStatus.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("img/green.jpg")));
    }

    public static void setDisconnected() {
        arduinoStatus.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("img/orange.jpg")));
    }

    public static void setError() {
        arduinoStatus.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("img/red.jpg")));
    }
}
