package eu.amaxilatis.codebender.command;

/**
 * Class that automatically generates a new command for AVRDUDE on Linux and MacOS
 */
public class AvrdudeLinuxCommand {

    private StringBuilder flashCommand = new StringBuilder();

    /**
     * Creates the new Command to execute.
     *
     * @param basepath the base path of avrdude.
     * @param port     the port of the arduino.
     * @param tempFile the hex file to flash.
     * @param baudRate the baudrate to use.
     */
    public AvrdudeLinuxCommand(String basepath, String port, String tempFile, String baudRate) {
        flashCommand.append(basepath).append("avrdude ")
                .append(" -C ").append(basepath).append("avrdude.conf ")
                .append(" -P ").append(port)
                .append(" -c stk500v1 ")
                .append(" -p m328p ")
                .append(" -u -U flash:w:").append(tempFile)
                .append(" -b ").append(baudRate)
                .append(" -F");
    }

    @Override
    public String toString() {
        return flashCommand.toString();
    }
}
