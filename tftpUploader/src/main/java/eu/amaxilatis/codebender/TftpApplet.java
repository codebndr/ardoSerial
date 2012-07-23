package eu.amaxilatis.codebender;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Properties;

/**
 * A JApplet class.
 * Allows to upload a bin file to an arduino using a tftp connection.
 */
public class TftpApplet extends JApplet {

    private final transient String[] rates = new String[12];
    private transient String[] ports;
    public static transient String version; //NOPMD
    public static transient String buildNum;//NOPMD

    public static final int FLASH_OK = 0;

    public static String errorMessage;

    public static String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public final void destroy() {
        System.out.println("TftpApplet called Destroy");
    }

    /**
     * default constructor.
     */
    public TftpApplet() {

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

    @Override
    public final void init() {
        System.out.println("TftpApplet called Init");
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


