import com.google.api.services.drive.model.File;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class customTreeCellRenderer extends DefaultTreeCellRenderer {
    private treeName fileRenderer;
    private folderTree folderRenderer;

    public customTreeCellRenderer() {
        fileRenderer = new treeName();
        folderRenderer = new folderTree();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = null;
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            if (userObject instanceof File) {
                c = fileRenderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            } else if (userObject instanceof String) {
                c = folderRenderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            }
        }
        if (c == null) {
            c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
        return c;
    }
}
