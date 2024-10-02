import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class TwoPassAssemblerGUI extends JFrame {
    
    // Components for GUI
    private JTextArea inputArea, outputArea;
    private JButton passOneButton, passTwoButton;
    private HashMap<String, Integer> symbolTable = new HashMap<>();
    private String intermediateCode = "";

    public TwoPassAssemblerGUI() {
        // Setting up the frame
        setTitle("Two-Pass Assembler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Creating text areas
        inputArea = new JTextArea(10, 40);
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);

        // Creating buttons
        passOneButton = new JButton("Pass One");
        passTwoButton = new JButton("Pass Two");

        // Setting up the layout
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JScrollPane(inputArea));
        panel.add(passOneButton);
        panel.add(passTwoButton);
        panel.add(new JScrollPane(outputArea));

        add(panel, BorderLayout.CENTER);

        // Pass One button action
        passOneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passOne();
            }
        });

        // Pass Two button action
        passTwoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passTwo();
            }
        });
    }

    // Method for Pass One
    private void passOne() {
        String sourceCode = inputArea.getText();
        String[] lines = sourceCode.split("\n");
        int locationCounter = 0;

        StringBuilder intermediateBuilder = new StringBuilder();
        symbolTable.clear();

        for (String line : lines) {
            String[] tokens = line.split(" ");
            String label = tokens[0];
            String instruction = tokens[1];

            // If label exists, add to symbol table
            if (!label.equals("-")) {
                symbolTable.put(label, locationCounter);
            }

            // Dummy intermediate code generation
            intermediateBuilder.append(locationCounter).append(" ").append(instruction).append("\n");
            locationCounter++;
        }

        intermediateCode = intermediateBuilder.toString();
        outputArea.setText("Pass One Completed. Intermediate Code:\n" + intermediateCode + "\nSymbol Table:\n" + symbolTable.toString());
    }

    // Method for Pass Two
    private void passTwo() {
        if (intermediateCode.isEmpty()) {
            outputArea.setText("Run Pass One first!");
            return;
        }

        StringBuilder finalCodeBuilder = new StringBuilder();
        String[] lines = intermediateCode.split("\n");

        for (String line : lines) {
            String[] tokens = line.split(" ");
            int address = Integer.parseInt(tokens[0]);
            String instruction = tokens[1];

            // In Pass Two, we would resolve addresses from the symbol table
            // Simulating machine code generation
            finalCodeBuilder.append("Address: ").append(address).append(", Machine Code: [")
                    .append(instruction).append("]\n");
        }

        outputArea.setText("Pass Two Completed. Final Machine Code:\n" + finalCodeBuilder.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TwoPassAssemblerGUI().setVisible(true);
        });
    }
}
