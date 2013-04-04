package interfaz;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class PlatoCelda extends JPanel{
	
	private JLabel lblNewLabel ;
	
	public PlatoCelda(JButton boton, String nombrePlato) {	

		
		//Como max dos lineas
		String linea1, linea2;
		if (nombrePlato.length() > 18){
			linea1 = nombrePlato.substring(0, 18);
			if (nombrePlato.length() > 36)
				linea2 = nombrePlato.substring(18, 36);
			else{
				linea2 = nombrePlato.substring(18, nombrePlato.length());
			}
			lblNewLabel = new JLabel("<html>" + linea1 + "<br>" + linea2 + "</html>");
		}
		else{
			//lblNewLabel = new JLabel("" + nombrePlato);
			lblNewLabel = new JLabel("<html>" + nombrePlato + "<br>" + "." + "</html>");
		//	lblNewLabel.setSize(new Dimension(100,10));
		}
		
		add(lblNewLabel);
		add(boton);

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(boton)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(0)
							.addComponent(lblNewLabel)))
					.addContainerGap(10, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(boton)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNewLabel)
					.addGap(20))
		);
		setLayout(groupLayout);	
	}
}
