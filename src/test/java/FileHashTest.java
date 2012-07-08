//import com.google.common.io.Files;
//import org.apache.log4j.BasicConfigurator;
//
//import java.io.File;
//import java.io.IOException;
//
///**
// * Created by IntelliJ IDEA.
// * User: amaxilatis
// * Date: 3/3/12
// * Time: 3:17 PM
// */
//public class FileHashTest {
//    public static void main(final String[] inp) {
//        BasicConfigurator.configure();
//
//        File myfile = new File("/home/amaxilatis/repositories/ardoSerial/target/classes/bins/avrdude.linux");
//        long checksum = 0;
//        try {
//            checksum = Files.getChecksum(myfile, new java.util.zip.CRC32());
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//
//        System.out.println(checksum);
//        myfile = new File(Thread.currentThread().getContextClassLoader().getResource("bins/avrdude.linux").getFile());
//        checksum = 0;
//        try {
//            checksum = Files.getChecksum(myfile, new java.util.zip.CRC32());
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//        System.out.println(checksum);
//
//    }
//}
