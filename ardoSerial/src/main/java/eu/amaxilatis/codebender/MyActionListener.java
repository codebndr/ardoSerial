package eu.amaxilatis.codebender;

import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyActionListener implements ActionListener {

    private final transient PortOutputViewerFrame viewerFrame;

    public MyActionListener(final PortOutputViewerFrame viewerFrame) {
        this.viewerFrame = viewerFrame;
    }


    @Override
    public void actionPerformed(final ActionEvent actionEvent) {

        ConnectionManager.getInstance().disconnect();
        viewerFrame.dispose();

    }
}