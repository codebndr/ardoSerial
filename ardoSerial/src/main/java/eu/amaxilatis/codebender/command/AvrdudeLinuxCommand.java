package eu.amaxilatis.codebender.command;

/**
 * Class that automatically generates a new command for AVRDUDE on Linux and MacOS.
 */
public class AvrdudeLinuxCommand {
    /**
     * command string builder.
     */
    private final transient StringBuilder flashCommand = new StringBuilder();

    /**
     * Creates the new Command to execute.
     *
     * @param basepath the base path of avrdude.
     * @param port     the port of the arduino.
     * @param tempFile the hex file to flash.
     * @param baudRate the baudrate to use.
     */
    public AvrdudeLinuxCommand(final String basepath, final String port, final String tempFile, final String baudRate) {
        flashCommand.append("").append(basepath).append("avrdude ")
                .append(" -C \"").append(basepath).append("avrdude.conf\" ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:\"").append(tempFile).append("\"")
                .append(" -b ").append(baudRate)
                .append(" -F");
    }

    @Override
    public final String toString() {
        return flashCommand.toString();
    }
}
