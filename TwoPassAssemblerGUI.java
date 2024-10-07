import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TwoPassAssemblerGUI extends JFrame {

    // UI components
    private JTextArea inputArea;
    private JTextArea optabArea;
    private JTextArea symtabArea;
    private JTextArea intermediateArea;
    private JTextArea outputArea;
    private JTextArea objectCodeArea;

    // Data structures
    private Map<String, Integer> symtab; // Symbol Table
    private Map<String, String> optab; // Operation Table
    private StringBuilder intermediateCode; // Intermediate Code
    private StringBuilder objectCode; // Object Code
    private StringBuilder outputCode; // Final Output Code
    int length;
    int locctr = 0;

    public TwoPassAssemblerGUI() {
        // Setup frame
        setTitle("Two-Pass Assembler");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create panels for input and output areas
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 1));

        // Panel for Input Assembly Code and OPTAB with labels
        JPanel inputSubPanel = new JPanel(new GridLayout(1, 2));

        JPanel inputCodePanel = new JPanel(new BorderLayout());
        inputArea = new JTextArea(10, 40);
        inputCodePanel.add(new JLabel("Input (Assembly Code)"), BorderLayout.NORTH);
        inputCodePanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputSubPanel.add(inputCodePanel);

        JPanel optabCodePanel = new JPanel(new BorderLayout());
        optabArea = new JTextArea(10, 20);
        optabCodePanel.add(new JLabel("OPTAB (Operation Table)"), BorderLayout.NORTH);
        optabCodePanel.add(new JScrollPane(optabArea), BorderLayout.CENTER);
        inputSubPanel.add(optabCodePanel);

        inputPanel.add(inputSubPanel);

        // Panel for Intermediate Code, SYMTAB, Output Code, and Object Code with labels
        JPanel outputSubPanel = new JPanel(new GridLayout(1, 4));

        JPanel intermediatePanel = new JPanel(new BorderLayout());
        intermediateArea = new JTextArea(10, 20);
        intermediatePanel.add(new JLabel("Intermediate Code"), BorderLayout.NORTH);
        intermediatePanel.add(new JScrollPane(intermediateArea), BorderLayout.CENTER);
        outputSubPanel.add(intermediatePanel);

        JPanel symtabPanel = new JPanel(new BorderLayout());
        symtabArea = new JTextArea(10, 20);
        symtabPanel.add(new JLabel("SYMTAB (Symbol Table)"), BorderLayout.NORTH);
        symtabPanel.add(new JScrollPane(symtabArea), BorderLayout.CENTER);
        outputSubPanel.add(symtabPanel);

        JPanel outputCodePanel = new JPanel(new BorderLayout());
        outputArea = new JTextArea(10, 20);
        outputCodePanel.add(new JLabel("Output (Pass 2 Output Code)"), BorderLayout.NORTH);
        outputCodePanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        outputSubPanel.add(outputCodePanel);

        JPanel objectCodePanel = new JPanel(new BorderLayout());
        objectCodeArea = new JTextArea(10, 20);
        objectCodePanel.add(new JLabel("Object Code"), BorderLayout.NORTH);
        objectCodePanel.add(new JScrollPane(objectCodeArea), BorderLayout.CENTER);
        outputSubPanel.add(objectCodePanel);

        inputPanel.add(outputSubPanel);

        // Add inputPanel to the frame
        add(inputPanel, BorderLayout.CENTER);

        // Button for Pass One and Pass Two
        JPanel buttonPanel = new JPanel();
        JButton passOneButton = new JButton("Pass One");
        JButton passTwoButton = new JButton("Pass Two");
        buttonPanel.add(passOneButton);
        buttonPanel.add(passTwoButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize symbol table, operation table, intermediate code, object code, and
        // output code
        symtab = new HashMap<>();
        optab = new HashMap<>();
        intermediateCode = new StringBuilder();
        objectCode = new StringBuilder();
        outputCode = new StringBuilder();

        // Load sample OPTAB
        loadSampleOptab();

        // Pass One Button Action
        passOneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passOne();
            }
        });

        // Pass Two Button Action
        passTwoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passTwo();
            }
        });
    }

    // Load Sample OPTAB (Operation Table)
    private void loadSampleOptab() {
        optab.put("LDA", "33");
        optab.put("STA", "44");
        optab.put("LDCH", "53");
        optab.put("STCH", "57");

        // Display OPTAB in the area
        optabArea.setText("");
        for (Map.Entry<String, String> entry : optab.entrySet()) {
            optabArea.append(entry.getKey() + " -> " + entry.getValue() + "\n");
        }
    }

    // Pass One: Generate Symbol Table and Intermediate Code
    // Pass One: Generate Symbol Table and Intermediate Code
    private void passOne() {
        // Read input and optab from text areas
        String inputContent = inputArea.getText();
        symtab.clear();
        intermediateCode.setLength(0); // Clear previous intermediate code

        int start = 0;
        Scanner inputScanner = new Scanner(inputContent);

        // Read first line of input
        if (inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine();
            String[] parts = line.split("\\s+");
            if (parts.length == 3 && parts[1].equals("START")) {
                start = Integer.parseInt(parts[2], 16); // Parse START address as hexadecimal
                locctr = start;
                intermediateCode.append(String.format("\t%s\t%s\t%s\n", parts[0], parts[1], parts[2]));
            }
        }

        while (inputScanner.hasNextLine()) {
            String line = inputScanner.nextLine();
            String[] parts = line.split("\\s+");

            if (parts.length == 3) {
                String label = parts[0];
                String opcode = parts[1];
                String operand = parts[2];

                if (!opcode.equals("END")) {
                    intermediateCode.append(String.format("%04X\t%s\t%s\t%s\n", locctr, label, opcode, operand)); // Hexadecimal
                                                                                                                  // LOCCTR
                    if (!label.equals("") && (!label.equals("-"))) {
                        symtab.put(label, locctr);
                    }

                    if (optab.containsKey(opcode)) {
                        locctr += 3;
                    } else if (opcode.equals("WORD")) {
                        locctr += 3;
                    } else if (opcode.equals("RESW")) {
                        locctr += 3 * Integer.parseInt(operand); // RESW increments LOCCTR by multiple of 3
                    } else if (opcode.equals("BYTE")) {
                        locctr += 1;
                    } else if (opcode.equals("RESB")) {
                        locctr += Integer.parseInt(operand);
                    }
                }
            }
        }

        // Final END line and program length
        length = locctr - start;
        intermediateCode.append(String.format("%04X\t*\tEND\t*\n", locctr)); // Hexadecimal LOCCTR

        // Display intermediate code and symbol table
        intermediateArea.setText(intermediateCode.toString());
        symtabArea.setText("");
        for (Map.Entry<String, Integer> entry : symtab.entrySet()) {
            symtabArea.append(entry.getKey() + ": " + Integer.toHexString(entry.getValue()).toUpperCase() + "\n"); // Hexadecimal
                                                                                                                   // values
                                                                                                                   // for
                                                                                                                   // SYMTAB
        }

        JOptionPane.showMessageDialog(this, "Pass One Completed");
    }

    private void passTwo() {
        String[] assemblyCode = inputArea.getText().split("\\n");
        objectCode.setLength(0); // Clear previous object code
        outputCode.setLength(0); // Clear previous output code
        int startAddress = 0x002000; // Example starting address in hexadecimal

        // Write the header record (H) in object code
        objectCode.append(String.format("H^%s^%06X^%06X\n", "--", startAddress, length));

        StringBuilder textRecord = new StringBuilder("T^" + String.format("%06X", startAddress));
        StringBuilder textRecordContent = new StringBuilder();
        int currentRecordLength = 0;

        for (String line : assemblyCode) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            String[] parts = line.split("\\s+");

            if (parts.length < 2)
                continue;

            String label = (parts.length == 3) ? parts[0] : "-";
            String opcode = parts[(parts.length == 3) ? 1 : 0];
            String operand = (parts.length == 3) ? parts[2] : parts[1];

            String objectCodeLine = "";

            // Generate object code based on the opcode
            if (opcode.equals("WORD")) {
                objectCodeLine = String.format("%06X", Integer.parseInt(operand)); // Handle WORD as hexadecimal
            } else if (optab.containsKey(opcode)) {
                String opcodeHex = optab.get(opcode);
                String resolvedAddress = "0000";

                if (symtab.containsKey(operand)) {
                    resolvedAddress = String.format("%04X", symtab.get(operand)); // Hexadecimal address from SYMTAB
                } else if (operand.matches("\\d+")) {
                    resolvedAddress = String.format("%04X", Integer.parseInt(operand)); // Operand as hexadecimal
                }

                objectCodeLine = opcodeHex + resolvedAddress;
            } else if (opcode.equals("BYTE")) {
                if (operand.startsWith("C'") && operand.endsWith("'")) {
                    for (char c : operand.substring(2, operand.length() - 1).toCharArray()) {
                        objectCodeLine += String.format("%02X", (int) c); // Convert characters to hexadecimal
                    }
                } else if (operand.startsWith("X'") && operand.endsWith("'")) {
                    objectCodeLine = operand.substring(2, operand.length() - 1); // Handle hex string
                }
            }

            // Add the current instruction to the output table (whether it generates object
            // code or not)
            outputCode.append(
                    String.format("%04X\t%s\t%s\t%s\t%s\n", startAddress, label, opcode, operand, objectCodeLine)); // Hexadecimal
                                                                                                                    // start
                                                                                                                    // address

            // Append the generated object code to the text record (only for instructions
            // that generate object code)
            if (!objectCodeLine.isEmpty()) {
                if ((currentRecordLength + objectCodeLine.length() / 2) > 30) { // Ensure max 30 bytes in a T record
                    textRecord.append("^").append(String.format("%02X", currentRecordLength)).append("^")
                            .append(textRecordContent.toString()).append("\n");
                    objectCode.append(textRecord.toString());

                    // Start a new text record
                    textRecord.setLength(0);
                    textRecord.append("T^" + String.format("%06X", startAddress));
                    textRecordContent.setLength(0);
                    currentRecordLength = 0;
                }

                textRecordContent.append(objectCodeLine).append("^");
                currentRecordLength += objectCodeLine.length() / 2;
            }

            // Update the start address for the next instruction
            if (optab.containsKey(opcode)) {
                startAddress += 3;
            } else if (opcode.equals("WORD")) {
                startAddress += 3;
            } else if (opcode.equals("RESW")) {
                startAddress += 3 * Integer.parseInt(operand);
            } else if (opcode.equals("BYTE")) {
                startAddress += (objectCodeLine.length() / 2); // Calculate byte size
            } else if (opcode.equals("RESB")) {
                startAddress += Integer.parseInt(operand);
            }
        }

        // Finalize the last text record
        if (currentRecordLength > 0) {
            textRecord.append("^").append(String.format("%02X", currentRecordLength)).append("^")
                    .append(textRecordContent.toString()).append("\n");
            objectCode.append(textRecord.toString());
        }

        // Write the end record (E) in object code
        objectCode.append(String.format("E^%06X\n", 0x002000)); // End at the starting address

        // Display the object code and output
        objectCodeArea.setText(objectCode.toString());
        outputArea.setText(outputCode.toString());

        JOptionPane.showMessageDialog(this, "Pass Two Completed");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TwoPassAssemblerGUI().setVisible(true);
            }
        });
    }
}