import javax.swing.*;

public class menuBar  extends JFrame {
    private JMenuBar menuBar = new JMenuBar();
    private JMenu startMenu, helpMenu;
    private JMenuItem addFile, delFile, showCloud, authorize, cred, howTo, disc, exit, logOff;


    public JMenuBar menuBar() {
        menuBar = new JMenuBar();
        startMenu = new JMenu("Start");
        addFile = new JMenuItem("Datei hochladen");
        delFile = new JMenuItem("Datei loeschen");
        showCloud = new JMenuItem("Cloud anzeigen");
        authorize = new JMenuItem("Mit der Cloud verbinden");
        disc = new JMenuItem("Verbindung trennen");
        logOff = new JMenuItem("Abmelden");
        exit = new JMenuItem("Exit");
        helpMenu = new JMenu("Hilfe");
        cred = new JMenuItem("Credits");
        howTo = new JMenuItem("Bedienung");
        JMenuItem showTrash = new JMenuItem("Papierkorb anzeigen");
        startMenu.add(addFile).setEnabled(false);
        startMenu.add(delFile).setEnabled(false);
        startMenu.add(showCloud).setEnabled(false);
        startMenu.add(disc).setEnabled(false);
        startMenu.add(authorize).setEnabled(false);
        startMenu.add(logOff).setEnabled(false);
        startMenu.add(disc);
        startMenu.add(exit);
        helpMenu.add(cred).setEnabled(false);
        helpMenu.add(howTo).setEnabled(false);
        helpMenu.add(showTrash).setEnabled(false);
        menuBar.add(startMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        menuBar.setVisible(true);

        exit.addActionListener(e -> System.exit(0));
        return menuBar;
    }
}
