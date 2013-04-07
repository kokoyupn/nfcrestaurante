package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
import javax.swing.table.DefaultTableModel;

import tpv.AuxDeshacerRehacer;
import tpv.Bebida;
import tpv.Mesa;
import tpv.Plato;
import tpv.Producto;
import tpv.Restaurante;

public class InterfazPlatos extends JFrame {

	private JPanel contentPaneGlobal, panelPlatos ;
	private JTable tablaPlatos;
	private JButton rehacer, deshacer;
	static GridBagConstraints gbc_btnNewButton2,gbc_btnBotones,gbc_btnPopup;
	static JScrollPane scrollPane, scrollPanePl,scrollPaneBotones,scrollPaneTable;
	private ArrayList<String> categorias;
	private ArrayList<AuxDeshacerRehacer>  auxiliarDeshacer;
	private ArrayList<AuxDeshacerRehacer>  auxiliarRehacer;
	private ArrayList<Producto> productosEnMesa;
	private Restaurante unRestaurante ;
	private TablaNoEditable dtm;
	private String idMesa,precioAux,obsAux,extrasAux;
	private Producto productoATabla;
	private JPanel menuConfig;
	private String categoriaExtraPadre ;
	private HashMap<String,String> hashExtras; //la clave es el tipo de extra
	private int contDeshacer,idsUnicos=0; 
	private JTextField textoObs;
	private boolean esExtras,esObs;
	private JLabel total;
	private double dinero;
	
	/**
	 * Create the frame.
	 */
	public InterfazPlatos(final String idMesa, Restaurante unRestaurante) {
		this.idMesa = idMesa;
		this.unRestaurante = unRestaurante;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);//Eliminamos los bordes de la ventana.
		contentPaneGlobal = new JPanel();
		contentPaneGlobal.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPaneGlobal);
		contentPaneGlobal.setLayout(null);
		
		//Panel categorias
		JPanel panelCategorias = new JPanel();
		scrollPane = new JScrollPane(panelCategorias);
		scrollPane.setBounds(500, 11, 824, 307);
		contentPaneGlobal.add(scrollPane);
		GridBagLayout gbl_panelCategorias = new GridBagLayout();
		panelCategorias.setLayout(gbl_panelCategorias);
		
		//Panel platos
		panelPlatos = new JPanel();
		scrollPanePl = new JScrollPane(panelPlatos);
		scrollPanePl.setBounds(500, 329, 824, 415);
		contentPaneGlobal.add(scrollPanePl);
		GridBagLayout gbl_panelPlatos = new GridBagLayout();
		panelPlatos.setLayout(gbl_panelPlatos);
		
		//Panel botones
		JPanel panelBotones = new JPanel();
		panelBotones.setBounds(11, 504, 479, 240);
		contentPaneGlobal.add(panelBotones);
		GridLayout gbl_panelBotones = new GridLayout(3,3);
		panelBotones.setLayout(gbl_panelBotones);
		
////////////////////////////INICIALIZACIONES/////////////////////		
		auxiliarDeshacer = new ArrayList<AuxDeshacerRehacer>();
		auxiliarRehacer = new ArrayList<AuxDeshacerRehacer>();
		productosEnMesa = new ArrayList<Producto>();
		contDeshacer = 0;
		dinero = 0;
		esExtras = false;
		esObs = false;

///////////////GRUPO TOTAL PRECIO////////////////////	
		
		dinero = calculaDineroTotal();
		total = new JLabel("Total: " + dinero + " euros");
		total.setBounds(114,22,317,40);
		contentPaneGlobal.add(total);
		total.setFont(new Font(total.getFont().getName(), total.getFont().getStyle(), 30));


		
////////////////////////////ELIMINAR PLATO/////////////////////
		JButton eliminar = new JButton("Eliminar Plato");
		panelBotones.add(eliminar);
		eliminar.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent arg0){
						int row = -1;
						row = tablaPlatos.getSelectedRow();
						if (row == -1){
							JOptionPane.showMessageDialog(
										contentPaneGlobal,
									   "Debes seleccionar una línea de la tabla y después pulsar el botón eliminar"); 
						}else{
							auxiliarDeshacer.add(new AuxDeshacerRehacer(false, productosEnMesa.get(row)));
							deshacer.setEnabled(true);
							dinero = Math.rint((dinero - productosEnMesa.get(row).getPrecio())*100)/100;
							total.setText("Total: " + dinero + " euros");
							productosEnMesa.remove(row);
							dtm.removeRow(row);
						}
						}	
					});
////////////////////////////BOTON COBRAR/////////////////////		
		JButton cobrar = new JButton("Cobrar mesa");
		panelBotones.add(cobrar);
		cobrar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				int seleccion = JOptionPane.showOptionDialog(contentPaneGlobal ,"¿Seguro?",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,null,new Object[] {"Aceptar", "Cancelar"},"Cancelar");
				if (seleccion == 0){//aceptar
					//TODO cobrar
				}
			}
		});

////////////////////////////BOTON ENVIAR A COCINA/////////////////////	
		JButton enviar = new JButton("Enviar a cocina");
		panelBotones.add(enviar);
		enviar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				int seleccion = JOptionPane.showOptionDialog(contentPaneGlobal ,"¿Seguro?",null,JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE,null,new Object[] {"Aceptar", "Cancelar"},"Cancelar");
				if (seleccion == 0){//aceptar
					//TODO cobrar
				}
			}
		});

////////////////////////////BOTON PROMOCIONES/////////////////////	
		JButton promociones = new JButton("<html>" + "Aplicar" + "<br>" + "promociones" + "</html>");
		panelBotones.add(promociones);

		
////////////////////////////BOTON ACEPTAR/////////////////////		
		
		JButton aceptar = new JButton("Aceptar");
		aceptar.setBackground(Color.green);
		panelBotones.add(aceptar);
		aceptar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				//TODO añadir a la base de datos y a Restaurante 
				String idCam = "";
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
					if (productosEnMesa.get(i) instanceof Bebida){ //No tiene configuración
						configuracion = "No configurable";
					}else{
						configuracion = ((Plato)productosEnMesa.get(i)).getExtrasMarcados(); 
					}
					dinero = Math.rint((dinero + productosEnMesa.get(i).getPrecio())*100)/100;
					total.setText("Total: " + dinero + " euros");
					nombre = productosEnMesa.get(i).getNombre();
					observaciones = productosEnMesa.get(i).getObservaciones();
					precio = productosEnMesa.get(i).getPrecio();
					
					Object[] newRow={nombre,observaciones,configuracion,precio};
					dtm.addRow(newRow);
				}
			}
		}

		tablaPlatos = new JTable(dtm);
		scrollPaneTable = new JScrollPane(tablaPlatos);
		scrollPaneTable.setBounds(11, 92, 479, 400);
		contentPaneGlobal.add(scrollPaneTable);
		GridBagLayout gbl_panelTabla = new GridBagLayout();
		tablaPlatos.setLayout(gbl_panelTabla);	
		tablaPlatos.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0){
				//si la columna es precio (3) saco el teclado numérico
				JTable tablaAux = (JTable)arg0.getComponent();
				int comlumnaPinchada = tablaAux.getSelectedColumn();
				String dato=String.valueOf(tablaAux.getValueAt(tablaAux.getSelectedRow(),tablaAux.getSelectedColumn()));
				if(comlumnaPinchada == 3){ // Precio
					precioAux = dato;
					JPanel tecladoNum = new TecladoNumerico();
					JFrame marco = new JFrame();
					int res = JOptionPane.showOptionDialog(marco, tecladoNum,"Precio", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
					if (res == 0){//aceptar
						tablaAux.setValueAt(((TecladoNumerico)tecladoNum).getPrecio(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
						productosEnMesa.get(tablaAux.getSelectedRow()).setPrecio(((TecladoNumerico)tecladoNum).getPrecio());
						dinero = calculaDineroTotal();
						total.setText("Total: " + dinero + " euros");
					}
				}else if (comlumnaPinchada == 2){ //Extras
						//miro que no sea bebida
						if (!productosEnMesa.get(tablaAux.getSelectedRow()).getCategoria().equals("Bebidas")){
							extrasAux = dato;
							esExtras = true;
							esObs = false;
							JPanel tecladoAlfaNum = new TecladoAlfaNumerico();
							JFrame marco = new JFrame();
							int res = JOptionPane.showOptionDialog(marco, tecladoAlfaNum,"Editar configuración de plato", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
							if (res == 0){//aceptar
								tablaAux.setValueAt(((TecladoAlfaNumerico)tecladoAlfaNum).getObs(),tablaAux.getSelectedRow(),tablaAux.getSelectedColumn());
								((Plato)productosEnMesa.get(tablaAux.getSelectedRow())).setExtrasMarcados(((TecladoAlfaNumerico)tecladoAlfaNum).getObs());
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
							productosEnMesa.get(tablaAux.getSelectedRow()).setObservaciones(((TecladoAlfaNumerico)tecladoAlfaNum).getObs());
						}
					}
				
			}			
		});
		
		
///////////////BOTON DESHACER////////////////////		
	deshacer = new JButton();
	deshacer.setIcon(tamanioImagen(new ImageIcon("Imagenes/Undo.png"), 70, 70));
	deshacer.setEnabled(false);
	deshacer.setBounds(11,11,70,70);
	deshacer.addMouseListener(new MouseAdapter() {//eliminar el ultimo registro de la tabla
		@Override
		public void mousePressed(MouseEvent arg0){

			if (auxiliarDeshacer.size()>0){
				if(auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getAccion() == false){//la ultima accion ha sido eliminar asi que añadimos
					aniadeFilaATabla(auxiliarDeshacer.get(auxiliarDeshacer.size()-1).getProd());
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
	rehacer.setIcon(tamanioImagen(new ImageIcon("Imagenes/Redo.png"), 70, 70));
	rehacer.setBounds(420,11,70,70);
	rehacer.setEnabled(false);
	rehacer.addMouseListener(new MouseAdapter() {//eliminar el ultimo registro de la tabla
	@Override
	public void mousePressed(MouseEvent arg0){
		
		if(contDeshacer > 0){
			if (auxiliarRehacer.size()>0){
				if(auxiliarRehacer.get(auxiliarRehacer.size()-1).getAccion() == true){//tenemos que añadir
					aniadeFilaATabla(auxiliarRehacer.get(auxiliarRehacer.size()-1).getProd());
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
		int j = 0;
		int i = 0;
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		//leemos las categorias			
			categorias = new ArrayList<String>();
			Iterator<Producto> iteratorProductos = unRestaurante.getIteratorProductos();
			int k = 0; //para delimitar el numero de platos por fila
			while(iteratorProductos.hasNext()){
				Producto prod = iteratorProductos.next();
				String categoria = prod.getCategoria();
				if (!categorias.contains(categoria)){
					categorias.add(categoria);
					String foto = buscaFotoConCategoria(categoria);
				
					JButton btnNewButton = new JButton();
					btnNewButton.setPreferredSize(new Dimension(160, 120));
					btnNewButton.setIcon(tamanioImagen(new ImageIcon("Imagenes/"+ foto + ".jpg"), 160, 120));
					btnNewButton.setName(categoria);
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
										
										final JButton btnNewButton2 = new JButton();
										btnNewButton2.setPreferredSize(new Dimension(160, 120));
										btnNewButton2.setName(nombre);
										btnNewButton2.setIcon(tamanioImagen(new ImageIcon("Imagenes/"+ foto + ".jpg"), 160, 120));
										btnNewButton2.addMouseListener(new MouseAdapter(){
											@Override
											public void mousePressed(MouseEvent arg0){
												generarMenuConfig(prod);		
											}
											
										});//fin listener plato
										if ((k != 0) && (k % 4) == 0) 
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
					
					if ((k != 0) && (k % 4) == 0) 
					{j++;i=0;}
					gbc_btnNewButton.gridx = i;
					gbc_btnNewButton.gridy = j;
					PlatoCelda celda = new PlatoCelda(btnNewButton, categoria);
					panelCategorias.add(celda, gbc_btnNewButton);	
					i++;k++;
				}	
			}				
	}
	
	public void aniadeFilaATabla(Producto prod){
		String configuracion;
		
		productosEnMesa.add(prod);
		dinero = Math.rint((dinero + prod.getPrecio())*100)/100;
		total.setText("Total: " + dinero + " euros");
		
		if (prod instanceof Bebida){ //No tiene configuración
			configuracion = "No configurable";
		}else{
			configuracion = ((Plato)prod).getExtrasMarcados(); 
		}		
		Object[] newRow={prod.getNombre(),prod.getObservaciones(),configuracion,prod.getPrecio()};
		dtm.addRow(newRow);
		
		//Refrescamos
		tablaPlatos.validate();
		tablaPlatos.repaint();
		scrollPaneTable.validate();
		scrollPaneTable.repaint();
	}
	
	public void generarMenuConfig(final Producto prod){
		productoATabla = prod;
		menuConfig = new JPanel(new GridBagLayout());
		
		GridBagConstraints grid = new GridBagConstraints();
		
		int i = 0;//para situarlo en el grid;
		gbc_btnPopup = new GridBagConstraints();

		if (prod instanceof Plato){ 
			Plato plato = (Plato) prod;
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
		int result = JOptionPane.showOptionDialog(marco, menuConfig,"Configuración", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE , null, new String[]{"Aceptar","Cancelar"}, "Cancelar");
	
		if(result == 0 ){//aceptar
			//TODO si todo bien configurado añade si no muestra mensaje de error

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
			aniadeFilaATabla(productoATabla);
		}
	
	
	}
	
	public static ImageIcon tamanioImagen(ImageIcon imag, int ancho, int alto){
		//nuevos ancho y alto: para que conserve la proporcion pasamos -1
		ImageIcon imagen = new ImageIcon(imag.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_DEFAULT));
		return imagen;		
	}
	
	public int buscaPosicion(ArrayList<Producto> lista, Producto pr){
		Boolean enc = false;
		int i = 0;
		while (!enc && i < lista.size()){
			if(pr.equals(lista.get(i))){
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
			dineroTotalEnMesa = Math.rint((dineroTotalEnMesa + productosEnMesa.get(i).getPrecio())*100)/100;
		}
		return dineroTotalEnMesa;
	}
	
	 private String buscaFotoConCategoria(String categoria){
		 //buscamos en productos en unRestaurante
		 Iterator<Producto> iteratorProductos = unRestaurante.getIteratorProductos();
		 boolean enc = false;
		 String foto = "fnd_fh";
		 while(iteratorProductos.hasNext() && !enc){
			 Producto p = iteratorProductos.next();
			 if (p.getCategoria().equals(categoria) && !p.getFoto().equals("fnd_fh")){
				 foto = p.getFoto();
				 enc = true;
			 }
		 }
		 return foto;
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
			botonBorrarUno.setIcon(tamanioImagen(new ImageIcon("Imagenes/Undo.png"), 30, 30));
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
		botonBorrar.setIcon(tamanioImagen(new ImageIcon("Imagenes/Undo.png"), 30, 30));
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
	
}
