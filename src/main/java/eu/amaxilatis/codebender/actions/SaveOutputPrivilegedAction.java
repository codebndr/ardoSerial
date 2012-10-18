package eu.amaxilatis.codebender.actions;

import javax.swing.JFileChooser;
import java.io.*;
import java.security.PrivilegedAction;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/24/12
 * Time: 2:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SaveOutputPrivilegedAction implements PrivilegedAction {

    private final transient String text;

    public SaveOutputPrivilegedAction(final String text) {
        this.text = text;
    }

    public final Object run() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(fileChooser);
        final File file = fileChooser.getSelectedFile();
        try {

            final FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
