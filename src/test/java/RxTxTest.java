import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 12:00 PM
 */
public class RxTxTest {
    private String portName = "/dev/ttyUSB1";

    @Test
    public void TestRxTx() throws Exception {
        System.out.println("blah");
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

        CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

        if (commPort instanceof SerialPort) {
            SerialPort serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            InputStream in = serialPort.getInputStream();
            OutputStream out = serialPort.getOutputStream();

            (new Thread(new SerialReader(in))).start();
            (new Thread(new SerialWriter(out))).start();

            Thread.sleep(50000);

        } else {
            System.out.println("Error: Only serial ports are handled by this example.");
        }
    }


    /** */
    public static class SerialReader implements Runnable {
        InputStream in;

        public SerialReader(InputStream in) {
            this.in = in;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while ((len = this.in.read(buffer)) > -1) {
                    System.out.print(new String(buffer, 0, len));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** */
    public static class SerialWriter implements Runnable {
        OutputStream out;

        public SerialWriter(OutputStream out) {
            this.out = out;
        }

        public void run() {
            try {
                int c = 0;
                while ((c = System.in.read()) > -1) {
                    this.out.write(c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
