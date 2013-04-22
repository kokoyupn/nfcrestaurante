package tpv;

import java.awt.*;
import java.awt.print.*;

class ObjetoDeImpresion implements Printable{
	
	private String texto;
	private int x, y;
	
	public ObjetoDeImpresion(String texto, int x, int y){
		this.texto = texto; 
		this.x = x;
		this.y = y;
	}
	
	public int print(Graphics g, PageFormat f, int pageIndex){
		if(pageIndex == 0){
			String textoAux = texto;
			int yAux = y;
			int conPlatos = 0;
			
			String[] platos = textoAux.split("Nombre: ");
			while(conPlatos < platos.length){
				int conPalabras = 0;
				int contCaracteres = 0;
				boolean cabeTodoEnUna = false;
				textoAux = "";
				//Tokenizamos por espacios para ver si cabe la palabra
				String[] palabras = platos[conPlatos].split(" ");
				while (conPalabras < palabras.length){
					if((palabras[conPalabras].length() + contCaracteres) < 60){// cabe
						textoAux += palabras[conPalabras] + " ";			
						contCaracteres += palabras[conPalabras].length() + 1;
						if(conPalabras == palabras.length -1){//Ha cabido todo en una linea
							cabeTodoEnUna = true;
						}
					}else{//no cabe. Salto a la siguiente línea
						g.drawString(textoAux, x, yAux);
						yAux += 20;
						textoAux = palabras[conPalabras] + " ";
						contCaracteres = textoAux.length();
						//si es la ultima palabra tambien tiene que imprimirla
						if(conPalabras == palabras.length - 1){
							g.drawString(textoAux, x, yAux);
							yAux += 20;
						}
					}
					conPalabras ++;
				}	
				if(cabeTodoEnUna){
					g.drawString(textoAux, x, yAux);
					yAux += 20;
				}
				conPlatos ++;
			}
			return PAGE_EXISTS;
		}else{
			return NO_SUCH_PAGE;
		}
	}
}