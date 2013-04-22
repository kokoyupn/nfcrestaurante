package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import basesDeDatos.Operaciones;

import tpv.AuxDeshacerRehacer;
import tpv.Bebida;
import tpv.Cobro;
import tpv.Mesa;
import tpv.Plato;
import tpv.Producto;
import tpv.Restaurante;
import tpv.TuplaProdEnv;

public class InterfazPlatos extends JFrame {

	private JPanel contentPaneGlobal, panelPlatos, menuConfig, cobrar, eliminar, enviar, aceptar, promociones;
	private JTable tablaPlatos;
	private JButton rehacer, deshacer;
	static GridBagConstraints gbc_btnNewButton2,gbc_btnBotones,gbc_btnPopup;
	static JScrollPane scrollPane, scrollPanePl,scrollPaneBotones,scrollPaneTable;
	private ArrayList<String> categorias;
	private ArrayList<AuxDeshacerRehacer>  auxiliarDeshacer, auxiliarRehacer;
	private ArrayList<TuplaProdEnv> productosEnMesa;
	private Restaurante unRestaurante ;
	private TablaNoEditable dtm;
	private String idMesa,precioAux,obsAux,extrasAux, idCam, categoriaExtraPadre ;
	private Producto productoATabla;
	private HashMap<String,String> hashExtras; //la clave es el tipo de extra
	private int contDeshacer,idsUnicos=0; 
	private JTextField textoObs;
	private boolean esExtras,esObs;
	private JLabel total;
	private double dinero,dineroAcobrar;
	
	/**
	 * Create the frame.
	 */
	public InterfazPlatos(final String idMesa, final Restaurante unRestaurante) {
		this.idMesa = idMesa;
		this.unRestaurante = unRestaurante;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);//Eliminamos los bordes de la ventana.
		//Calculamos el tamaño de la pantalla
		Dimension dimensionesPantalla = getToolkit().getScreenSize();
		contentPaneGlobal = new JPanel();
		contentPaneGlobal.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPaneGlobal);
		contentPaneGlobal.setLayout(null);
		
		//Panel categorias
		JPanel panelCategorias = new JPanel();
		scrollPane = new JScrollPane(panelCategorias);
		scrollPane.setBounds(1050, 10,307, 437);
		scrollPane.setBorder(null);
		contentPaneGlobal.add(scrollPane);
		panelCategorias.setLayout(new GridLayout(numeroDeCategorias(),1));
		
		//Panel platos
		panelPlatos = new JPanel();
		scrollPanePl = new JScrollPane(panelPlatos);
		scrollPanePl.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		//scrollPanePl.setBorder(BorderFactory.createLineBorder(Color.decode("#2C6791")));
		scrollPanePl.setBounds(10,460,1350,295);
		contentPaneGlobal.add(scrollPanePl);
		GridBagLayout gbl_panelPlatos = new GridBagLayout();
		panelPlatos.setLayout(gbl_panelPlatos);
		
		//Panel botones
		JPanel panelBotones = new JPanel();
		panelBotones.setBounds(10, 10, 70, 530);
		contentPaneGlobal.add(panelBotones);
		GridLayout gbl_panelBotones = new GridLayout(6,1);
		panelBotones.setLayout(gbl_panelBotones);
		
////////////////////////////INICIALIZACIONES/////////////////////		
		auxiliarDeshacer = new ArrayList<AuxDeshacerRehacer>();
		auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
		productosEnMesa = new ArrayList<TuplaProdEnv>();
		contDeshacer = 0;
		dinero = 0;
		esExtras = false;
		esObs = false;
		idCam = "";

///////////////GRUPO TOTAL PRECIO////////////////////	
		
		dinero = calculaDineroTotal();
		total = new JLabel("Total: " + dinero + " euros");
		total.setBounds(460,20,310,60);
		contentPaneGlobal.add(total);
		total.setFont(new Font(total.getFont().getName(), total.getFont().getStyle(), 30));

		
////////////////////////////ELIMINAR PLATO/////////////////////
		
		//JButton eliminar = new JButton();
		
		//damos forma y color a los botones de categorias
		eliminar = new JPanelBordesRedondos();
		((JPanelBordesRedondos) eliminar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) eliminar).setColorSecundario(Color.cyan);
		//((JPanelBordesRedondos) btnNewButton).setColorContorno(Color.blue);
		JLabel aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/delete.png"),70,70));
		eliminar.add(aux);
		
		//eliminar.setIcon(tamanioImagen(new ImageIcon("Imagenes/delete.png"), 70, 70));
		panelBotones.add(eliminar);
		eliminar.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent arg0){
						int row = -1;
						row = tablaPlatos.getSelectedRow();
						if (row == -1){
							JOptionPane.showOptionDialog(contentPaneGlobal ,"Debes seleccionar una línea de la tabla y después pulsar el botón eliminar",null,JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/warning.png"),50,50), new Object[] {"Aceptar"},"Aceptar");
						}else{
							if(!productosEnMesa.get(row).isEnviado()){
								auxiliarDeshacer.add(new AuxDeshacerRehacer(false, productosEnMesa.get(row).getProd()));
								deshacer.setEnabled(true);
								dinero = Math.rint((dinero - productosEnMesa.get(row).getProd().getPrecio())*100)/100;
								total.setText("Total: " + dinero + " euros");
								productosEnMesa.remove(row);
								dtm.removeRow(row);
								
								auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
								contDeshacer = 0;
								rehacer.setEnabled(false);
							}else{
								JOptionPane.showOptionDialog(contentPaneGlobal ,"Ese plato ya ha sido enviado a cocina",null,JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/warning.png"),50,50), new Object[] {"Aceptar"},"Aceptar");
						
							}
						}
						}	
					});
////////////////////////////BOTON COBRAR/////////////////////	
		//damos forma y color a los botones de categorias
		cobrar = new JPanelBordesRedondos();
		((JPanelBordesRedondos) cobrar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) cobrar).setColorSecundario(Color.cyan);
		//((JPanelBordesRedondos) cobrar).setColorContorno(Color.blue);
		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/pagar.png"),70,70));
		cobrar.add(aux);
		
		
//		JButton cobrar = new JButton();
		panelBotones.add(cobrar);
//		cobrar.setIcon(tamanioImagen(new ImageIcon("Imagenes/pagar.png"), 70, 70));
		cobrar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				if (productosEnMesa.size() != 0){
					int seleccion = JOptionPane.showOptionDialog(contentPaneGlobal ,"¿Seguro?",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/pagar.png"), 50, 50),new Object[] {"Aceptar", "Cancelar"},"Cancelar");
					if (seleccion == 0){//aceptar
						//TODO cobrar 
						
						ArrayList<Producto> aCobrar = platosACobrar();
						Cobro c = new Cobro(aCobrar,idMesa, idCam, dineroAcobrar, unRestaurante.getNombreRestaurante());
						
						reseteaTablaYPrecio();
						
						JOptionPane.showOptionDialog(contentPaneGlobal ,"Cobrado con éxito",null,JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/check.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");			
					}else{
						JOptionPane.showOptionDialog(contentPaneGlobal ,"No se ha realizado la acción",null,JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
					}
				}else{
					JOptionPane.showOptionDialog(contentPaneGlobal ,"No hay platos para cobrar",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
				}
			}
		});

////////////////////////////BOTON ENVIAR A COCINA/////////////////////
		//damos forma y color a los botones de categorias
		enviar = new JPanelBordesRedondos();
		((JPanelBordesRedondos) enviar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) enviar).setColorSecundario(Color.cyan);
		//((JPanelBordesRedondos) enviar).setColorContorno(Color.blue);
		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/chef.png"),70,70));
		enviar.add(aux);
				
				
		//JButton enviar = new JButton();
		panelBotones.add(enviar);
		//enviar.setIcon(tamanioImagen(new ImageIcon("Imagenes/chef.png"), 70, 70));
		enviar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				if (productosEnMesa.size() != 0){
					if (hayPlatosSinEnviar()){
						int seleccion = JOptionPane.showOptionDialog(contentPaneGlobal ,"¿Seguro?",null,JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/chef.png"), 50, 50),new Object[] {"Aceptar", "Cancelar"},"Cancelar");
						if (seleccion == 0){//aceptar 
							//saco el cam
							Iterator<Mesa> itm = getRestaurante().getIteratorMesas();
							boolean enc = false;
							while(itm.hasNext() && !enc){
								Mesa mesa = itm.next();
								if (mesa.getIdMesa().equals(idMesa)){
									idCam = mesa.getIdCamarero();
									enc=true;
									}
							}
							ArrayList<Producto> aEnviar = platosAEnviar();
							getRestaurante().addComandaAMesa(idMesa, idCam, aEnviar);
							
							//actualizar en producto
							for(int i = 0; i < productosEnMesa.size();i++){
								 productosEnMesa.get(i).setEnviado(true);
							}
							
							auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
							auxiliarDeshacer = new ArrayList<AuxDeshacerRehacer>();
							contDeshacer = 0;
							rehacer.setEnabled(false);
							deshacer.setEnabled(false);
							
							tablaPlatos.repaint();

							JOptionPane.showOptionDialog(contentPaneGlobal ,"Enviado con éxito",null,JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/check.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
						}else{
							JOptionPane.showOptionDialog(contentPaneGlobal ,"No enviado",null,JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
						}
					}else{
						JOptionPane.showOptionDialog(contentPaneGlobal ,"No hay platos nuevos para enviar",null,JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
					}
				}else{
					JOptionPane.showOptionDialog(contentPaneGlobal ,"No hay platos para enviar",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
				}
			}
		});

////////////////////////////BOTON PROMOCIONES/////////////////////	
		//damos forma y color a los botones de categorias
		promociones = new JPanelBordesRedondos();
		((JPanelBordesRedondos) promociones).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) promociones).setColorSecundario(Color.cyan);
		//((JPanelBordesRedondos) promociones).setColorContorno(Color.blue);
		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/oferta.png"),70,70));
		promociones.add(aux);		
		
//		JButton promociones = new JButton();
//		promociones.setIcon(tamanioImagen(new ImageIcon("Imagenes/oferta.png"), 70, 70));
		panelBotones.add(promociones);
		promociones.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){

			}
		});

		
		
////////////////////////////BOTON ACEPTAR/////////////////////		
		//damos forma y color a los botones de categorias
		aceptar = new JPanelBordesRedondos();
		((JPanelBordesRedondos) aceptar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) aceptar).setColorSecundario(Color.cyan);
		//((JPanelBordesRedondos) aceptar).setColorContorno(Color.blue);
		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/back.png"),70,70));
		aceptar.add(aux);

		
//		JButton aceptar = new JButton();
//		aceptar.setIcon(tamanioImagen(new ImageIcon("Imagenes/back.png"), 70, 70));
		aceptar.setBackground(Color.green);
		panelBotones.add(aceptar);
		aceptar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				boolean enc = false;
				Iterator<Mesa> iteratorMesas = getRestaurante().getIteratorMesas();
				while(iteratorMesas.hasNext() && !enc)
				{
					Mesa mesa = iteratorMesas.next();
					if (mesa.getIdMesa().equals(idMesa)){
						mesa.setProductosEnMesa(productosEnMesa);
						mesa.actualizarDineroTotal();
						idCam = mesa.getIdCamarero();
						enc = true;
						}
				}
				dispose();
				VentanaMesas ventanaMesa = new VentanaMesas(getRestaurante(),idCam);
				ventanaMesa.setVisible(true);
				
				Operaciones operacionSQlite = new Operaciones("MesasRestaurante.db");
				operacionSQlite.actualizaVisitadoMesa(idMesa, false);
				operacionSQlite.cerrarBaseDeDatos();
				
			} 	
		});
		
///////////////TABLA DE PLATOS////////////////////
		//creamos las columnas
		Object[][] datos = {};
		String[] columnNames = {"Nombre","Observaciones","Configuración","Precio"};
		dtm= new TablaNoEditable(datos,columnNames);
		//Cargamos los platos de la mesa
		Iterator<Mesa> iteratorMesas = unRestaurante.getIteratorMesas();
		String nombre,configuracion,observaciones;
		double precio;
		while(iteratorMesas.hasNext())
		{
			Mesa mesa = iteratorMesas.next();
			if (mesa.getIdMesa().equals(idMesa)){ 
				//mesa encontrada, cargamos los platos
				productosEnMesa = mesa.getProductosEnMesa();
				for (int i = 0; i < productosEnMesa.size(); i++){
					if (productosEnMesa.get(i).getProd() instanceof Bebida){ //No tiene configuración
						configuracion = "No configurable";
					}else{
						configuracion = ((Plato)productosEnMesa.get(i).getProd()).getExtrasMarcados(); 
					}
					dinero = Math.rint((dinero + productosEnMesa.get(i).getProd().getPrecio())*100)/100;
					total.setText("Total: " + dinero + " euros");
					nombre = productosEnMesa.get(i).getProd().getNombre();
					observaciones = productosEnMesa.get(i).getProd().getObservaciones();
					precio = productosEnMesa.get(i).getProd().getPrecio();
					
					Object[] newRow={nombre,observaciones,configuracion,precio};
					dtm.addRow(newRow);
				}
			}
		}

		tablaPlatos = new JTable(dtm);
		tablaPlatos.setRowHeight(20);
		tablaPlatos.setDefaultRenderer (Object.class, new MiRender());
		// Cambio la fuente de dentro de la tabla
		tablaPlatos.setFont(new Font(tablaPlatos.getFont().getName(), 0, 20)); 
		//Cambio de fuente de la cacecera de la tabla
		JTableHeader th; 
		th = tablaPlatos.getTableHeader(); 
		//Font fuente = new Font("Verdana", Font.ITALIC, 25); 
		th.setFont(new Font(th.getFont().getName(), 0, 25)); 
		
		scrollPaneTable = new JScrollPane(tablaPlatos);
		scrollPaneTable.setBounds(90,90,950,360);
		scrollPaneTable.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		contentPaneGlobal.add(scrollPaneTable);
		GridBagLayout gbl_panelTabla = new GridBagLayout();
		tablaPlatos.setLayout(gbl_panelTabla);	
		tablaPlatos.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				//si la columna es precio (3) saco el teclado numérico
				JTable tablaAux = (JTable)arg0.getComponent();
				int filaPinchada = tablaAux.getSelectedRow();
				if(!productosEnMesa.get(filaPinchada).isEnviado()){
					int comlumnaPinchada = tablaAux.getSelectedColumn();
					String dato=String.valueOf(tablaAux.getValueAt(tablaAux.getSelectedRow(),tablaAux.getSelectedColumn()));
					if(comlumnaPinchada == 3){ // Precio
						precioAux = dato;
						JPanel tecladoNum = new TecladoNumerico();
						JFrame marco = new JFrame();
						int res = JOptionPane.showOptionDialog(marco, tecladoNum,"Editar precio", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
						if (res == 0){//aceptar
							tablaAux.setValueAt(((TecladoNumerico)tecladoNum).getPrecio(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
							productosEnMesa.get(tablaAux.getSelectedRow()).getProd().setPrecio(((TecladoNumerico)tecladoNum).getPrecio());
							dinero = calculaDineroTotal();
							total.setText("Total: " + dinero + " euros");
						}
					}else if (comlumnaPinchada == 2){ //Extras
							//miro que no sea bebida
							if (!productosEnMesa.get(tablaAux.getSelectedRow()).getProd().getCategoria().equals("Bebidas")){
								extrasAux = dato;
								esExtras = true;
								esObs = false;
								JPanel tecladoAlfaNum = new TecladoAlfaNumerico();
								JFrame marco = new JFrame();
								int res = JOptionPane.showOptionDialog(marco, tecladoAlfaNum,"Editar configuración de plato", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
								if (res == 0){//aceptar
									tablaAux.setValueAt(((TecladoAlfaNumerico)tecladoAlfaNum).getObs(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
									((Plato)productosEnMesa.get(tablaAux.getSelectedRow()).getProd()).setExtrasMarcados(((TecladoAlfaNumerico)tecladoAlfaNum).getObs());
								}
							}
						}else if (comlumnaPinchada == 1){//Observaciones
							obsAux = dato;
							esObs = true;
							esExtras = false;
							JPanel tecladoAlfaNum = new TecladoAlfaNumerico();
							JFrame marco = new JFrame();
							int res = JOptionPane.showOptionDialog(marco, tecladoAlfaNum,"Editar observaciones", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
							if (res == 0){//aceptar
								tablaAux.setValueAt(((TecladoAlfaNumerico)tecladoAlfaNum).getObs(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
								productosEnMesa.get(tablaAux.getSelectedRow()).getProd().setObservaciones(((TecladoAlfaNumerico)tecladoAlfaNum).getObs());
							}
						}
				}else{
					JOptionPane.showOptionDialog(contentPaneGlobal ,"Un plato enviado no es configurable",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/warning.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");			
			}
			}			
		});
		
		
///////////////BOTON DESHACER////////////////////		
		
	deshacer = new JButton();
	deshacer.setBorder(null);
	deshacer.setBackground(new Color(-1118482));
	deshacer.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/Undo.png"), 70, 70));
	deshacer.setEnabled(false);
	deshacer.setBounds(90,10,70,70);
	deshacer.addMouseListener(new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent arg0){

			if (auxiliarDeshacer.size()>0){
				if(auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getAccion() == false){//la ultima accion ha sido eliminar asi que añadimos
					aniadeFilaATabla(new TuplaProdEnv(auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getProd(),false));
					auxiliarRehacer.add(auxiliarDeshacer.get(auxiliarDeshacer.size()-1));
					auxiliarDeshacer.remove(auxiliarDeshacer.size()-1);
				}else{//la ultima accion ha sido añadir, asi que borramos
					int linea = buscaPosicion(productosEnMesa, auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getProd());
					dtm.removeRow(linea); 
					auxiliarRehacer.add(auxiliarDeshacer.get(auxiliarDeshacer.size()-1));
					dinero = Math.rint((dinero - auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getProd().getPrecio())*100)/100;
					auxiliarDeshacer.remove(auxiliarDeshacer.size()-1);
					productosEnMesa.remove(linea);
					total.setText("Total: " + dinero + " euros");
				}
				contDeshacer ++;
				rehacer.setEnabled(true);
			}
			if (auxiliarDeshacer.size() == 0){
				deshacer.setEnabled(false);
			}

		}
	});
	contentPaneGlobal.add(deshacer);		

///////////////BOTON REHACER////////////////////
	rehacer = new JButton();
	rehacer.setBorder(null);
	rehacer.setBackground(new Color(-1118482));
	rehacer.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/Redo.png"), 70, 70));
	rehacer.setBounds(968,10,70,70);
	rehacer.setEnabled(false);
	rehacer.addMouseListener(new MouseAdapter() {
	@Override
	public void mousePressed(MouseEvent arg0){
		
		if(contDeshacer > 0){
			if (auxiliarRehacer.size()>0){
				if(auxiliarRehacer.get(auxiliarRehacer.size()-1).getAccion() == true){//tenemos que añadir
					aniadeFilaATabla(new TuplaProdEnv(auxiliarRehacer.get(auxiliarRehacer.size()-1).getProd(),false));
					auxiliarDeshacer.add(auxiliarRehacer.get(auxiliarRehacer.size()-1));
					auxiliarRehacer.remove(auxiliarRehacer.size()-1);
				}else{//tenemos que eliminar
					int linea = buscaPosicion(productosEnMesa, auxiliarRehacer.get(auxiliarRehacer.size()-1).getProd());
					dtm.removeRow(linea);
					auxiliarDeshacer.add(auxiliarRehacer.get(auxiliarRehacer.size()-1));
					dinero = Math.rint((dinero - auxiliarRehacer.get(auxiliarRehacer.size()-1).getProd().getPrecio())*100)/100;
					total.setText("Total: " + dinero + " euros");
					auxiliarRehacer.remove(auxiliarRehacer.size()-1);
					productosEnMesa.remove(linea);
				}	
			}
			contDeshacer --;
			if (contDeshacer == 0){
				rehacer.setEnabled(false);
			}
			deshacer.setEnabled(true);
		}else{
			auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
		}
	}
	});
	contentPaneGlobal.add(rehacer);
	
///////////////CATEGORIAS Y PLATOS////////////////////		
		//rellenamos de las categorias
		//leemos las categorias			
		categorias = new ArrayList<String>();
		Iterator<Producto> iteratorProductos = unRestaurante.getIteratorProductos();
		while(iteratorProductos.hasNext()){
			Producto prod = iteratorProductos.next();
			String categoria = prod.getCategoria();
			if (!categorias.contains(categoria)){
				categorias.add(categoria);
				//JButton btnNewButton = new JButton(categoria);
				
				//damos forma y color a los botones de categorias
				JPanel btnNewButton = new JPanelBordesRedondos();
				JLabel jLabelCategoria = new JLabel(categoria.toUpperCase());
				jLabelCategoria.setFont(new Font(jLabelCategoria.getFont().getFontName(), jLabelCategoria.getFont().getStyle(), 30));
				jLabelCategoria.setForeground(Color.WHITE);
				((JPanelBordesRedondos) btnNewButton).setColorPrimario(new Color(105,25,254));
				((JPanelBordesRedondos) btnNewButton).setColorSecundario(Color.cyan);
				//((JPanelBordesRedondos) btnNewButton).setColorContorno(Color.blue);
				btnNewButton.add(jLabelCategoria);
				
				
				btnNewButton.setName(categoria);
//				btnNewButton.setFont(new Font(btnNewButton.getFont().getName(),btnNewButton.getFont().getStyle(),20));
				btnNewButton.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					panelPlatos.removeAll();
					String catPulsada = arg0.getComponent().getName();
					Iterator<Producto> iteratorProductosHijos =  getRestaurante().getIteratorProductos();
						//rellenamos de los platos
						int j = 0;
						int i = 0;
						int k = 0; //para delimitar el numero de platos por fila
						gbc_btnNewButton2 = new GridBagConstraints();
							while(iteratorProductosHijos.hasNext()){
								final Producto prod = iteratorProductosHijos.next();
								if (prod.getCategoria().equals(catPulsada)){
									String nombre = prod.getNombre();
									String foto = prod.getFoto();
									
//									
//									//damos forma y color a los botones de categorias
//									final JPanel btnNewButton2 = new JPanelBordesRedondos();
//									((JPanelBordesRedondos) btnNewButton2).setColorPrimario(Color.blue);
//									((JPanelBordesRedondos) btnNewButton2).setColorSecundario(Color.cyan);
//									//((JPanelBordesRedondos) btnNewButton).setColorContorno(Color.blue);
//									JLabel aux = new JLabel();
//									aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/Platos/"+ foto + ".jpg"), 137, 99));
//									btnNewButton2.add(aux);
									
									
									final JButton btnNewButton2 = new JButton();
									btnNewButton2.setPreferredSize(new Dimension(137, 99));
									btnNewButton2.setName(nombre);
									btnNewButton2.setIcon(tamanioImagen(new ImageIcon("Imagenes/Platos/"+ foto + ".jpg"), 137, 99));
									btnNewButton2.addMouseListener(new MouseAdapter(){
										@Override
										public void mousePressed(MouseEvent arg0){
//											prod.setIdUnico(idsUnicos);
//											idsUnicos ++;
											generarMenuConfig(new TuplaProdEnv((Producto)prod.clone(), false));		
										}
										
									});//fin listener plato
									if ((k != 0) && (k % 9) == 0) 
									{j++;i=0;}
									gbc_btnNewButton2.gridx = i;
									gbc_btnNewButton2.gridy = j;	
									PlatoCelda celda = new PlatoCelda(btnNewButton2, nombre);
									panelPlatos.add(celda, gbc_btnNewButton2);
								i++;k++;
								}
							}
						panelPlatos.validate();
						panelPlatos.repaint();
						scrollPanePl.validate();
						scrollPanePl.repaint();
				}
				});
				panelCategorias.add(btnNewButton);
			}	
		}				
	}
	
	public void aniadeFilaATabla(TuplaProdEnv prod){
		String configuracion;
		
		productosEnMesa.add(prod);
		dinero = Math.rint((dinero + prod.getProd().getPrecio())*100)/100;
		total.setText("Total: " + dinero + " euros");
		
		if (prod.getProd() instanceof Bebida){ //No tiene configuración
			configuracion = "No configurable";
		}else{
			configuracion = ((Plato)prod.getProd()).getExtrasMarcados(); 
		}		
		Object[] newRow={prod.getProd().getNombre(),prod.getProd().getObservaciones(),configuracion,prod.getProd().getPrecio()};
		dtm.addRow(newRow);
		
		//Refrescamos
		tablaPlatos.validate();
		tablaPlatos.repaint();
		scrollPaneTable.validate();
		scrollPaneTable.repaint();
	}
	
	public void generarMenuConfig(final TuplaProdEnv prod){
//		productoATabla = new Producto(prod.getProd().getId(),prod.getProd().getCategoria(), prod.getProd().getTipo(), prod.getProd().getNombre(),
//				prod.getProd().getDescripción(), prod.getProd().getFoto(),prod.getProd().getPrecio(),prod.getProd().getObservaciones(), prod.getProd().getIdUnico());
		productoATabla = prod.getProd();
		menuConfig = new JPanel(new GridBagLayout());
		
		GridBagConstraints grid = new GridBagConstraints();
		
		int i = 0;//para situarlo en el grid;
		gbc_btnPopup = new GridBagConstraints();

		if (prod.getProd() instanceof Plato){ 
			Plato plato = (Plato) prod.getProd();
			String extras = plato.getExtras();	
			hashExtras = new HashMap<String,String>();
			if (!extras.equals("")){
				JLabel etiqueta = new JLabel("CONFIGURACIÓN DE PLATO");
				gbc_btnPopup.gridx = 0;
				gbc_btnPopup.gridy = i;
				menuConfig.add(etiqueta, gbc_btnPopup);
				i++;
				
	            String[] tokens = extras.split("/");
		        // Creamos las distintas categorías de extras
		        for(int k = 0; k < tokens.length ;k++)
		        {	
					String[] nombreExtra;			
					try{
						nombreExtra = tokens[k].split(":");
						categoriaExtraPadre = nombreExtra[0];
						
						hashExtras.put(categoriaExtraPadre, "");
						
						// Creamos la variedad de extras
						String[] elementosExtra = null;
						
			        	JLabel etq = new JLabel("<html><u>"+nombreExtra[0]+"</u></html>");
						gbc_btnPopup.gridx = 0;
						gbc_btnPopup.gridy = i;
						menuConfig.add(etq, gbc_btnPopup);
						i++;
						
						ButtonGroup grupo = new ButtonGroup();
						
						//Creo un panel con gridlayout para los radiobuttons
						JPanel panelRadio = new JPanel();
						GridLayout gridRadio = new GridLayout(2,1);
						panelRadio.setLayout(gridRadio);

						elementosExtra = nombreExtra[1].split(",");
						for(int j=0; j<elementosExtra.length; j++)
						{  
														
							JRadioButton extra2 = new JRadioButton(elementosExtra[j]);
							extra2.setName(categoriaExtraPadre + "/" + elementosExtra[j]);
							panelRadio.add(extra2);
							
							gbc_btnPopup.gridx = 0;
							gbc_btnPopup.gridy = i;
							menuConfig.add(panelRadio, gbc_btnPopup);
							i++;
							
							extra2.addMouseListener(new MouseAdapter() {
								@Override
								public void mousePressed(MouseEvent arg0) { 						
									String[] nombrePadre;			
									nombrePadre = arg0.getComponent().getName().split("/");
									hashExtras.put(nombrePadre[0], nombrePadre[1]);
								}
							});
							
							grupo.add(extra2);
						}
					}catch(Exception e){
						System.out.println(e.toString());
						}
				}
	        }else{
	        	//no tiene extras
	        	hashExtras.put(categoriaExtraPadre, "No configurable");
	        	 }
		}	
		//Común para plato y bebida
		
		JLabel etqObs = new JLabel("OBSERVACIONES");
		gbc_btnPopup.gridx = 0;
		gbc_btnPopup.gridy = i;
		menuConfig.add(etqObs, gbc_btnPopup);
		i++;

		textoObs = new JTextField(30);
		grid.gridx = 0;
		grid.gridy = i;
		menuConfig.add(textoObs,grid);
		i++;
		
		textoObs.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) { 						
				JPanel teclado = new TecladoAlfaNumerico();
				JFrame marco = new JFrame();
				JOptionPane.showOptionDialog(marco, teclado,"Observaciones", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
				textoObs.setText(((TecladoAlfaNumerico)teclado).getObs());
				}
		});
		        
		JFrame marco = new JFrame();
		int result = JOptionPane.showOptionDialog(marco, menuConfig, null, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
	
		if(result == 0 ){//aceptar
			//recorremos el has y si en alguno pone "" falta por rellenar
			boolean faltaCampo = false;
			Iterator<Entry<String, String>> itCampos = hashExtras.entrySet().iterator();
			while(itCampos.hasNext() && !faltaCampo){
				if(itCampos.next().getValue().equals("")){
					faltaCampo = true;
				}
			}
			if (!faltaCampo){
				productoATabla.setObservaciones(textoObs.getText());
				productoATabla.setIdUnico(idsUnicos);
				idsUnicos ++;
				
				if (productoATabla instanceof Plato){
					Plato platoATabla = (Plato) productoATabla;
					Iterator<String> itExtras = hashExtras.values().iterator();//Recorre los extras
					String extrasConcat = "";
					while(itExtras.hasNext())
					{
						if (extrasConcat == "")
							extrasConcat = itExtras.next();
						else
							extrasConcat = extrasConcat + ", " + itExtras.next();
					}
					platoATabla.setExtrasMarcados(extrasConcat);	
					productoATabla = (Producto) platoATabla;
				}
				deshacer.setEnabled(true);
				auxiliarDeshacer.add(new AuxDeshacerRehacer(true, productoATabla));
				aniadeFilaATabla(new TuplaProdEnv(productoATabla, false));
				
				auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
				contDeshacer = 0;
				rehacer.setEnabled(false);
			}else{
				JOptionPane.showOptionDialog(contentPaneGlobal ,"<html>Debes rellenar todos los campos de configuración de plato.<br/>Plato NO añadido<html>",null,JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/warning.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
			}
		}
	
	
	}
	
	public static ImageIcon tamanioImagen(ImageIcon imag, int ancho, int alto){
		//nuevos ancho y alto: para que conserve la proporcion pasamos -1
		ImageIcon imagen = new ImageIcon(imag.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_DEFAULT));
		return imagen;		
	}
	
	public int buscaPosicion(ArrayList<TuplaProdEnv> lista, Producto pr){
		Boolean enc = false;
		int i = 0;
		while (!enc && i < lista.size()){
			if(pr.equals(lista.get(i).getProd()) && (!lista.get(i).isEnviado())){
				enc = true;
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public Restaurante getRestaurante(){
		return unRestaurante;
	}
	
	public double calculaDineroTotal(){
		double dineroTotalEnMesa = 0;
		for(int i = 0; i < productosEnMesa.size();i++){
			dineroTotalEnMesa = Math.rint((dineroTotalEnMesa + productosEnMesa.get(i).getProd().getPrecio())*100)/100;
		}
		return dineroTotalEnMesa;
	}
	
//	 private String buscaFotoConCategoria(String categoria){
//		 //buscamos en productos en unRestaurante
//		 Iterator<Producto> iteratorProductos = unRestaurante.getIteratorProductos();
//		 boolean enc = false;
//		 String foto = "fnd_fh";
//		 while(iteratorProductos.hasNext() && !enc){
//			 Producto p = iteratorProductos.next();
//			 if (p.getCategoria().equals(categoria) && !p.getFoto().equals("fnd_fh")){
//				 foto = p.getFoto();
//				 enc = true;
//			 }
//		 }
//		 return foto;
//	 }
	 
	 public int numeroDeCategorias(){
		ArrayList<String> categorias = new ArrayList<String>();
		Iterator<Producto> iteratorProductos = unRestaurante.getIteratorProductos();
		while(iteratorProductos.hasNext()){
				Producto prod = iteratorProductos.next();
				String categoria = prod.getCategoria();
				if (!categorias.contains(categoria)){
					categorias.add(categoria);
				}
		}
		return categorias.size();
	 }
	
	 public void reseteaTablaYPrecio(){
		productosEnMesa = new ArrayList<TuplaProdEnv>();
		dinero = 0;
		total.setText("Total: " + dinero + " euros");
		
		auxiliarDeshacer = new ArrayList<AuxDeshacerRehacer>();
		auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
		contDeshacer = 0;
		deshacer.setEnabled(false);
		rehacer.setEnabled(false);
		//borro las filas de la tabla
		int i = tablaPlatos.getRowCount();
		while(i > 0){
			dtm.removeRow(i-1);
			i--;
		}
		tablaPlatos.validate();
		tablaPlatos.repaint();
	 }
	 
	 
//	 public void actualizaTablaConColor(){
//		 int i = tablaPlatos.getRowCount()-1;
//			while(i >= 0){
//				if (productosEnMesa.get(i).isEnviado()){
//					//( (String)tablaPlatos.getValueAt(2, 2)).concat("wewewe");
//				}
//				i--;
//			}
//			tablaPlatos.validate();
//			tablaPlatos.repaint();
//	 }
	 
	 public boolean hayPlatosSinEnviar(){
		//mira si en la tabla hay platos con el campo enviado a false
		int i = tablaPlatos.getRowCount()-1;
		while(i >= 0){
			if (productosEnMesa.get(i).isEnviado() == false){
				return true;
			}
			i--;
		}
		return false;
	 }
	 
	 //busca los platos en la tabla (y array) que aun no han sido enviados y los devuelve en un ArrayList
	 public ArrayList<Producto> platosAEnviar(){
		 ArrayList<Producto> tmp = new ArrayList<Producto>();
		int i = tablaPlatos.getRowCount()-1;
		while(i >= 0){
			if (!productosEnMesa.get(i).isEnviado()){
				tmp.add(productosEnMesa.get(i).getProd());
			}
			i--;
		}
		return tmp;
	 }
	 
	 //busca los platos en la tabla (y array) que han sido enviados y los devuelve en un ArrayList
	 public ArrayList<Producto> platosACobrar(){
		 ArrayList<Producto> tmp = new ArrayList<Producto>();
		int i = tablaPlatos.getRowCount()-1;
		dineroAcobrar = 0;
		while(i >= 0){
			if (productosEnMesa.get(i).isEnviado()){
				dineroAcobrar = Math.rint((dineroAcobrar + productosEnMesa.get(i).getProd().getPrecio())*100)/100;
				tmp.add(productosEnMesa.get(i).getProd());
			}
			i--;
		}
		return tmp;
	 }
	
private class TecladoAlfaNumerico extends JPanel{
		
		private static final long serialVersionUID = 1L;
		
		private JTextField texto = new JTextField(30);
		private JPanel panelTeclado = new JPanel();
		private JPanel panelEsp = new JPanel();
		
		public TecladoAlfaNumerico(){
						
			if(esExtras){
				texto.setText(extrasAux);
				esExtras = false;
			}
			if (esObs){
				texto.setText(obsAux);
				esObs = false;
			}
			panelTeclado.setLayout(new GridLayout(4,10));
			
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 0;
						
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc_general = new GridBagConstraints();
			gbc_general.gridx = 0;
			gbc_general.gridy = 0;
			
			texto.setFont(new Font(texto.getFont().getName(), texto.getFont().getStyle(), 30));
						
			this.add(texto,gbc_general);
			gbc_general.gridx = 0;
			gbc_general.gridy = 1;
			this.add(panelTeclado,gbc_general);
			
			//numeros
			for(int i = 0; i<10; i++){
				JButton botonNumero = new JButton(i+"");
				Font fuenteBotonNumero = botonNumero.getFont();
				botonNumero.setFont(new Font(fuenteBotonNumero.getFontName(), fuenteBotonNumero.getStyle(), 30));
				botonNumero.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						texto.setText(texto.getText()+((JButton)arg0.getSource()).getText());
					}
				});
				panelTeclado.add(botonNumero);
			}
			
			//letras
			for(int i = 0; i<26; i++){
				JButton botonLetra = new JButton(""+(char) ('a' + i ));
				Font fuenteBotonLetra = botonLetra.getFont();
				botonLetra.setFont(new Font(fuenteBotonLetra.getFontName(), fuenteBotonLetra.getStyle(), 30));
				botonLetra.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						texto.setText(texto.getText()+((JButton)arg0.getSource()).getText());
					}
				});
				panelTeclado.add(botonLetra);
			}
			//caracteres especiales
			for(int i = 0; i<4; i++){
				JButton botonLetra = new JButton(""+(char) (',' + i ));
				Font fuenteBotonLetra = botonLetra.getFont();
				botonLetra.setFont(new Font(fuenteBotonLetra.getFontName(), fuenteBotonLetra.getStyle(), 30));
				botonLetra.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						texto.setText(texto.getText()+((JButton)arg0.getSource()).getText());
					}
				});
				panelTeclado.add(botonLetra);
			}
			
			//Creo un panel para meter el espacio y los borrar
			panelEsp.setLayout(new GridLayout(1,3));
			
			//Espacio
			JButton botonEspacio = new JButton("");
			botonEspacio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					texto.setText(texto.getText()+" ");
				}
			});
			panelEsp.add(botonEspacio);
			
			//Boton borrar todo
			JButton botonBorrar = new JButton("C");
			Font fuenteBotonNumero = botonBorrar.getFont();
			botonBorrar.setFont(new Font(fuenteBotonNumero.getFontName(), fuenteBotonNumero.getStyle(), 30));
			botonBorrar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					texto.setText("");
				}
			});
			panelEsp.add(botonBorrar);
			
			//Boton borrar uno a uno
			JButton botonBorrarUno = new JButton();
			botonBorrarUno.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/Undo.png"), 30, 30));
			Font FuenteBotonBorrarUno = botonBorrarUno.getFont();
			botonBorrarUno.setFont(new Font(FuenteBotonBorrarUno.getFontName(), FuenteBotonBorrarUno.getStyle(), 30));
			botonBorrarUno.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(texto.getText().length()>0){
						String aux = texto.getText().substring(0,texto.getText().length()-1);
						texto.setText(aux);	
					}
				}
			});
			panelEsp.add(botonBorrarUno);
			
			gbc_general.gridx = 0;
			gbc_general.gridy = 2;
			this.add(panelEsp,gbc_general);			
		}

		public String getObs() {
			return texto.getText();
		}	
	}




private class TecladoNumerico extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private final JTextField precio = new JTextField();
	
	public TecladoNumerico(){
		precio.setText(precioAux);
		
		JPanel panelTeclado = new JPanel();
		panelTeclado.setLayout(new GridLayout(4,3));
		
		this.setLayout(new BorderLayout());
		
		precio.setFont(new Font(precio.getFont().getName(), precio.getFont().getStyle(), 30));
		
		this.add(precio,BorderLayout.NORTH);
		this.add(panelTeclado,BorderLayout.CENTER);
		
		for(int i = 0; i<10; i++){
			JButton botonNumero = new JButton(i+"");
			Font fuenteBotonNumero = botonNumero.getFont();
			botonNumero.setFont(new Font(fuenteBotonNumero.getFontName(), fuenteBotonNumero.getStyle(), 30));
			botonNumero.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					precio.setText(precio.getText()+((JButton)arg0.getSource()).getText());
				}
			});
			panelTeclado.add(botonNumero);
		}
		
		JButton botonPunto = new JButton(".");
		Font fuenteBotonPunto = botonPunto.getFont();
		botonPunto.setFont(new Font(fuenteBotonPunto.getFontName(), fuenteBotonPunto.getStyle(), 30));
		botonPunto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(precio.getText().length()>0){
					precio.setText(precio.getText()+((JButton)e.getSource()).getText());
				}			
			}
		});
		panelTeclado.add(botonPunto);
		
		JButton botonBorrar = new JButton();
		botonBorrar.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/Undo.png"), 30, 30));
		Font fuenteBotonNumero = botonBorrar.getFont();
		botonBorrar.setFont(new Font(fuenteBotonNumero.getFontName(), fuenteBotonNumero.getStyle(), 30));
		botonBorrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(precio.getText().length()>0){
					String aux = precio.getText().substring(0,precio.getText().length()-1);
					precio.setText(aux);	
				}			
			}
		});
		panelTeclado.add(botonBorrar);
	}

	public double getPrecio() {
		return Double.parseDouble(precio.getText());
	}
	
}


public class TablaNoEditable extends DefaultTableModel
{
	
	public TablaNoEditable(Object[][] f, String[] c){
		super(f,c);
	}
	
   public boolean isCellEditable (int row, int column)
   {
       return false;
   }
}
	


public class MiRender extends DefaultTableCellRenderer
	{
	   public Component getTableCellRendererComponent(JTable table,
	      Object value,
	      boolean isSelected,
	      boolean hasFocus,
	      int row,
	      int column)
	   {
	      super.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, column);
	      {
	    	 if(productosEnMesa.get(row).isEnviado()){
		         this.setOpaque(true);
		         this.setBackground(Color.GREEN);
		         this.setForeground(Color.BLUE);
	    	 }else{
		         this.setOpaque(true);
		         this.setBackground(Color.WHITE);
		         this.setForeground(Color.BLACK);
	    	 }
	      }
	      return this;
	   }
	}

}