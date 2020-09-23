import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

// Main class
public class HoughTransform extends Frame implements ActionListener {

    BufferedImage input;
    int width, height, diagonal;
    ImageCanvas source, target;
    TextField texRad, texThres;
    // Constructor

    public HoughTransform(String name) {
        super("Hough Transform");
        // load image
        try {
            input = ImageIO.read(new File(name));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        width = input.getWidth();
        height = input.getHeight();
        diagonal = (int) Math.sqrt(width * width + height * height);
        // prepare the panel for two images.
        Panel main = new Panel();
        source = new ImageCanvas(input);
        target = new ImageCanvas(input);
        main.setLayout(new GridLayout(1, 2, 10, 10));
        main.add(source);
        main.add(target);
        // prepare the panel for buttons.
        Panel controls = new Panel();
        Button button = new Button("Line Transform");
        button.addActionListener(this);
        controls.add(button);
        controls.add(new Label("Radius:"));
        texRad = new TextField("10", 3);
        controls.add(texRad);
        button = new Button("Circle Transform");
        button.addActionListener(this);
        controls.add(button);
        controls.add(new Label("Threshold:"));
        texThres = new TextField("25", 3);
        controls.add(texThres);
        button = new Button("Search");
        button.addActionListener(this);
        controls.add(button);
        // add two panels
        add("Center", main);
        add("South", controls);
        addWindowListener(new ExitListener());
        setSize(diagonal * 2 + 100, Math.max(height, 360) + 100);
        setVisible(true);
    }

    class ExitListener extends WindowAdapter {

        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
    // Action listener

    public void actionPerformed(ActionEvent e) {
        // perform one of the Hough transforms if the button is clicked.
        if (((Button) e.getSource()).getLabel().equals("Line Transform")) {
            int[][] g = new int[360][diagonal];
            // insert your implementation for straight-line here.
            int p;
            int offset = diagonal / 2;
         
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color clr = new Color(source.image.getRGB(x, y));
                    int red = clr.getRed();
                    int green = clr.getGreen();
                    int blue = clr.getBlue();
                    if (red == 0 && green == 0 && blue == 0) {
                        for (int i = 0; i < 360; i++) {
                            p = (int) (x * Math.cos(i*Math.PI/180) + y * Math.sin(i*Math.PI/180));
                            p += offset;
                            if (p > 0 && p < diagonal) {                               
                                g[i][p] += 2;
                            }
                        }
                    }

                }
            }
            // calculate threshold
            int max = 0;
            for (int y = 0; y < diagonal; y++) {
                for (int x = 0; x < 360; x++) {
                    if (g[x][y] >= max) {
                        max = g[x][y];
                    }
                }
            }           
            
            System.out.println(max);
            
            // calulate y = mx + b for 
            
            int threshold = Integer.parseInt(texThres.getText());
            threshold = max -  (max * threshold / 100);
            
            System.out.println(threshold);
            double x, y, m, b;
            for (int h = 0; h < diagonal; h++) {
                for (int t = 0; t < 360; t++) {
                    if (g[t][h] >= threshold) {
                        x = (h - offset) * Math.cos(t);
                        y = (h - offset) * Math.sin(t);
                        m = t + 90;
                        b = y - (m * Math.PI / 180 * x);
                        System.out.println(m);
                    }
                }
            } 
            

            

            DisplayTransform(diagonal, 360, g);
        } else if (((Button) e.getSource()).getLabel().equals("Circle Transform")) {
            // (x-xo)2 + (y-yo)2 = r2
            int[][] g = new int[height][width];
            int radius = Integer.parseInt(texRad.getText());
            int a;
            int b;
            // insert your implementation for circle here.

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color clr = new Color(source.image.getRGB(x, y));
                    int red = clr.getRed();
                    int green = clr.getGreen();
                    int blue = clr.getBlue();
                    if (red == 0 && green == 0 && blue == 0) {
                        for (int i = 0; i < 360; i++) {
                            a = (int) (x - radius * Math.cos(i * Math.PI / 180));
                            b = (int) (y - radius * Math.sin(i * Math.PI / 180));
                            if (a >= 0 && a < width && b >= 0 && b < height) {
                                g[b][a]++;
                            }
                        }       
                    }
                }
            }

            DisplayTransform(width, height, g);
        }
    }
    // display the spectrum of the transform.

    public void DisplayTransform(int wid, int hgt, int[][] g) {
        target.resetBuffer(wid, hgt);
        for (int y = 0, i = 0; y < hgt; y++) {
            for (int x = 0; x < wid; x++, i++) {
                int value = g[y][x] > 255 ? 255 : g[y][x];
                target.image.setRGB(x, y, new Color(value, value, value).getRGB());
            }
        }
        target.repaint();
    }

    public static void main(String[] args) {
        new HoughTransform(args.length == 1 ? args[0] : "rectangle.png");
    }
}