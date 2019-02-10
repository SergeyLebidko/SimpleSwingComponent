package mainpack;

import javax.swing.*;
import java.awt.*;

public class GUI {

    private final int WIDTH_WINDOW = 1000;
    private final int HEIGHT_WINDOW = 400;
    private final int COUNT_COLS = 6;
    private final String TITLE_WINDOW = "Главное окно";

    private JFrame window;
    private JPanel content;

    private JLabel[] labels;
    private CircleComponent[] circleComponents;
    private CircleComponentListener circleComponentListener;

    public GUI() {
        window = new JFrame(TITLE_WINDOW);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(WIDTH_WINDOW, HEIGHT_WINDOW);
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - WIDTH_WINDOW / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - HEIGHT_WINDOW / 2;
        window.setLocation(xPos, yPos);

        content=new JPanel();
        content.setLayout(new GridLayout(2, COUNT_COLS, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        window.add(content);
        labels = new JLabel[COUNT_COLS];
        circleComponents = new CircleComponent[COUNT_COLS];

        circleComponentListener = new CircleComponentListener() {
            @Override
            public void componentClick(CircleComponentEvent event) {
                CircleComponent source=(CircleComponent)(event.getSource());
                for (int i=0;i<COUNT_COLS;i++){
                    if (source==circleComponents[i]){
                        labels[i].setText(""+circleComponents[i].getValue());
                    }
                }
            }
        };

        for (int i = 0; i < COUNT_COLS; i++) {
            labels[i] = new JLabel("" + i * 20);
            labels[i].setHorizontalAlignment(JLabel.CENTER);
            labels[i].setFont(new Font(null, Font.PLAIN, 16));
            circleComponents[i] = new CircleComponent(i * 20);
            circleComponents[i].addComponentMouseListener(circleComponentListener);
        }

        for (int i = 0; i < COUNT_COLS; i++) {
            content.add(labels[i]);
        }

        for (int i = 0; i < COUNT_COLS; i++) {
            content.add(circleComponents[i]);
        }

        window.setVisible(true);
    }
}
