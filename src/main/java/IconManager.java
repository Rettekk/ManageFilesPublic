import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class IconManager {
    private ImageIcon icon;
   public String pathToIcon = "filemanage.png";

    public IconManager() {
        icon = new ImageIcon(getClass().getResource(pathToIcon));
    }

    public void setIcon(JFrame frame) {
        frame.setIconImage(icon.getImage());
    }
}
