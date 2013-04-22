package tpv;

import java.awt.Image;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class Imprimir{
	static public void imprime(String texto, Image img){
		PrinterJob job = PrinterJob.getPrinterJob();
		
		job.setPrintable(new ObjetoDeImpresion(texto,100,200,img));
		if(job.printDialog()){

			try{
				
				job.print();
				
			}catch(PrinterException e){
				System.out.println(e);
			}
		}
	}
}