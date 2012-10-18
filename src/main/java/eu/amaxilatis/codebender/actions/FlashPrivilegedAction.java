package eu.amaxilatis.codebender.actions;

import eu.amaxilatis.codebender.CodeBenderApplet;
import eu.amaxilatis.codebender.command.AvrdudeLinuxCommand;
import eu.amaxilatis.codebender.command.AvrdudeWindowsCommand;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.*;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to copy the files needed for flashing to the hard drive and perform flashing using avrdude.
 */
public class FlashPrivilegedAction implements PrivilegedAction {
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
    private transient String baudRate;
    private static final String TEMP_HEX_UNIX = "/tmp/file.hex";
    private static final String AVRDUDE_PATH_UNIX = "/tmp/avrdude";
    private transient String basepath;

    private static final Map<String, String> changemap = new HashMap<String, String>();
    private String maximumSize;
    private String board;
    private String protocol;
    private static final String TEMP_BIN_UNIX = "/tmp/file.bin";

    /**
     * Constructs a new flash action.
     *
     * @param port     the port to use.
     * @param file     the hex file to flash.
     * @param baudRate the baudrate to use during flashing.
     */
    public FlashPrivilegedAction(final String port, final String file, final String baudRate) {
        System.out.println("FlashPrivilegedAction");
        this.port = port;
        this.file = file;
        this.baudRate = baudRate;
        maximumSize = "";

        populateChangemap();

        System.out.println("FlashPrivilegedAction");

    }

    public FlashPrivilegedAction(final String port, final String file, final String maximumSize, final String protocol, final String baudRate, final String board) {
        System.out.println("FlashPrivilegedAction");
        this.port = port;
        this.file = file;
        this.maximumSize = this.maximumSize;
        this.baudRate = baudRate;
        this.protocol = protocol;


        populateChangemap();

        this.board = changemap.get(board);

        System.out.println("FlashPrivilegedAction");

    }

    public final Object run() {
        System.out.println("run");

        final String osys = System.getProperty("os.name").toLowerCase();
        System.out.println(System.getProperty("user.home"));
        System.out.println(osys);

        basepath = System.getProperty("java.io.tmpdir");
        if ((osys.indexOf("win") >= 0)) {
            return flashWindows();
        } else if ((osys.indexOf("linux") >= 0)) {
            basepath = basepath + "/";
            return flashLinux();
        } else {
            basepath = basepath + "/";
            return flashMacOSX();
        }
    }

    /**
     * Used to flash on Windows.
     *
     * @return The flash Status: 0 is OK , else an Error Code is returned.
     */
    private Object flashWindows() {
        System.out.println("flashWindows");

        try {
            downloadBinaryToDisk("http://codebender.cc/dudes/libusb0.dll", basepath + "\\libusb0.dll");
            makeExecutable(basepath + "\\libusb0.dll");
        } catch (IOException e) {
            reportError(e);
            return CodeBenderApplet.LIBUSB_ERROR;
        }

        try {
            downloadBinaryToDisk("http://codebender.cc/dudes/avrdude.exe", basepath + "\\avrdude.exe");
            makeExecutable(basepath + "\\avrdude.exe");
        } catch (IOException e) {
            reportError(e);
            return CodeBenderApplet.AVRDUDE_ERROR;
        }

        try {
            downloadBinaryToDisk("http://codebender.cc/dudes/avrdude.conf.windows", basepath + "\\avrdude.conf");
        } catch (IOException e) {
            reportError(e);
            return CodeBenderApplet.CONF_ERROR;
        }

        AvrdudeWindowsCommand flashCommand;
        if ("".equals(maximumSize)) {

            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(basepath + "\\file.hex");
                fileWriter.write(file);
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
                reportError(e);
                return CodeBenderApplet.HEX_ERROR;
            } finally {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    reportError(e);
                }
            }

            flashCommand =
                    new AvrdudeWindowsCommand(basepath, port, basepath + "\\file.hex\"", baudRate);
        } else {
            byte[] bytes = Base64.decodeBase64(file);

            File file = new File(basepath + "\\file.bin");
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(file);
                fout.write(bytes);

            } catch (IOException e) {
                e.printStackTrace();
                reportError(e);
                return CodeBenderApplet.BIN_ERROR;
            } finally {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    reportError(e);
                }
            }

            flashCommand =
                    new AvrdudeWindowsCommand(basepath, port, basepath + "\\file.bin\"", maximumSize, protocol, baudRate, board);
        }

        System.out.println("running : " + flashCommand.toString());

        Process flashProc1 = null;
        try {
            flashProc1 = Runtime.getRuntime().exec(flashCommand.toString());
        } catch (IOException e) {
            reportError(e, flashCommand.toString());
            return CodeBenderApplet.PROCESS_ERROR;
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flashProc1.destroy();

        return CodeBenderApplet.FLASH_OK;
    }


    private void reportError(Exception exception, String flashCommand) {
        final StringBuilder builder = new StringBuilder(flashCommand);
        for (StackTraceElement element : exception.getStackTrace()) {
            builder.append(element.toString()).append("\n");
        }
        CodeBenderApplet.errorMessage = builder.toString();
        try {
            callUrl("http://codebender.cc/misc/notify?message=" + URLEncoder.encode(builder.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void reportError(final Exception exception) {

        final StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            builder.append(element.toString()).append("\n");
        }
        CodeBenderApplet.errorMessage = builder.toString();
        try {
            callUrl("http://codebender.cc/misc/notify?message=" + URLEncoder.encode(builder.toString(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Opens a connection over the Rest Interfaces to the server and adds the event.
     *
     * @param urlString the string url that describes the event
     */

    private static void callUrl(final String urlString) {
        HttpURLConnection httpURLConnection = null;

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            if (!(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)) {
                final StringBuilder errorBuilder = new StringBuilder("Problem ");
                errorBuilder.append("with ").append(urlString);
                errorBuilder.append(" Response: ").append(httpURLConnection.getResponseCode());
            }
            httpURLConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private Object flashMacOSX() {
        try {
            downloadBinaryToDisk("http://codebender.cc/dudes/avrdude.mac", AVRDUDE_PATH_UNIX);
            makeExecutable(AVRDUDE_PATH_UNIX);
        } catch (IOException e) {
            reportError(e);
            return CodeBenderApplet.AVRDUDE_ERROR;
        }


        try {
            downloadBinaryToDisk("http://codebender.cc/dudes/avrdude.conf.mac", basepath + "avrdude.conf");
        } catch (IOException e) {
            reportError(e);
            return CodeBenderApplet.CONF_ERROR;
        }

        final AvrdudeLinuxCommand flashCommand;
        if ("".equals(maximumSize)) {
            try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(TEMP_HEX_UNIX);
                fileWriter.write(file);
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
                reportError(e);
                return CodeBenderApplet.HEX_ERROR;
            }


            flashCommand =
                    new AvrdudeLinuxCommand(basepath, port, TEMP_HEX_UNIX, baudRate);
        } else {
            byte[] bytes = Base64.decodeBase64(file);

            File file = new File(TEMP_BIN_UNIX);
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(file);
                fout.write(bytes);

            } catch (IOException e) {
                e.printStackTrace();
                reportError(e);
                return CodeBenderApplet.BIN_ERROR;
            } finally {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    reportError(e);
                }
            }

            flashCommand =
                    new AvrdudeLinuxCommand(basepath, port, TEMP_BIN_UNIX, maximumSize, protocol, baudRate, board);
        }

        try {
            System.out.println("running : " + flashCommand.toString());
            final Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProc.waitFor();
                System.out.println("flashed=" + flashProc.exitValue());
                return flashProc.exitValue();

            } catch (InterruptedException e) {
                e.printStackTrace();
                reportError(e, flashCommand.toString());
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            reportError(e);
            return CodeBenderApplet.PROCESS_ERROR;
        }
    }

    private Object flashLinux() {

        try {
            downloadBinaryToDisk("http://codebender.cc/dudes/avrdude.linux", AVRDUDE_PATH_UNIX);
            makeExecutable(AVRDUDE_PATH_UNIX);
        } catch (IOException e) {
            reportError(e);
            return CodeBenderApplet.AVRDUDE_ERROR;
        }

        try {
            downloadBinaryToDisk("http://codebender.cc/dudes/avrdude.conf.linux", "/tmp/avrdude.conf");
        } catch (IOException e) {
            reportError(e);
            return CodeBenderApplet.CONF_ERROR;
        }

        final AvrdudeLinuxCommand flashCommand;
        if ("".equals(maximumSize)) {
            try {
                FileWriter fileWriter = null;
                fileWriter = new FileWriter(TEMP_HEX_UNIX);
                fileWriter.write(file);
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
                reportError(e);
                return CodeBenderApplet.HEX_ERROR;
            }

            flashCommand =
                    new AvrdudeLinuxCommand(basepath, port, TEMP_HEX_UNIX, baudRate);
        } else {
            byte[] bytes = Base64.decodeBase64(file);

            File file = new File(TEMP_BIN_UNIX);
            FileOutputStream fout = null;
            try {
                fout = new FileOutputStream(file);
                fout.write(bytes);

            } catch (IOException e) {
                e.printStackTrace();
                reportError(e);
                return CodeBenderApplet.BIN_ERROR;
            } finally {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    reportError(e);
                }
            }
            flashCommand =
                    new AvrdudeLinuxCommand(basepath, port, TEMP_BIN_UNIX, maximumSize, protocol, baudRate, board);
        }

        try {
            System.out.println("running : " + flashCommand.toString());
            final Process flashProcess = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProcess.waitFor();
                System.out.println("flashed=" + flashProcess.exitValue());
                return flashProcess.exitValue();

            } catch (InterruptedException e) {
                e.printStackTrace();
                reportError(e, flashCommand.toString());
                return CodeBenderApplet.INTERUPTED_ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
            reportError(e);
            return CodeBenderApplet.PROCESS_ERROR;
        }

    }


    public static void main(final String[] args) {

        File dFile = new File("/tmp/avrdude");
        System.out.println(dFile.length());
        dFile = new File("/tmp/avrdude.conf");
        System.out.println(dFile.length());
    }

    private static void downloadBinaryToDisk(final String inputFile, final String destinationFile) throws IOException {
        final File dFile = new File(destinationFile);

        if (dFile.exists()) {
            return;
        }

        System.out.println("downloading to disk " + inputFile);
        final URL url = new URL(inputFile);
        url.openConnection();
        final InputStream input = url.openStream();
        final FileOutputStream dFileOS = new FileOutputStream(dFile);
        int data = input.read();
        while (data != -1) {
            dFileOS.write(data);
            data = input.read();
        }
        input.close();
        dFileOS.close();

    }

    private void makeExecutable(final String filename) {
        final File dudeFile = new File(filename);
        dudeFile.setExecutable(true);
    }

    private void populateChangemap() {
        changemap.put("wiring", "wiring");
        changemap.put("arduino", "arduino");
        changemap.put("ft2232d based generic programmer", "avrftdi");
        changemap.put("ft2232h based generic programmer", "2232HIO");
        changemap.put("amontec jtagkey, jtagkey-tiny and jtagkey2", "jtagkey");
        changemap.put("atmel avr isp", "avrisp");
        changemap.put("atmel avr isp v2", "avrispv2");
        changemap.put("atmel avr isp mkii", "avrispmkII");
        changemap.put("atmel avr isp mkii", "avrisp2");
        changemap.put("the bus pirate", "buspirate");
        changemap.put("atmel stk500", "stk500");
        changemap.put("atmel stk500 version 1.x firmware", "stk500v1");
        changemap.put("crossbow mib510 programming board", "mib510");
        changemap.put("atmel stk500 version 2.x firmware", "stk500v2");
        changemap.put("atmel stk500 v2 in parallel programming mode", "stk500pp");
        changemap.put("atmel stk500 v2 in high-voltage serial programming mode", "stk500hvsp");
        changemap.put("atmel stk600", "stk600");
        changemap.put("atmel stk600 in parallel programming mode", "stk600pp");
        changemap.put("atmel stk600 in high-voltage serial programming mode", "stk600hvsp");
        changemap.put("atmel low cost serial programmer", "avr910");
        changemap.put("usbasp, http://www.fischl.de/usbasp/", "usbasp");
        changemap.put("usbtiny simple usb programmer, http://www.ladyada.net/make/usbtinyisp/", "usbtiny");
        changemap.put("atmel butterfly development board", "butterfly");
        changemap.put("atmel appnote avr109 boot loader", "avr109");
        changemap.put("atmel appnote avr911 avrosp", "avr911");
        changemap.put("mikrokopter.de butterfly", "mkbutterfly");
        changemap.put("mikrokopter.de butterfly", "butterfly_mk");
        changemap.put("atmel jtag ice (mki)", "jtagmkI");
        changemap.put("atmel jtag ice (mki)", "jtag1");
        changemap.put("atmel jtag ice (mki)", "jtag1slow");
        changemap.put("atmel jtag ice mkii", "jtagmkII");
        changemap.put("atmel jtag ice mkii", "jtag2slow");
        changemap.put("atmel jtag ice mkii", "jtag2fast");
        changemap.put("atmel jtag ice mkii", "jtag2");
        changemap.put("atmel jtag ice mkii in isp mode", "jtag2isp");
        changemap.put("atmel jtag ice mkii in debugwire mode", "jtag2dw");
        changemap.put("atmel jtag ice mkii im avr32 mode", "jtagmkII_avr32");
        changemap.put("atmel jtag ice mkii im avr32 mode", "jtag2avr32");
        changemap.put("atmel jtag ice mkii pdi mode", "jtag2pdi");
        changemap.put("atmel avr dragon in jtag mode", "dragon_jtag");
        changemap.put("atmel avr dragon in isp mode", "dragon_isp");
        changemap.put("atmel avr dragon in pp mode", "dragon_pp");
        changemap.put("atmel avr dragon in hvsp mode", "dragon_hvsp");
        changemap.put("atmel avr dragon in debugwire mode", "dragon_dw");
        changemap.put("atmel avr dragon in pdi mode", "dragon_pdi");
        changemap.put("jason kyle's pavr serial programmer", "pavr");
        changemap.put("brian dean's programmer, http://www.bsdhome.com/avrdude/", "bsd");
        changemap.put("stk200", "stk200");
        changemap.put("pony prog stk200", "pony-stk200");
        changemap.put("dontronics dt006", "dt006");
        changemap.put("bascom sample programming cable", "bascom");
        changemap.put("nightshade alf-pgmavr, http://nightshade.homeip.net/", "alf");
        changemap.put("steve bolt's programmer", "sp12");
        changemap.put("picoweb programming cable, http://www.picoweb.net/", "picoweb");
        changemap.put("abcmini board, aka dick smith hotchip", "abcmini");
        changemap.put("futurlec.com programming cable.", "futurlec");
        changemap.put("xilinx jtag cable", "xil");
        changemap.put("direct avr parallel access cable", "dapa");
        changemap.put("at-isp v1.1 programming cable for avr-sdk1 from <http://micro-research.co.th/> micro-research.co.th", "atisp");
        changemap.put("ere isp-avr <http://www.ere.co.th/download/sch050713.pdf>", "ere-isp-avr");
        changemap.put("altera byteblaster", "blaster");
        changemap.put("frank stk200", "frank-stk200");
        changemap.put("atmel at89isp cable", "frank-stk200");
        changemap.put("design ponyprog serial, reset", "ponyser");
        changemap.put("lancos si-prog <http://www.lancos.com/siprogsch.html>", "siprog");
        changemap.put("serial port banging, reset", "dasa");
        changemap.put("serial port banging, reset", "dasa3");
        changemap.put("serial port banging, reset", "c2n232i");
        changemap.put("attiny11", "t11");
        changemap.put("attiny12", "t12");
        changemap.put("attiny13", "t13");
        changemap.put("attiny15", "t15");
        changemap.put("at90s1200", "1200");
        changemap.put("at90s4414", "4414");
        changemap.put("at90s2313", "2313");
        changemap.put("at90s2333", "2333");
        changemap.put("at90s2343", "2343");
        changemap.put("at90s4433", "4433");
        changemap.put("at90s4434", "4434");
        changemap.put("at90s8515", "8515");
        changemap.put("at90s8535", "8535");
        changemap.put("atmega103", "m103");
        changemap.put("atmega64", "m64");
        changemap.put("atmega128", "m128");
        changemap.put("at90can128", "c128");
        changemap.put("at90can64", "c64");
        changemap.put("at90can32", "c32");
        changemap.put("atmega16", "m16");
        changemap.put("atmega164p", "m164p");
        changemap.put("atmega324p", "m324p");
        changemap.put("atmega324pa", "m324pa");
        changemap.put("atmega644", "m644");
        changemap.put("atmega644p", "m644p");
        changemap.put("atmega1284p", "m1284p");
        changemap.put("atmega162", "m162");
        changemap.put("atmega163", "m163");
        changemap.put("atmega169", "m169");
        changemap.put("atmega329", "m329");
        changemap.put("atmega329p", "m329p");
        changemap.put("atmega3290", "m3290");
        changemap.put("atmega3290p", "m3290p");
        changemap.put("atmega649", "m649");
        changemap.put("atmega6490", "m6490");
        changemap.put("atmega32", "m32");
        changemap.put("atmega161", "m161");
        changemap.put("atmega8", "m8");
        changemap.put("atmega8515", "m8515");
        changemap.put("atmega8535", "m8535");
        changemap.put("attiny26", "t26");
        changemap.put("attiny261", "t261");
        changemap.put("attiny461", "t461");
        changemap.put("attiny861", "t861");
        changemap.put("atmega48", "m48");
        changemap.put("atmega88", "m88");
        changemap.put("atmega88p", "m88p");
        changemap.put("atmega168", "m168");
        changemap.put("atmega168p", "m168p");
        changemap.put("attiny88", "t88");
        changemap.put("atmega328p", "m328p");
        changemap.put("attiny2313", "t2313");
        changemap.put("attiny4313", "t4313");
        changemap.put("at90pwm2", "pwm2");
        changemap.put("at90pwm3", "pwm3");
        changemap.put("at90pwm2b", "pwm2b");
        changemap.put("at90pwm3b", "pwm3b");
        changemap.put("attiny25", "t25");
        changemap.put("attiny45", "t45");
        changemap.put("attiny85", "t85");
        changemap.put("atmega640", "m640");
        changemap.put("atmega1280", "m1280");
        changemap.put("atmega1281", "m1281");
        changemap.put("atmega2560", "m2560");
        changemap.put("atmega2561", "m2561");
        changemap.put("atmega128rfa1", "m128rfa1");
        changemap.put("attiny24", "t24");
        changemap.put("attiny44", "t44");
        changemap.put("attiny84", "t84");
        changemap.put("atmega32u4", "m32u4");
        changemap.put("at90usb646", "usb646");
        changemap.put("at90usb647", "usb647");
        changemap.put("at90usb1286", "usb1286");
        changemap.put("at90usb1287", "usb1287");
        changemap.put("at90usb162", "usb162");
        changemap.put("at90usb82", "usb82");
        changemap.put("atmega32u2", "m32u2");
        changemap.put("atmega16u2", "m16u2");
        changemap.put("atmega8u2", "m8u2");
        changemap.put("atmega325", "m325");
        changemap.put("atmega645", "m645");
        changemap.put("atmega3250", "m3250");
        changemap.put("atmega6450", "m6450");
        changemap.put("atxmega64a1", "x64a1");
        changemap.put("atxmega128a1", "x128a1");
        changemap.put("atxmega128a1revd", "x128a1d");
        changemap.put("atxmega192a1", "x192a1");
        changemap.put("atxmega256a1", "x256a1");
        changemap.put("atxmega64a3", "x64a3");
        changemap.put("atxmega128a3", "x128a3");
        changemap.put("atxmega192a3", "x192a3");
        changemap.put("atxmega256a3", "x256a3");
        changemap.put("atxmega256a3b", "x256a3b");
        changemap.put("atxmega16a4", "x16a4");
        changemap.put("atxmega32a4", "x32a4");
        changemap.put("atxmega64a4", "x64a4");
        changemap.put("atxmega128a4", "x128a4");
        changemap.put("32uc3a0512", "ucr2");
        changemap.put("attiny4", "t4");
        changemap.put("attiny5", "t5");
        changemap.put("attiny9", "t9");
        changemap.put("attiny10", "t10");
    }
}
