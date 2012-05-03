package eu.amaxilatis.codebender.graphics;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 3:21 PM
 */
public class ArduinoStatusImage {
    /**
     * Logger.
     */

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ArduinoStatusImage.class);

    private static final JLabel arduinoStatus = new JLabel();

    public static JLabel getArduinoStatus() {
        return arduinoStatus;
    }

    public ArduinoStatusImage() {

    }

    public static void setConnected() {
        LOGGER.info("setConnected");

    }

    public static void setDisconnected() {
        LOGGER.info("setDisconnected");

    }

    public static void setError() {
        LOGGER.info("setError");

    }
}
