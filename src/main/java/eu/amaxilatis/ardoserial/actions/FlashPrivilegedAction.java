package eu.amaxilatis.ardoserial.actions;

import jssc.SerialNativeInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivilegedAction;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/24/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlashPrivilegedAction implements PrivilegedAction {
    private final String port;
    private final String file;

    public FlashPrivilegedAction(String port, String file) {
        this.port = port;
        this.file = file;
    }

    public Object run() {

        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_LINUX) {
            return flashLinux();
        } else if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            return flashMacOSX();
        } else {
            return flashWindows();
        }
    }

    private Object flashWindows() {
        return null;
    }

    private Object flashMacOSX() {
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        StringBuilder flashCommand = new StringBuilder();

        flashCommand.append("/Applications/Arduino.app/Contents/Resources/Java/hardware/tools/avr/bin/avrdude ")
                .append(" -C /Applications/Arduino.app/Contents/Resources/Java/hardware/tools/avr/etc/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("/tmp/file.hex")
                .append(" -b 115200 -F");


        try {
            System.out.println("running : " + flashCommand.toString());
            Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object flashLinux() {
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        StringBuilder flashCommand = new StringBuilder();

        flashCommand.append("avrdude ")
//                .append(" -C /usr/share/arduino/hardware/tools/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("/tmp/file.hex")
                .append(" -b 115200 -F");


        try {
            System.out.println("running : " + flashCommand.toString());
            Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
