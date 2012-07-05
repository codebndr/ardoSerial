package eu.amaxilatis.codebender.actions;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import eu.amaxilatis.codebender.CodeBenderApplet;
import jssc.SerialNativeInterface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
    private final transient String port;
    /**
     * The file to flash.
     */
    private final transient String file;
    /**
     * The baudRate to use.
     */
    private final transient String baudRate;
    private static final String TEMP_HEX_UNIX = "/tmp/file.hex";
    private static final String AVRDUDE_PATH_UNIX = "/tmp/avrdude";

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
        final String basepath = System.getProperty("user.home").substring(0, System.getProperty("user.home").indexOf('\\'));

        try {
            downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/libusb0.dll", basepath + "\\Temp\\libusb0.dll");
            makeExecutable(basepath + "\\Temp\\libusb0.dll");
        } catch (IOException e) {
            return CodeBenderApplet.LIBUSB_ERROR;
        }

        try {
            downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.exe", basepath + "\\Temp\\avrdude.exe");
            makeExecutable(basepath + "\\Temp\\avrdude.exe");
        } catch (IOException e) {
            return CodeBenderApplet.AVRDUDE_ERROR;
        }

        try {
            downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.conf.windows", basepath + "\\Temp\\avrdude.conf");
        } catch (IOException e) {
            return CodeBenderApplet.CONF_ERROR;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(basepath + "\\Temp\\file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return CodeBenderApplet.HEX_ERROR;
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                LOGGER.error(e, e);
            }
        }

        final StringBuilder flashCommand = (new StringBuilder()).append(basepath + "\\Temp\\avrdude.exe ")
                .append(" -C " + basepath + "\\Temp\\avrdude.conf ")
                .append(" -b ").append(baudRate)
                .append(" -P \\\\.\\").append(port)
                .append(" -c arduino ")
                .append(" -p m328p ")
                .append(" -U flash:w:\"").append(basepath+"\\Temp\\file.hex\":i -F");

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
            LOGGER.error(e, e);
        }
        flashProc1.destroy();

        return CodeBenderApplet.FLASH_OK;
    }

    private Object flashMacOSX() {
        try {
            downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.mac", AVRDUDE_PATH_UNIX);
            makeExecutable(AVRDUDE_PATH_UNIX);
        } catch (IOException e) {
            return CodeBenderApplet.AVRDUDE_ERROR;
        }


        try {
            downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.conf.mac", "/tmp/avrdude.conf");
        } catch (IOException e) {
            return CodeBenderApplet.CONF_ERROR;
        }

        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(TEMP_HEX_UNIX);
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
                .append(" -u -U flash:w:").append(TEMP_HEX_UNIX)
                .append(" -b ").append(baudRate)
                .append(" -F");

        try {
            LOGGER.info("running : " + flashCommand.toString());
            final Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProc.waitFor();
                LOGGER.info("flashed=" + flashProc.exitValue());
                return flashProc.exitValue();

            } catch (InterruptedException e) {
                LOGGER.error(e);
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
            return CodeBenderApplet.PROCESS_ERROR;
        }
    }

    private Object flashLinux() {

        try {
            downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.linux", AVRDUDE_PATH_UNIX);
            makeExecutable(AVRDUDE_PATH_UNIX);
        } catch (IOException e) {
            return CodeBenderApplet.AVRDUDE_ERROR;
        }

        try {
            downloadBinaryToDisk("http://students.ceid.upatras.gr/~amaxilatis/dudes/avrdude.conf.linux", "/tmp/avrdude.conf");
        } catch (IOException e) {
            return CodeBenderApplet.CONF_ERROR;
        }

        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter(TEMP_HEX_UNIX);
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return CodeBenderApplet.HEX_ERROR;
        }

        final StringBuilder flashCommand = new StringBuilder();

        flashCommand.append("/tmp/avrdude ")
                .append(" -C /tmp/avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append(TEMP_HEX_UNIX)
                .append(" -b ").append(baudRate)
                .append(" -F");


        try {
            LOGGER.info("running : " + flashCommand.toString());
            final Process flashProcess = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProcess.waitFor();
                LOGGER.info("flashed=" + flashProcess.exitValue());
                return flashProcess.exitValue();

            } catch (InterruptedException e) {
                LOGGER.error(e);
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
        } catch (IOException e) {
            LOGGER.error(e, e);
            return CodeBenderApplet.PROCESS_ERROR;
        }

    }

//    private void writeBinaryToDisk(final String inputFile, final String destinationFile) throws IOException {
//        LOGGER.info("writing to disk " + inputFile);
//        final InputStream input = getClass().getResourceAsStream(inputFile);
//        byte[] barr = ByteStreams.toByteArray(input);
//        Files.write(barr, new File(destinationFile));
//    }

    private void downloadBinaryToDisk(final String inputFile, final String destinationFile) throws IOException {
        LOGGER.info("downloading to disk " + inputFile);
        final URL url = new URL(inputFile);
        url.openConnection();
        final InputStream input = url.openStream();
        final byte[] barr = ByteStreams.toByteArray(input);
        Files.write(barr, new File(destinationFile));
    }

    private void makeExecutable(final String filename) {
        final File dudeFile = new File(filename);
        dudeFile.setExecutable(true);
    }

}
