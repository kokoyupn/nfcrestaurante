package tpv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
 
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

public class QRCodeJava {
    public static void generaQR(String texto) {
    	 ByteArrayOutputStream out = QRCode.from(texto)
                 .to(ImageType.PNG).withSize(300,300).stream();
                
        try {
            FileOutputStream fout = new FileOutputStream(new File(
            		"ArchivoQR/QR_Code.PNG"));
//            		"C:\\Archivos de Guillermo\\Uni\\IS\\QR_Code.PNG"));
 
            fout.write(out.toByteArray());
 
            fout.flush();
            fout.close();
 
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
}
