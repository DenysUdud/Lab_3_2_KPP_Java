import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server extends JFrame {

    private ServerSocket ss;
    private Socket s;
    private ObjectInputStream in;

    private JPanel serverPanel;
    private JButton button;
    private JPanel panel;
    private JLabel panelLabel;

    public Server() {
        try {
            initComponents();
            ss = new ServerSocket(9999);
            s = ss.accept();
            in = new ObjectInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        serverPanel = new JPanel();
        button = new JButton("Button");
        panel = new JPanel();
        panelLabel = new JLabel("Panel");

        GroupLayout layout = new GroupLayout(serverPanel);
        serverPanel.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(button)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(panel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(panelLabel)))
                                .addContainerGap(315, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(button)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(panel)
                                        .addComponent(panelLabel))
                                .addContainerGap(233, Short.MAX_VALUE))
        );

        setContentPane(serverPanel);
        setTitle("Server");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setSize(600, 300);
        setVisible(true);
    }

    private void receiveMessage() {
        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                String msg;
                while (true) {
                    try {
                        msg = (String) in.readObject();
                        publish(msg);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String msg : chunks) {
                    processMessage(msg);
                }
            }
        }.execute();
    }

    private void processMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            switch (msg) {
                case "1":
                    toggleButton();
                    break;
                case "2":
                    togglePanel();
                    break;
                case "3":
                    changeBackgroundColor(Color.WHITE);
                    break;
                case "4":
                    changeBackgroundColor(Color.RED);
                    break;
                case "5":
                    changeBackgroundColor(Color.YELLOW);
                    break;
                default:
                    break;
            }
        });
    }

    private void toggleButton() {
        button.setEnabled(!button.isEnabled());
    }

    private void togglePanel() {
        panel.setVisible(!panel.isVisible());
        panelLabel.setVisible(!panelLabel.isVisible());
    }

    private void changeBackgroundColor(Color color) {
        serverPanel.setBackground(color);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Server server = new Server();
                server.receiveMessage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
