import eu.amaxilatis.codebender.command.AvrdudeLinuxCommand;
import eu.amaxilatis.codebender.command.AvrdudeWindowsCommand;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 12:00 PM
 */
public class AvrdudeCommandTest {
    @Test
    public void TestWindowsCommand() throws Exception {
        final String basepath = "C:\\Temp\\";
        final String port = "COM1";
        final String filepath = basepath + "\\file.hex\"";
        final String baudRate = "115200";
        final AvrdudeWindowsCommand avrdudeWindowsFlashCommand =
                new AvrdudeWindowsCommand(basepath, port, filepath, baudRate);

        System.out.println(avrdudeWindowsFlashCommand);
        final StringBuilder flashCommand = (new StringBuilder()).append("\""+basepath + "\\avrdude.exe\" ")
                .append(" -C \"" + basepath + "\\avrdude.conf\" ")
                .append(" -b ").append(baudRate)
                .append(" -P \\\\.\\").append(port)
                .append(" -c arduino ")
                .append(" -p m328p ")
                .append(" -U flash:w:\"").append(basepath + "\\file.hex\":a -F");


        System.out.println(flashCommand.toString());
        assert ((flashCommand.toString().equals(avrdudeWindowsFlashCommand.toString())));
    }

    @Test
    public void TestUnixCommand() throws Exception {

        final String basepath = "/tmp/";
        final String port = "/dev/ttyUSB0";
        final String filepath = basepath + "file.hex";
        final String baudRate = "115200";
        final AvrdudeLinuxCommand avrdudeFlashCommand =
                new AvrdudeLinuxCommand(basepath, port, filepath, baudRate);

        System.out.println(avrdudeFlashCommand);

        final StringBuilder oldFlashCommand = new StringBuilder();
        oldFlashCommand.append("/tmp/avrdude ")
                .append(" -C /tmp/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append(filepath).append("")
                .append(" -b ").append(baudRate)
                .append(" -F");


        System.out.println(oldFlashCommand.toString());
        assert ((oldFlashCommand.toString().equals(avrdudeFlashCommand.toString())));
    }

}
