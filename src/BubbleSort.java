import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BubbleSort extends AlgorithmSort {
    Button btnStartBubbleSort, nextPassButton, resetButton;
    Thread animationThread;
    String BTN_LABEL = "Bubble Sort";
    final static int BTN_RELATIVE_VAL = 10;

    public BubbleSort(final JFrame mainFrame) {
        super();

        System.out.println("oldArr in construct" + oldInputArr);

        screenWidth = 225 + (inputArr.size() * 75);
        jframe = new JFrame("Bubble Sort Simulation");
        jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        jframe.setLayout(new BorderLayout());
        jframe.setContentPane(new JLabel(new ImageIcon(getClass().getResource(MainMenu.BACKGROUND_IMG))));
        jframe.setLayout(null);

        jframe.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                // @DOGGO maybe I can optimize these
                // close if list was entered but not animated
                if (animationThread == null) {
                    jframe.dispose();
                    mainFrame.setVisible(true);
                    return;
                }

                // @DOGGO this is my last resort
                // if animation is still on going stop all the system
                if(animationThread.isAlive()) {
                    int confirm = JOptionPane.showConfirmDialog(null, "Closing this window while animating will terminate the whole program.", "Message", JOptionPane.OK_CANCEL_OPTION);

                    if (confirm == 0)
                        System.exit(0);
                    else
                        jframe.setVisible(true);
                } else {
                    jframe.dispose();
                    mainFrame.setVisible(true);
                }
            }
        });

        // single instance btns to keep track of our boxes
        boxes = new Button[inputArr.size()];

        createBoxes();

        // ADD BUTTONS
        jframe.add(createResetButton());
        jframe.add(createSortingButton());
        jframe.add(createNextPassButton());

        jframe.setSize(screenWidth, SCREEN_HEIGHT);
        jframe.setLocationRelativeTo(null);

        showGroupBanner(jframe);
        showLegends(jframe, false);

        jframe.setVisible(true);
    }

    private int doBubbleSort() {
        if (lastArr > 0) {
            printArray(inputArr, 0);
            int temp = 0;
            for (; getCurrentPass() < getMaxPass(); setCurrentPass(getCurrentPass() + 1)) {
                for (int j = 1; j < (inputArr.size() - getCurrentPass()); j++) {
                    updateLegends(getCurrentPass(), j - 1, lastArr - 1); // update all legends

                    System.out.println("currentPass: " + getCurrentPass());
                    System.out.println("maxPass: " + getMaxPass());
                    changeColor(boxes[j - 1], Color.RED, false);
                    changeColor(boxes[j], Color.RED, false);
                    if (inputArr.get(j - 1) > inputArr.get(j)) {
                        changeColor(boxes[j - 1], Color.GREEN, false);
                        changeColor(boxes[j], Color.GREEN, true);
                        //swap elements
                        temp = inputArr.get(j - 1);
                        inputArr.set(j - 1, inputArr.get(j));
                        inputArr.set(j, temp);

                        System.out.println("Swapping" + inputArr.get(j - 1) + " and " + inputArr.get(j));

                        swap(j - 1, j, "bubble_sort");
                    }
                    changeColor(boxes[j - 1], Color.WHITE, true);
                    changeColor(boxes[j], Color.WHITE, true);
                }

                updateLegend(2, (lastArr - 1) + "", false); // update all legends

                changeColor(boxes[--lastArr], Color.CYAN, false);
                printArray(inputArr, getCurrentPass() + 1);

                if(getCurrentPass() == getMaxPass())
                    return 0;
            }

        }
        enableButtons();
        return getCurrentPass();
    }

    public Button createResetButton() {
        resetButton = new Button("Reset");
        resetButton.setBounds( ((screenWidth/2) - 70) - BTN_RELATIVE_VAL, (SCREEN_HEIGHT - 30) - 80, 50, 50);
        resetButton.addActionListener(e -> {
            resetPassValues();
            resetBoxes();
        });
        return resetButton;
    }

    public Button createSortingButton() {
        btnStartBubbleSort = new Button(BTN_LABEL);
        btnStartBubbleSort.setBounds( ( ((screenWidth/2) + 30) - BTN_LABEL.length() * 4 ) - BTN_RELATIVE_VAL, (SCREEN_HEIGHT - 30) - 80, 80, 50);
        btnStartBubbleSort.addActionListener(actionEvent -> {
            setMaxPass(inputArr.size());
            disableButtons();
            animationThread = new Thread(() -> {
                doBubbleSort();
            });
            animationThread.start();
        });

        return btnStartBubbleSort;
    }

    public Button createNextPassButton() {
        nextPassButton = new Button("Next Pass");
        nextPassButton.setBounds( ((screenWidth/2) + 70) - BTN_RELATIVE_VAL, (SCREEN_HEIGHT - 30) - 80, 70, 50);
        nextPassButton.addActionListener(e -> {
            new Thread(() -> {
                disableButtons();
                System.out.println("clicking next pass");
                setMaxPass(getCurrentPass() + 1);
                // execute after thread have finished
                setCurrentPass(doBubbleSort());
            }).start();

        });
        return nextPassButton;
    }

    public void disableButtons() {
        resetButton.setEnabled(false);
        btnStartBubbleSort.setEnabled(false);
        nextPassButton.setEnabled(false);
    }

    public void enableButtons() {
        resetButton.setEnabled(true);
        btnStartBubbleSort.setEnabled(true);
        nextPassButton.setEnabled(true);

    }

}