package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.search.FlagTerm;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.sun.mail.imap.protocol.FLAGS;

import sockets.ClienteFichero;
import sockets.EscuchaCliente;
import sockets.OperacionesSocketsSinBD;
import sockets.ShutdownHook;
import tpv.Mesa;
import tpv.Plato;
import tpv.Producto;
import tpv.Restaurante;

public class VentanaLogin extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private static GraphicsDevice grafica = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private boolean esPantallaCompleta;
	private static Restaurante unRestaurante;
	private static String clave = "";
	
	private static boolean registrado = false;

	
	public VentanaLogin(){
		
		// ShutdownHook nos permite gestionar cuando cerramos la aplicacion (para enviar al servidor la IP del TPV a eliminar)
		ShutdownHook shutdownHook = new ShutdownHook(null);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		//Eliminamos los bordes de la ventana.
		setUndecorated(true);
		
		//Calculamos el tamaño de la pantalla
		Dimension dimenionesPantalla = getToolkit().getScreenSize();
				
		unRestaurante = new Restaurante();
		unRestaurante.cargarRestaurante();
		
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
		panelImagen.setBounds(10, 10, (int) ((dimenionesPantalla.getWidth()/2)-170), (int) dimenionesPantalla.getHeight()-20); //(x, y, w, z) -> X = despazamiento a derecha; Y = despazamiento encima; W = ancho ; Z = largo.
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
		panelTecladoLogin.setBounds((int) ((dimenionesPantalla.getWidth()/2)-150), 10, (int) (dimenionesPantalla.getWidth()-((dimenionesPantalla.getWidth()/2)-170))-30, (int) dimenionesPantalla.getHeight()-20); //(x, y, w, z) -> X = despazamiento a derecha; Y = despazamiento encima; W = ancho ; Z = largo.
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
	/*
	 * Sera utilizado para poderIntroducir las comandas que lleguen por red local.
	 */
	public static Restaurante getRestaurante(){
		return unRestaurante;
	}
	
	private class TecladoParaLogin extends JPanel{
		
		private static final long serialVersionUID = 1L;
		
		private JTextField textFieldnumero = new JTextField();
		
		
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
						VentanaMesas ventanaMesas = new VentanaMesas(unRestaurante, clave);
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
	
    /**
     * Metodo recursivo.
     * Si la parte que se pasa es compuesta, se extrae cada una de las subpartes y
     * el metodo se llama a si mismo con cada una de ellas.
     * Si la parte es un text, se escribe en pantalla.
     * Si la parte es una image, se guarda en un fichero y se visualiza en un JFrame.
     * En cualquier otro caso, simplemente se escribe el tipo recibido, pero se
     * ignora el mensaje.
     *
     * @param unaParte Parte del mensaje a analizar.
     */
    private static void analizaParteDeMensaje(Message unaParte)
    {
        try
        {
    		// Si es texto, se escribe el texto.
            if (unaParte.isMimeType("text/*")) {
                InputStream stream = unaParte.getInputStream();
                String pedido = "";
                while(stream.available() != 0){
                	pedido += (char) stream.read();
                }
                procesaPedido(pedido);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
	public static void escuchaReceptorNFC(){
		// Se obtiene la Session
        Properties prop = new Properties();
        prop.setProperty("mail.pop3.starttls.enable", "false");
        prop.setProperty(
            "mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.setProperty("mail.pop3.socketFactory.fallback", "false");
        prop.setProperty("mail.pop3.port", "995");
        prop.setProperty("mail.pop3.socketFactory.port", "995");
        Session sesion = Session.getInstance(prop);
        try{
        	// Se obtiene el Store y el Folder, para poder leer el
        	// correo.
            Store store = sesion.getStore("pop3");
            store.connect(
                "pop.gmail.com", "nfcookapp@gmail.com", "Macarrones");
            Folder folder = store.getFolder("Inbox");
            folder.open(Folder.READ_WRITE);

            // Se obtienen los mensajes.
            Flags leidos = new Flags(Flags.Flag.SEEN);
	        FlagTerm unseenFlagTerm = new FlagTerm(leidos, false);
	        //Buscamos los mensajes NO leidos
	        Message[] mensajes = folder.search(unseenFlagTerm);

            // Se escribe from y subject de cada mensaje
            for (int i = 0; i < mensajes.length; i++)
            {
            	// Miramos si se trata de la sincronización de un pedido
            	if(mensajes[i].getFrom()[0].toString().equals("nfcookapp@gmail.com") &&
            			mensajes[i].getSubject().equals("PEDIDO")){
            		// Se visualiza, si se sabe como, el contenido de cada mensaje
            		analizaParteDeMensaje(mensajes[i]);
            		mensajes[i].setFlag(Flag.SEEN, true);
            	}
            }
            
            folder.close(false);
            store.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
	
	public static void procesaPedido(String pedido){
		/**
		 * TODO Diniel procesar el string del pedido, lo deciodifica y lo añade a 
		 * la mesa que corresponda.
		 */
		
		
		
		//1@h@V73@V74@V20+rvkc*Cebolla frita@V37*Al punto, Patatas fritas, Queso@255
		//restaurante@idplato "+"observaciones"*" extras
		StringTokenizer platos = new StringTokenizer (pedido, "@");
		boolean parar=false;
		//Restaurante
		int restaurante = Integer.parseInt(platos.nextToken());
		ArrayList<Producto> aEnviar= new ArrayList<Producto>();
		String numeroMesa=platos.nextToken();
		String numeroPersonas = platos.nextToken();
		if (restaurante==0){
		while (platos.hasMoreTokens() && !parar)
		{
			//----------------------------------------
			//Para cada plato lo decodificamos y lo añadimos a la base de datos
			String plato = platos.nextToken();
			StringTokenizer stTodoSeparado =  new StringTokenizer(plato,"*+");
			
			String extras,comentario;
			extras=comentario="";
			// id
			String id =  stTodoSeparado.nextToken();
			parar= id.equals("255");
			if(!parar){//Si no ha acabado el mensaje		
				// comentarios
				if (plato.contains("+"))  {
					comentario =  stTodoSeparado.nextToken();
				}
				
				// extras
				if (plato.contains("*"))  {
					extras =  stTodoSeparado.nextToken();
						
				}
				
				System.out.println("\n PLATO:"+id+":"+extras+":"+comentario);
				Producto  producto = unRestaurante.dameProductoRestauranteDadoID(id);
				
				

				String categoria = producto.getCategoria();
				String tipo = producto.getTipo();
				String nombre = producto.getNombre();
				String descripcion = producto.getDescripción();
				String foto = producto.getFoto();
		     	Double precio = producto.getPrecio();
		     	String extrasMarcados="";
		     	int cantidad =1;
				
				if (extras.equals(""))
					extras="No configurable"; 
				unRestaurante.añadirProductoEnMesaTrue("M"+numeroMesa,producto, extras, comentario);
				aEnviar.add(producto);
			}	
		}
		numeroMesa= "M"+numeroMesa;
		String claveS = "1235";
		OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
		operacion.actualizarMesaBDLLegadaExterna(null, numeroMesa, claveS, Integer.parseInt(numeroPersonas), 2);
		operacion.actualizarMesaBD(numeroMesa, claveS, Integer.parseInt(numeroPersonas), 2);
		
		////////////////////////
		
        unRestaurante.addComandaAMesa(numeroMesa, claveS, aEnviar);
				
		}
		else {
			System.out.println("\n Esos platos no corresponden a este restaurante");
		}
		
	
	}
	
	public static void main(String args[]){
		
		ClienteFichero.pide("MesasRestaurante.db");
		ClienteFichero.pide("MiBase.db");
		ClienteFichero.pide("login.db");
		ClienteFichero.pide("FichaCamareros.db");
		ClienteFichero.pide("InfoMesas.db");
		ClienteFichero.pide("Equivalencia_Restaurantes.db");
		ClienteFichero.pide("MiBaseFav.db");
		
		//procesaPedido("0@9@fh45@fh46@fh47@fh19+hola*Patatas Fritas@fh41+vabh@255@\r\n");		

		VentanaLogin ventanaLogin = new VentanaLogin();
		//0@h@V20+rvkc*Cebolla frita@V37*Al punto, Patatas fritas, Queso@255
		//0@9@fh45@fh46@fh47@fh19+hola*Patatas Fritas@fh41+vabh@255
		//0@1@fh1@fh8+Sin cebolla ni salsasa barbacoa*Al punto, Barbacoa, Patata Asada@fh43@fh26+Sin queso@fh3*Mexicana@255
		//procesaPedido("0@9@fh45@fh46@fh47@fh19+hola*Patatas Fritas@fh41+vabh@255");
				
		ventanaLogin.pack();
		ventanaLogin.setVisible(true);
		
		EscuchaCliente thread = new EscuchaCliente(); // lanzamos el thread de escucha
        thread.start();
                
        new Thread(new Runnable() {
    	    public void run() {
    	    	while(clave.equals(""));
    	    	while(clave.contentEquals("1235")){
    	    		try {
    	    			escuchaReceptorNFC();
    	    			registrado = true;
    	    		} catch (Exception e) {
    	    			System.out.println("Error en la recepción NFC del TPV");
    	    		}
    	    	}
    	    }
        }).start();
  	}
}
