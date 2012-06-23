package eu.amaxilatis.codebender.util;

import jssc.SerialNativeInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 12:01 PM
 */
public final class SerialPortList {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SerialPortList.class);

    private static SerialPortList ourInstance = new SerialPortList();
    private static final String DEVICES_PATH = "/dev";

    public static SerialPortList getInstance() {
        return ourInstance;
    }

    private SerialPortList() {
        LOGGER.trace("SerialPortList");
    }


//    private static Comparator<String> comparator = new Comparator<String>() {
//        @Override
//        public int compare(String valueA, String valueB) {
//            int result = 0;
//            if (valueA.toLowerCase().contains("com") && valueB.toLowerCase().contains("com")) {
//                try {
//                    int index1 = Integer.valueOf(valueA.toLowerCase().replace("com", ""));
//                    int index2 = Integer.valueOf(valueB.toLowerCase().replace("com", ""));
//                    result = index1 - index2;
//                } catch (Exception ex) {
//                    result = valueA.compareToIgnoreCase(valueB);
//                }
//            } else {
//                result = valueA.compareToIgnoreCase(valueB);
//            }
//            return result;
//        }
//    };


    public static String[] getPortNames() {
        if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_LINUX) {
            return getLinuxPortNames();
        } else if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_SOLARIS) {//since 0.9.0 ->
            return getSolarisPortNames();
        } else if (SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            return getMacOSXPortNames();
        }//<-since 0.9.0

        final java.util.List<String> ports = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
//            final int handle = serialInterface.openPort("COM" + i);
//            if (handle < 0) {
//                serialInterface.closePort(handle);
            ports.add("COM" + i);
//            }
        }
        String[] portsString = new String[ports.size()];
        for (int i = 0; i < ports.size(); i++) {
            portsString[i] = ports.get(i);
        }
        return portsString;
    }

    public static String[] getLinuxPortNames() {
//        String[] returnArray = new String[]{};
//        try {
//            final Process dmesgProcess = Runtime.getRuntime().exec("ls /dev/");
//            final BufferedReader reader = new BufferedReader(new InputStreamReader(dmesgProcess.getInputStream()));
//            final TreeSet<String> portsTree = new TreeSet<String>();
//            String buffer = "";
//            while ((buffer = reader.readLine()) != null && !buffer.isEmpty()) {
//                if (buffer.contains("ttyUSB")) {
//                    portsTree.add("/dev/" + buffer);
//                } else if (buffer.contains("ttyACM")) {
//                    portsTree.add("/dev/" + buffer);
//                }
//            }
//            returnArray = portsTree.toArray(returnArray);
//            reader.close();
//        } catch (IOException ex) {
//            LOGGER.error(ex, ex);
//        }
//        return returnArray;


        final List<String> portsList = new ArrayList<String>();
        portsList.addAll(seachSerialPorts("ttyACM"));
        portsList.addAll(seachSerialPorts("ttyUSB"));

        return (String[]) portsList.toArray(new String[portsList.size()]);
    }

    public static String[] getSolarisPortNames() {
        return (String[]) (new ArrayList<String>()).toArray();
    }

    public static String[] getMacOSXPortNames() {

        final List<String> portsList = new ArrayList<String>();
        portsList.addAll(seachSerialPorts("cu."));
        return (String[]) portsList.toArray(new String[portsList.size()]);

    }

    private static List<String> seachSerialPorts(final String key) {
        final File dir = new File(DEVICES_PATH);
        ArrayList<String> portsList = new ArrayList<String>();
        if (dir.exists() && dir.isDirectory()) {
//            System.out.println("found /dev");
            final File[] files = dir.listFiles();
//            System.out.println("contains " + files.length + " files");
            if (files.length > 0) {
                final TreeSet<String> portsTree = new TreeSet<String>();
                portsList = new ArrayList<String>();
                for (File file : files) {
//                    System.out.println("checking " + file.getName());
                    if (!file.isDirectory() && !file.isFile() && file.getName().contains(key)) {
                        portsTree.add("/dev/" + file.getName());
                    }
                }
                for (String portName : portsTree) {
                    portsList.add(portName);
                }
            }
        }

        return portsList;
    }

}
