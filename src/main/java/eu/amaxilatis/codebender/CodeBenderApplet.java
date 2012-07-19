package eu.amaxilatis.codebender;

import eu.amaxilatis.codebender.actions.FlashPrivilegedAction;
import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;
import eu.amaxilatis.codebender.util.SerialPortList;
import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * A JApplet class.
 * Provides user interface to connecto to an arduino using a usb connection.
 */
public class CodeBenderApplet extends JApplet {

    private final transient String[] rates = new String[12];
    private transient String[] ports;
    public static transient String version; //NOPMD
    public static transient String buildNum;//NOPMD

    public static final int FLASH_OK = 0;
    public static final int LIBUSB_ERROR = 8;
    public static final int AVRDUDE_ERROR = 2;
    public static final int CONF_ERROR = 3;
    public static final int HEX_ERROR = 4;
    public static final int PROCESS_ERROR = 5;
    public static final int INTERUPTED_ERROR = 6;
    public static final int PORT_ERROR = 7;
    public static String errorMessage;

    public static String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public final void destroy() {
        System.out.println("CodeBenderApplet called Destroy");
        ConnectionManager.getInstance().disconnect();
    }

    /**
     * default constructor.
     */
    public CodeBenderApplet() {

        final Properties properties = new Properties();
        try {

            properties.load(this.getClass().getResourceAsStream("/props/version.properties"));
            version = (String) properties.get("version");
            buildNum = (String) properties.get("build");
            System.out.println("Version:" + version);
            System.out.println("Build:" + buildNum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRates() {
        return rates.toString();//NOPMD
    }

    public String getFireRates() {
        return ConnectionManager.getInstance().getBaudrates();
    }

    @Override
    public final void init() {
        System.out.println("CodeBenderApplet called Init");
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
//                            System.out.println(ports);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        final StringBuilder protsAvail = new StringBuilder();
//        System.out.println(ports);
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
        System.out.println("Returing value : " + response);
        return response;
    }

    public int tftpUpload(final String ip, byte[] file) {
        TFTPClient tftp = new TFTPClient();
        tftp.setDefaultTimeout(60000);
        try {
            tftp.open();
        } catch (SocketException e) {
            System.err.println("Error: could not open local UDP socket.");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(file);

        tftp.setDefaultTimeout(TFTP.DEFAULT_TIMEOUT);
        System.out.println("sending..");

        try {
            tftp.sendFile("file", TFTP.OCTET_MODE, bis, ip);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tftp.close();
        }

        return 0;

    }

}


