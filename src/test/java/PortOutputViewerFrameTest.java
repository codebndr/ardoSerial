import eu.amaxilatis.codebender.graphics.PortOutputViewerFrame;

/**
 * Created by IntelliJ IDEA.
 * User: amaxilatis
 * Date: 3/3/12
 * Time: 3:17 PM
 */
public class PortOutputViewerFrameTest {
    public static void main(final String[] inp) {
        final PortOutputViewerFrame frame = new PortOutputViewerFrame();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //do nothing
            }
            frame.appendText("test string at " + System.currentTimeMillis());
        }
    }
}
