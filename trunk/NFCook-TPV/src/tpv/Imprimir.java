package tpv;

import java.io.*;
import java.awt.HeadlessException;
import java.awt.print.*;
import java.awt.print.PrinterJob.*;
import java.awt.print.PageFormat.*;

public class Imprimir{
	static public void imprime(String texto){
		PrinterJob job = PrinterJob.getPrinterJob();
		
			job.setPrintable(new ObjetoDeImpresion(texto,100,200));
			if(job.printDialog()){
				try{
					
					job.print();
					
				}catch(PrinterException e){
					System.out.println(e);
				}
		}
	}
}