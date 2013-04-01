package interfaz;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import tpv.Mesa;
import tpv.Restaurante;

public class VentanaMesas extends JFrame{
	
	public VentanaMesas(){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Restaurante unRestaurante = new Restaurante();
		
		setLayout(new BorderLayout());
		
		JPanel panelMesas = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JPanel panelMesasCamarero = new JPanel(new FlowLayout(FlowLayout.CENTER));
		
		JSplitPane panelVert = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
	    add( panelVert,BorderLayout.CENTER );
	    // Se incorporan las dos zonas que se habían creado a las dos
	    // partes en que se ha dividido el panel principal
	    panelVert.setLeftComponent( panelMesas );
	    panelVert.setRightComponent( panelMesasCamarero );
		
		Iterator<Mesa> iteradorMesas = unRestaurante.getIteratorMesas();
		
		while(iteradorMesas.hasNext()){
			Mesa unaMesa = iteradorMesas.next();
			JButton botonMesa = new JButton(unaMesa.getIdMesa());
			panelMesas.add(botonMesa);
		}
		
		Iterator<Mesa> iteradorMesasCamarero = unRestaurante.getIteratorMesas();
		while(iteradorMesasCamarero.hasNext()){
			Mesa unaMesa = iteradorMesasCamarero.next();
			JButton botonMesa = new JButton(unaMesa.getIdMesa());
			panelMesasCamarero.add(botonMesa);
		}

	}
	
	
	public static void main(String args[]){
		VentanaMesas ventanaMesas = new VentanaMesas();
		ventanaMesas.setExtendedState(JFrame.MAXIMIZED_BOTH);
		ventanaMesas.setVisible(true);
	}

}
