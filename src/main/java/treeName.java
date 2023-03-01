import com.google.api.services.drive.model.File;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class treeName extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (c instanceof javax.swing.JLabel && value instanceof DefaultMutableTreeNode) {
            javax.swing.JLabel l = (javax.swing.JLabel) c;
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof File) {
                l.setText(((File) userObject).getName());
            }
        }
        return c;
    }
}