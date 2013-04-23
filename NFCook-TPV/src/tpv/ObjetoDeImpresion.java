package tpv;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.lang.Character.Subset;

class ObjetoDeImpresion implements Printable{
	
	private String texto;
	private int x, y;
	private Image img;
	private double limite;
	private String resto;
	private int pos, pos2;
	
	public ObjetoDeImpresion(String texto, int x, int y, Image img, double limite){
		this.texto = texto; 
		this.x = x;
		this.y = y;
		this.img = img;
		this.limite = limite;
	}
	
	public int print(Graphics g, PageFormat f, int pageIndex){
		boolean sal = false;
	
		switch (pageIndex)
	      {
	         case 0 : 	pos = 0;
	        	 		resto = pasaAstring(texto.split("Nombre: "));
	        	 		String textoAux = texto;
						int yAux = y;
						int conPlatos = 0;
						
						String[] platos = textoAux.split("Nombre: ");
						while(conPlatos < platos.length && !sal && yAux < (int)limite){
							int conPalabras = 0;
							int contCaracteres = 0;
							boolean cabeTodoEnUna = false;
							textoAux = "";
							//Tokenizamos por espacios para ver si cabe la palabra
							String[] palabras = platos[conPlatos].split(" ");
							while (conPalabras < palabras.length && !sal){
								if((palabras[conPalabras].length() + contCaracteres) < 60){// cabe
									textoAux += palabras[conPalabras] + " ";			
									contCaracteres += palabras[conPalabras].length() + 1;
									if(conPalabras == palabras.length -1){//Ha cabido todo en una linea
										cabeTodoEnUna = true;
									}
									if(yAux >= (int)limite){
										sal = true;
									}
								}else{//no cabe. Salto a la siguiente línea
									g.drawString(textoAux, x, yAux);
									if(textoAux.length() > 0){
										pos += textoAux.length()-1;
										resto = resto.substring(textoAux.length()-1);
									}
									yAux += 20;
									textoAux = palabras[conPalabras] + " ";
									contCaracteres = textoAux.length();
									//si es la ultima palabra tambien tiene que imprimirla
									if(conPalabras == palabras.length - 1){
										g.drawString(textoAux, x, yAux);
										if(textoAux.length() > 0){
											pos += textoAux.length()-1;
											resto = resto.substring(textoAux.length()-1);
										}
										yAux += 20;
									}
									
									if(yAux >= (int)limite){
										sal = true;
									}
								}
								conPalabras ++;
							}	
							if(cabeTodoEnUna){
								g.drawString(textoAux, x, yAux);
								if(textoAux.length() > 0){
									pos += textoAux.length()-1;
									resto = resto.substring(textoAux.length()-1);
								}
								yAux += 20;
							}
							pos += 8;
							conPlatos ++;
						}
						if (img != null){
							if (yAux + 200 < limite){ //cabe la imagen
								g.drawImage(img,x + 20,yAux,null);
								
							}else{
								sal = true;
							}
							
						}
						return PAGE_EXISTS;
	                  
	         case 1 :
	        	 	pos2 = 0;
	        	 	if(pos < texto.length()){
	        	 		pos2 = pos;
	        	 		resto = pasaAstring(texto.substring(pos).split("Nombre: "));
	        	 	textoAux = texto.substring(pos);
					yAux = y;
					conPlatos = 0;
					
					platos = textoAux.split("Nombre: ");
					while(conPlatos < platos.length && !sal){
						int conPalabras = 0;
						int contCaracteres = 0;
						boolean cabeTodoEnUna = false;
						textoAux = "";
						//Tokenizamos por espacios para ver si cabe la palabra
						String[] palabras = platos[conPlatos].split(" ");
						while (conPalabras < palabras.length && !sal){
							if((palabras[conPalabras].length() + contCaracteres) < 60){// cabe
								textoAux += palabras[conPalabras] + " ";			
								contCaracteres += palabras[conPalabras].length() + 1;
								if(conPalabras == palabras.length -1){//Ha cabido todo en una linea
									cabeTodoEnUna = true;
								}
							}else{//no cabe. Salto a la siguiente línea
								g.drawString(textoAux, x, yAux);
								if(textoAux.length() > 0){
									pos2 += textoAux.length()-1;
									resto = resto.substring(textoAux.length()-1);
								}
								yAux += 20;
								textoAux = palabras[conPalabras] + " ";
								contCaracteres = textoAux.length();
								//si es la ultima palabra tambien tiene que imprimirla
								if(conPalabras == palabras.length - 1){
									g.drawString(textoAux, x, yAux);
									if(textoAux.length() > 0){
										pos2 += textoAux.length()-1;
										resto = resto.substring(textoAux.length()-1);
									}
									yAux += 20;
								}
								
								if(yAux >= (int)limite){
									sal = true;
								}
								
							}
							conPalabras ++;
						}	
						if(cabeTodoEnUna){
							g.drawString(textoAux, x, yAux);
							if(textoAux.length() > 0){
								pos2 += textoAux.length()-1;
								resto = resto.substring(textoAux.length()-1);
							}
							yAux += 20;
						}
						pos2 += 8;
						conPlatos ++;
					}
					if (img != null){
						if (yAux + 200 < limite){ //cabe la imagen
							g.drawImage(img,x + 20,yAux,null);
							
						}else{
							sal = true;
						}
						
					}
					return PAGE_EXISTS;
	         			}else  return NO_SUCH_PAGE;  
	        
	         case 3: if(pos2 < texto.length()){
	        	 g.drawString("Hola", x, 200);
	        	 return PAGE_EXISTS;
	         }else{
	        	 return NO_SUCH_PAGE; 
	         }
	         
	         default: return NO_SUCH_PAGE;        // No other pages
		
	}
}

	private String pasaAstring(String[] s) {
		String result = "";
		for (int i = 0; i < s.length ;i++){
			result += s[i];
		}
		return result;
	}
}