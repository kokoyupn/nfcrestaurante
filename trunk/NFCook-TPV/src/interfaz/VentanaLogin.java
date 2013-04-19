package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import sockets.ClienteFichero;
import sockets.EscuchaCliente;
import tpv.Mesa;
import tpv.Restaurante;

public class VentanaLogin extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private static GraphicsDevice grafica = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private boolean esPantallaCompleta;
	private Restaurante unRestaurante;
	private final static int puerto = 5000;
	private final static String servidor = "nfcook.no-ip.org";

	public VentanaLogin(){
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		//Eliminamos los bordes de la ventana.
		setUndecorated(true);
		
		//Calculamos el tamaño de la pantalla
		Dimension dimenionesPantalla = getToolkit().getScreenSize();
				
		unRestaurante = new Restaurante();
		
		/*
		 * JPanel principal (contiene el JPanel de la imagen y del login ).
		 */
		
		// Panel principal, es el que contendra los paneles de la imagen y del teclado.
		JPanel panelContenedorTecladoEimagen = new JPanel();
		// Ponemos un borde al panel por estetica.
		panelContenedorTecladoEimagen.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Para poder decidir el tamaño de los paneles de dentro.
		panelContenedorTecladoEimagen.setLayout(null);
		setContentPane(panelContenedorTecladoEimagen);
		
		/*
		 * JPanel izquierdo ( contiene una imagen para darle mas vida a la pantalla ).
		 */
		
		// Creamos el panel contenedor de la imagen
		JPanel panelImagen = new JPanel(new BorderLayout());
		// Ajustamos el tamaño del panel a partir del tamaño de la ventana.
		panelImagen.setBounds(10, 10, (int) dimenionesPantalla.getWidth()-800, (int) dimenionesPantalla.getHeight()-20); //(x, y, w, z) -> X = despazamiento a derecha; Y = despazamiento encima; W = ancho ; Z = largo.
		// Ponemos un borde al panel por estetica, asi las mesas no saldran pegadas al techo.
		panelImagen.setBorder(new EmptyBorder(17, 0, 17, 0));
		// Creamos la imagen que contendra el panel.
		JLabel labelImagenRestaurante = new JLabel(new ImageIcon("Imagenes/Restaurantes/foster2.jpg"));
		// Añadimos la imagen al panel.
		panelImagen.add(labelImagenRestaurante, BorderLayout.CENTER);
		panelImagen.setBackground(Color.BLACK);
		// Añadimos el panel al panel principal.
		panelContenedorTecladoEimagen.add(panelImagen);
		
		/*
		 *  JPanel derecho ( contiene el teclado para hacer el login).
		 */
		
		// Creamos el panel contenedor del teclado para el login.
		JPanel panelTecladoLogin = new JPanel();
		// Ajustamos el tamaño del panel a partir del tamaño de la ventana.
		panelTecladoLogin.setBounds((int) dimenionesPantalla.getWidth()-780, 10, (int) dimenionesPantalla.getWidth()-510, (int) dimenionesPantalla.getHeight()-20); //(x, y, w, z) -> X = despazamiento a derecha; Y = despazamiento encima; W = ancho ; Z = largo.
		// Ponemos un borde al panel por estetica, asi las mesas no saldran pegadas al techo.
		panelTecladoLogin.setBorder(new EmptyBorder(150, 0, 17, 0));
		// Añadimos el panel al panel principal.
		panelTecladoLogin.setBackground(Color.BLACK);
		panelTecladoLogin.add(new TecladoParaLogin());
		panelContenedorTecladoEimagen.add(panelTecladoLogin);		

	}
	
	public void desactivarVentanaLogin(){
		setEnabled(false);
	}
	
	public void activarVentanaLogin(){
		setEnabled(true);
		validate();
		repaint();
	}
	
	public JFrame getVentanaLogin(){
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(!esPantallaCompleta){
            grafica.setFullScreenWindow(this);
		}
		else{
            grafica.setFullScreenWindow(null);
		}
		esPantallaCompleta = !esPantallaCompleta;
	}
	
	private class TecladoParaLogin extends JPanel{
		
		private static final long serialVersionUID = 1L;
		
		private JTextField textFieldnumero = new JTextField();
		private String clave = "";
		
		public TecladoParaLogin(){
			
			JPanel panelTeclado = new JPanel();
			panelTeclado.setLayout(new GridBagLayout());
			this.add(panelTeclado);
			cargarNumeros(panelTeclado);
			cargarAreaTexto(panelTeclado);

			
		}
		public void cargarAreaTexto(JPanel panelTeclado){
						
			textFieldnumero.setFont(new Font(textFieldnumero.getFont().getName(), textFieldnumero.getFont().getStyle(), 50));
			textFieldnumero.setHorizontalAlignment(JTextField.CENTER);
			textFieldnumero.setFocusable(false);
			
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 5;
			//Decimos que ocupe esas 3 columnas.
			constraints.fill = GridBagConstraints.BOTH;
			panelTeclado.add(textFieldnumero,constraints);
			
		}
		public void cargarNumeros(JPanel panelTeclado){
			
			GridBagConstraints constraints = new GridBagConstraints();
			Font fuente = new Font(Font.SANS_SERIF, Font.CENTER_BASELINE, 60);
			
			int numeroBoton = 1;
			for(int fila = 1; fila<4 ; fila++){
				for(int columna = 0; columna<3; columna++){
					JButton botonNumero = new JButton(numeroBoton +"");
					botonNumero.setFont(fuente);
					constraints.gridx = columna;
					constraints.gridy = fila;
					constraints.insets = new Insets(3, 3, 3, 3);
					panelTeclado.add(botonNumero, constraints);
					numeroBoton++;
					
					botonNumero.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent boton) {
							textFieldnumero.setText(textFieldnumero.getText() + "*");
							clave += ((JButton) boton.getSource()).getText();
						}
					});
				}
			}
			
			JButton botonCero = new JButton("0");
			botonCero.setFont(fuente);
			constraints.gridx = 0;
			constraints.gridy = 4;
			constraints.insets = new Insets(3, 3, 3, 3);
			panelTeclado.add(botonCero, constraints);
			botonCero.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent boton) {
					textFieldnumero.setText(textFieldnumero.getText() + "*");
					clave += ((JButton) boton.getSource()).getText();
				}
			});
			
			JButton botonBorrar = new JButton("C");
			botonBorrar.setFont(fuente);
			constraints.gridx = 3;
			constraints.gridy = 1;
			constraints.gridwidth = 2;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.insets = new Insets(3, 3, 3, 3);
			botonBorrar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					textFieldnumero.setText("");
					clave = "";
				}
			});
			panelTeclado.add(botonBorrar, constraints);
			
			
			
			JButton botonLogin = new JButton(new ImageIcon("Imagenes/Botones/loginLogin.png"));
			Font fuenteLogin = new Font(fuente.getName(), fuente.getStyle(),40);
			botonLogin.setFont(fuenteLogin);
			constraints.gridx = 1;
			constraints.gridy = 4;
			constraints.gridwidth = 4;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.insets = new Insets(3, 3, 3, 3);
			panelTeclado.add(botonLogin, constraints);
			botonLogin.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(unRestaurante.existeCamarero(clave)){
						VentanaMesas ventanaMesas = new VentanaMesas(unRestaurante, textFieldnumero.getText().toString());
						ventanaMesas.pack();
						ventanaMesas.setVisible(true);
						dispose();
					}else{
						JFrame marco = new JFrame();
						textFieldnumero.setText("");
						clave = "";
						JOptionPane.showMessageDialog(marco, "No existe camarero");
					}
				}
			});
			
			JButton botonFichar = new JButton(new ImageIcon("Imagenes/Botones/ficharLogin.png"));
			constraints.gridx = 3;
			constraints.gridy = 2;
			constraints.gridwidth = 2;
			constraints.gridheight = 2;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.insets = new Insets(3, 3, 3, 3);
			panelTeclado.add(botonFichar, constraints);
			botonFichar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(unRestaurante.existeCamarero(clave)){
						desactivarVentanaLogin();
						JFrame ventanaFichar = new VentanaFichar(clave, getVentanaLogin());
						ventanaFichar.pack();
						ventanaFichar.setLocationRelativeTo(null);
						ventanaFichar.setVisible(true);
						textFieldnumero.setText("");
						clave = "";
					}else{
						JFrame marco = new JFrame();
						textFieldnumero.setText("");
						clave = "";
						JOptionPane.showMessageDialog(marco, "No existe camarero");
					}
				}
			});
		}
		
	}
	
	public static void main(String args[]){
		ClienteFichero.pide("MesasRestaurante.db", servidor, puerto);
		ClienteFichero.pide("MiBase.db", servidor, puerto);
		ClienteFichero.pide("login.db", servidor, puerto);
		ClienteFichero.pide("FichaCamareros.db", servidor, puerto);
		
		VentanaLogin ventanaLogin = new VentanaLogin();
		ventanaLogin.pack();
		ventanaLogin.setVisible(true);
		
		EscuchaCliente thread = new EscuchaCliente(); // lanzamos el thread de escucha
        thread.start();
        
	}
}
