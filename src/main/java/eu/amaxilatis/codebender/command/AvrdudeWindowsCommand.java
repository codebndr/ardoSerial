package eu.amaxilatis.codebender.command;

/**
 * Class that automatically generates a new command for AVRDUDE on Windows.
 */
public class AvrdudeWindowsCommand {
    /**
     * command string builder.
     */
    private final transient StringBuilder flashCommand;

    /**
     * Creates the new Command to execute.
     *
     * @param bpath    the base path of avrdude.
     * @param port     the port of the arduino.
     * @param tempFile the hex file to flash.
     * @param baudRate the baudrate to use.
     */
    public AvrdudeWindowsCommand(final String bpath, final String port, final String tempFile, final String baudRate) {

        flashCommand = (new StringBuilder()).append("\"").append(bpath + "\\avrdude.exe\" ")
                .append(" -C \"" + bpath + "\\avrdude.conf\" ")
                .append(" -b ").append(baudRate)
                .append(" -P \\\\.\\").append(port)
                .append(" -c arduino ")
                .append(" -p m328p ")
                .append(" -U flash:w:\"").append(tempFile + ":i -F");

    }

    public AvrdudeWindowsCommand(final String bpath, final String port, final String tempFile, String maximumSize, String protocol, String baudRate, String board) {
        flashCommand = (new StringBuilder()).append("\"").append(bpath + "\\avrdude.exe\" ")
                .append(" -C \"" + bpath + "\\avrdude.conf\" ")
                .append(" -P \\\\.\\").append(port)
                .append(" -b ").append(baudRate)
                .append(" -c ").append(protocol)
//                .append(" -c ").append(maximumSize)//need to fix this
                .append(" -p ").append(board)
                .append(" -U flash:w:\"").append(tempFile + ":i -F");

    }

    @Override
    public final String toString() {
        return flashCommand.toString();
    }
}
