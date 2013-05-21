package alertDialogPersonalizado;

import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;

/**
 * Action item, para ver en el menú un icono y texto si queremos.
 */
public class ActionItem {
	private Drawable icono;
	private Bitmap thumb;
	private String titulo;
	private int actionId = -1;
    private boolean selected;
    private boolean sticky;
	
    /**
     * Constructor
     * 
     * @param actionId   ID de la acción para despues tratarla en el swith
     * @param titulo     título de la acción
     * @param icono      icono de la acción
     */
    public ActionItem(int actionId, String titulo, Drawable icono) {
        this.titulo = titulo;
        this.icono = icono;
        this.actionId = actionId;
    }
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitle() {
		return this.titulo;
	}
	
	public void setIcon(Drawable icono) {
		this.icono = icono;
	}
	
	public Drawable getIcon() {
		return this.icono;
	}
	
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }
    
    public int getActionId() {
        return actionId;
    }
    
    /**
     * Set sticky status of button
     * 
     * @param sticky  true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
    
    /**
     * @return  true if button is sticky, menu stays visible after press
     */
    public boolean isSticky() {
        return sticky;
    }
    
	/**
	 * Set selected flag;
	 * 
	 * @param selected Flag to indicate the item is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Check if item is selected
	 * 
	 * @return true or false
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * Set thumb
	 * 
	 * @param thumb Thumb image
	 */
	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}
	
	/**
	 * Get thumb image
	 * 
	 * @return Thumb image
	 */
	public Bitmap getThumb() {
		return this.thumb;
	}
}