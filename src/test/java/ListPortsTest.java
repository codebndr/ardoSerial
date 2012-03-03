import eu.amaxilatis.ardoserial.util.SerialPortList;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 12:00 PM
 */
public class ListPortsTest {
    public static void main(final String[] argv) {

        String[] ports = SerialPortList.getInstance().getPortNames();
        for (String port : ports) {
            System.out.println(port);
        }
    }
}
