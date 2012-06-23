package eu.amaxilatis.codebender.actions;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import eu.amaxilatis.codebender.CodeBenderApplet;
import jssc.SerialNativeInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PrivilegedAction;

/**
 * Used to copy the files needed for flashing to the hard drive and perform flashing using avrdude.
 */
public class FlashPrivilegedAction implements PrivilegedAction {
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
                LOGGER.info("flashed=" + flashProc.exitValue());
                return flashProc.exitValue();
//                String line;
//
//                while ((line = br.readLine()) != null) {
//                    LOGGER.info(line);
//                    if (line.contains("can't open device")) {
//                        return CodeBenderApplet.PORT_ERROR;
//                    }
//                    if (line.contains("flash verified")) {
//                        return CodeBenderApplet.FLASH_OK;
//                    }
//                }

            } catch (InterruptedException e) {
                LOGGER.error(e);
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return CodeBenderApplet.PROCESS_ERROR;
        }
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
                LOGGER.info("flashed=" + flashProcess.exitValue());
                return flashProcess.exitValue();
//                String line;
//                while ((line = br.readLine()) != null) {
//                    LOGGER.info(line);
//                    if (line.contains("can't open device")) {
//                        return CodeBenderApplet.PORT_ERROR;
//                    }
//                    if (line.contains("flash verified")) {
//                        System.out.println(flashProcess.exitValue());
//                        LOGGER.info(flashProcess.exitValue());
//                        return CodeBenderApplet.FLASH_OK;
//                    }
//                }

            } catch (InterruptedException e) {
                LOGGER.error(e);
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return CodeBenderApplet.PROCESS_ERROR;
        }

    }

    private void checkLibUsb0Windows() throws IOException {
//        File dudeFile = new File("C:\\Temp\\libusb0.dll");
//        if (!dudeFile.exists() || filesDiffer("/libusb0.dll", "C:\\Temp\\libusb0.dll")) {
        downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/libusb0.dll", "C:\\Temp\\libusb0.dll");
//        writeBinaryToDisk("/libusb0.dll", "C:\\Temp\\libusb0.dll");
        makeExecutable("C:\\Temp\\libusb0.dll");
//        }
    }

    public void checkAvrdudeConfLinux() throws IOException {
//        File confFile = new File("/tmp/avrdude.conf");
//        if (!confFile.exists() || filesDiffer("/avrdude.conf.linux", "/tmp/avrdude.conf")) {
//            LOGGER.info("avrdude.conf does not exist");

        downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.conf.linux", "/tmp/avrdude.conf");
//        writeBinaryToDisk("/avrdude.conf.linux", "/tmp/avrdude.conf");
//        }
    }

    public void checkAvrdudeConfWindows() throws IOException {
//        File confFile = new File("C:\\Temp\\avrdude.conf");
//        if (!confFile.exists() || filesDiffer("/avrdude.conf.windows", "C:\\Temp\\avrdude.conf")) {
//            LOGGER.info("avrdude.conf does not exist");
        downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.conf.windows", "C:\\Temp\\avrdude.conf");
//        writeBinaryToDisk("/avrdude.conf.windows", "C:\\Temp\\avrdude.conf");
//        }
    }

    public void checkAvrdudeConfMac() throws IOException {
//        File confFile = new File("/tmp/avrdude.conf");
//        if (!confFile.exists() || filesDiffer("/avrdude.conf.mac", "/tmp/avrdude.conf")) {
//            LOGGER.info("avrdude.conf does not exist");
        downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.conf.mac", "/tmp/avrdude.conf");
//        writeBinaryToDisk("/avrdude.conf.mac", "/tmp/avrdude.conf");
//        }
    }

    private void writeBinaryToDisk(final String inputFile, final String destinationFile) throws IOException {
        LOGGER.info("writing to disk " + inputFile);
        final InputStream input = getClass().getResourceAsStream(inputFile);
        byte[] barr = ByteStreams.toByteArray(input);
        Files.write(barr, new File(destinationFile));
    }

    private void downloadBinaryToDisk(final String inputFile, final String destinationFile) throws IOException {
        LOGGER.info("downloading to disk " + inputFile);
        URL url = new URL(inputFile);
        url.openConnection();
        final InputStream input = url.openStream();
        byte[] barr = ByteStreams.toByteArray(input);
        Files.write(barr, new File(destinationFile));
    }

    private void makeExecutable(final String filename) {
        final File dudeFile = new File(filename);
        dudeFile.setExecutable(true);
    }

    public void checkAvrdudeLinux() throws IOException {
//        final File dudeFile = new File("/tmp/avrdude");
//        if (!dudeFile.exists() || filesDiffer("/bins/avrdude.linux", "/tmp/avrdude")) {
//        writeBinaryToDisk("/bins/avrdude.linux", "/tmp/avrdude");
        downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.linux", "/tmp/avrdude");
        makeExecutable("/tmp/avrdude");
//        }
    }

    public void checkAvrdudeMac() throws IOException {
//        final File dudeFile = new File("/tmp/avrdude");
//        if (!dudeFile.exists() || filesDiffer("/bins/avrdude.mac", "/tmp/avrdude")) {
        downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.mac", "/tmp/avrdude");
        makeExecutable("/tmp/avrdude");
//        }
    }


    public void checkAvrdudeWindows() throws IOException {
//        final File dudeFile = new File("C:\\Temp\\avrdude.exe");
//        if (!dudeFile.exists() || filesDiffer("/bins/avrdude.exe", "C:\\Temp\\avrdude.exe")) {
        downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.exe", "/tmp/avrdude");

        makeExecutable("C:\\Temp\\avrdude.exe");
//        }
    }


}
