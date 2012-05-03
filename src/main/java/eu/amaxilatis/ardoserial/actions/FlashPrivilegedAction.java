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
        int retval = checkAvrdudeWindows();
        String avrdudePath;
        if (retval > 0) {
            avrdudePath = "C:\\Temp\\ ";
        } else {
            return null;
        }
        if (!checkAvrdudeConfWindows()) {
            return null;
        }
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("C:\\Temp\\file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        StringBuilder flashCommand = new StringBuilder();

        flashCommand.append(avrdudePath)
                .append(" -C C:\\Temp\\avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append("C:\\Temp\\file.hex")
                .append(" -b ").append(baudRate)
                .append(" -F");


        try {
            LOGGER.info("running : " + flashCommand.toString());
            Process flashProc = Runtime.getRuntime().exec(flashCommand.toString());
            try {
                flashProc.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object flashMacOSX() {
        int retval = checkAvrdudeMac();
        String avrdudePath;
        if (retval > 0) {
            avrdudePath = "/tmp/avrdude ";
        } else {
            return null;
        }
        if (!checkAvrdudeConfMac()) {
            return null;
        }
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            System.out.println("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object flashLinux() {
        int retval = checkAvrdudeLinux();
        String avrdudePath;
        if (retval > 0) {
            avrdudePath = "/tmp/avrdude ";
        } else {
            return null;
        }
        if (!checkAvrdudeConfLinux()) {
            return null;
        }
        try {
            FileWriter fileWriter = null;
            fileWriter = new FileWriter("/tmp/file.hex");
            fileWriter.write(file);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e);
            return null;
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
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            LOGGER.info("flashed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    public boolean checkAvrdudeConfLinux() {
        File avrdudeConf = new File("/tmp/avrdude.conf");
        if (!avrdudeConf.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            InputStream input = this.getClass().getResourceAsStream("/avrdude.conf.linux");
            InputStreamReader reader = new InputStreamReader(input);
            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(avrdudeConf)));
            } catch (FileNotFoundException e) {
                LOGGER.error(e);
                return false;
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
                LOGGER.error(e);
                return false;
            }

        }
        return true;
    }

    public boolean checkAvrdudeConfWindows() {
        File avrdudeConf = new File("C:\\Temp\\avrdude.conf");
        if (!avrdudeConf.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            InputStream input = this.getClass().getResourceAsStream("/avrdude.conf.linux");
            InputStreamReader reader = new InputStreamReader(input);
            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(avrdudeConf)));
            } catch (FileNotFoundException e) {
                LOGGER.error(e);
                return false;
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
                LOGGER.error(e);
                return false;
            }

        }
        return true;
    }

    public boolean checkAvrdudeConfMac() {
        File avrdudeConf = new File("/tmp/avrdude.conf");
        if (!avrdudeConf.exists()) {
            LOGGER.info("avrdude.conf does not exist");
            InputStream input = this.getClass().getResourceAsStream("/avrdude.conf.mac");
            InputStreamReader reader = new InputStreamReader(input);
            BufferedWriter writer = null;

            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(avrdudeConf)));
            } catch (FileNotFoundException e) {
                LOGGER.error(e);
                return false;
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
                LOGGER.error(e);
                return false;
            }

        }
        return true;
    }

    public int checkAvrdudeLinux() {
        try {
            Process checkProcess = Runtime.getRuntime().exec("ls /tmp/avrdude");
            try {
                checkProcess.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            InputStreamReader stream = new InputStreamReader(checkProcess.getInputStream());
            final BufferedReader reader = new BufferedReader(stream);
            String line = reader.readLine();
            if (line == null) {
                InputStream input = this.getClass().getResourceAsStream("/bins/avrdude.linux");
                FileOutputStream output;
                try {
                    output = new FileOutputStream(new File("/tmp/avrdude"));
                } catch (FileNotFoundException e) {
                    LOGGER.error(e);
                    return -1;
                }
                int c;

                while ((c = input.read()) != -1) {
                    output.write(c);
                }
                input.close();
                output.close();
                Process chmodProcess = Runtime.getRuntime().exec("chmod u+x /tmp/avrdude");

            } else {
                return 2;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public int checkAvrdudeWindows() {
        try {
            Process checkProcess = Runtime.getRuntime().exec("dir C:\\Temp\\avrdude.exe");
            try {
                checkProcess.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            InputStreamReader stream = new InputStreamReader(checkProcess.getInputStream());
            final BufferedReader reader = new BufferedReader(stream);
            String line = reader.readLine();
            if (line == null) {
                InputStream input = this.getClass().getResourceAsStream("/bins/avrdude.exe");
                FileOutputStream output;
                try {
                    output = new FileOutputStream(new File("C:\\Temp\\avrdude.exe"));
                } catch (FileNotFoundException e) {
                    LOGGER.error(e);
                    return -1;
                }
                int c;

                while ((c = input.read()) != -1) {
                    output.write(c);
                }
                input.close();
                output.close();
                Process chmodProcess = Runtime.getRuntime().exec("CACLS /p /e %USERNAME%:f ");

            } else {
                return 2;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public int checkAvrdudeMac() {
        try {
            Process checkProcess = Runtime.getRuntime().exec("ls /tmp/avrdude");
            try {
                checkProcess.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error(e);
            }
            InputStreamReader stream = new InputStreamReader(checkProcess.getInputStream());
            final BufferedReader reader = new BufferedReader(stream);
            String line = reader.readLine();
            if (line == null) {
                InputStream input = this.getClass().getResourceAsStream("/bins/avrdude.mac");
                FileOutputStream output;
                try {
                    output = new FileOutputStream(new File("/tmp/avrdude"));
                } catch (FileNotFoundException e) {
                    LOGGER.error(e);
                    return -1;
                }
                int c;

                while ((c = input.read()) != -1) {
                    output.write(c);
                }
                input.close();
                output.close();
                Process chmodProcess = Runtime.getRuntime().exec("chmod u+x /tmp/avrdude");

            } else {
                return 2;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }
}
