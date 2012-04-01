package eu.amaxilatis.ardoserial.actions;

import jssc.SerialNativeInterface;

import java.io.*;
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
            try {
                flashProc.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object flashLinux() {
        checkAvrdudeConfLinux();
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
                .append(" -C /tmp/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("/tmp/file.hex")
                .append(" -b 115200 -F");


        try {
            System.out.println("running : " + flashCommand.toString());
            Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProc.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public void checkAvrdudeConfLinux() {
        File avrdudeConf = new File("/tmp/avrdude.conf");
        if (!avrdudeConf.exists()) {
            System.out.println("avrdude.conf does not exist");
            InputStream input = this.getClass().getResourceAsStream("/avrdude.conf.linux");
            InputStreamReader reader = new InputStreamReader(input);
            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(avrdudeConf)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            final BufferedReader bufferedReader = new BufferedReader(reader);
            try {
                String line = bufferedReader.readLine();
                while (line != null) {
                    writer.write(line + "\n");
                    line = bufferedReader.readLine();
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
