package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import basesDeDatos.Operaciones;

import sockets.OperacionesSocketsSinBD;
import tpv.AuxDeshacerRehacer;
import tpv.Bebida;
import tpv.Cobro;
import tpv.Mesa;
import tpv.Plato;
import tpv.Producto;
import tpv.Restaurante;
import tpv.TuplaProdEnv;

/**
 * Clase gestora de los platos del restaurante. Añade y elimina platos, envia a cocina, cobra, aplica promociones,  
 * tiene en cuenta platos mas pedidos en el restaurante e imprime cuentas con codigos qr
 * @author Guille
 *
 */
public class InterfazPlatos extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPaneGlobal, panelPlatos, menuConfig, cobrar, eliminar, enviar, aceptar, promociones;
	private JTable tablaPlatos;
	private JButton rehacer, deshacer, subir, bajar,subirTabla, bajarTabla;
	static GridBagConstraints gbc_btnNewButton2,gbc_btnBotones,gbc_btnPopup,gbc_promo;
	static JScrollPane scrollPane, scrollPanePl,scrollPaneBotones,scrollPaneTable;
	private ArrayList<String> categorias;
	private ArrayList<AuxDeshacerRehacer>  auxiliarDeshacer, auxiliarRehacer;
	private ArrayList<TuplaProdEnv> productosEnMesa;
	private Restaurante unRestaurante ;
	private TablaNoEditable dtm;
	private String idMesa,precioAux,obsAux,extrasAux, idCam, categoriaExtraPadre ;
	private Producto productoATabla;
	private HashMap<String,String> hashExtras; //la clave es el tipo de extra
	private int contDeshacer,idsUnicos=0, promocion; //Promocion=1 --> 2x1, Promocion=2 --> 30%, Promocion= 0 --> No promo, 
	private JTextField textoObs;
	private boolean esExtras,esObs;
	private JLabel total;
	private double dinero,dineroAcobrar,ancho;
	private Dimension dimensionesPantalla;
	
	/**
	 * Create the frame.
	 * @param idCamarero 
	 */
	public InterfazPlatos(final String idMesa, final Restaurante unRestaurante, String idCamarero) {
		this.idMesa = idMesa;
		this.unRestaurante = unRestaurante;
		this.idCam = idCamarero;
		this.unRestaurante.setIterfazPlatos(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);//Eliminamos los bordes de la ventana.
		//Calculamos el tamaño de la pantalla
		dimensionesPantalla = getToolkit().getScreenSize();
		ancho = dimensionesPantalla.width;
		contentPaneGlobal = new JPanel();
		contentPaneGlobal.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPaneGlobal);
		contentPaneGlobal.setLayout(null);
		
		//Panel categorias
		JPanel panelCategorias = new JPanel();
		scrollPane = new JScrollPane(panelCategorias);
		scrollPane.setBounds((int)((ancho/11) +30 + (ancho - ((ancho/11) + (ancho/4) + 40)) ), 10, (int)( (ancho/4)) , 437);
		scrollPane.setBorder(null);
		contentPaneGlobal.add(scrollPane);
		panelCategorias.setLayout(new GridLayout(numeroDeCategorias()+1,1)); //Mas uno por el boton favoritos
		
		//Panel platos
		panelPlatos = new JPanel();
		scrollPanePl = new JScrollPane(panelPlatos);
		scrollPanePl.getVerticalScrollBar().setUnitIncrement(150);
		scrollPanePl.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		scrollPanePl.setBounds(10,460,(int)(ancho-90),(int)(dimensionesPantalla.getHeight()-470));
		contentPaneGlobal.add(scrollPanePl);
		GridBagLayout gbl_panelPlatos = new GridBagLayout();
		panelPlatos.setLayout(gbl_panelPlatos);
		
		//Panel botones
		JPanel panelBotones = new JPanel();
		panelBotones.setBounds(10, 10, (int)ancho/11, 530);
		contentPaneGlobal.add(panelBotones);
		GridLayout gbl_panelBotones = new GridLayout(5,1);
		panelBotones.setLayout(gbl_panelBotones);
		
		
////////////////////////////INICIALIZACIONES/////////////////////		
		auxiliarDeshacer = new ArrayList<AuxDeshacerRehacer>();
		auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
		productosEnMesa = new ArrayList<TuplaProdEnv>();
		contDeshacer = 0;
		dinero = 0;
		esExtras = false;
		esObs = false;
		hashExtras = new HashMap<String,String>();

///////////////GRUPO TOTAL PRECIO////////////////////	
		dinero = calculaDineroTotal();
		total = new JLabel("Mesa: " + idMesa +"   Total: " + dinero + " €");
		total.setBounds((int)(((ancho-((ancho/11)+(ancho/4)))-240)/2),20,410,60);
		contentPaneGlobal.add(total);
		total.setFont(new Font(total.getFont().getName(), total.getFont().getStyle(), 30));


		
////////////////////////////ELIMINAR PLATO/////////////////////
		
		//damos forma y color a los botones
		eliminar = new JPanelBordesRedondos(false);
		((JPanelBordesRedondos) eliminar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) eliminar).setColorSecundario(Color.cyan);
		JLabel aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/delete.png"),70,70));
		eliminar.add(aux);
		
		//añadimos al panel y establecemos el oyente
		panelBotones.add(eliminar);
		eliminar.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent arg0){
						int row = -1;
						//obtenemos la fila seleccionada
						row = tablaPlatos.getSelectedRow();
						if (row == -1){//no ha seleccionado nada
							JOptionPane.showOptionDialog(contentPaneGlobal ,"Debes seleccionar una línea de la tabla y después pulsar el botón eliminar",null,JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/warning.png"),50,50), new Object[] {"Aceptar"},"Aceptar");
						}else{
							//Si el producto no ha sido enviado lo eliminamos
							if(!productosEnMesa.get(row).isEnviado()){
								//actualizamos el dinero, y los auxiliares para deshacer y rehacer, los productos en el array y en la tabla.
								auxiliarDeshacer.add(new AuxDeshacerRehacer(false, productosEnMesa.get(row).getProd()));
								deshacer.setEnabled(true);
								dinero = Math.rint((dinero - productosEnMesa.get(row).getProd().getPrecio())*100)/100;
								total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
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
		//damos forma y color a los botones 
		cobrar = new JPanelBordesRedondos(false);
		((JPanelBordesRedondos) cobrar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) cobrar).setColorSecundario(Color.cyan);
		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/pagar.png"),70,70));
		cobrar.add(aux);
		
		//Añadimos el boton al panel y establecemos el oyente
		panelBotones.add(cobrar);
		cobrar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				//Si no hay productos en mesa saca un mensaje de aviso
				if (productosEnMesa.size() != 0){
					int seleccion = JOptionPane.showOptionDialog(contentPaneGlobal ,"¿Seguro?",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/pagar.png"), 50, 50),new Object[] {"Aceptar", "Cancelar"},"Cancelar");
					if (seleccion == 0){//aceptar
						//TODO cobrar 
						//cobramos solo los platos que han sido enviados a cocina
						ArrayList<Producto> aCobrar = platosACobrar();						
						if (aCobrar.size()>0){
							//Construimos el panel con las promociones
							generaPromociones();
							
							//Pasamos los parametros necesarios para imprimir el tiket
							Cobro c = new Cobro(aCobrar,idMesa, idCam, dineroAcobrar, unRestaurante.getNombreRestaurante(),promocion);
							//mostramos mensaje de acción realizada con éxito
							JOptionPane.showOptionDialog(contentPaneGlobal ,"Cobrado con éxito",null,JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/check.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");			
							//Borramos la tabla, el array y el precio
							reseteaTablaYPrecio();
							}else{
								//mostramos mensaje indicando que no hay platos por cobrar
								JOptionPane.showOptionDialog(contentPaneGlobal ,"No hay platos para cobrar",null,JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
							}
						}else{
						//mostramos mensaje de acción realizada sin éxito
						JOptionPane.showOptionDialog(contentPaneGlobal ,"No se ha realizado la acción",null,JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
					}
				}else{
					//mostramos mensaje indicando que no hay platos por cobrar
					JOptionPane.showOptionDialog(contentPaneGlobal ,"No hay platos para cobrar",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
				}
			}
		});

////////////////////////////BOTON ENVIAR A COCINA/////////////////////
		//damos forma y color a los botones 
		enviar = new JPanelBordesRedondos(false);
		((JPanelBordesRedondos) enviar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) enviar).setColorSecundario(Color.cyan);
		//((JPanelBordesRedondos) enviar).setColorContorno(Color.blue);
		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/chef.png"),70,70));
		enviar.add(aux);
		
		//Añadimos el boton al panel y establecemos el oyente
		panelBotones.add(enviar);
		enviar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				if (productosEnMesa.size() != 0){
					if (hayPlatosSinEnviar()){
						int seleccion = JOptionPane.showOptionDialog(contentPaneGlobal ,"¿Seguro?",null,JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/chef.png"), 50, 50),new Object[] {"Aceptar", "Cancelar"},"Cancelar");
						if (seleccion == 0){//aceptar 

							//Solo enviamos los platos que no han sido enviados ya
							ArrayList<Producto> aEnviar = platosAEnviar();
							getRestaurante().addComandaAMesa(idMesa, idCam, aEnviar);
							
							//Actualizamos los favoritos
							//Le sumamos los pedidos para favoritos
							for(int i = 0; i < aEnviar.size();i++){
								if(aEnviar.get(i) instanceof Plato){
									Plato plato = (Plato)aEnviar.get(i);
									int cant = buscaCantidadNoEnviados(plato, aEnviar);
									int aux = buscaMaxEnviados(plato);
									int ant = 0;
									
									if(plato.getCantiadPedido() >= aux){//Han añadido desde fuera mas platos de ese tipo
										ant = plato.getCantiadPedido();
										actualizaEnProductosEnMesa(plato.getIdUnico(),plato.getId(), cant+ant);
										//Actualizamos la base de datos de favoritos
										Restaurante.actualizaFavs(plato);
										actualizaEnviados(plato, cant+ant);
									}else{
										actualizaEnProductosEnMesa(plato.getIdUnico(),plato.getId(),aux);
										//Actualizamos la base de datos de favoritos
										Restaurante.actualizaFavs(plato);
									}
								}
							}
							
								
							//actualizar en producto
							for(int i = 0; i < productosEnMesa.size();i++){
								 productosEnMesa.get(i).setEnviado(true);
							}
							
							//Actualizamos los datos en Restaurante en mesa
							boolean enc = false;
							Iterator<Mesa> iteratorMesas = getRestaurante().getIteratorMesas();
							while(iteratorMesas.hasNext() && !enc)
							{
								Mesa mesa = iteratorMesas.next();
								if (mesa.getIdMesa().equals(idMesa)){
									mesa.setProductosEnMesa(productosEnMesa);
									mesa.actualizarDineroTotal();
									enc = true;
									}
							}
							
							//actualizar auxiliares
							auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
							auxiliarDeshacer = new ArrayList<AuxDeshacerRehacer>();
							contDeshacer = 0;
							rehacer.setEnabled(false);
							deshacer.setEnabled(false);
							
							tablaPlatos.repaint();
							
							//mostramos mensaje de acción realizada con éxito
							JOptionPane.showOptionDialog(contentPaneGlobal ,"Enviado con éxito",null,JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/check.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");

						}else{
							//mostramos mensaje de acción realizada sin éxito
							JOptionPane.showOptionDialog(contentPaneGlobal ,"No enviado",null,JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
						}
					}else{
						//mostramos mensaje de acción realizada sin éxito
						JOptionPane.showOptionDialog(contentPaneGlobal ,"No hay platos nuevos para enviar",null,JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
					}
				}else{
					//mostramos mensaje indicando que no hay platos por enviar
					JOptionPane.showOptionDialog(contentPaneGlobal ,"No hay platos para enviar",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/wrong.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
				}
			}

		});


		
		
////////////////////////////BOTON ACEPTAR/////////////////////		
		//damos forma y color a los botones 
		aceptar = new JPanelBordesRedondos(false);
		((JPanelBordesRedondos) aceptar).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) aceptar).setColorSecundario(Color.cyan);
		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/back.png"),70,70));
		aceptar.add(aux);
		
		//Ponemos el fondo del boton en verde
		aceptar.setBackground(Color.green);
		//Añadimos el boton al panel y establecemos el oyente
		panelBotones.add(aceptar);
		aceptar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){

				//Actualizamos los datos en Restaurante en mesa
				boolean enc = false;
				Iterator<Mesa> iteratorMesas = getRestaurante().getIteratorMesas();
				while(iteratorMesas.hasNext() && !enc)
				{
					Mesa mesa = iteratorMesas.next();
					if (mesa.getIdMesa().equals(idMesa)){
						mesa.setProductosEnMesa(productosEnMesa);
						mesa.actualizarDineroTotal();
						enc = true;
						}
				}
				
				//Lanzamos VentanaMesas
				dispose();
				getRestaurante().setIterfazPlatos(null);
				VentanaMesas ventanaMesa = new VentanaMesas(getRestaurante(),idCam);
				ventanaMesa.setVisible(true);
				
				OperacionesSocketsSinBD operacion = new OperacionesSocketsSinBD();
				operacion.actualizaVisitadoMesa(idMesa, false);
				
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
					}else{ //Cargamos los extras marcados
						configuracion = ((Plato)productosEnMesa.get(i).getProd()).getExtrasMarcados(); 
					}
					//Actualizamos el dinero y sacamos los valores para las filas
					dinero = Math.rint((dinero + productosEnMesa.get(i).getProd().getPrecio())*100)/100;
					total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
					nombre = productosEnMesa.get(i).getProd().getNombre();
					observaciones = productosEnMesa.get(i).getProd().getObservaciones();
					precio = productosEnMesa.get(i).getProd().getPrecio();
					
					//Creamos una nueva fila y la añadimos al contenido de la tabla
					Object[] newRow={nombre,observaciones,configuracion,precio};
					dtm.addRow(newRow);
				}
			}
		}
		
		//Creamos la tabla con el contenido anterior
		tablaPlatos = new JTable(dtm);
		//ponemos como anchura maxima 120 pixeles en la columna precio
		tablaPlatos.getColumn("Precio").setMaxWidth(120);
		//alineamos el precio a la derecha
		DefaultTableCellRenderer tcr = new MiRender();
		tcr.setHorizontalAlignment(SwingConstants.RIGHT);
		tablaPlatos.getColumnModel().getColumn(3).setCellRenderer(tcr);
		//Establecemos la altura de la fila 
		tablaPlatos.setRowHeight(20);
		//Creamos un render y se lo aplicamos a la tabla para que cuando esten los platos enviados salga en verde
		tablaPlatos.setDefaultRenderer (Object.class, new MiRender());
		// Cambio la fuente de dentro de la tabla
		tablaPlatos.setFont(new Font(tablaPlatos.getFont().getName(), 0, 20)); 
		//Cambio de fuente de la cabecera de la tabla
		JTableHeader th; 
		th = tablaPlatos.getTableHeader(); 
		th.setFont(new Font(th.getFont().getName(), 0, 25)); 
		
		scrollPaneTable = new JScrollPane(tablaPlatos);
		scrollPaneTable.setBounds((int)((ancho/11) +20),90,(int)((ancho - ((ancho/11) + (ancho/4) + 40))),360);
		//Establecemos un borde
		scrollPaneTable.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		contentPaneGlobal.add(scrollPaneTable);
		GridBagLayout gbl_panelTabla = new GridBagLayout();
		tablaPlatos.setLayout(gbl_panelTabla);	
		//Establecemos el oyente 
		tablaPlatos.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				//Sacamos la columna y la fila pinchada
				//si la columna es precio (3), y no ha sido enviado, saco el teclado numérico
				JTable tablaAux = (JTable)arg0.getComponent();
				int filaPinchada = tablaAux.getSelectedRow();
				if(!productosEnMesa.get(filaPinchada).isEnviado()){
					int comlumnaPinchada = tablaAux.getSelectedColumn();
					String dato=String.valueOf(tablaAux.getValueAt(tablaAux.getSelectedRow(),tablaAux.getSelectedColumn()));
					if(comlumnaPinchada == 3){ // Precio
						//Guardamos lo que habia en la celda para pasarselo al teclado
						precioAux = dato;
						JPanel tecladoNum = new TecladoNumerico();
						JFrame marco = new JFrame();
						int res = JOptionPane.showOptionDialog(marco, tecladoNum,"Editar precio", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
						if (res == 0){//aceptar
							//Realizamos las modificaciones y actualizamos el dinero
							tablaAux.setValueAt(((TecladoNumerico)tecladoNum).getPrecio(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
							productosEnMesa.get(tablaAux.getSelectedRow()).getProd().setPrecio(((TecladoNumerico)tecladoNum).getPrecio());
							dinero = calculaDineroTotal();
							total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
						}
					}else if (comlumnaPinchada == 2){ //Extras
							//miramos que no sea bebida
							if (!productosEnMesa.get(tablaAux.getSelectedRow()).getProd().getCategoria().equals("Bebidas")){
								//Guardamos lo que habia en la celda para pasarselo al teclado
								extrasAux = dato;
								esExtras = true;
								esObs = false;
								JPanel tecladoAlfaNum = new TecladoAlfaNumerico();
								JFrame marco = new JFrame();
								int res = JOptionPane.showOptionDialog(marco, tecladoAlfaNum,"Editar configuración de plato", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
								if (res == 0){//aceptar
									//Realizamos las modificaciones
									tablaAux.setValueAt(((TecladoAlfaNumerico)tecladoAlfaNum).getObs(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
									((Plato)productosEnMesa.get(tablaAux.getSelectedRow()).getProd()).setExtrasMarcados(((TecladoAlfaNumerico)tecladoAlfaNum).getObs());
								}
							}
						}else if (comlumnaPinchada == 1){//Observaciones
							//Guardamos lo que habia en la celda para pasarselo al teclado
							obsAux = dato;
							esObs = true;
							esExtras = false;
							JPanel tecladoAlfaNum = new TecladoAlfaNumerico();
							JFrame marco = new JFrame();
							int res = JOptionPane.showOptionDialog(marco, tecladoAlfaNum,"Editar observaciones", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
							if (res == 0){//aceptar
								//Realizamos las modificaciones
								tablaAux.setValueAt(((TecladoAlfaNumerico)tecladoAlfaNum).getObs(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
								productosEnMesa.get(tablaAux.getSelectedRow()).getProd().setObservaciones(((TecladoAlfaNumerico)tecladoAlfaNum).getObs());
							}
						}
				}else{
					//Se ha pinchado sobre un plato ya enviado y no son configurables
					JOptionPane.showOptionDialog(contentPaneGlobal ,"Un plato enviado no es configurable",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/warning.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");			
				}
			}			
		});

		generaBotonesScrollTabla();
		
///////////////BOTON DESHACER////////////////////		
		
	deshacer = new JButton();
	deshacer.setBorder(null);
	deshacer.setBackground(new Color(-1118482));
	deshacer.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/Undo.png"), 70, 70));
	deshacer.setEnabled(false);
	deshacer.setBounds((int)((ancho/11) +20),10,70,70);
	//Establecemos el oyente
	deshacer.addMouseListener(new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent arg0){				
			if (auxiliarDeshacer.size()>0){
				//Si hay platos guardados
				if(auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getAccion() == false){
					//la ultima accion ha sido eliminar asi que añadimos
					aniadeFilaATabla(new TuplaProdEnv(auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getProd(),false));
					auxiliarRehacer.add(auxiliarDeshacer.get(auxiliarDeshacer.size()-1));
					auxiliarDeshacer.remove(auxiliarDeshacer.size()-1);
				}else{
					//la ultima accion ha sido añadir, asi que borramos
					int linea = buscaPosicion(productosEnMesa, auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getProd());
					dtm.removeRow(linea); 
					auxiliarRehacer.add(auxiliarDeshacer.get(auxiliarDeshacer.size()-1));
					dinero = Math.rint((dinero - auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getProd().getPrecio())*100)/100;
					auxiliarDeshacer.remove(auxiliarDeshacer.size()-1);
					productosEnMesa.remove(linea);
					total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
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
	rehacer.setBounds((int)((ancho/11) -60 + (ancho - ((ancho/11) + (ancho/4) + 40)) -80),10,70,70);
	rehacer.setEnabled(false);
	//Establecemos el oyente
	rehacer.addMouseListener(new MouseAdapter() {
	@Override
	public void mousePressed(MouseEvent arg0){
		//Los platos al pulsar deshacer se guardan en rehacer
		if(contDeshacer > 0){
			//Si hay platos guardados
			if (auxiliarRehacer.size()>0){
				if(auxiliarRehacer.get(auxiliarRehacer.size()-1).getAccion() == true){
					//tenemos que añadir
					aniadeFilaATabla(new TuplaProdEnv(auxiliarRehacer.get(auxiliarRehacer.size()-1).getProd(),false));
					auxiliarDeshacer.add(auxiliarRehacer.get(auxiliarRehacer.size()-1));
					auxiliarRehacer.remove(auxiliarRehacer.size()-1);
				}else{
					//tenemos que eliminar
					int linea = buscaPosicion(productosEnMesa, auxiliarRehacer.get(auxiliarRehacer.size()-1).getProd());
					dtm.removeRow(linea);
					auxiliarDeshacer.add(auxiliarRehacer.get(auxiliarRehacer.size()-1));
					dinero = Math.rint((dinero - auxiliarRehacer.get(auxiliarRehacer.size()-1).getProd().getPrecio())*100)/100;
					total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
					auxiliarRehacer.remove(auxiliarRehacer.size()-1);
					productosEnMesa.remove(linea);
				}	
			}
			//Si no se puede rehacer deshabilitamos el boton
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
		//Primero creamos el boton favoritos
		JPanel btnNewButtonFav = new JPanelBordesRedondos(false);
		JLabel jLabelCategoriaFav = new JLabel("FAVORITOS");
		jLabelCategoriaFav.setFont(new Font(jLabelCategoriaFav.getFont().getFontName(), jLabelCategoriaFav.getFont().getStyle(), 30));
		jLabelCategoriaFav.setForeground(Color.WHITE);
		((JPanelBordesRedondos) btnNewButtonFav).setColorPrimario(new Color(105,25,254));
		((JPanelBordesRedondos) btnNewButtonFav).setColorSecundario(Color.cyan);
		btnNewButtonFav.add(jLabelCategoriaFav);
		//Ponemos la categoria en el nombre para luego poder acceder a ella
		btnNewButtonFav.setName("favoritos");
		//Establecemos el oyente
		btnNewButtonFav.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//borramos lo que hubiera en en panel de los platos
				panelPlatos.removeAll();
				Restaurante.cargarProductosFavoritos();
				PriorityQueue<Producto> colaTmp = getRestaurante().getCopiaFavoritos();
								
					//rellenamos de los platos de favoritos
					int j = 0;
					int i = 0;
					int k = 0; //para delimitar el numero de platos por fila
					gbc_btnNewButton2 = new GridBagConstraints();
						while(!colaTmp.isEmpty()){
							final Producto prod = colaTmp.remove();
							if (!prod.getCategoria().equals("Bebidas")){
								//Sacamos los datos necesarios para el boton
								String nombre = prod.getNombre();
								String foto = prod.getFoto();
								
								//Creamos el boton y establecemos su oyente y sus propiedades
								final JButton btnNewButton2 = new JButton();
								btnNewButton2.setPreferredSize(new Dimension(137, 99));
								btnNewButton2.setName(nombre);
								btnNewButton2.setIcon(tamanioImagen(new ImageIcon("Imagenes/Platos/"+ foto + ".jpg"), 137, 99));
								btnNewButton2.addMouseListener(new MouseAdapter(){
									@Override
									public void mousePressed(MouseEvent arg0){
										//Si el producto seleccionado es un plato creamos un nuevo objeto Plato con extras y extrasMarcados. No enviado
										generarMenuConfig(new TuplaProdEnv(new Plato(prod.getId(), prod.getCategoria(),prod.getTipo(), prod.getNombre(),
												prod.getDescripción(), prod.getFoto(), prod.getPrecio(), prod.getObservaciones(),((Plato) prod).getExtras(),
												((Plato) prod).getExtrasMarcados(), ((Plato) prod).getCantiadPedido()), false));		
									}
									
								});//fin listener plato
								//El 8 delimita el numero de platos por fila que se ven en el panel
								if ((k != 0) && (k % 8) == 0) 
								{j++;i=0;}
								gbc_btnNewButton2.gridx = i;
								gbc_btnNewButton2.gridy = j;
								//Pasamos el boton y el nombre a la clase PlatoCelda que le da formato
								PlatoCelda celda = new PlatoCelda(btnNewButton2, nombre);
								//Lo añadimos al panel
								panelPlatos.add(celda, gbc_btnNewButton2);
							i++;k++;
							}
						}
					//Refrescamos
					panelPlatos.validate();
					panelPlatos.repaint();
					scrollPanePl.validate();
					scrollPanePl.repaint();
			}
			});
			panelCategorias.add(btnNewButtonFav);
		
			
			
		//rellenamos las categorias	y los platos genericamente
		categorias = new ArrayList<String>();
		Iterator<Producto> iteratorProductos = unRestaurante.getIteratorProductos();
		while(iteratorProductos.hasNext()){
			Producto prod = iteratorProductos.next();
			String categoria = prod.getCategoria();
			//Preguntamos si esa categoria ya se ha puesto para no repetir
			if (!categorias.contains(categoria)){
				categorias.add(categoria);
				//damos forma y color a los botones de categorias
				JPanel btnNewButton = new JPanelBordesRedondos(false);
				JLabel jLabelCategoria = new JLabel(categoria.toUpperCase());
				jLabelCategoria.setFont(new Font(jLabelCategoria.getFont().getFontName(), jLabelCategoria.getFont().getStyle(), 30));
				jLabelCategoria.setForeground(Color.WHITE);
				((JPanelBordesRedondos) btnNewButton).setColorPrimario(new Color(105,25,254));
				((JPanelBordesRedondos) btnNewButton).setColorSecundario(Color.cyan);
				btnNewButton.add(jLabelCategoria);
				//Ponemos la categoria en el nombre para luego poder acceder a ella
				btnNewButton.setName(categoria);
				//Establecemos el oyente
				btnNewButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					//borramos lo que hubiera en en panel de los platos
					panelPlatos.removeAll();
					//Sacamos el nombre de la categoria pulsada
					String catPulsada = arg0.getComponent().getName();
					Iterator<Producto> iteratorProductosHijos =  getRestaurante().getIteratorFavoritos();
						//rellenamos de los platos de esa categoria
						int j = 0;
						int i = 0;
						int k = 0; //para delimitar el numero de platos por fila
						gbc_btnNewButton2 = new GridBagConstraints();
							while(iteratorProductosHijos.hasNext()){
								final Producto prod = iteratorProductosHijos.next();
								if (prod.getCategoria().equals(catPulsada)){
									//Sacamos los datos necesarios para el boton
									String nombre = prod.getNombre();
									String foto = prod.getFoto();
									
									//Creamos el boton y establecemos su oyente y sus propiedades
									final JButton btnNewButton2 = new JButton();
									btnNewButton2.setPreferredSize(new Dimension(137, 99));
									btnNewButton2.setName(nombre);
									btnNewButton2.setIcon(tamanioImagen(new ImageIcon("Imagenes/Platos/"+ foto + ".jpg"), 137, 99));
									btnNewButton2.addMouseListener(new MouseAdapter(){
										@Override
										public void mousePressed(MouseEvent arg0){
											if (prod instanceof Bebida){
												//Si el producto seleccionado es una bebida creamos un nuevo objeto Bebida. No enviado
												generarMenuConfig(new TuplaProdEnv(new Bebida(prod.getId(), prod.getCategoria(),prod.getTipo(), prod.getNombre(),
														prod.getDescripción(), prod.getFoto(), prod.getPrecio(), prod.getObservaciones()), false));	
											}else{
												//Si el producto seleccionado es un plato creamos un nuevo objeto Plato con extras y extrasMarcados. No enviado
												generarMenuConfig(new TuplaProdEnv(new Plato(prod.getId(), prod.getCategoria(),prod.getTipo(), prod.getNombre(),
														prod.getDescripción(), prod.getFoto(), prod.getPrecio(), prod.getObservaciones(),((Plato) prod).getExtras(),
														((Plato) prod).getExtrasMarcados(),((Plato) prod).getCantiadPedido()), false));		
											}
										}
										
									});//fin listener plato
									//El 8 delimita el numero de platos por fila que se ven en el panel
									if ((k != 0) && (k % 8) == 0) 
									{j++;i=0;}
									gbc_btnNewButton2.gridx = i;
									gbc_btnNewButton2.gridy = j;
									//Pasamos el boton y el nombre a la clase PlatoCelda que le da formato
									PlatoCelda celda = new PlatoCelda(btnNewButton2, nombre);
									//Lo añadimos al panel
									panelPlatos.add(celda, gbc_btnNewButton2);
								i++;k++;
								}
							}
						//Refrescamos
						panelPlatos.validate();
						panelPlatos.repaint();
						scrollPanePl.validate();
						scrollPanePl.repaint();
						
				}

				});
				panelCategorias.add(btnNewButton);
			}	
		}				
		generaBotonesScrollPlatos();
}

	
	private void generaBotonesScrollPlatos() {
		//Calculamos el tamaño de los botones en funcion del de la pantalla
		int altoBotones = (int)(((dimensionesPantalla.getHeight() - 460)/2)-10) ;
		
		scrollPanePl.setBounds(10,460,(int)(ancho-110),(int)(dimensionesPantalla.getHeight()-470));
		
		subir = new JButton();
		subir.setBorder(null);
		subir.setBackground(new Color(-1118482));
		subir.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/up.png"), 80, altoBotones));
		subir.setBounds((int)(ancho-90), 460, 80 ,altoBotones);
		subir.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				scrollPanePl.getVerticalScrollBar().setValue(scrollPanePl.getVerticalScrollBar().getValue()-200);
				}
				
		});
		contentPaneGlobal.add(subir);	
	
		
		bajar = new JButton();
		bajar.setBorder(null);
		bajar.setBackground(new Color(-1118482));
		bajar.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/down.png"), 80, altoBotones));
		bajar.setBounds((int)(ancho-90), 470 + altoBotones, 80 , altoBotones);
		bajar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				scrollPanePl.getVerticalScrollBar().setValue(scrollPanePl.getVerticalScrollBar().getValue()+200);
			}
		
		});
		contentPaneGlobal.add(bajar);		
	}
	
	
	/**
	 * @param prod
	 * Añade el producto prod a la tabla y al array
	 */
	public void aniadeFilaATabla(TuplaProdEnv prod){

		String configuracion;
		//Lo añadimos al array
		productosEnMesa.add(prod);
		//Actualizamos el dinero
		dinero = Math.rint((dinero + prod.getProd().getPrecio())*100)/100;
		total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
		
		if (prod.getProd() instanceof Bebida){ 
			//Si es bebida no tiene configuración
			configuracion = "No configurable";
		}else{
			configuracion = ((Plato)prod.getProd()).getExtrasMarcados(); 
		}		
		//Creamos una nueva fila y la añadimos al contenido de la tabla
		Object[] newRow={prod.getProd().getNombre(),prod.getProd().getObservaciones(),configuracion,prod.getProd().getPrecio()};
		dtm.addRow(newRow);
		
		//Refrescamos
		tablaPlatos.validate();
		tablaPlatos.repaint();
		scrollPaneTable.validate();
		scrollPaneTable.repaint();

	}
	
	
	public void generaBotonesScrollTabla(){
		//Ponemos los botones para el scroll
		//Calculamos el tamaño de los botones en funcion del de la pantalla
		scrollPaneTable.setBounds((int)((ancho/11) +20),90,(int)((ancho - ((ancho/11) + (ancho/4) + 130))),360);
		
		subirTabla = new JButton();
		subirTabla.setBorder(null);
		subirTabla.setBackground(new Color(-1118482));
		subirTabla.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/up.png"), 80, 180));
		subirTabla.setBounds((int)((((ancho/11) +30))+((ancho - ((ancho/11) + (ancho/4) + 130)))), 90, 80 ,180);
		subirTabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				scrollPaneTable.getVerticalScrollBar().setValue(scrollPaneTable.getVerticalScrollBar().getValue()-200);
			}
		
		});
		contentPaneGlobal.add(subirTabla);
		
		bajarTabla = new JButton();
		bajarTabla.setBorder(null);
		bajarTabla.setBackground(new Color(-1118482));
		bajarTabla.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/down.png"), 80, 180));
		bajarTabla.setBounds((int)((((ancho/11) +30))+((ancho - ((ancho/11) + (ancho/4) + 130)))),270, 80 ,180);
		bajarTabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				scrollPaneTable.getVerticalScrollBar().setValue(scrollPaneTable.getVerticalScrollBar().getValue()+200);
			}
		
		});
		contentPaneGlobal.add(bajarTabla);
	}
	
	
	
	public void generaPromociones(){
		ButtonGroup grupo = new ButtonGroup();
		//Creo un panel con gridlayout para los radiobuttons
		JPanel panelRadio = new JPanel();
		
		GridLayout gridRadio = new GridLayout(3,1);
		panelRadio.setLayout(gridRadio);

		//Generamos el boton de NoPromo
		JRadioButton oferta = new JRadioButton();
		panelRadio.add(oferta);
		grupo.add(oferta);
		oferta.setBackground(Color.BLUE.darker());
		oferta.setForeground(Color.WHITE);
		
		JPanel panelAux = new JPanelBordesRedondos(true);
		((JPanelBordesRedondos) panelAux).setColorPrimario(Color.BLUE);
		((JPanelBordesRedondos) panelAux).setColorSecundario(Color.BLUE);

		JLabel aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/SinPromo.png"),170,90));
		oferta.add(aux);
		panelAux.add(oferta);
		panelRadio.add(panelAux);
		
		//Generamos el boton de 2x1
		JRadioButton oferta2x1 = new JRadioButton();
		panelRadio.add(oferta2x1);
		grupo.add(oferta2x1);
		oferta2x1.setBackground(Color.BLUE.darker());
		oferta2x1.setForeground(Color.WHITE);


		panelAux = new JPanelBordesRedondos(true);
		((JPanelBordesRedondos) panelAux).setColorPrimario(Color.BLUE);
		((JPanelBordesRedondos) panelAux).setColorSecundario(Color.BLUE);

		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/2x1.png"),170,90));
		oferta2x1.add(aux);
		panelAux.add(oferta2x1);
		panelRadio.add(panelAux);
		
		//Generamos el boton de 30%
		JRadioButton oferta30 = new JRadioButton();
		panelRadio.add(oferta30);
		grupo.add(oferta30);
		oferta30.setBackground(Color.BLUE.darker());
		oferta30.setForeground(Color.WHITE);
		
		panelAux = new JPanelBordesRedondos(true);
		((JPanelBordesRedondos) panelAux).setColorPrimario(Color.BLUE);
		((JPanelBordesRedondos) panelAux).setColorSecundario(Color.BLUE);

		aux = new JLabel();
		aux.setIcon(tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/30off.png"),170,90));
		oferta30.add(aux);
		panelAux.add(oferta30);
		panelRadio.add(panelAux);

		int opcion = JOptionPane.showOptionDialog(contentPaneGlobal , panelRadio,"¿Desea aplicar alguna promoción?",JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE,null,new Object[] {"Aceptar"},"Aceptar");			
	
		if(opcion == 0){//Miramos que radiobutton esta seleccionado
			if(oferta.isSelected()){//No promo
				promocion = 0;
			}else if(oferta2x1.isSelected()){//2x1
				promocion = 1;
			}else{//30%
				promocion = 2;
			}
		}else{//No se aplica promocion
			promocion = 0 ;
		}
	}
	
	
	
	
	/**
	 * @param prod
	 * Genera un menu en una subventana emergente con las posibles configuraciones del producto prod
	 */
	public void generarMenuConfig(final TuplaProdEnv prod){
		productoATabla = prod.getProd();

		menuConfig = new JPanelBordesRedondos(true);
		menuConfig.setLayout(new GridBagLayout());
		((JPanelBordesRedondos) menuConfig).setColorPrimario(Color.BLUE);
		((JPanelBordesRedondos) menuConfig).setColorSecundario(Color.BLUE);
		
		GridBagConstraints grid = new GridBagConstraints();
		
		int i = 0;//para situarlo en el grid;
		gbc_btnPopup = new GridBagConstraints();

		if (prod.getProd() instanceof Plato){ 
			//Si es un plato tiene extras
			Plato plato = (Plato) prod.getProd();
			String extras = plato.getExtras();	
			//Guardamos en el hashMap el tipo de extra como clave y el extra seleccionado como valor
			hashExtras = new HashMap<String,String>();
			if (!extras.equals("")){
				JLabel etiqueta = new JLabel("CONFIGURACIÓN DE PLATO");
				gbc_btnPopup.gridx = 0;
				gbc_btnPopup.gridy = i;
				etiqueta.setFont(new Font(etiqueta.getFont().getName(), etiqueta.getFont().getStyle(), 25));
				etiqueta.setForeground(Color.WHITE);
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
						//Guardamos en el hashMap inicialmente el tipo de extra y vacio (porque aun no se ha seleccionado nunguno)
						hashExtras.put(categoriaExtraPadre, "");
						
						// Creamos la variedad de extras
						String[] elementosExtra = null;
						//Ponemos el tipo de extra subrayado
			        	JLabel etq = new JLabel(nombreExtra[0].toUpperCase());
						gbc_btnPopup.gridx = 0;
						gbc_btnPopup.gridy = i;
						etq.setFont(new Font(etq.getFont().getName(), etq.getFont().getStyle(), 20));
						etq.setForeground(Color.WHITE);
						menuConfig.add(etq, gbc_btnPopup);
						i++;
						
						//Creamos un radioGroup para que solo se pueda seleccionar un extra por tipo de extra
						ButtonGroup grupo = new ButtonGroup();
						
						//Creo un panel con gridlayout para los radiobuttons
						JPanel panelRadio = new JPanel();
						panelRadio.setBackground(Color.BLUE.darker());
						
						GridLayout gridRadio = new GridLayout(2,1);
						panelRadio.setLayout(gridRadio);

						elementosExtra = nombreExtra[1].split(",");
						for(int j=0; j<elementosExtra.length; j++)
						{  
							JPanel panelExtra = new JPanelBordesRedondos(true);
							((JPanelBordesRedondos) panelExtra).setColorPrimario(Color.BLUE);
							((JPanelBordesRedondos) panelExtra).setColorSecundario(Color.BLUE);
							((JPanelBordesRedondos) panelExtra).setColorContorno(Color.CYAN);

							//En cada radioButton va un extra						
							JRadioButton extra2 = new JRadioButton(elementosExtra[j]);
							panelExtra.add(extra2);
							extra2.setFont(new Font(extra2.getFont().getName(), extra2.getFont().getStyle(), 20));
							extra2.setBackground(Color.BLUE.darker());
							extra2.setForeground(Color.WHITE);
							
							//En el nombre guardamos la el tipo de extra / y el extra seleccionado,
							//para luego tener acceso al tipo de extra
							extra2.setName(categoriaExtraPadre + "/" + elementosExtra[j]);
							panelRadio.add(panelExtra);
							
							gbc_btnPopup.gridx = 0;
							gbc_btnPopup.gridy = i;
							menuConfig.add(panelRadio, gbc_btnPopup);
							i++;
							//Establecemos el oyente del RadioButton
							extra2.addMouseListener(new MouseAdapter() {
								@Override
								public void mousePressed(MouseEvent arg0) { 
									//Buscamos en el hashMap el padre(obtenido de tokenizar el nombre) y le ponemos 
									//como valor el extra seleccionado
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
		etqObs.setFont(new Font(etqObs.getFont().getName(), etqObs.getFont().getStyle(), 20));
		etqObs.setForeground(Color.WHITE);
		menuConfig.add(etqObs, gbc_btnPopup);
		i++;

		//Ponemos un campo para poder escribir observaciones
		textoObs = new JTextField(30);
		grid.gridx = 0;
		grid.gridy = i;
		menuConfig.add(textoObs,grid);
		i++;
		//Establecemos el oyente de las observaciones
		textoObs.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) { 
				//Sacamos un teclado alfanumerico
				JPanel teclado = new TecladoAlfaNumerico();
				JFrame marco = new JFrame();
				JOptionPane.showOptionDialog(marco, teclado,"Observaciones", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
				//Guardamos las observaciones
				textoObs.setText(((TecladoAlfaNumerico)teclado).getObs());
				}
		});
		        
		JFrame marco = new JFrame();
		int result = JOptionPane.showOptionDialog(marco, menuConfig, null, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
	
		if(result == 0 ){//aceptar
			//Obligamos a que rellene todos los campos de los extras
			//recorremos el has y si en alguno pone "", falta por rellenar
			boolean faltaCampo = false;
			Iterator<Entry<String, String>> itCampos = hashExtras.entrySet().iterator();
			while(itCampos.hasNext() && !faltaCampo){
				if(itCampos.next().getValue().equals("")){
					faltaCampo = true;
				}
			}
			if (!faltaCampo){
				//Si todo esta correcto 
				productoATabla.setObservaciones(textoObs.getText());
				productoATabla.setIdUnico(idsUnicos);
				idsUnicos ++;
				
				if (productoATabla instanceof Plato){
					//Si es un plato
					Plato platoATabla = (Plato) productoATabla;
					Iterator<String> itExtras = hashExtras.values().iterator();//Recorre los extras
					String extrasConcat = "";
					//Recorremos los extras seleccionados que estan en el hashMap y los vamos concatenando en un 
					//String separados por comas
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
				//Actualizamos los auxliares
				deshacer.setEnabled(true);
				auxiliarDeshacer.add(new AuxDeshacerRehacer(true, productoATabla));
				//Añadimos el producto creado a la tabla y al array
				aniadeFilaATabla(new TuplaProdEnv(productoATabla, false));
				//Al añadir reseteamos el rehacer
				auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
				contDeshacer = 0;
				rehacer.setEnabled(false);
			}else{
				//mostramos mensaje para que rellene todos los campos bien
				JOptionPane.showOptionDialog(contentPaneGlobal ,"<html>Debes rellenar todos los campos de configuración de plato.<br/>Plato NO añadido<html>",null,JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,tamanioImagen(new ImageIcon("Imagenes/BotonesInterfazPlatos/warning.png"), 50, 50),new Object[] {"Aceptar"},"Aceptar");
			}
		}else {
			//Reseteo el hashMap
			hashExtras = new HashMap<String,String>();
		}
	
	
	}
	/**
	 * 
	 * @param imag
	 * @param ancho
	 * @param alto
	 * @return devuelve una una Imagen con el ancho y el alto establecido en los parametros de entrada
	 */
	public static ImageIcon tamanioImagen(ImageIcon imag, int ancho, int alto){
		//nuevos ancho y alto: para que conserve la proporcion pasamos -1
		ImageIcon imagen = new ImageIcon(imag.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_DEFAULT));
		return imagen;		
	}
	
	/**
	 * 
	 * @param lista
	 * @param pr
	 * @return devuelve la posicion del producto pr en el ArrayList lista
	 */
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
	
	/**
	 * 
	 * @return recorre los productos en la mesa y calcula el dinero total
	 */
	public double calculaDineroTotal(){
		double dineroTotalEnMesa = 0;
		for(int i = 0; i < productosEnMesa.size();i++){
			dineroTotalEnMesa = Math.rint((dineroTotalEnMesa + productosEnMesa.get(i).getProd().getPrecio())*100)/100;
		}
		return dineroTotalEnMesa;
	}
	
	/**
	 * 
	 * @return devuelve el numero de categorias existentes en un restaurante para graficamente poder establecer su posicion
	 */
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
	
	 /**
	  * Borra las filas de la tabla, los productos del array y pone el dinero a cero
	  */
	 public void reseteaTablaYPrecio(){
		productosEnMesa = new ArrayList<TuplaProdEnv>();
		dinero = 0;
		total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
		
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
		
		boolean enc = false;
		Iterator<Mesa> iteratorMesas = getRestaurante().getIteratorMesas();
		while(iteratorMesas.hasNext() && !enc)
		{
			Mesa mesa = iteratorMesas.next();
			if (mesa.getIdMesa().equals(idMesa)){
				mesa.setProductosEnMesa(productosEnMesa);
				mesa.actualizarDineroTotal();
				idCam = mesa.getIdCamarero();
				mesa.cerrarMesa();
				enc = true;
				}
		}
		
		getRestaurante().actualizaMesaEstaVisitadaCobrarLLegadaExterna(idMesa);
		
		
		OperacionesSocketsSinBD operacionCobrar = new OperacionesSocketsSinBD();
		operacionCobrar.operacionCobrarMesa(idMesa);
				
		VentanaMesas ventanaMesa = new VentanaMesas(getRestaurante(),idCam);
		ventanaMesa.setVisible(true);
		dispose();
		getRestaurante().setIterfazPlatos(null);
		
	 }
	 
	 /**
	  * 
	  * @return si en la tabla hay platos con el campo enviado a false
	  */
	 public boolean hayPlatosSinEnviar(){
		int i = tablaPlatos.getRowCount()-1;
		while(i >= 0){
			if (productosEnMesa.get(i).isEnviado() == false){
				return true;
			}
			i--;
		}
		return false;
	 }
	 
	 /**
	  * @return busca los platos en el array que aun no han sido enviados y los devuelve en un ArrayList
	  */
	 public ArrayList<Producto> platosAEnviar(){
		ArrayList<Producto> tmp = new ArrayList<Producto>();
		int i = 0;
		while(i < productosEnMesa.size()){
			if (!productosEnMesa.get(i).isEnviado()){
				tmp.add(productosEnMesa.get(i).getProd());
			}
			i++;
		}
		return tmp;
	 }
	 
	 /**
	  * 
	  * @return busca los platos en el array que han sido enviados y los devuelve en un ArrayList
	  */
	 public ArrayList<Producto> platosACobrar(){
		 ArrayList<Producto> tmp = new ArrayList<Producto>();
		int i = 0;
		dineroAcobrar = 0;
		while(i < productosEnMesa.size()){
			if (productosEnMesa.get(i).isEnviado()){
				dineroAcobrar = Math.rint((dineroAcobrar + productosEnMesa.get(i).getProd().getPrecio())*100)/100;
				tmp.add(productosEnMesa.get(i).getProd());
			}
			i++;
		}
		return tmp;
	 }

	 
	 /**
	  * Borra los platos de la tabla y del array y los vuelve a cargar. Actualiza el dinero
	  * Se usa para mantener actualizada la mesa
	  */
	 public void refrescarTabla(){
		//borro las filas de la tabla
			int j = tablaPlatos.getRowCount();
			while(j > 0){
				dtm.removeRow(j-1);
				j--;
			}

			//Cargo de nuevo los platos en la tabla y en el array
			Iterator<Mesa> iteratorMesas = unRestaurante.getIteratorMesas();
			String nombre,configuracion,observaciones;
			double precio;
			dinero = 0;
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
						total.setText("Mesa: " + idMesa +"   Total: " + dinero + " €");
						nombre = productosEnMesa.get(i).getProd().getNombre();
						observaciones = productosEnMesa.get(i).getProd().getObservaciones();
						precio = productosEnMesa.get(i).getProd().getPrecio();
						
						Object[] newRow={nombre,observaciones,configuracion,precio};
						dtm.addRow(newRow);
					}
				}
			}
			//Refrescamos
			tablaPlatos.validate();
			tablaPlatos.repaint();
	 }
	 
	 public void mostrarAvisoYcerrar(String idMesa) {
		 if(this.idMesa.equals(idMesa)){
			 JFrame marco = new JFrame();
			 JOptionPane.showMessageDialog(marco, "Esta mesa ha sido cobrada desde otro TPV y se va ha cerrar.");
			 dispose();
			 getRestaurante().setIterfazPlatos(null);
			 VentanaMesas ventanaMesa = new VentanaMesas(getRestaurante(),idCam);
			 ventanaMesa.setVisible(true);
		 }
	 }
	 
	 /**
	  * Recorre los platos ya enviados y actualiza la cantidad de los que coincidan con el id de plato
	  * @param plato
	  * @param cant
	  */
	 public void actualizaEnviados(Plato plato,int cant) {
		 for(int i = 0; i< productosEnMesa.size();i++){
			 if(productosEnMesa.get(i).isEnviado() && (productosEnMesa.get(i).getProd() instanceof Plato)
					 && (productosEnMesa.get(i).getProd().getId().equals(plato.getId()))){
				 Plato pl = (Plato) productosEnMesa.get(i).getProd();
				 pl.setCantiadPedido(cant);
			 }
		 }
			
	}

	 /**
	  * Busca la cantidad de productos producto en aEnviar
	  * @param producto
	  * @param aEnviar
	  * @return
	  */
	 public int buscaCantidadNoEnviados(Producto producto,ArrayList<Producto> aEnviar) {
			int result = 0;
			for(int i = 0; i< aEnviar.size();i++){
				if (producto.getId().equals(aEnviar.get(i).getId())){
					result++;
				}
			}
			return result;
	}

	/**
	 * Busca el maximo de cantidadPedido de plato en productos en mesa enviados
	 * @param plato
	 * @return
	 */
	private int buscaMaxEnviados(Plato plato) {
		int max = 0;
		for(int i = 0; i< productosEnMesa.size();i++){
			 if((productosEnMesa.get(i).getProd() instanceof Plato) 
					 && (productosEnMesa.get(i).isEnviado()) 
					 && plato.getId().equals(productosEnMesa.get(i).getProd().getId())){
				 if(((Plato) productosEnMesa.get(i).getProd()).getCantiadPedido() >= max ){
					 max = ((Plato) productosEnMesa.get(i).getProd()).getCantiadPedido();
				 }
				 ((Plato) productosEnMesa.get(i).getProd()).setCantiadPedido(plato.getCantiadPedido());
			 }
		 }
		return max;
	}

 
	/**
	 * Busca en productosEnMesa el id, idunico y que no este enviado y le pone cantidad a cant
	 * @param idUnico
	 * @param i
	 * @param cant
	 */
	private void actualizaEnProductosEnMesa(int idUnico ,String id, int cant) {
		boolean enc  = false;
		int i = 0;
		while(i < productosEnMesa.size() && !enc){
			if(productosEnMesa.get(i).getProd().getIdUnico() == idUnico
					&& productosEnMesa.get(i).getProd().getId().equals(id)
					&& !productosEnMesa.get(i).isEnviado()){
				enc = true;
				((Plato)productosEnMesa.get(i).getProd()).setCantiadPedido(cant);
			}
			i++;
		}
		
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
			
			//Espacio
			JButton botonEspacio = new JButton("                    ");
			botonEspacio.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					texto.setText(texto.getText()+" ");
				}
			});
			panelEsp.add(botonEspacio);
			
			//Espacio2
			JButton botonEspacio2 = new JButton("");
			botonEspacio2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					texto.setText(texto.getText()+" ");
				}
			});
			panelEsp.add(botonEspacio2);
			
			//Espacio3
			JButton botonEspacio3 = new JButton("");
			botonEspacio3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					texto.setText(texto.getText()+" ");
				}
			});
			panelEsp.add(botonEspacio3);
			
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