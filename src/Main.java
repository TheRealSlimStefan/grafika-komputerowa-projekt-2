import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class Main extends JFrame implements MouseMotionListener, ActionListener {

    private JPanel mainPanel;
    private JPanel picture;
    private JTextField textFieldQuality;
    private JButton saveButton1;
    private JButton saveButton;
    private JTextField textFieldFilePath;
    private JButton openButton;
    private JTextField textFieldFileName;

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

        saveButton.addActionListener(this);
        saveButton1.addActionListener(this);
        openButton.addActionListener(this);
    }

    private void makePanelImage(boolean compressed) throws IOException {
        if (!compressed) {
            try {
                if (!textFieldFileName.getText().isEmpty()) {
                    File file = new File(textFieldFileName.getText().trim() + ".jpeg");
                    if (!file.exists()) {
                        ImageIO.write(image, "jpeg", file);
                        Toast toast = new Toast("File saved", getWidth() / 2, getHeight());
                        toast.showtoast();
                    } else {
                        Toast toast = new Toast("File " + textFieldFileName.getText().trim() + ".jpeg already exists", getWidth() / 2, getHeight());
                        toast.showtoast();
                    }
                } else {
                    Toast toast = new Toast("The 'Name' field must not be empty", getWidth() / 2, getHeight());
                    toast.showtoast();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (!textFieldQuality.getText().isEmpty()) {
                String textQuality = textFieldQuality.getText();
                float quality = Float.parseFloat(textQuality) / 100;

                if (quality <= 0 || quality > 1) {
                    Toast toast = new Toast("The compression quality entered is incorrect. Enter an integer between 1 and 100", getWidth() / 2, getHeight());
                    toast.showtoast();
                } else {
                    File compressedImageFile = new File(textFieldFileName.getText().trim() + ".jpeg");
                    if (!compressedImageFile.exists()) {

                        OutputStream os = new FileOutputStream(compressedImageFile);
                        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");

                        if (!writers.hasNext())
                            throw new IllegalStateException("No writers found");

                        ImageWriter writer = writers.next();
                        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                        writer.setOutput(ios);

                        ImageWriteParam param = writer.getDefaultWriteParam();

                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                        param.setCompressionQuality(quality);
                        writer.write(null, new IIOImage(image, null, null), param);

                        os.close();
                        ios.close();
                        writer.dispose();
                        Toast toast = new Toast("File saved", getWidth() / 2, getHeight());
                        toast.showtoast();
                    } else {
                        Toast toast = new Toast("File " + textFieldFileName.getText().trim() + ".jpeg already exists", getWidth() / 2, getHeight());
                        toast.showtoast();
                    }
                }
            } else {
                Toast toast = new Toast("The 'Name' field must not be empty", getWidth() / 2, getHeight());
                toast.showtoast();
            }
        }

    }

    public void readyToDraw() {
        picture.setSize(width, height);
//        g2d = (Graphics2D) picture.getGraphics();
        g2d = (Graphics2D) picture.getGraphics().create();
        if (image == null && flag == 2) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            g2image = image.createGraphics();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    drawPicture();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void drawPicture() throws IOException {
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
        picture.paintComponents(g2d);
        g2d.dispose();
    }

    public void readFromFile(String filePath) {
        String line;
        if ((filePath).startsWith("jpeg", filePath.length() - 4)) {
            flag = 1;
            try {
                image = ImageIO.read(new File(filePath));
                width = image.getWidth();
                height = image.getHeight();
                g2image = (Graphics2D) image.getGraphics();
                readyToDraw();
            } catch (Exception e) {
            }

        } else if ((filePath).startsWith("ppm", filePath.length() - 3)) {
            flag = 2;

            try {
                File file = new File(filePath);

                BufferedReader scanner = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));

                int iterator = 0;
                String type = null;

                while ((line = scanner.readLine()) != null) {

                    if (line.contains("#")) line = line.substring(0, line.indexOf("#"));

                    String[] values = line.split("\\s");

                    if (type == null) type = values[0];
                    if (type.equals("P3")) {
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
//                    else if (type.equals("P6")) {
//                        for (int i = 0; i < values.length; i++) {
//                            if (values[i].length() != 0) {
//                                if (iterator == 0) {
//                                    System.out.println(values[i]);
//                                } else if (iterator == 1) {
//                                    width = Integer.parseInt(values[i]);
//                                    System.out.println(width);
//                                } else if (iterator == 2) {
//                                    height = Integer.parseInt(values[i]);
//                                    System.out.println(height);
//                                } else if (iterator == 3) {
//                                    maxValue = Integer.parseInt(values[i]);
//                                    System.out.println(maxValue);
//                                } else {
//                                    if (iterator % 4 == 0) {
//                                        byte[] value = values[i].getBytes(StandardCharsets.UTF_8);
//                                        System.out.println(Integer.parseInt(String.valueOf(value)));
//                                    }
////                                        tempColor = new Color();
////                                        System.out.println(values[i]);
////
////
////
//////                                        tempColor.setR((int) (Integer.parseInt(values[i]) * 255 / maxValue));
////                                    } else if (iterator % 5 == 0) {
////                                        System.out.println(values[i]);
//////                                        tempColor.setG((int) (Integer.parseInt(values[i]) * 255 / maxValue));
////                                    } else if (iterator % 6 == 0) {
////                                        System.out.println(values[i]);
//////                                        tempColor.setB((int) (Integer.parseInt(values[i]) * 255 / maxValue));
////
//////                                System.out.println("(R: " + tempColor.getR() + " G: " + tempColor.getG() + " B: " + tempColor.getB() + ")");
////
//////                                        colors.add(tempColor);
////                                    } else {
////                                        iterator = 3;
////                                        i--;
////                                    }
//                                }
//                                iterator++;
//                            }
//                        }
//                    }
                }
                readyToDraw();
                scanner.close();
            } catch (FileNotFoundException error) {
                System.out.println("An error occurred.");
                error.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast toast = new Toast("Incorrect file type specified, specify file with extension *.ppm or *.jpeg", getWidth() / 2, getHeight());
            toast.showtoast();
        }
    }

    public static void main(String args[]) {
        new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Open") {
            if (!textFieldFilePath.getText().isEmpty()) {
                File f = new File(textFieldFilePath.getText().trim());
                if (f.exists() && !f.isDirectory() && f.isFile()) {
                    picture.revalidate();
                    readFromFile(textFieldFilePath.getText().trim());
                } else {
                    Toast toast = new Toast("The specified file does not exist or is not a file", getWidth() / 2, getHeight());
                    toast.showtoast();
                }
            } else {
                Toast toast = new Toast("The 'file name' field must not be empty", getWidth() / 2, getHeight());
                toast.showtoast();
            }
        }
        if (e.getActionCommand() == "Save") {
            try {
                makePanelImage(false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (e.getActionCommand() == "Save Compressed") {
            try {
                makePanelImage(true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
