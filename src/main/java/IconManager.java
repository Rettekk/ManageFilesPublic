import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.util.Objects;

public class IconManager {
    private final ImageIcon icon;
   public String pathToIcon = "filemanage.png";

    public IconManager() {
        icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(pathToIcon)));
    }

    public void setIcon(JFrame frame) {
        frame.setIconImage(icon.getImage());
    }
}
