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



    public Main() {
        super("GIMP");
        setContentPane(mainPanel);

        readFromFile("C:\\Users\\19sma\\OneDrive\\Desktop\\ppm-obrazy-testowe\\ppm-test-04-p3-16bit.ppm");

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);
        setVisible(true);


    }

    public static void main(String args[]){
        new Main();
    }

    public void readFromFile(String filePath){
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                int iterator = 0;

                if(iterator == 0){
                    String line = scanner.next();
//                    System.out.println(line);
                } else if(iterator == 1){
                    width = Integer.parseInt(scanner.next());
//                    System.out.println(width);
                } else if(iterator == 2){
                    height = Integer.parseInt(scanner.next());
//                    System.out.println(height);
                } else if(iterator == 3){
                    maxValue = Integer.parseInt(scanner.next());
//                    System.out.println(height);
                } else {
                    if(iterator % 4 == 0){
                        tempColor = new Color();
                        tempColor.setR(Integer.parseInt(scanner.next()));
                    } else if(iterator % 5 == 0){
                        tempColor.setG(Integer.parseInt(scanner.next()));
                    } else if(iterator % 6 == 0){
                        tempColor.setB(Integer.parseInt(scanner.next()));

//                        System.out.println(tempColor.getR() + tempColor.getG() + tempColor.getB() + ", ");

                        colors.add(tempColor);
                    } else iterator = 4;
                }

                iterator++;

            }
            scanner.close();
        } catch (FileNotFoundException error) {
            System.out.println("An error occurred.");
            error.printStackTrace();
        }
    }
}
