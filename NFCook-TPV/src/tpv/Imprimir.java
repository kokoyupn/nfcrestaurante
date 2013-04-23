package tpv;

import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class Imprimir{
	static public void imprime(String texto, Image img){
//		PrinterJob job = PrinterJob.getPrinterJob();
//		
//		job.setPrintable(new ObjetoDeImpresion(texto,100,200,img));
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pf = job.defaultPage();
		Paper paper = new Paper();
		paper.setSize(612.0,832.0);
		double margin = 30;
		paper.setImageableArea(margin, margin, paper.getWidth() - 2*margin, paper.getHeight() - 2*margin);
		pf.setPaper(paper);
		pf.setOrientation(PageFormat.PORTRAIT);
		
		job.setPrintable(new ObjetoDeImpresion( texto,100,100, img, paper.getHeight() - margin ), pf);
		if(job.printDialog()){

			try{
				if(img == null){//es la comanda
					job.setJobName("Comanda");
				}else {//es el tiket
					job.setJobName("Cuenta");
				}
				job.print();
				
			}catch(PrinterException e){
				System.out.println(e);
			}
		
//		PrinterJob job = PrinterJob.getPrinterJob();
//		PageFormat pf = job.defaultPage();
//		Paper paper = new Paper();
//		paper.setSize(612.0,832.0);
//		double margin = 10;
//		paper.setImageableArea(margin, margin, paper.getWidth() - margin, paper.getHeight() - margin);
//		pf.setPaper(paper);
//		pf.setOrientation(PageFormat.LANDSCAPE);
//		
//		job.setPrintable(new ObjetoDeImpresion( texto,100,200, img), pf);
//		
//		try{
//		job.print();
//		}catch(PrinterException e){
//		System.out.println(e);
//		}
//		}
		
		
		
		
		}
	}
}