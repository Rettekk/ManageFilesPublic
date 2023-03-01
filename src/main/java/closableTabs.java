import javax.swing.*;
import java.awt.*;

public class closableTabs extends JTabbedPane {
    public closableTabs() {
        super();
    }

    private class CloseButtonTab extends JPanel {
        public CloseButtonTab(String title, Icon icon, Component content) {
            setOpaque(false);

            JLabel label = new JLabel(title);
            label.setIcon(icon);

            JButton button = new JButton("x");
            button.setForeground(Color.RED);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.addActionListener(e -> {
                int tabIndex = indexOfComponent(content);
                removeTabAt(tabIndex);
            });

            add(label);
            add(Box.createHorizontalStrut(5));
            add(button);
        }
    }

    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        CloseButtonTab tab = new CloseButtonTab(title, icon, component);
        super.addTab(null, null, component, tip);
        setTabComponentAt(getTabCount() - 1, tab);
    }
}
