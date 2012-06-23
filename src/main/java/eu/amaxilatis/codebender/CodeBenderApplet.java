package eu.amaxilatis.codebender;

import eu.amaxilatis.codebender.actions.FlashPrivilegedAction;
import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;
import eu.amaxilatis.codebender.util.SerialPortList;
import org.apache.log4j.BasicConfigurator;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * A JApplet class.
 * Provides user interface to connecto to an arduino using a usb connection.
 */
public class CodeBenderApplet extends JApplet {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CodeBenderApplet.class);

    private final transient String[] rates = new String[12];
    private transient String[] ports;
    public static transient String version;
    public static transient String buildNum;

    public static final int FLASH_OK = 0;
    public static final int LIBUSB_ERROR = 1;
    public static final int AVRDUDE_ERROR = 2;
    public static final int CONF_ERROR = 3;
    public static final int HEX_ERROR = 4;
    public static final int PROCESS_ERROR = 5;
    public static final int INTERUPTED_ERROR = 6;
    public static final int PORT_ERROR = 7;

    @Override
    public final void destroy() {
        LOGGER.info("CodeBenderApplet called Destroy");
        ConnectionManager.getInstance().disconnect();
    }

    /**
     * default constructor.
     *
     * @throws HeadlessException an exception.
     */
    public CodeBenderApplet() {
        BasicConfigurator.configure();
        final Properties properties = new Properties();
        try {

            properties.load(this.getClass().getResourceAsStream("/props/version.properties"));
            version = (String) properties.get("version");
            buildNum = (String) properties.get("build");
            LOGGER.info("Version:" + version);
            LOGGER.info("Build:" + buildNum);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public String getRates() {
        return rates.toString();
    }

    public String getFireRates() {
        return ConnectionManager.getInstance().getBaudrates();
    }

    @Override
    public final void init() {
        LOGGER.info("CodeBenderApplet called Init");
    }

    /**
     * Called from javascript.
     *
     * @return a comma separated list of all available usb ports.
     */
    public String getFire2() {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            ports = SerialPortList.getInstance().getPortNames();
//                            LOGGER.info(ports);
                        }
                    });
                } catch (InterruptedException e) {
                    LOGGER.error(e, e);
                } catch (InvocationTargetException e) {
                    LOGGER.error(e, e);
                }
                return 0;
            }
        });

        final StringBuilder protsAvail = new StringBuilder();
//        LOGGER.info(ports);
        for (int i = 0; i < ports.length; i++) {
            protsAvail.append(",");
            protsAvail.append(ports[i]);

        }
        return (protsAvail.toString()).substring(1);
    }

    /**
     * Override connect function to be used by javascript.
     *
     * @param port the index of the port to connect to.
     * @param rate the rate to use when connecting.
     */
    public void overrideConnect(final int port, final int rate) {
        ConnectionManager.getInstance().setjTextArea(new PortOutputViewerFrame());

        ConnectionManager.getInstance().setPort(ports[port], rate);
        ConnectionManager.getInstance().connect();
    }

    public int flash(final int port, final String filename, final String baudrate) {
        final FlashPrivilegedAction action = new FlashPrivilegedAction(ports[port], filename, baudrate);
        final int response = (Integer) AccessController.doPrivileged(action);
        LOGGER.info("Returing value : " + response);
        return response;
    }


}


