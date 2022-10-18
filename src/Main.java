import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends JFrame {

    private JPanel mainPanel;
    private JPanel picture;

    int width;
    int height;
    float maxValue;
    Color tempColor;
    ArrayList<Color> colors;
    Graphics2D g2d;

    BufferedImage image;
    Graphics2D g2image;

    int flag = 0;


    public Main() {
        super("GIMP");

        colors = new ArrayList<Color>();

        setContentPane(mainPanel);
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

//        readFromFile("C:\\Users\\19sma\\OneDrive\\Desktop\\ppm-test-01-p3.ppm");
//        readFromFile("C:\\Users\\19sma\\OneDrive\\Desktop\\ppm-test-02-p3-comments.ppm");
//        readFromFile("C:\\Users\\19sma\\OneDrive\\Desktop\\ppm-test-07-p3-big.ppm");
//        readFromFile("C:\\Users\\19sma\\OneDrive\\Desktop\\ppm-test-04-p3-16bit.ppm");
        readFromFile("C:\\Users\\19sma\\OneDrive\\Desktop\\ppm-test-03-p6.ppm");

        picture.setSize(width, height);
        picture.setBackground(java.awt.Color.BLACK);
        g2d = (Graphics2D) picture.getGraphics();
        if (image == null && flag == 2) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2image = image.createGraphics();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                drawPicture();
            }
        });

        mainPanel.add(picture);

    }

    private void makePanelImage() {

        try {
//            ImageIO.write(image, "jpeg", new File("C:\\Users\\justy\\Desktop\\ImageAsJPeg.jpeg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawPicture() {
        int colorIterator = 0;
        if (flag == 1) {
            float[] scales = {1f, 1f, 1f};
            float[] offsets = new float[4];
            RescaleOp rop = new RescaleOp(scales, offsets, null);
            g2d.drawImage(image, rop, 0, 0);

        } else if (flag == 2) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {

                    g2d.setColor(new java.awt.Color(colors.get(colorIterator).getR(), colors.get(colorIterator).getG(), colors.get(colorIterator).getB()));

                    g2d.fillRect(j, i, 1, 1);
                    g2d.drawRect(j, i, 1, 1);

                    g2image.setColor(new java.awt.Color(colors.get(colorIterator).getR(), colors.get(colorIterator).getG(), colors.get(colorIterator).getB()));
                    g2image.fillRect(j, i, 1, 1);
                    g2image.drawRect(j, i, 1, 1);

                    colorIterator++;
                }
            }
        }
        makePanelImage();
    }

    public void readFromFile(String filePath) {
        String line;
        if ((filePath.substring(filePath.length() - 4, filePath.length())).equals("jpeg")) {
            flag = 1;
            try {
                image = ImageIO.read(new File(filePath));
                width = image.getWidth();
                height = image.getHeight();
                g2image = (Graphics2D) image.getGraphics();
            } catch (Exception e) {
            }
        } else {
            flag = 2;

            try {
                File file = new File(filePath);

                BufferedReader scanner = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));

                int iterator = 0;
                String type = null;

                while ((line = scanner.readLine()) != null) {




                    if (line.contains("#")) line = line.substring(0, line.indexOf("#"));

                    String[] values = line.split("\\s");
                    if(type == null) type = values[0];
                    if(type.equals("P3")){
                        for (int i = 0; i < values.length; i++) {
                            if (values[i].length() != 0) {
                                if (iterator == 0) {
                                    System.out.println(values[i]);
                                } else if (iterator == 1) {
                                    width = Integer.parseInt(values[i]);
                                    System.out.println(width);
                                } else if (iterator == 2) {
                                    height = Integer.parseInt(values[i]);
                                    System.out.println(height);
                                } else if (iterator == 3) {
                                    maxValue = Integer.parseInt(values[i]);
                                    System.out.println(maxValue);
                                } else {
//                            System.out.println("Iterator: " + iterator);
//                            System.out.println("I: " + i);
//                            System.out.println("Value[i]: " + values[i]);
//                            System.out.println("\n");
                                    if (iterator % 4 == 0) {
                                        tempColor = new Color();
                                        tempColor.setR((int) (Integer.parseInt(values[i]) * 255 / maxValue));
                                    } else if (iterator % 5 == 0) {
                                        tempColor.setG((int) (Integer.parseInt(values[i]) * 255 / maxValue));
                                    } else if (iterator % 6 == 0) {
                                        tempColor.setB((int) (Integer.parseInt(values[i]) * 255 / maxValue));

//                                System.out.println("(R: " + tempColor.getR() + " G: " + tempColor.getG() + " B: " + tempColor.getB() + ")");

                                        colors.add(tempColor);
                                    } else {
                                        iterator = 3;
                                        i--;
                                    }
                                }
                                iterator++;
                            }
                        }
                    }
                    else if(type.equals("P6")){
                        for (int i = 0; i < values.length; i++) {
                            if (values[i].length() != 0) {
                                if (iterator == 0) {
                                    System.out.println(values[i]);
                                } else if (iterator == 1) {
                                    width = Integer.parseInt(values[i]);
                                    System.out.println(width);
                                } else if (iterator == 2) {
                                    height = Integer.parseInt(values[i]);
                                    System.out.println(height);
                                } else if (iterator == 3) {
                                    maxValue = Integer.parseInt(values[i]);
                                    System.out.println(maxValue);
                                } else {
                                    if (iterator % 4 == 0) {
                                        byte [] value = values[i].getBytes(StandardCharsets.UTF_8);
                                        System.out.println(Integer.parseInt(String.valueOf(value)));
                                    }
//                                        tempColor = new Color();
//                                        System.out.println(values[i]);
//
//
//
////                                        tempColor.setR((int) (Integer.parseInt(values[i]) * 255 / maxValue));
//                                    } else if (iterator % 5 == 0) {
//                                        System.out.println(values[i]);
////                                        tempColor.setG((int) (Integer.parseInt(values[i]) * 255 / maxValue));
//                                    } else if (iterator % 6 == 0) {
//                                        System.out.println(values[i]);
////                                        tempColor.setB((int) (Integer.parseInt(values[i]) * 255 / maxValue));
//
////                                System.out.println("(R: " + tempColor.getR() + " G: " + tempColor.getG() + " B: " + tempColor.getB() + ")");
//
////                                        colors.add(tempColor);
//                                    } else {
//                                        iterator = 3;
//                                        i--;
//                                    }
                                }
                                iterator++;
                            }
                        }
                    }
                }

                scanner.close();
            } catch (FileNotFoundException error) {
                System.out.println("An error occurred.");
                error.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        new Main();
    }
}
