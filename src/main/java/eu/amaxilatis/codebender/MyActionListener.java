package eu.amaxilatis.codebender;

import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyActionListener implements ActionListener {

    private PortOutputViewerFrame portOutputViewerFrame;

    public MyActionListener(PortOutputViewerFrame portOutputViewerFrame) {
        this.portOutputViewerFrame = portOutputViewerFrame;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        ConnectionManager.getInstance().disconnect();
        portOutputViewerFrame.dispose();

    }
}