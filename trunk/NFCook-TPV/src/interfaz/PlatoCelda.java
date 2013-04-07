package interfaz;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class PlatoCelda extends JPanel{
	
	private JLabel lblNewLabel ;
	private JLabel lblNewLabelLinea2 ;
	
	public PlatoCelda(JButton boton, String nombrePlato) {	

		
		JPanel panel = new JPanel();
		//panel.setBorder((new EmptyBorder(5, 5, 5, 5)));
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
		String linea1, linea2;
		if (nombrePlato.length() > 20){
			linea1 = nombrePlato.substring(0, 20);
			if (nombrePlato.length() > 40)
				linea2 = nombrePlato.substring(20, 40);
			else{
				linea2 = nombrePlato.substring(20, nombrePlato.length());
			}
//			lblNewLabel = new JLabel("<html>" + linea1 + "<br>" + linea2 + "</html>");
			
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
			//lblNewLabel = new JLabel("" + nombrePlato);
			lblNewLabel.setText(nombrePlato);
			lblNewLabelLinea2.setText("");
		//	lblNewLabel.setSize(new Dimension(100,10));
			grid.gridx = 0;
			grid.gridy = 1;
			panel.add(lblNewLabel,grid);
			grid.gridx = 0;
			grid.gridy = 2;
			panel.add(lblNewLabelLinea2,grid);
		}
		add(panel);
//		add(lblNewLabel);
//		add(boton);

//		GroupLayout groupLayout = new GroupLayout(this);
//		groupLayout.setHorizontalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//						.addComponent(boton)
//						.addGroup(groupLayout.createSequentialGroup()
//							.addGap(0)
//							.addComponent(lblNewLabel)))
//					.addContainerGap(10, Short.MAX_VALUE))
//		);
//		groupLayout.setVerticalGroup(
//			groupLayout.createParallelGroup(Alignment.LEADING)
//				.addGroup(groupLayout.createSequentialGroup()
//					.addComponent(boton)
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addComponent(lblNewLabel)
//					.addGap(20))
//		);
//		setLayout(groupLayout);	
		
	}
}
