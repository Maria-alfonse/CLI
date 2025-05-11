package com.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class cli {
    public File currentDirectory;

    public cli() {
        currentDirectory = new File(System.getProperty("user.dir"));
    }

    public static void main(String[] args) {
        cli commandline = new cli();
        String command;
        Scanner scanner = new Scanner(System.in);

        System.out.println(
                "Welcome to our CLI program! Enter your desired command, type help to discover command list, type exit to end CLI");

        while (true) {
            System.out.print("> ");
            command = scanner.nextLine().trim();

            if (command.equals("exit")) {
                System.out.println("Thank you for using our CLI");
                break;
            } else if (command.equals("help")) {
                System.out.println("Available commands:\n" +
                        "- pwd: Prints the working directory.\n" +
                        "- cd: Changes the current directory.\n" +
                        "- ls: Lists contents of the current directory.\n" +
                        "- ls -r: Lists contents in reverse order.\n" +
                        "- mkdir: Creates directories with given names.\n" +
                        "- rmdir: Removes empty directories with given names.\n" +
                        "- touch: Creates files with given names.\n" +
                        "- mv: Moves files or directories; if two files are specified, moves first to second.\n" +
                        "- rm: Removes files (with -r for recursive directory deletion); prompts if file is unwritable.\n"
                        +
                        "- s -a: Search for a given file name\n" +
                        "- cat: Prints file contents; with no args, reads from user input.\n" +
                        "- >: Redirects output to a file, overwriting existing content.\n" +
                        "- >>: Appends output to a file if it exists.\n" +
                        "- |: Pipes output of one command as input to the next.");

            } else if (command.equals("pwd")) {
                commandline.pwd();
            } else if (command.startsWith("cd")) {
                String path = command.substring(3).trim();
                if (command.equals("cd..")) {
                    path = "..";
                }
                commandline.cd(path);
            } else if (command.equals("ls")) {
                commandline.ls();
            } else if (command.startsWith("rm ")) {
                String filename = command.substring(3).trim();
                commandline.rm(filename);
            } else if (command.startsWith("cat ")) {
                String filename = command.substring(4).trim();
                commandline.cat(filename);
            } else if (command.startsWith("> ")) {
                String filename = command.substring(2).trim();
                commandline.redirect(filename, false, scanner);
            } else if (command.startsWith(">> ")) {
                String filename = command.substring(3).trim();
                commandline.redirect(filename, true, scanner);
            }

            else if (command.startsWith("rmdir ")) {
                String path = command.substring(6).trim();
                commandline.rmdir(path);
            } else if (command.startsWith("touch")) {
                String filename = command.substring(6).trim();
                commandline.touch(filename);
            } else if (command.startsWith("mv")) {
                String[] parts = command.substring(3).trim().split("\\s+");
                if (parts.length == 2) {
                    commandline.mv(parts[0], parts[1]);
                } else {
                    System.out.println("Usage: mv <source> <destination>");
                }
            } else if (command.equals("ls -r")) {
                commandline.ls_r(commandline.currentDirectory);
            } else if (command.startsWith("mkdir ")) {
                String dirname = command.substring(6).trim();
                commandline.mkdir(dirname);
            } else if (command.startsWith("s -a")) {
                String filename = command.substring(5).trim();
                commandline.searchAll(commandline.currentDirectory, filename);
            } else if (command.equals("ls -r | cat")) {
                commandline.lspipeToCat("ls -r");
            } else if (command.equals("ls | cat")) {
                commandline.lspipeToCat("ls ");
            } else {
                System.out.println("Command Unknown");
            }
        }
        scanner.close();
    }

    public void pwd() {
        System.out.println("Your Current Working Directory is: " + currentDirectory.getAbsolutePath());
    }

    public void cd(String path) {
        if (path.equals("..")) {
            currentDirectory = currentDirectory.getParentFile();
            if (currentDirectory == null) {
                currentDirectory = new File(System.getProperty("user.dir"));
                System.out.println("You are at the Root Directory");
            } else {
                System.out.println(currentDirectory.getAbsolutePath());
            }
        } else {
            File newDirectory = new File(currentDirectory, path);
            if (newDirectory.exists() && newDirectory.isDirectory()) {
                currentDirectory = newDirectory;
                System.out.println(currentDirectory.getAbsolutePath());
            } else {
                System.out.println("Directory does not exist: " + path);
            }
        }
    }

    public void ls() {
        File[] list = currentDirectory.listFiles();
        if (list == null || list.length == 0) {
            System.out.println("Directory " + currentDirectory + " is empty or can't display content");
        } else {
            Arrays.sort(list, (file1, file2) -> file1.getName().compareToIgnoreCase(file2.getName()));
            for (File item : list) {
                if (item.isDirectory()) {
                    System.out.println("[DIR] " + item.getName());
                } else {
                    System.out.println("      " + item.getName());
                }
            }
        }
    }

    public void rm(String filename) {
        File fileToDelete = new File(currentDirectory, filename);
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            if (fileToDelete.delete()) {
                System.out.println("Deleted: " + filename);
            } else {
                System.out.println("Failed to delete: " + filename);
            }
        } else {
            System.out.println("File does not exist: " + filename);
        }
    }

    public void cat(String filename) {
        File fileToRead = new File(currentDirectory, filename);
        if (fileToRead.exists() && fileToRead.isFile()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(fileToRead.getAbsolutePath())));
                System.out.println(content);
            } catch (IOException e) {
                System.out.println("Error reading file: " + filename);
            }
        } else {
            System.out.println("File does not exist: " + filename);
        }
    }

    public void redirect(String filename, boolean append, Scanner scanner) {
        StringBuilder contentBuilder = new StringBuilder();

        System.out.println("Enter content (end with a blank line):");

        while (true) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isEmpty()) {
                    break; // Exit if a blank line is encountered
                }
                contentBuilder.append(line).append(System.lineSeparator());
            } else {
                System.out.println("No input detected. Exiting content entry.");
                break; // Exit if no input is detected
            }
        }

        String content = contentBuilder.toString();

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(currentDirectory, filename), append))) {
            writer.print(content);
            System.out.println("Content written to: " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
        }
    }

    // Overloaded method for testing
    public void redirect(String filename, boolean append, String content) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(currentDirectory, filename), append))) {
            writer.print(content);
            System.out.println("Content written to: " + filename);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
        }
    }

    public void rmdir(String path) {
        File DirectoryToDelete = new File(currentDirectory, path);

        if (!DirectoryToDelete.exists()) {
            System.out.println("Directory does not exist: " + path);
            return;
        } else if (!DirectoryToDelete.isDirectory()) {
            System.out.println("Specified path is not a directory: " + path);
        }

        File[] contents = DirectoryToDelete.listFiles();
        if (contents != null) {
            for (File file : contents) {
                file.delete();
            }
        }

        if (DirectoryToDelete.delete()) {
            System.out.println("Directory deleted successfully: " + path);
        } else {
            System.out.println("Failed to delete directory: " + path);
        }
    }

    public void touch(String filename) {
        File newfile = new File(currentDirectory, filename);

        try {
            if (newfile.createNewFile()) {
                System.out.println("File created successfully: " + filename);
            } else {
                System.out.println("File already exists: " + filename);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + filename);
            e.printStackTrace();
        }

    }

    public void mv(String source, String destination) {
        File sourceFile = new File(currentDirectory, source);

        if (!sourceFile.exists()) {
            System.out.println("Source file does not exist: " + source);
            return;
        }

        File destenationFile = new File(currentDirectory, destination);

        if (destenationFile.isDirectory()) {
            File movedFile = new File(destenationFile, sourceFile.getName());
            if (sourceFile.renameTo(movedFile)) {
                System.out.println("File moved successfully: " + source + " -> " + movedFile.getAbsolutePath());
            } else {
                System.out.println("Failed to move file: " + source);
            }
        } else {
            if (sourceFile.renameTo(destenationFile)) {
                System.out.println("File renamed successfully: " + source + " -> " + destination);
            } else {
                System.out.println("Failed to rename file: " + source);
            }
        }

    }

    public void ls_r(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            Arrays.sort(files, Collections.reverseOrder());
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("[DIR] " + file.getName());
                } else {
                    System.out.println("      " + file.getName());
                }
            }
        }
    }

    public void mkdir(String dirname) {
        File newDir = new File(currentDirectory, dirname);
        if (newDir.mkdir()) {
            System.out.println("Directory created: " + dirname);
        } else {
            System.out.println("Failed to create directory: " + dirname);
        }
    }

    public void searchAll(File directory, String searchName) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().equalsIgnoreCase(searchName)) {
                    System.out.println("Found: " + file.getAbsolutePath());
                }
                if (file.isDirectory()) {
                    searchAll(file, searchName);
                }
            }
        }
    }

    public void lspipeToCat(String command) {
        File[] list = currentDirectory.listFiles();
        if (list == null || list.length == 0) {
            System.out.println("Directory " + currentDirectory + " is empty or can't display content");
            return;
        } else if (command.equals("ls")) {
            Arrays.sort(list, (file1, file2) -> file1.getName().compareToIgnoreCase(file2.getName()));
        } else if (command.equals("ls -r")) {
            Arrays.sort(list, Collections.reverseOrder());
        }
        for (File item : list) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(item.getAbsolutePath())));
                System.out.println("File Name: " + item.getName());
                System.out.println(content);
                System.out.println("-----------------------------------------------");
            } catch (IOException e) {
                System.out.println("Error reading file: " + item.getName());
            }
        }
    }

}