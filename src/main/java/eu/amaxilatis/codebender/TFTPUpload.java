package eu.amaxilatis.codebender;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;

import java.io.ByteArrayInputStream;
import java.net.SocketException;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 7/19/12
 * Time: 10:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class TFTPUpload {
    public TFTPUpload(String ip, byte[] file) {
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

    }
}
