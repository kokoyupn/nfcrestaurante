package interfaz;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import tpv.FechaYHora;

import basesDeDatos.Operaciones;

public class VentanaFichar extends JFrame implements ActionListener{
	
	private String idCamarero;
	private JButton botonEntrar;
	private JButton botonParada;
	private JButton botonSalir;
	private JButton botonCancelar;
	private JFrame ventanaLogin;
	
	VentanaFichar(String idCamarero, JFrame ventanaLogin){
		
		this.idCamarero = idCamarero;
		this.setUndecorated(true);
		this.ventanaLogin = ventanaLogin;
		
		JPanel panelFichar = new JPanel(new GridBagLayout());
		
		botonEntrar = new JButton(new ImageIcon("Imagenes/Botones/startLogin.png"));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		panelFichar.add(botonEntrar, constraints);
		botonEntrar.addActionListener(this);
		
		botonParada = new JButton(new ImageIcon("Imagenes/Botones/stopLogin.png"));
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		panelFichar.add(botonParada, constraints);
		botonParada.addActionListener(this);
		
		botonSalir = new JButton(new ImageIcon("Imagenes/Botones/exitLogin.png"));
		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 2;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		panelFichar.add(botonSalir, constraints);
		botonSalir.addActionListener(this);
		
		botonCancelar = new JButton("Cancelar");
		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		constraints.gridheight = 2;
		constraints.fill = GridBagConstraints.BOTH;
		panelFichar.add(botonCancelar, constraints);
		botonCancelar.addActionListener(this);
		
		add(panelFichar);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(botonEntrar == arg0.getSource()){
			if(!camareroYaFichadoEntrar()){
				Operaciones operacionSQlite = new Operaciones("FichaCamareros.db");
				operacionSQlite.ficharEntrar(idCamarero, new FechaYHora());
				operacionSQlite.cerrarBaseDeDatos();
				dispose();
				((VentanaLogin)ventanaLogin).activarVentanaLogin();
			}
		}else if(botonParada == arg0.getSource()){
			if(camareroYaFichadoEntrar() && !camareroYaFichadoParada()){
				Operaciones operacionSQlite = new Operaciones("FichaCamareros.db");
				operacionSQlite.ficharParada(idCamarero, new FechaYHora());
				operacionSQlite.cerrarBaseDeDatos();
				dispose();
				((VentanaLogin)ventanaLogin).activarVentanaLogin();
			}
		}else if(botonSalir == arg0.getSource()){
			if(camareroYaFichadoEntrar() && !camareroYaFichadoSalir()){
				Operaciones operacionSQlite = new Operaciones("FichaCamareros.db");
				operacionSQlite.ficharSalir(idCamarero, new FechaYHora());
				operacionSQlite.cerrarBaseDeDatos();
				dispose();
				((VentanaLogin)ventanaLogin).activarVentanaLogin();
			}
		}else if(botonCancelar == arg0.getSource()){
			dispose();
			((VentanaLogin)ventanaLogin).activarVentanaLogin();
		}
	}
	
	private boolean camareroYaFichadoEntrar(){
		Operaciones operacionSQlite = new Operaciones("FichaCamareros.db");
		boolean aFichado = operacionSQlite.camareroFichadoEntrar(idCamarero, new FechaYHora());
		operacionSQlite.cerrarBaseDeDatos();
		return aFichado;
	}

	private boolean camareroYaFichadoParada() {
		Operaciones operacionSQlite = new Operaciones("FichaCamareros.db");
		boolean aFichado = operacionSQlite.camareroFichadoParada(idCamarero, new FechaYHora());
		operacionSQlite.cerrarBaseDeDatos();
		return aFichado;
	}

	private boolean camareroYaFichadoSalir() {
		Operaciones operacionSQlite = new Operaciones("FichaCamareros.db");
		boolean aFichado = operacionSQlite.camareroFichadoSalir(idCamarero, new FechaYHora());
		operacionSQlite.cerrarBaseDeDatos();
		return aFichado;
	}

}
