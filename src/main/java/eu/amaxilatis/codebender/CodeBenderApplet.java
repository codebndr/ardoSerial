package eu.amaxilatis.codebender;

import eu.amaxilatis.codebender.graphics.ArduinoStatusImage;
import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;
import eu.amaxilatis.codebender.util.SerialPortList;
import jssc.SerialNativeInterface;
import org.apache.log4j.BasicConfigurator;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static final int FLASH_OK = 0;
    public static final int LIBUSB_ERROR = 1;
    public static final int AVRDUDE_ERROR = 2;
    public static final int CONF_ERROR = 3;
    public static final int HEX_ERROR = 4;
    public static final int PROCESS_ERROR = 5;
    public static final int INTERUPTED_ERROR = 6;
    public static final int PORT_ERROR=7;

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
        System.out.println("Returing value : " + response);
        return response;
    }


}


/**
 * Used to copy the files needed for flashing to the hard drive and perform flashing using avrdude.
 */
class FlashPrivilegedAction implements PrivilegedAction {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(FlashPrivilegedAction.class);
    /**
     * Port to be used.
     */
    private final String port;
    /**
     * The file to flash.
     */
    private final String file;
    /**
     * The baudRate to use.
     */
    private final String baudRate;

    /**
     * Constructs a new flash action.
     *
     * @param port     the port to use.
     * @param file     the hex file to flash.
     * @param baudRate the baudrate to use during flashing.
     */
    public FlashPrivilegedAction(final String port, final String file, final String baudRate) {
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

    /**
     * Used to flash on Windows.
     *
     * @return The flash Status: 0 is OK , else an Error Code is returned.
     */
    private Object flashWindows() {
        int result = 0;

        try {
            checkLibUsb0Windows();
        } catch (IOException e) {
            return CodeBenderApplet.LIBUSB_ERROR;
        }

        try {
            checkAvrdudeWindows();
        } catch (IOException e) {
            return CodeBenderApplet.AVRDUDE_ERROR;
        }

        try {
            checkAvrdudeConfWindows();
        } catch (IOException e) {
            return CodeBenderApplet.CONF_ERROR;
        }

        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("C:\\Temp\\file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return CodeBenderApplet.HEX_ERROR;
        }

        final StringBuilder flashCommand = (new StringBuilder()).append("C:\\Temp\\avrdude.exe ")
                .append(" -C C:\\Temp\\avrdude.conf ")
                .append(" -b ").append(baudRate)
                .append(" -P \\\\.\\").append(port)
                .append(" -c arduino ")
                .append(" -p m328p ")
                .append(" -U flash:w:\"").append("C:\\Temp\\file.hex\":i -F");

        LOGGER.info("running : " + flashCommand.toString());

        Process flashProc1 = null;
        try {
            flashProc1 = Runtime.getRuntime().exec(flashCommand.toString());
        } catch (IOException e) {
            return CodeBenderApplet.PROCESS_ERROR;
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
        return CodeBenderApplet.FLASH_OK;
    }

    private Object flashMacOSX() {
        try {
            checkAvrdudeMac();
        } catch (IOException e) {
            return CodeBenderApplet.AVRDUDE_ERROR;
        }


        try {
            checkAvrdudeConfMac();
        } catch (IOException e) {
            return CodeBenderApplet.CONF_ERROR;
        }

        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return CodeBenderApplet.HEX_ERROR;
        }

        final StringBuilder flashCommand = (new StringBuilder()).append("/tmp/avrdude ")
                .append(" -C /tmp/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("/tmp/file.hex")
                .append(" -b ").append(baudRate)
                .append(" -F");

        try {
            LOGGER.info("running : " + flashCommand.toString());
            final Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProc.waitFor();
                final InputStream is = flashProc.getInputStream();
                final InputStreamReader isr = new InputStreamReader(is);
                final BufferedReader br = new BufferedReader(isr);
                String line;

                while ((line = br.readLine()) != null) {
                    LOGGER.info(line);
                    if (line.contains("can't open device")){
                        return CodeBenderApplet.PORT_ERROR;
                    }
                    if (line.contains("flash verified")) {
                        return CodeBenderApplet.FLASH_OK;
                    }
                }

            } catch (InterruptedException e) {
                LOGGER.error(e);
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
            return CodeBenderApplet.PROCESS_ERROR;
        }
        return CodeBenderApplet.FLASH_OK;
    }

    private Object flashLinux() {

        try {
            checkAvrdudeLinux();
        } catch (IOException e) {
            return CodeBenderApplet.AVRDUDE_ERROR;
        }

        try {
            checkAvrdudeConfLinux();
        } catch (IOException e) {
            return CodeBenderApplet.CONF_ERROR;
        }

        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return CodeBenderApplet.HEX_ERROR;
        }

        StringBuilder flashCommand = new StringBuilder();

        flashCommand.append("/tmp/avrdude ")
                .append(" -C /tmp/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("/tmp/file.hex")
                .append(" -b ").append(baudRate)
                .append(" -F");


        try {
            LOGGER.info("running : " + flashCommand.toString());
            final Process flashProcess = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProcess.waitFor();
                final InputStream is = flashProcess.getInputStream();
                final InputStreamReader isr = new InputStreamReader(is);
                final BufferedReader br = new BufferedReader(isr);

                String line;
                while ((line = br.readLine()) != null) {
                    LOGGER.info(line);
		    if (line.contains("can't open device")){
			return CodeBenderApplet.PORT_ERROR;
		    }
                    if (line.contains("flash verified")) {
                        System.out.println(flashProcess.exitValue());
                        LOGGER.info(flashProcess.exitValue());
                        return CodeBenderApplet.FLASH_OK;
	            }
                }

            } catch (InterruptedException e) {
                LOGGER.error(e);
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
            LOGGER.info("flashed");
        } catch (IOException e) {
            e.printStackTrace();
            return CodeBenderApplet.PROCESS_ERROR;
        }
        return CodeBenderApplet.FLASH_OK;

    }

    private void checkLibUsb0Windows() throws IOException {
        File dudeFile = new File("C:\\Temp\\libusb0.dll");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/libusb0.dll", "C:\\Temp\\libusb0.dll");
            makeExecutable("C:\\Temp\\libusb0.dll");
        }
    }

    public void checkAvrdudeConfLinux() throws IOException {
        File confFile = new File("/tmp/avrdude.conf");
        if (!confFile.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            writeBinaryToDisk("/avrdude.conf.linux", "/tmp/avrdude.conf");
        }
    }

    public void checkAvrdudeConfWindows() throws IOException {
        File confFile = new File("C:\\Temp\\avrdude.conf");
        if (!confFile.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            writeBinaryToDisk("/avrdude.conf.windows", "C:\\Temp\\avrdude.conf");
        }
    }

    public void checkAvrdudeConfMac() throws IOException {
        File confFile = new File("/tmp/avrdude.conf");
        if (!confFile.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            writeBinaryToDisk("/avrdude.conf.mac", "/tmp/avrdude.conf");
        }
    }

    private void writeBinaryToDisk(final String inputFile, final String destinationFile) throws IOException {
        final InputStream input = getClass().getResourceAsStream(inputFile);
        FileOutputStream output;
        try {
            output = new FileOutputStream(new File(destinationFile));
        } catch (FileNotFoundException e) {
            LOGGER.error(e);
            throw new IOException();
        }
        int c;
        while ((c = input.read()) != -1) {
            output.write(c);
        }
        output.flush();
        input.close();
        output.close();
    }

    private void makeExecutable(final String filename) {
        final File dudeFile = new File(filename);
        dudeFile.setExecutable(true);
    }

    public void checkAvrdudeLinux() throws IOException {
        final File dudeFile = new File("/tmp/avrdude");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/bins/avrdude.linux", "/tmp/avrdude");
            makeExecutable("/tmp/avrdude");
        }
    }

    public void checkAvrdudeMac() throws IOException {
        final File dudeFile = new File("/tmp/avrdude");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/bins/avrdude.mac", "/tmp/avrdude");
            makeExecutable("/tmp/avrdude");
        }
    }


    public void checkAvrdudeWindows() throws IOException {
        final File dudeFile = new File("C:\\Temp\\avrdude.exe");
        if (!dudeFile.exists()) {
            writeBinaryToDisk("/bins/avrdude.exe", "C:\\Temp\\avrdude.exe");
            makeExecutable("C:\\Temp\\avrdude.exe");
        }
    }


}

