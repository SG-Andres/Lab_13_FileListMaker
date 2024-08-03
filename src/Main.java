import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<String> list = new ArrayList<>();
    static boolean needsToBeSaved = false;
    static File currentFile = null;

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        JFileChooser fileChooser = new JFileChooser("src");
        final String menu = "A - Add D - Delete I - Insert V - View Q - Quit M - Move O - Open a list file from disk S - Save the current list file to disk C - Clear removes all the elements from the current list N - Create a new list";
        boolean done = false;
        String cmd = "";

        do {
            // Display list
            displayList();
            // Display options and get choice
            cmd = SafeInput.getRegExString(console, menu, "[AaDdIiVvQqMmOoSsCcNn]");
            cmd = cmd.toLowerCase();
            switch (cmd) {
                case "a": //add
                    String addThing = SafeInput.getNonZeroLenString(console, "What do you wish to add?");
                    list.add(addThing);
                    needsToBeSaved = true;
                    break;

                case "d": //delete
                    int number = SafeInput.getInt(console, "Enter the number of the item you wish to delete");
                    int actual = number - 1;
                    if (actual >= 0 && actual < list.size()) {
                        list.remove(actual);
                        needsToBeSaved = true;
                    } else {
                        System.out.println("Invalid index. Please try again.");
                    }
                    break;

                case "i": //insert
                    int insertIndex = SafeInput.getInt(console, "Enter the position to insert") - 1;
                    if (insertIndex >= 0 && insertIndex <= list.size()) {
                        String insertThing = SafeInput.getNonZeroLenString(console, "What do you wish to insert?");
                        list.add(insertIndex, insertThing);
                        needsToBeSaved = true;
                    } else {
                        System.out.println("Invalid index. Please try again.");
                    }
                    break;

                case "v": // view
                    displayList();
                    break;

                case "m": //move
                    int fromIndex = SafeInput.getInt(console, "Enter the number of the item you wish to move") - 1;
                    if (fromIndex >= 0 && fromIndex < list.size()) {
                        int toIndex = SafeInput.getInt(console, "Enter the new position for the item") - 1;
                        if (toIndex >= 0 && toIndex <= list.size()) {
                            String item = list.get(fromIndex);
                            list.remove(fromIndex);
                            if (toIndex > fromIndex) {
                                toIndex--;
                            }
                            list.add(toIndex, item);
                            needsToBeSaved = true;
                        } else {
                            System.out.println("Invalid position Please try again.");
                        }
                    } else {
                        System.out.println("Invalid index Please try again.");
                    }
                    break;

                case "o": //open File idk if i had to add this this wasn't in the word doc
                    if (needsToBeSaved) {
                        if (SafeInput.getYNConfirm(console, "You have unsaved changes. Would you like to save them before opening a new file?")) {
                            saveList();
                        }
                    }
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile.exists()) {
                            loadList(selectedFile);
                        } else {
                            System.out.println("Please select a valid file.");
                        }
                    } else {
                        System.out.println("No file selected.");
                    }
                    break;

                case "s": //save
                    saveList();
                    break;

                case "c": //clear
                    if (list.size() > 0) {
                        if (SafeInput.getYNConfirm(console, "Are you sure you want to clear the list?")) {
                            list.clear();
                            needsToBeSaved = true;
                            System.out.println("List cleared.");
                        }
                    } else {
                        System.out.println("The list is already empty.");
                    }
                    break;

                case "n": //new List idk if i had to add this this wasn't in the word doc
                    if (needsToBeSaved) {
                        if (SafeInput.getYNConfirm(console, "You have unsaved changes would you like to save them before creating a new list?")) {
                            saveList();
                        }
                    }
                    String newListName = SafeInput.getNonZeroLenString(console, "Enter the basename for the new list");
                    currentFile = new File("src/" + newListName + ".txt");
                    list.clear();
                    needsToBeSaved = true;
                    System.out.println("New list created with name: " + currentFile.getName());
                    break;

                case "q": // Quit
                    if (needsToBeSaved) {
                        if (SafeInput.getYNConfirm(console, "You have unsaved changes would you like to save them before quitting?")) {
                            saveList();
                        }
                    }
                    done = true;
                    break;
            }
            System.out.println("cmd is " + cmd);

        } while (!done);
    }

    private static void displayList() {
        System.out.println("===========================================");
        if (list.size() != 0) {
            for (int i = 0; i < list.size(); i++) {
                System.out.printf("%3d: %s\n", i + 1, list.get(i));
            }
        } else {
            System.out.println("The list is empty.");
        }
        System.out.println("===========================================");
    }

    private static void saveList() {
        if (currentFile == null) {
            JFileChooser fileChooser = new JFileChooser("src");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt"));
            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
                if (!currentFile.getName().endsWith(".txt")) {
                    currentFile = new File(currentFile.getPath() + ".txt");
                }
            } else {
                System.out.println("No file selected. Save operation canceled.");
                return;
            }
        }
        try (FileWriter writer = new FileWriter(currentFile)) {
            for (String record : list) {
                writer.write(record + "\n");
            }
            needsToBeSaved = false;
            System.out.println("Data saved successfully to " + currentFile.getName());
        } catch (IOException e) {
            System.out.println("An error occurred while saving the data.");
            e.printStackTrace();
        }
    }



    private static void loadList(File file) {
        list.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
            currentFile = file;
            needsToBeSaved = false;
            System.out.println("Data loaded successfully from " + file.getName());
        } catch (IOException e) {
            System.out.println("An error occurred while loading the data.");
            e.printStackTrace();
        }
    }
}
