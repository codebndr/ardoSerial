package eu.amaxilatis.ardoserial.util;

import jssc.SerialNativeInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 12:01 PM
 */
public class SerialPortList {

    private static SerialNativeInterface serialInterface;

    private static SerialPortList ourInstance = new SerialPortList();

    public static SerialPortList getInstance() {
        return ourInstance;
    }

    private SerialPortList() {
    }

    public static String[] getPortNames() {
        System.out.println("getPortNames");
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_LINUX) {
            return getLinuxPortNames();
        } else if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_SOLARIS) {//since 0.9.0 ->
            return getSolarisPortNames();
        } else if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            return getMacOSXPortNames();
        }//<-since 0.9.0
//        String[] portNames = serialInterface.getSerialPortNames();
//        if (portNames == null) {
//            return new String[]{};
//        }
//        TreeSet<String> ports = new TreeSet<String>(comparator);
//        ports.addAll(Arrays.asList(portNames));
//        return ports.toArray(new String[ports.size()]);
        return null;
    }

    public static String[] getLinuxPortNames() {
        System.out.println("getLinuxPortNames");
        String[] returnArray = new String[]{};
        try {
            Process dmesgProcess = Runtime.getRuntime().exec("ls /dev/");
            BufferedReader reader = new BufferedReader(new InputStreamReader(dmesgProcess.getInputStream()));
            TreeSet<String> portsTree = new TreeSet<String>();
            ArrayList<String> portsList = new ArrayList<String>();
            String buffer = "";
            while ((buffer = reader.readLine()) != null && !buffer.isEmpty()) {
                if (buffer.contains("ttyUSB")) {
                    portsTree.add("/dev/" + buffer);
                } else if (buffer.contains("ttyACM")) {
                    portsTree.add("/dev/" + buffer);
                }
            }
            returnArray = portsTree.toArray(returnArray);
            reader.close();
        } catch (IOException ex) {
            //Do nothing
        }
        return returnArray;
    }

    public static String[] getSolarisPortNames() {
        return null;
    }

    public static String[] getMacOSXPortNames() {
        System.out.println("getMacOSXPortNames");
        String[] returnArray = new String[]{};
        File dir = new File("/dev");
        if (dir.exists() && dir.isDirectory()) {
            System.out.println("found /dev");
            File[] files = dir.listFiles();
            System.out.println("contains " + files.length + " files");
            if (files.length > 0) {
                TreeSet<String> portsTree = new TreeSet<String>();
                ArrayList<String> portsList = new ArrayList<String>();
                for (File file : files) {
                    System.out.println("cheching " + file.getName());
                    if (!file.isDirectory() && !file.isFile() && (file.getName().contains("tty.") || file.getName().contains("cu."))) {
                        System.out.println("adding " + file.getName());
                        portsTree.add("/dev/" + file.getName());
                    }
                }
                for (String portName : portsTree) {
                    portsList.add(portName);
                }
                returnArray = portsList.toArray(returnArray);
            }
        }
        return returnArray;
    }
}
