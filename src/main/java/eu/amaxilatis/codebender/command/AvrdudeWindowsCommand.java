package eu.amaxilatis.codebender.command;

/**
 * Class that automatically generates a new command for AVRDUDE on Windows
 */
public class AvrdudeWindowsCommand {
    private StringBuilder flashCommand;

    /**
     * Creates the new Command to execute.
     *
     * @param basepath the base path of avrdude.
     * @param port     the port of the arduino.
     * @param tempFile the hex file to flash.
     * @param baudRate the baudrate to use.
     */
    public AvrdudeWindowsCommand(String basepath, String port, String tempFile, String baudRate) {

        flashCommand = (new StringBuilder()).append(basepath + "\\avrdude.exe ")
                .append(" -C " + basepath + "\\avrdude.conf ")
                .append(" -b ").append(baudRate)
                .append(" -P \\\\.\\").append(port)
                .append(" -c arduino ")
                .append(" -p m328p ")
                .append(" -U flash:w:\"").append(tempFile + ":i -F");

    }

    @Override
    public String toString() {
        return flashCommand.toString();
    }
}
