package interfaz;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

class ImageButtonMesa extends JButton {
	
	private String idMesa;
	private int numeroPersonas;
	
	public ImageButtonMesa(String img, String idMesa, int numeroPersonas) {
		this(new ImageIcon(img)); //llamada a la segunda constructora
		this.idMesa = idMesa;
		this.numeroPersonas = numeroPersonas;
    }
	
	private ImageButtonMesa(ImageIcon icon) {
		setLayout(new BorderLayout());
		setIcon(icon);
		setMargin(new Insets(0, 0, 0, 0));
		setIconTextGap(0);
		setBorderPainted(false);
		setBorder(null);
		setText("hola");
		setSize(icon.getImage().getWidth(null), icon.getImage().getHeight(null));
	}

}
