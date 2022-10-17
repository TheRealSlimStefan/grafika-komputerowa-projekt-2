import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends JFrame {

    private JPanel mainPanel;
    private JPanel picture;

    int width;
    int height;
    int maxValue;
    Color tempColor;
    ArrayList<Color> colors;
    Graphics2D g2d;

    public Main() {
        super("GIMP");

        colors = new ArrayList<Color>();

        setContentPane(mainPanel);
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        readFromFile("C:\\Users\\19sma\\OneDrive\\Desktop\\cake.ppm");

        picture.setSize(width,height);
        picture.setBackground(java.awt.Color.BLACK);
        g2d = (Graphics2D) picture.getGraphics();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                drawPicture();
            }
        });

        mainPanel.add(picture);
    }

    private void drawPicture() {
        int colorIterator = 0;


        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){

                g2d.setColor(new java.awt.Color(colors.get(colorIterator).getR(), colors.get(colorIterator).getG(), colors.get(colorIterator).getB()));

                g2d.fillRect(j, i, 1, 1);
                g2d.drawRect(j, i, 1, 1);

                colorIterator++;
            }
        }
    }

    public void readFromFile(String filePath){
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            int iterator = 0;

            while (scanner.hasNext()) {
                if(iterator == 0){
                    String line = scanner.next();
                    System.out.println(line);
                } else if(iterator == 1){
                    width = Integer.parseInt(scanner.next());
                    System.out.println(width);
                } else if(iterator == 2){
                    height = Integer.parseInt(scanner.next());
                    System.out.println(height);
                } else if(iterator == 3){
                    maxValue = Integer.parseInt(scanner.next());
                    System.out.println(maxValue);
                } else {
                    if(iterator % 4 == 0){
                        tempColor = new Color();
                        tempColor.setR(Integer.parseInt(scanner.next()));
                    } else if(iterator % 5 == 0){
                        tempColor.setG(Integer.parseInt(scanner.next()));
                    } else if(iterator % 6 == 0){
                        tempColor.setB(Integer.parseInt(scanner.next()));

//                        System.out.println("(R: " + tempColor.getR() + " G: " + tempColor.getG() + " B: " + tempColor.getB() + ")");

                        colors.add(tempColor);
                    } else iterator = 3;
                }

                iterator++;

            }
            scanner.close();
        } catch (FileNotFoundException error) {
            System.out.println("An error occurred.");
            error.printStackTrace();
        }
    }

    public static void main(String args[]){
        new Main();
    }
}
