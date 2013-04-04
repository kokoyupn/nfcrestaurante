package interfaz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import tpv.Bebida;
import tpv.Mesa;
import tpv.Plato;
import tpv.Producto;
import tpv.Restaurante;

public class InterfazPlatos extends JFrame {

	private JPanel contentPaneGlobal, panelPlatos ;
	private JTable tablaPlatos;
	static GridBagConstraints gbc_btnNewButton2,gbc_btnBotones,gbc_btnPopup;
	static JScrollPane scrollPane, scrollPanePl,scrollPaneBotones,scrollPaneTable;
	private ArrayList<String> categorias ;
	private ArrayList<Producto> eliminados ;
//	private HashMap<String,Producto> productos;//La clave es el id del producto
	private Restaurante unRestaurante ;
//	private HashMap<String,Mesa> mesasRestaurante;
	private DefaultTableModel dtm;
	private String idMesa = "22"; //nos vendra de VentanaMesa
	private Producto productoATabla;
	private JPopupMenu popup;
	String categoriaExtraPadre ;
	private HashMap<String,String> hashExtras; //la clave es el tipo de extra

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					InterfazPlatos frame = new InterfazPlatos();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
	
	public InterfazPlatos(String idMesa, Restaurante unRestaurante) {
		this.idMesa = idMesa;
		this.unRestaurante = unRestaurante;
		InterfazPlatos frame = new InterfazPlatos();
		frame.setVisible(true);
	}
	

	/**
	 * Create the frame.
	 */
	public InterfazPlatos() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		contentPaneGlobal = new JPanel();
		contentPaneGlobal.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPaneGlobal);
		contentPaneGlobal.setLayout(null);
		
		//Panel categorias
		JPanel panelCategorias = new JPanel();
		scrollPane = new JScrollPane(panelCategorias);
		scrollPane.setBounds(500, 11, 824, 237);
		contentPaneGlobal.add(scrollPane);
		GridBagLayout gbl_panelCategorias = new GridBagLayout();
		panelCategorias.setLayout(gbl_panelCategorias);
		
		//Panel platos
		panelPlatos = new JPanel();
		scrollPanePl = new JScrollPane(panelPlatos);
		scrollPanePl.setBounds(500, 259, 824, 442);
		contentPaneGlobal.add(scrollPanePl);
		GridBagLayout gbl_panelPlatos = new GridBagLayout();
		panelPlatos.setLayout(gbl_panelPlatos);
		
		//Panel botones
		JPanel panelBotones = new JPanel();
		panelBotones.setBounds(11, 11, 140, 689);
		contentPaneGlobal.add(panelBotones);
		GridLayout gbl_panelBotones = new GridLayout(5,1);
		panelBotones.setLayout(gbl_panelBotones);

////////////////////////////ELIMINAR PLATO/////////////////////
		JButton eliminar = new JButton("Eliminar Plato");
		panelBotones.add(eliminar);
		eliminar.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent arg0){
						int row = -1;
						row = tablaPlatos.getSelectedRow();
						if (row == -1){
							//TODO mensaje para que seleccione linea de la tabla y luego pulse el boton
						}else{
							//Creamos el producto y lo metemos en eliminados
							copiaSeguridad(row);
							dtm.removeRow(row);
						}
						}	
					});
///////////////////////////////////////		
		JButton cobrar = new JButton("Cobrar mesa");
		panelBotones.add(cobrar);
		
		JButton enviar = new JButton("Enviar a cocina");
		panelBotones.add(enviar);
		
		JButton promociones = new JButton("<html>" + "Aplicar" + "<br>" + "promociones" + "</html>");
		panelBotones.add(promociones);

		JButton aceptar = new JButton("Aceptar");
		aceptar.setBackground(Color.green);
		panelBotones.add(aceptar);
		
////////////////////////////RESTAURANTE/////////////////////		
		//unRestaurante = new Restaurante();
		eliminados = new ArrayList<Producto>();
		
///////////////TABLA DE PLATOS////////////////////
		//creamos las columnas
		Object[][] datos = {};
		String[] columnNames = {"Nombre","Observaciones","Configuración","Precio"};
		dtm= new DefaultTableModel(datos,columnNames);
		//Cargamos los platos de la mesa
		Iterator<Mesa> iteratorMesas = unRestaurante.getIteratorMesas();
		String nombre,configuracion,observaciones;
		double precio;
		while(iteratorMesas.hasNext())
		{
			Mesa mesa = iteratorMesas.next();
			if (mesa.getIdMesa().equals(idMesa)){ 
				//mesa encontrada, cargamos los platos
				ArrayList<Producto> platos = mesa.getProductosEnMesa();
				for (int i = 0; i < platos.size(); i++){
					if (platos.get(i) instanceof Bebida){ //No tiene configuración
						configuracion = "No configurable";
					}else{
						configuracion = ((Plato)platos.get(i)).getExtrasMarcados(); 
					}
					nombre = platos.get(i).getNombre();
					observaciones = platos.get(i).getObservaciones();
					precio = platos.get(i).getPrecio();
					
					Object[] newRow={nombre,observaciones,configuracion,precio};
					dtm.addRow(newRow);
				}
			}
		}

		tablaPlatos = new JTable(dtm);
		scrollPaneTable = new JScrollPane(tablaPlatos);
		scrollPaneTable.setBounds(162, 11, 328, 400);
		contentPaneGlobal.add(scrollPaneTable);
		GridBagLayout gbl_panelTabla = new GridBagLayout();
		tablaPlatos.setLayout(gbl_panelTabla);	
		
		
//		private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {                                     
//			 
//		    String ele = txtElemento.getText();
//		 
//		    for (int i = 0; i < tbComponentes.getRowCount(); i++) {
//		           if (tbComponentes.getValueAt(i, 1).equals(ele)) {                                           
//		                  tbComponentes.changeSelection(i, 1, false, false);
//		                  break;
//		           }
//		    }
//		}
		
		
		
///////////////BOTON DESHACER////////////////////		
	JButton deshacer = new JButton("Eliminar último plato añadido");
	deshacer.setBounds(162,422,328,30);
	deshacer.addMouseListener(new MouseAdapter() {//eliminar el ultimo registro de la tabla
		@Override
		public void mousePressed(MouseEvent arg0){
			if (dtm.getRowCount() > 0){
				copiaSeguridad(dtm.getRowCount()-1);
				dtm.removeRow(dtm.getRowCount()-1);
			}
		}
	});
	contentPaneGlobal.add(deshacer);		

///////////////BOTON REHACER////////////////////		
	JButton rehacer = new JButton("Recuperar último plato borrado");
	rehacer.setBounds(162,452,328,30);
	rehacer.addMouseListener(new MouseAdapter() {//eliminar el ultimo registro de la tabla
	@Override
	public void mousePressed(MouseEvent arg0){
		if(eliminados.size() > 0){
			aniadeFilaATabla(eliminados.get(eliminados.size()-1));
			eliminados.remove(eliminados.size()-1);
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
					String foto = prod.getFoto();
				
					JButton btnNewButton = new JButton();
					btnNewButton.setPreferredSize(new Dimension(140, 100));
					//btnNewButton.setBackground();
					btnNewButton.setIcon(tamanioImagen(new ImageIcon("Imagenes/"+ foto + ".jpg"), 140, 100));
					btnNewButton.setName(categoria);
					btnNewButton.addMouseListener(new MouseAdapter() {
					
					@Override
					public void mousePressed(MouseEvent arg0) {
						panelPlatos.removeAll();
						String catPulsada = arg0.getComponent().getName();
						Iterator<Producto> iteratorProductosHijos = unRestaurante.getIteratorProductos();
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
										btnNewButton2.setPreferredSize(new Dimension(140, 100));
										btnNewButton2.setName(nombre);
										btnNewButton2.setIcon(tamanioImagen(new ImageIcon("Imagenes/"+ foto + ".jpg"), 140, 100));
										btnNewButton2.addMouseListener(new MouseAdapter(){
											@Override
											public void mousePressed(MouseEvent arg0){
												generarPopup(prod);		
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
					//el segundo parametro sobra, si ponemos como nombre al boton el nombre del plato.
					//Luego lo recuperamos en PlatoCelda con boton.getName();
					PlatoCelda celda = new PlatoCelda(btnNewButton, categoria);
					panelCategorias.add(celda, gbc_btnNewButton);	
					i++;k++;
				}	
			}				
	}
	
	public void aniadeFilaATabla(Producto prod){
		String configuracion;
		if (prod instanceof Bebida){ //No tiene configuración
			configuracion = "No configurable";
		}else{
			configuracion = ((Plato)prod).getExtrasMarcados(); 
		}		
		Object[] newRow={prod.getNombre(),prod.getObservaciones(),configuracion,prod.getPrecio()};
		dtm.addRow(newRow);
		
		//TODO añadir tambien a la base de datos y a Restaurante 
		
		//Refrescamos
		tablaPlatos.validate();
		tablaPlatos.repaint();
		scrollPaneTable.validate();
		scrollPaneTable.repaint();
	}
	
	public void generarPopup(final Producto prod){
		productoATabla = prod;
		popup = new JPopupMenu();	
		
		JPanel panelPopup = new JPanel();
		GridBagLayout gbl_panelPopup = new GridBagLayout();
		panelPopup.setLayout(gbl_panelPopup);
		
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
				panelPopup.add(etiqueta, gbc_btnPopup);
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
						panelPopup.add(etq, gbc_btnPopup);
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
							panelPopup.add(panelRadio, gbc_btnPopup);
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
	        	
	        }
		}	
		//Comun para plato y bebida
		
		JLabel etqObs = new JLabel("OBSERVACIONES");
		gbc_btnPopup.gridx = 0;
		gbc_btnPopup.gridy = i;
		panelPopup.add(etqObs, gbc_btnPopup);
		i++;
		
		popup.add(panelPopup);
		
		// Lo meto directamente al popup para que el texto tenga dimension
		final JTextField texto = new JTextField();
		popup.add(texto);
	    
	    JPanel panelAceptarCancelar = new JPanel();
	    GridLayout gbl_panelBotones = new GridLayout(1,2);
	    panelAceptarCancelar.setLayout(gbl_panelBotones);
		JButton aceptar = new JButton("ACEPTAR");
		panelAceptarCancelar.add(aceptar);
		JButton cancelar = new JButton("CANCELAR");
		panelAceptarCancelar.add(cancelar);
		popup.add(panelAceptarCancelar);
       
        popup.show(contentPaneGlobal, contentPaneGlobal.getWidth()/2, contentPaneGlobal.getHeight()/3);
        
        aceptar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent ag0){
				//TODO si todo bien configurado añade si no muestra mensaje de error

				//prod.setObservaciones(texto.getText());
				productoATabla.setObservaciones(texto.getText());
				
				if (productoATabla instanceof Plato){
					Plato platoATabla = (Plato) productoATabla;
			
					//Si el formato de extras marcados no pone carne, salsa etc:
					///////Paso de los extras en hashExtras a PlatoATabla en el formato correspondiente
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
					///////FIN Paso de los extras en hashExtras a PlatoATabla en el formato correspondiente
					
					
					productoATabla = (Producto) platoATabla;
				}
				
				aniadeFilaATabla(productoATabla);
		        popup.setVisible(false);
		        popup.removeNotify();
			}
		});
        
        cancelar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent ag0){
		        popup.setVisible(false);
		        popup.removeNotify();
			}
		});
		
		
	}
	
	public void copiaSeguridad(int row){
		//TODO al hacer copia de seguridad perdemos datos del producto METER UN BOTON ATRAS,ACEPTAR
		String nombre, observaciones, configuracion;
		double precio;
		nombre = (String) dtm.getValueAt(row, 0);
		observaciones = (String) dtm.getValueAt(row, 1);
		configuracion = (String) dtm.getValueAt(row, 2);
		precio = (double) dtm.getValueAt(row, 3);

		if (configuracion != "No configurable"){// es un plato y tiene extras marcados
			Plato plato = new Plato("", "", "", nombre, "", "", precio, observaciones, "");
			plato.setExtrasMarcados(configuracion);
			eliminados.add(plato);
		}else {
			Bebida bebida = new Bebida("", "", "", nombre, "", "", precio, observaciones);
			eliminados.add(bebida);
		}
	}
	
	public static ImageIcon tamanioImagen(ImageIcon imag, int ancho, int alto){
		//nuevos ancho y alto: para que conserve la proporcion pasamos -1
		ImageIcon imagen = new ImageIcon(imag.getImage().getScaledInstance(ancho, alto, java.awt.Image.SCALE_DEFAULT));
		return imagen;		
	}
	
}
