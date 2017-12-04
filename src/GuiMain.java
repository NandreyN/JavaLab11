import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class PaintFrame extends JFrame {

    class PaintPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(currentGraphics, 0, 0, null);
        }
    }

    private JScrollPane scrollPane;
    private JPanel globContent, rbPanel;
    private BufferedImage currentGraphics;
    private Point prevPoint, currentPoint;
    private JRadioButton blackRB, redRB, greenRB;
    private ButtonGroup rbGroup;
    private JButton openDialogButton, saveDialogButton;

    PaintFrame(String wndName) {
        super(wndName);

        prevPoint = currentPoint = null;
        currentGraphics = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) currentGraphics.getGraphics();
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, currentGraphics.getWidth(), currentGraphics.getHeight());
        g.dispose();

        globContent = new PaintPanel();
        globContent.setLayout(new BorderLayout());
        globContent.setPreferredSize(new Dimension(1000, 1000));
        globContent.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                prevPoint = currentPoint;
                currentPoint = e.getPoint();
                updateImage();
            }
        });
        globContent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                prevPoint = currentPoint = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                currentPoint = e.getPoint();
                updateImage();
            }
        });

        setLayout(new BorderLayout());
        scrollPane = new JScrollPane(globContent);
        scrollPane.setPreferredSize(this.getPreferredSize());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);

        rbPanel = new JPanel();
        redRB = new JRadioButton("Red");
        blackRB = new JRadioButton("Black");
        greenRB = new JRadioButton("Green");

        rbGroup = new ButtonGroup();
        rbGroup.add(blackRB);
        rbGroup.add(redRB);
        rbGroup.add(greenRB);
        blackRB.setSelected(true);

        rbPanel.setLayout(new FlowLayout());
        rbPanel.add(blackRB);
        rbPanel.add(greenRB);
        rbPanel.add(redRB);

        saveDialogButton = new JButton("Save");
        openDialogButton = new JButton("Open");
        rbPanel.add(saveDialogButton);
        rbPanel.add(openDialogButton);

        saveDialogButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int ret = chooser.showSaveDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    String absolutePath = file.getAbsolutePath();

                    // save as image
                    try {
                        file = new File(absolutePath + ".jpg");
                        boolean creationResult = file.createNewFile();
                        if (!creationResult) {
                            JOptionPane.showMessageDialog(null, "Cannot create file");
                            return;
                        }
                        ImageIO.write(currentGraphics, "jpg", file);
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage());
                    }


                }
            }
        });

        openDialogButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int ret = chooser.showOpenDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = chooser.getSelectedFile();
                    try {
                        currentGraphics = ImageIO.read(selectedFile);
                        repaint();
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, e1.getMessage());
                    }
                }

            }
        });
        add(rbPanel, BorderLayout.SOUTH);
    }

    private void updateImage() {
        Graphics g = currentGraphics.createGraphics();
        if (rbGroup.getSelection().equals(redRB.getModel())) {
            g.setColor(Color.RED);
        }
        if (rbGroup.getSelection().equals(blackRB.getModel())) {
            g.setColor(Color.BLACK);
        }
        if (rbGroup.getSelection().equals(greenRB.getModel())) {
            g.setColor(Color.GREEN);
        }

        if (prevPoint == null) {
            g.drawLine(currentPoint.x, currentPoint.y, currentPoint.x, currentPoint.y);
            globContent.repaint();
            return;
        }

        g.drawLine(prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y);
        g.dispose();
        repaint();
    }

    public static void main(String[] args) {
        PaintFrame mainFrame = new PaintFrame("Painting");
        mainFrame.setBounds(30, 30, 500, 500);
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}
