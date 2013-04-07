package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.Timer;

import tpv.Restaurante;

class BotonMesa extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static ArrayList<JButton> instanciasBotones = new ArrayList<JButton>();
	
	private static boolean menuMesaAbierto = false;
	private static boolean mesasRecienActivadas = false;

	private String idMesa;
	private int numeroPersonas;
	private Restaurante miRestaurante;
	private tpv.Mesa.estadoMesa estado;
	
	private Timer timerPopupMenu;
	
	private JButton imagenBotonMesa;
	private JMenuItem cambiarNumeroPersonas;
	private JLabel jLabelNumeroPersonas;
	private JLabel jLabelIconoMesa;
	
	
	public BotonMesa(Restaurante miRestaurante, int numeroPersonas, String idMesa){
		
		this.miRestaurante = miRestaurante;
		this.idMesa = idMesa;
		this.numeroPersonas = numeroPersonas;
		estado = miRestaurante.dameEstadoMesa(idMesa);
		//Creamos el manejador de eventos de raton para el boton de la mesa.
		EventosRatonBotones manejadorEventosRaton = new EventosRatonBotones();
		
		// Asignamos el layout de la clase, GridLayout permite añadir los elementos en forma de matriz, estan obligados a tener el mismo tamaño todos los componentes.
		setLayout(new GridBagLayout());
		
		/*
		 * Panel inferior ( contiene el icono de las personas y número de personas en la mesa ).
		 */
		// Creamos el panel inferior, contiene el icono de las personas y la cantidad de comensales.
		JPanel jPanelNumeroPersonasEicono= new JPanel(new FlowLayout());
		// Creamos el label que contiene el icono de las personas. 
		JLabel jLabelIconoPersonas = new JLabel();
		// Asignamos el icono al label.
		jLabelIconoPersonas.setIcon(new ImageIcon("Imagenes/iconoPersonas.png"));
		
		// Creamos el label que contiene el numero de personas.
		jLabelNumeroPersonas = new JLabel();
		// Asignamos el número de personas al label.
		jLabelNumeroPersonas.setText(numeroPersonas +"");
		
		// Añadimos el label del icono al panel inferior.
		jPanelNumeroPersonasEicono.add(jLabelIconoPersonas);
		// Añadimos el label del número de personas al panel inferior.
		jPanelNumeroPersonasEicono.add(jLabelNumeroPersonas);
		
		//Añadimos el panel inferior al panel global, en la columna 0 fila 1 e indicamos que no tiene mas capas encima.
		añadirAPanelMesas(jPanelNumeroPersonasEicono, 0, 1, -1);
		
		/*
		 *  Botón mesa.
		 */	
		
		imagenBotonMesa = new JButton();
		// Ajustamos el tamaño del botón al de la imagen.
		imagenBotonMesa.setMargin(new Insets(0, 0, 0, 0));
		// Especificamos que manejadorEventosRaton será la clase encargada de manejar sus eventos de ratón.
		imagenBotonMesa.addMouseListener(manejadorEventosRaton);
		// Cambiamos el layout del boton para poder añadir el idMesa.
		imagenBotonMesa.setLayout(new BorderLayout());
		
		/*
		 *  Ventana emergente con las opciones de la mesa, saldra al mantener pulsado un tiempo gracias al Timer.
		 */
		// Creamos la ventana emergente.
		JPopupMenu propiedadesMesa = new JPopupMenu();
		// Preparamos la fuente para poder modificar el tamaño de la letra de los JMenuItem.
		Font fuentePopupMenuItem = propiedadesMesa.getFont();
		
		JMenuItem abrirMesa = new JMenuItem("Abrir mesa");
		abrirMesa.setBackground(Color.YELLOW);
		// Cambiamos el tamaño de la letra, mantenemos el estilo y el tipo de letra.
		abrirMesa.setFont(new Font(fuentePopupMenuItem.getFontName(), fuentePopupMenuItem.getStyle(), 35));
		// Añadimos oyente para cuando pulsemos en el.
		abrirMesa.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Activamos todos los botones de VentanaMesa, al salir de este metodo el JPopupMenu se cerra luego tendremos que poder pinchar en otra mesa.
				activarTodosLosBotones();
				// Creamos el teclado para introducir el numero de personas.
				JPanel teclado = new TecladoParaNumeroPersonas();
				JFrame marco = new JFrame();
				int resultado = JOptionPane.showOptionDialog(marco, teclado, getIdMesa(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Cancelar","OK"}, "Cancelar");
				switch(resultado){
				case 0 :
					break;
				case 1 : //OK
					actualizaNumeroPersonas(((TecladoParaNumeroPersonas)teclado).getNumeroPersonas());
					actualizarEstadoMesaAbierta();
					break;
				}				
			}
		});
		JMenuItem cobrarMesa = new JMenuItem("Cobrar mesa");
		cobrarMesa.setFont(new Font(fuentePopupMenuItem.getFontName(), fuentePopupMenuItem.getStyle(), 35));
		JMenuItem sincronizarMesa = new JMenuItem("Sincronizar mesa");
		sincronizarMesa.setFont(new Font(fuentePopupMenuItem.getFontName(), fuentePopupMenuItem.getStyle(), 35));
		cambiarNumeroPersonas =  new JMenuItem("Editar nºpersonas");
		cambiarNumeroPersonas.setFont(new Font(fuentePopupMenuItem.getFontName(), fuentePopupMenuItem.getStyle(), 35));
		
		cambiarNumeroPersonas.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				activarTodosLosBotones();
				if(estado != tpv.Mesa.estadoMesa.CERRADA){
					JPanel teclado = new TecladoParaNumeroPersonas();
					JFrame marco = new JFrame();
					int resultado = JOptionPane.showOptionDialog(marco, teclado, getIdMesa(), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Cancelar","OK"}, "Cancelar");
					switch(resultado){
					case 0 :
						break;
					case 1 : //OK
						actualizaNumeroPersonas(((TecladoParaNumeroPersonas)teclado).getNumeroPersonas());
						break;
					}	
				}
			}
		});
		
		JMenuItem cerrarMesa = new JMenuItem("Cerrar mesa");
		cerrarMesa.setFont(new Font(fuentePopupMenuItem.getFontName(), fuentePopupMenuItem.getStyle(), 35));
		
		cerrarMesa.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				activarTodosLosBotones();
				if(estado != tpv.Mesa.estadoMesa.CERRADA ){
					if(!mesaVacia()){
						JLabel mensaje = new JLabel("Esta mesa contiene platos, imposible cerrarla.");
						JFrame marco = new JFrame();
						JOptionPane.showMessageDialog(marco, mensaje);
					}else{
						actualizarEstadoMesaCerrada();
						actualizaNumeroPersonas(0);
					}
				}
			}
		});
		
		
		propiedadesMesa.add(abrirMesa);
		propiedadesMesa.addSeparator();
		propiedadesMesa.add(cerrarMesa);
		propiedadesMesa.addSeparator();
		propiedadesMesa.add(cobrarMesa);
		propiedadesMesa.addSeparator();
		propiedadesMesa.add(sincronizarMesa);
		propiedadesMesa.addSeparator();
		propiedadesMesa.add(cambiarNumeroPersonas);
		
		//Añadimos el JPopupMenu al botón.
		imagenBotonMesa.add(propiedadesMesa);
		// Creamos el Timer que saltara al mantener pulsado X segundos y mostrará el JPopupmenú.
		crearTimer(imagenBotonMesa, propiedadesMesa);
		
		
		/*
		 * Componentes del boton imagenBotonMesa, son el icon de la mesa y su id.
		 */
		
		// Creamos el label que contiene la imagen de la mesa.
		jLabelIconoMesa = new JLabel();
		// En función de su estado cargamos una u otra.
		if(estado == tpv.Mesa.estadoMesa.CERRADA){
			jLabelIconoMesa.setIcon(new ImageIcon("Imagenes/mesaejemplo.jpg"));
		}else if(estado == tpv.Mesa.estadoMesa.ABIERTA){
			jLabelIconoMesa.setIcon(new ImageIcon("Imagenes/mesaEjemploAbierta.jpg"));
		}
		
		// Creamos el panel que contiene el label con el idMesa y de esta forma podremos verlo centrado.
		JPanel jPanelIdMesa = new JPanel();
		jPanelIdMesa.setBackground(Color.BLACK);
		// Creamos el label contenedor del idMesa.
		JLabel jLabelIdMesa = new JLabel(idMesa);
		// Cambiamos el tamaño de la letra.
		Font fuentejLabelIdMesa = jLabelIdMesa.getFont();
		jLabelIdMesa.setFont(new Font(fuentejLabelIdMesa.getFontName(), fuentejLabelIdMesa.getStyle(), 25));
		jLabelIdMesa.setForeground(Color.WHITE);
		// Añadimos el label al panel.
		jPanelIdMesa.add(jLabelIdMesa);
		
		//Añadimos los dos paneles al botón el id en la aprte inferior y el icono de la mesa centrado.
		imagenBotonMesa.add(BorderLayout.SOUTH, jPanelIdMesa);
		imagenBotonMesa.add(BorderLayout.CENTER, jLabelIconoMesa);
		añadirAPanelMesas(imagenBotonMesa, 0, 0, 1);
		
		// Añadimos el botón a un array que contendra todos lo botones que se creen al hacer instancias de esta clase, asi podremos controlar todos los botones que aparecen en VentanaMesa.
		instanciasBotones.add(imagenBotonMesa);
	}
	
	protected String getIdMesa() {
		return idMesa;
	}

	/**
	 * Actualiza el estado de la mesa a cerrada en el restaurante y actualiza el icono en la interfaz.
	 */
	protected void actualizarEstadoMesaCerrada() {
		miRestaurante.actualizaEstadoMesaCerrada(idMesa);
		estado = tpv.Mesa.estadoMesa.CERRADA;
		jLabelIconoMesa.setIcon(new ImageIcon("Imagenes/mesaejemplo.jpg"));
	}
	
	/**
	 * Actualiza el estado de la mesa a abierta en el restaurante y actualiza el icono en la interfaz.
	 */
	protected void actualizarEstadoMesaAbierta() {
		miRestaurante.actualizaEstadoMesaAbierta(idMesa);
		estado = tpv.Mesa.estadoMesa.ABIERTA;
		jLabelIconoMesa.setIcon(new ImageIcon("Imagenes/mesaEjemploAbierta.jpg"));
	}

	/**
	 *  Mira en el restuarante si la mesa tiene algun plato.
	 * @return 
	 */
	protected boolean mesaVacia() {
		return miRestaurante.mesaVacia(idMesa);
	}
	
	/**
	 * Actualiza el numero de personas de la mesa en el restaurante y en la interfaz.
	 * @param numeroPersonas
	 */

	protected void actualizaNumeroPersonas(int numeroPersonas) {
		this.numeroPersonas = numeroPersonas;
		jLabelNumeroPersonas.setText(numeroPersonas+"");
		miRestaurante.actualizarNumeroPersonasMesa(idMesa, numeroPersonas);
		
	}
	
	/**
	 * Añade al panel gobal el componente dada una fila, columna y capa por si quisieramos meter varios elementos en esa celda.
	 * @param componente
	 * @param posGridCol
	 * @param posGridFil
	 * @param capaLayout
	 */
	public void añadirAPanelMesas(Component componente, int posGridCol, int posGridFil, int capaLayout){
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = posGridCol; // El área de texto empieza en la columna cero.
		constraints.gridy = posGridFil; // El área de texto empieza en la fila cero
		if(capaLayout!=-1){
			add(componente, constraints, new Integer(capaLayout));
		}else{
			add(componente, constraints);
		}
	}
	
	/**
	 * Crea el Timer que lanzara el JPopupMenu, es necesario el boton para mostrarlo encima de el.
	 * @param imagenBotonMesa
	 * @param propiedadesMesa
	 */
	
	public void crearTimer(final JButton imagenBotonMesa, final JPopupMenu propiedadesMesa){
		
		timerPopupMenu = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Mostramos ventana emergente y definimos su posición.
				propiedadesMesa.show(imagenBotonMesa,0,10);
				// Paramos el timer, no queremos que abra más el JPopupMenu, ya esta abierto.
				timerPopupMenu.stop();
				// Indicamos que el JPopupMenu esta abierto para no dejar abrir el de los otros botones.
				menuMesaAbierto = true;
			}
		});
		
	}
	
	public boolean equals(Object object){
		if(this==object){
			return true;
		}else if (!(object instanceof BotonMesa)){
			return false;
		}else{
			BotonMesa unBoton = (BotonMesa) object;
			return this.idMesa == unBoton.idMesa;
		}
	}
	
	/**
	 * Desactiva todos los botones que aparecen en VentanaMesa, se utilizará cuando se lance el JPopupMenu.
	 */
	public void desactivarTodosLosBotones(){
		Iterator<JButton> itBotonesMesas = instanciasBotones.iterator();
		while(itBotonesMesas.hasNext()){
			JButton botonMesa = itBotonesMesas.next();
			botonMesa.setEnabled(false);
		}
	}
	
	/**
	 * Activa todos los botones que aparecen en VentanaMesa, se utilizará cuando se cierre el JPopupMenu.
	 */
	public static void activarTodosLosBotones(){
		menuMesaAbierto = false;
		Iterator<JButton> itBotonesMesas = instanciasBotones.iterator();
		while(itBotonesMesas.hasNext()){
			JButton botonMesa = itBotonesMesas.next();
			botonMesa.setEnabled(true);
		}
	}
	
	/**
	 * Clase encargada de gestionar eventos de ratón, extiende de MouseAdapter para no tener que implementar los metodos
	 * que no necesitemos.
	 * @author Prado
	 *
	 */
	private class EventosRatonBotones extends MouseAdapter{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// Solo abrimos la mesa si no esta JPopupMenu abierto, la mesa tiene cualquier estado que no sea cerrada y además no hemos salido justo en ese momento del JPopupMenu de otra mesa.
			if(!mesasRecienActivadas && !menuMesaAbierto && estado!= tpv.Mesa.estadoMesa.CERRADA){
				InterfazPlatos platosMesa = new InterfazPlatos(idMesa, miRestaurante);
				platosMesa.setVisible(true);
			}
			mesasRecienActivadas = false;
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			if(!menuMesaAbierto){
				desactivarTodosLosBotones();
				timerPopupMenu.start();
			}else{
				mesasRecienActivadas = true;
				activarTodosLosBotones();
				timerPopupMenu.stop();
			}
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			activarTodosLosBotones();
			timerPopupMenu.stop();
		}
		
	}
	/**
	 * Panel contenedor del teclado para el número de las personas.
	 * @author Prado
	 *
	 */
	private class TecladoParaNumeroPersonas extends JPanel{
		
		private static final long serialVersionUID = 1L;
		
		private final JTextField numero = new JTextField();
		
		public TecladoParaNumeroPersonas(){
			
						
			JPanel panelTeclado = new JPanel();
			panelTeclado.setLayout(new GridBagLayout());
			
			this.setLayout(new BorderLayout());
			
			numero.setFont(new Font(numero.getFont().getName(), numero.getFont().getStyle(), 30));
			
			this.add(numero,BorderLayout.NORTH);
			this.add(panelTeclado,BorderLayout.CENTER);
			
			int fila = 1;
			int columna = 0;
			for(int i = 0; i<10; i++){
				JButton botonNumero = new JButton(i+"");
				Font fuenteBotonNumero = botonNumero.getFont();
				botonNumero.setFont(new Font(fuenteBotonNumero.getFontName(), fuenteBotonNumero.getStyle(), 30));
				botonNumero.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0) {
						numero.setText(numero.getText()+((JButton)arg0.getSource()).getText());
					}
				});
				GridBagConstraints constraints = new GridBagConstraints();
				if(columna == 3){
					columna = 0;
					fila++;
				}
				constraints.gridx = columna; // El área de texto empieza en la columna columna.
				constraints.gridy = fila; // El área de texto empieza en la fila fila.
				panelTeclado.add(botonNumero, constraints);
				columna++;
			}
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = columna; // El área de texto empieza en la columna cero.
			constraints.gridy = fila; // El área de texto empieza en la fila cero
			JButton botonBorrar = new JButton("C");
			Font fuenteBotonNumero = botonBorrar.getFont();
			botonBorrar.setFont(new Font(fuenteBotonNumero.getFontName(), fuenteBotonNumero.getStyle(), 30));
			botonBorrar.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					numero.setText("");					
				}
			});
			panelTeclado.add(botonBorrar, constraints);
		}

		public int getNumeroPersonas() {
			return Integer.parseInt(numero.getText());
		}
		
	}

}
