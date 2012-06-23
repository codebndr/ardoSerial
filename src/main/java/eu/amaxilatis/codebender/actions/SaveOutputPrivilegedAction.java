package eu.amaxilatis.codebender.actions;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivilegedAction;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/24/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveOutputPrivilegedAction implements PrivilegedAction {
    /**
     * Logger.
     */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SaveOutputPrivilegedAction.class);

    private final transient String text;

    public SaveOutputPrivilegedAction(final String text) {
        this.text = text;
    }

    public Object run() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(fileChooser);
        final File file = fileChooser.getSelectedFile();
        try {

            final FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.close();

        } catch (IOException e) {
            LOGGER.error(e, e);
        }

        return 0;
    }
}
