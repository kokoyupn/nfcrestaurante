package interfaz;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlatoCelda extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private JLabel lblNewLabel ;
	private JLabel lblNewLabelLinea2 ;
	
	public PlatoCelda(JButton boton, String nombrePlato) {	
				
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		lblNewLabel = new JLabel();
		lblNewLabelLinea2 = new JLabel();
		lblNewLabel.setFont(new Font(lblNewLabel.getFont().getFontName(), lblNewLabel.getFont().getStyle(), 15));
		lblNewLabelLinea2.setFont(new Font(lblNewLabelLinea2.getFont().getFontName(), lblNewLabelLinea2.getFont().getStyle(), 15));
	
		GridBagConstraints grid = new GridBagConstraints();
		grid.gridx = 0;
		grid.gridy = 0;
		panel.add(boton,grid);
		
		//Como max dos lineas
		String linea1 = "", linea2 = "";
		if (nombrePlato.length() > 16){
			String[] palabras = nombrePlato.split(" ");
			int cont = 0;
			int i = 0;
			boolean llena = false;
			while (i < palabras.length && !llena){
				if((palabras[i].length() + cont) < 16){// cabe
					linea1 += palabras[i] + " ";
					cont += palabras[i].length() + 1;
					i ++;
				}else{//no cabe
					llena = true;
				}
			}
			if (nombrePlato.length() > 32)
				linea2 = nombrePlato.substring(cont, cont + 17);
			else{
				linea2 = nombrePlato.substring(cont, nombrePlato.length());
			}			
			lblNewLabel.setText(linea1) ;
			lblNewLabelLinea2.setText(linea2);
			grid.gridx = 0;
			grid.gridy = 1;
			panel.add(lblNewLabel,grid);
			grid.gridx = 0;
			grid.gridy = 2;
			panel.add(lblNewLabelLinea2,grid);
			
		}
		else{
			lblNewLabel.setText(nombrePlato);
			lblNewLabelLinea2.setText("-----------");
			lblNewLabelLinea2.setForeground(new Color(-1118482));
			grid.gridx = 0;
			grid.gridy = 1;
			panel.add(lblNewLabel,grid);
			grid.gridx = 0;
			grid.gridy = 2;
			panel.add(lblNewLabelLinea2,grid);
		}
		add(panel);		
	}
}
