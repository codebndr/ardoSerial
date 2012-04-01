package eu.amaxilatis.ardoserial.util;

import jssc.SerialNativeInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;


/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 12:01 PM
 */
public class SerialPortList {

    private static SerialNativeInterface serialInterface = new SerialNativeInterface();

    private static SerialPortList ourInstance = new SerialPortList();

    public static SerialPortList getInstance() {
        return ourInstance;
    }

    private SerialPortList() {
    }


    private static Comparator<String> comparator = new Comparator<String>() {
        @Override
        public int compare(String valueA, String valueB) {
            int result = 0;
            if (valueA.toLowerCase().contains("com") && valueB.toLowerCase().contains("com")) {
                try {
                    int index1 = Integer.valueOf(valueA.toLowerCase().replace("com", ""));
                    int index2 = Integer.valueOf(valueB.toLowerCase().replace("com", ""));
                    result = index1 - index2;
                } catch (Exception ex) {
                    result = valueA.compareToIgnoreCase(valueB);
                }
            } else {
                result = valueA.compareToIgnoreCase(valueB);
            }
            return result;
        }
    };


    public static String[] getPortNames() {
        System.out.println("getPortNames");
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_LINUX) {
            return getLinuxPortNames();
        } else if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_SOLARIS) {//since 0.9.0 ->
            return getSolarisPortNames();
        } else if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            return getMacOSXPortNames();
        }//<-since 0.9.0

        java.util.List<String> ports = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            final int handle = serialInterface.openPort("COM" + i);
            if (handle < 0) {
                serialInterface.closePort(handle);
                ports.add("COM" + i);
            }
        }
        String[] portsString = new String[ports.size()];
        for (int i = 0; i < ports.size(); i++) {
            portsString[i] = ports.get(i);
        }
        return portsString;
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
                    System.out.println("checking " + file.getName());
                    if (!file.isDirectory() && !file.isFile() && file.getName().contains("cu.")) {
//                    if (!file.isDirectory() && !file.isFile() && (file.getName().contains("tty.") || file.getName().contains("cu."))) {
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
