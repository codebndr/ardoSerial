package eu.amaxilatis.ardoserial.actions;

import eu.amaxilatis.ardoserial.graphics.PortOutputViewerFrame;

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
    private final PortOutputViewerFrame frame;

    public SaveOutputPrivilegedAction(String text, PortOutputViewerFrame frame) {
        this.text = text;
        this.frame = frame;
    }

    public Object run() {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(frame);
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
