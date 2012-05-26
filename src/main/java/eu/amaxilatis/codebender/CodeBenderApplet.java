package eu.amaxilatis.codebender;

import eu.amaxilatis.codebender.graphics.ArduinoStatusImage;
import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;
import eu.amaxilatis.codebender.util.SerialPortList;
import jssc.SerialNativeInterface;
import org.apache.log4j.BasicConfigurator;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/**
 * A JApplet class.
 * Provides user interface to connecto to an arduino using a usb connection.
 */
public class CodeBenderApplet extends JApplet {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(CodeBenderApplet.class);

    private static SerialNativeInterface serialInterface = new SerialNativeInterface();

    /**
     * a connection handler.
     */
    private Thread serialPortThread;
    private final String[] rates = new String[12];
    private String[] detectedPorts;
    private String[] ports;
    private boolean started = false;
    public static String version;
    public static String buildNum;


    @Override
    public final void destroy() {
        LOGGER.info("CodeBenderApplet called Destroy");
        ConnectionManager.getInstance().disconnect();
    }

    /**
     * default constructor.
     *
     * @throws HeadlessException an exception.
     */
    public CodeBenderApplet() {
        BasicConfigurator.configure();
        Properties properties = new Properties();
        try {

            properties.load(this.getClass().getResourceAsStream("/props/version.properties"));
            version = (String) properties.get("version");
            buildNum = (String) properties.get("build");
            LOGGER.info("Version:" + version);
            LOGGER.info("Build:" + buildNum);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * Initialize the baudrates array.
     */
    private void initBaudRates() {

    }

    public String getRates() {
        return rates.toString();
    }

    public String getFireRates() {
        return ConnectionManager.getInstance().getBaudrates();
    }

    @Override
    public final void init() {
        LOGGER.info("CodeBenderApplet called Init");
    }

    /**
     * Build the default user interface.
     */
    private void createGUI() {
        LOGGER.info("CodeBenderApplet called CreateGUI");

        this.setBackground(Color.white);

        ArduinoStatusImage.setDisconnected();

        LOGGER.info("booting up");
    }

    /**
     * Called from javascript.
     *
     * @return a comma separated list of all available usb ports.
     */
    public String getFire2() {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            ports = SerialPortList.getInstance().getPortNames();
//                            LOGGER.info(ports);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return 0;
            }
        });

        final StringBuilder protsAvail = new StringBuilder();
//        LOGGER.info(ports);
        for (int i = 0; i < ports.length; i++) {
            protsAvail.append(",");
            protsAvail.append(ports[i]);

        }
        return (protsAvail.toString()).substring(1);
    }

    /**
     * Override connect function to be used by javascript.
     *
     * @param port the index of the port to connect to.
     * @param rate the rate to use when connecting.
     */
    public void overrideConnect(final int port, final int rate) {
        ConnectionManager.getInstance().setjTextArea(new PortOutputViewerFrame());

        ConnectionManager.getInstance().setPort(ports[port], rate);
        ConnectionManager.getInstance().connect();
    }

    public int flash(final int port, final String filename, final String baudrate) {
        FlashPrivilegedAction action = new FlashPrivilegedAction(ports[port], filename, baudrate);
        int response = (Integer) AccessController.doPrivileged(action);
        return response;
    }
}


/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/24/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
class FlashPrivilegedAction implements PrivilegedAction {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FlashPrivilegedAction.class);

    private final String port;
    private final String file;
    private final String baudRate;

    public FlashPrivilegedAction(String port, String file, String baudRate) {
        this.port = port;
        this.file = file;
        this.baudRate = baudRate;
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
        int result = 0;

        int retval1 = checkLibUsb0Windows();
        int retval = checkAvrdudeWindows();
        String avrdudePath;
        if (retval > 0) {
            avrdudePath = "avrdude.exe ";
        } else {
            return 1;
        }
        if (!checkAvrdudeConfWindows()) {
            return 2;
        }
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("C:\\Temp\\file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return 3;
        }
        System.out.println("Memory?" + Runtime.getRuntime().freeMemory());


        StringBuilder flashCommand = new StringBuilder();
        //avrdude -b 57600 -c arduino -p m168 -P usb -U flash:w:

        flashCommand.append("C:\\Temp\\avrdude.exe ")
                .append(" -C C:\\Temp\\avrdude.conf ")
                .append(" -b ").append(baudRate)
                .append(" -P \\\\.\\").append(port)
                .append(" -c arduino ")
                .append(" -p m328p ")
                .append(" -U flash:w:\"").append("C:\\Temp\\file.hex\":i -F");


        LOGGER.info("running : " + flashCommand.toString());

//        File batFile = new File(System.getProperty("user.home") + "\\Desktop\\codebenderFlash");


//        try {
//
//            FileOutputStream output;
//            try {
//                output = new FileOutputStream(batFile);
//            } catch (FileNotFoundException e) {
//                LOGGER.error(e);
//                return 1;
//            }
//            output.write(flashCommand.toString().getBytes());
//
//            output.flush();
//            output.close();
//        } catch (IOException e) {
//            LOGGER.error("writeBinaryToDisk", e);
//        }
//
        Process flashProc1 = null;
        try {
            flashProc1 = Runtime.getRuntime().exec(flashCommand.toString());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        flashProc1.destroy();
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//
//            }
//
//            try {
//                flashProc.waitFor();
//                InputStream is = flashProc.getInputStream();
//                InputStreamReader isr = new InputStreamReader(is);
//                BufferedReader br = new BufferedReader(isr);
//                String line;
//
//                while ((line = br.readLine()) != null) {
//                    LOGGER.info(line);
//                    if (line.contains("flash verified")) {
//                        result = 0;
//                    }
//                }
//
//            } catch (InterruptedException e) {
//                System.out.println(e.getMessage());
//                LOGGER.error(e);
//            }
        return result;
    }

    private int checkLibUsb0Windows() {
        File dudeFile = new File("C:\\Temp\\libusb0.dll");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/libusb0.dll", "C:\\Temp\\libusb0.dll");
            makeExecutable("C:\\Temp\\libusb0.dll");
        }
        return 1;

    }

    private Object flashMacOSX() {
        int result = 0;
        int retval = checkAvrdudeMac();
        String avrdudePath;
        if (retval > 0) {
            avrdudePath = "/tmp/avrdude ";
        } else {
            return 1;
        }
        if (!checkAvrdudeConfMac()) {
            return 2;
        }
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return 3;
        }

        StringBuilder flashCommand = new StringBuilder();

        flashCommand.append(avrdudePath)
                .append(" -C /tmp/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("/tmp/file.hex")
                .append(" -b ").append(baudRate)
                .append(" -F");


        try {
            LOGGER.info("running : " + flashCommand.toString());
            Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProc.waitFor();
                InputStream is = flashProc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;

                while ((line = br.readLine()) != null) {
                    LOGGER.info(line);
                    if (line.contains("flash verified")) {
                        result = 0;
                    }
                }

            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Object flashLinux() {
        int result = 0;
        int retval = checkAvrdudeLinux();
        String avrdudePath;
        if (retval > 0) {
            avrdudePath = "/tmp/avrdude ";
        } else {
            return 1;
        }
        if (!checkAvrdudeConfLinux()) {
            return 2;
        }
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return 3;
        }

        StringBuilder flashCommand = new StringBuilder();

        flashCommand.append(avrdudePath)
                .append(" -C /tmp/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("/tmp/file.hex")
                .append(" -b ").append(baudRate)
                .append(" -F");


        try {
            LOGGER.info("running : " + flashCommand.toString());
            Process flashProcess = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProcess.waitFor();
                InputStream is = flashProcess.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;

                while ((line = br.readLine()) != null) {
                    LOGGER.info(line);
                    if (line.contains("flash verified")) {
                        result = 0;
                    }
                }

            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            LOGGER.info("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    private void writeToDisk(final String inputFile, final String destinationFile) {
        File file = new File(destinationFile);

        InputStream input = getClass().getResourceAsStream(inputFile);
        InputStreamReader reader = new InputStreamReader(input);
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
            return;
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
            LOGGER.error("writeToDisk", e);
            return;
        }
    }

    public boolean checkAvrdudeConfLinux() {
        File confFile = new File("/tmp/avrdude.conf");
        if (!confFile.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            writeBinaryToDisk("/avrdude.conf.linux", "/tmp/avrdude.conf");
        }
        return true;
    }

    public boolean checkAvrdudeConfWindows() {
        File confFile = new File("C:\\Temp\\avrdude.conf");
        if (!confFile.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            writeBinaryToDisk("/avrdude.conf.windows", "C:\\Temp\\avrdude.conf");
        }
        return true;
    }

    public boolean checkAvrdudeConfMac() {
        File confFile = new File("/tmp/avrdude.conf");
        if (!confFile.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            writeBinaryToDisk("/avrdude.conf.mac", "/tmp/avrdude.conf");
        }
        return true;
    }

    private void writeBinaryToDisk(final String inputFile, final String destinationFile) {
        try {
            System.out.println(inputFile.substring(1));
//            InputStream input =  ClassLoader.getSystemClassLoader().getResourceAsStream(inputFile);
            InputStream input = getClass().getResourceAsStream(inputFile);
            FileOutputStream output;
            try {
                output = new FileOutputStream(new File(destinationFile));
            } catch (FileNotFoundException e) {
                LOGGER.error(e);
                return;
            }
            int c;
            while ((c = input.read()) != -1) {
                output.write(c);
            }
            output.flush();
            input.close();
            output.close();
        } catch (IOException e) {
            LOGGER.error("writeBinaryToDisk", e);
        }
    }

    private void makeExecutable(String filename) {
        File dudeFile = new File(filename);
        dudeFile.setExecutable(true);
    }

    public int checkAvrdudeLinux() {
        File dudeFile = new File("/tmp/avrdude");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/bins/avrdude.linux", "/tmp/avrdude");
            makeExecutable("/tmp/avrdude");
        }
        return 1;
    }

    public int checkAvrdudeMac() {
        File dudeFile = new File("/tmp/avrdude");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/bins/avrdude.mac", "/tmp/avrdude");
            makeExecutable("/tmp/avrdude");
        }
        return 1;
    }


    public int checkAvrdudeWindows() {
        File dudeFile = new File("C:\\Temp\\avrdude.exe");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/bins/avrdude.exe", "C:\\Temp\\avrdude.exe");
            makeExecutable("C:\\Temp\\avrdude.exe");
        }
        return 1;
    }


}

