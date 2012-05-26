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
    private final String text;

    public SaveOutputPrivilegedAction(String text) {
        this.text = text;
    }

    public Object run() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(fileChooser);
        final File file = fileChooser.getSelectedFile();
        try {

            FileWriter fr = new FileWriter(file);
            fr.write(text);
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return 0;
    }
}
