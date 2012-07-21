package eu.amaxilatis.codebender;

import javax.swing.*;
import java.io.IOException;
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
        new TFTPUpload(ip, file);
        return 0;
    }

}


