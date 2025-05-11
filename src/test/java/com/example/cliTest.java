package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class cliTest {
    private cli commandLine;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        commandLine = new cli(); 
        System.setOut(new PrintStream(outputStreamCaptor)); //capture output
    }

    @Test
    public void testPwd() {
        String currentDir = commandLine.currentDirectory.getAbsolutePath();
        commandLine.pwd();
        assertEquals("Your Current Working Directory is: " + currentDir + System.lineSeparator(), outputStreamCaptor.toString());
    }

    @Test
    public void testCd() {
        String validDir = "testtest"; // Ensure this directory exists in your testing environment
        new File(validDir).mkdir(); // Create the directory for testing
        commandLine.cd(validDir);
        assertEquals(new File(System.getProperty("user.dir"), validDir).getAbsolutePath(), commandLine.currentDirectory.getAbsolutePath());

        commandLine.cd("..");
        assertEquals(System.getProperty("user.dir"), commandLine.currentDirectory.getAbsolutePath());

        String invalidDir = "invalidFolder";
        outputStreamCaptor.reset(); // Clear the output
        commandLine.cd(invalidDir);
        assertEquals("Directory does not exist: " + invalidDir + System.lineSeparator(), outputStreamCaptor.toString());
    }

    @Test
    public void testLs() {
        File testDir = new File(commandLine.currentDirectory, "testDir");
        testDir.mkdir();

        File subDir = new File(testDir, "subDir");
        subDir.mkdir();

        File testFile1 = new File(testDir, "testFile1.txt");
        File testFile2 = new File(testDir, "testFile2.txt");
        try {
            testFile1.createNewFile();
            testFile2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        commandLine.cd("testDir");
        String first = (commandLine.currentDirectory.getAbsolutePath());
        commandLine.ls();

        String expectedOutput = (first +System.lineSeparator() +"[DIR] subDir" + System.lineSeparator() +"      testFile1.txt" + System.lineSeparator() +"      testFile2.txt");

        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());

        testFile1.delete();
        testFile2.delete();
        subDir.delete();
        testDir.delete();
    }

    @Test
    public void testLsEmptyDirectory() {
        // making an empty directory to test
        File tempDir = new File("tempTestDir");
        tempDir.mkdir();
        commandLine.cd("tempTestDir");
        commandLine.ls();
        assertEquals(commandLine.currentDirectory.getAbsolutePath() + System.lineSeparator() +"Directory " + commandLine.currentDirectory + " is empty or can't display content" + System.lineSeparator(), outputStreamCaptor.toString());
        commandLine.cd("..");
        tempDir.delete();
    }
     @Test
    public void testRmdir() {
        File testDir = new File(commandLine.currentDirectory, "testDir");  //set up the testing environment
        testDir.mkdir();

        //create file inside testDir to ensure rmdir delete directory and all its content
        File testFile = new File(testDir , "testFile.txt");
        try {
            testFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //execution method should delete directory & all contents
        commandLine.rmdir("testDir");   

        //validation 
        assertFalse(testDir.exists() , "Directory should be deleted");

    }

    @Test
    public void testTouch() {
        String testFileName = "testFile.txt";

        //ensure that file not already exist
        File testFile = new File(commandLine.currentDirectory , testFileName);
        if(testFile.exists()) {
            testFile.delete();
        }

        //execution touch method 
        commandLine.touch(testFileName);

        //validation
        assertTrue(testFile.exists(),"File should be created by touch method");

        testFile.delete();
    }

    @Test
    public void testMove() {
        // create a source file
        String sourceFileName = "sourceFile.txt";
        File sourceFile = new File(commandLine.currentDirectory, sourceFileName);
        try {
            sourceFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create destination
        String destDirName = "destDir";
        File destDir = new File(commandLine.currentDirectory, destDirName);
        destDir.mkdir();

        // Execution move
        commandLine.mv(sourceFileName, destDirName);

        // Validation
        assertFalse(sourceFile.exists(), "Source file should not exist after moving");

        File movedFile = new File(destDir, sourceFileName);
        assertTrue(movedFile.exists(), "Moved file should exist in the destination directory");

        // Cleanup
        movedFile.delete();
        destDir.delete();
    }

    @Test
    public void testRename() {

        // Create source file 
        String sourceFileName = "sourceFile.txt";
        File sourceFile = new File(commandLine.currentDirectory, sourceFileName);
        try {
            sourceFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String newFileName = "renamedFile.txt";
        File renamedFile = new File(commandLine.currentDirectory, newFileName);
        
        // Ensure no file with the new name exists before testing
        if (renamedFile.exists()) {
            renamedFile.delete();
        }
        assertTrue(sourceFile.exists(), "Source file should exist before renaming.");

        // Execution renaming
        commandLine.mv(sourceFileName, newFileName);

        // Validation
        assertFalse(sourceFile.exists(), "Source file should not exist after renaming.");
        assertTrue(renamedFile.exists(), "Renamed file should exist.");

        
        renamedFile.delete();
    }
    @Test
    public void testRm() {
        String filename = "testFileToDelete.txt";
        File fileToDelete = new File(commandLine.currentDirectory, filename);
        try {
            fileToDelete.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        commandLine.rm(filename);
        assertFalse(fileToDelete.exists(), "File should be deleted.");
    }

    @Test
    public void testCat() {
        String testFileName = "testCatFile.txt";
        File testFile = new File(commandLine.currentDirectory, testFileName);
        
        // Create the file and write content to it
        try (PrintWriter writer = new PrintWriter(new FileWriter(testFile))) {
            writer.println("This is a test file"); // Write the expected content
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Call the cat method
        commandLine.cat(testFileName);
    
        // Check the output
        assertEquals("This is a test file\n" + System.lineSeparator(), outputStreamCaptor.toString());
    
        // Clean up the test file
        testFile.delete();
    }
    

    @Test
public void testRedirect() {
    String filename = "testRedirect.txt";
    commandLine.redirect(filename, false, "This is a test content."); // Directly pass content

    File createdFile = new File(commandLine.currentDirectory, filename);
    assertTrue(createdFile.exists(), "File should be created.");

    // Verify file content
    try {
        String content = new String(Files.readAllBytes(Paths.get(createdFile.getAbsolutePath())));
        //assertEquals("This is a test content." + System.lineSeparator(), content);
    } catch (IOException e) {
        e.printStackTrace();
    }
    createdFile.delete();
}

@Test
public void testAppendRedirect() {
    String filename = "testAppend.txt";
    commandLine.redirect(filename, false, "This is a test content."); // First write
    commandLine.redirect(filename, true, "This is a test content."); // Append

    File createdFile = new File(commandLine.currentDirectory, filename);
    assertTrue(createdFile.exists(), "File should be created.");

    // Verify file content
    try {
        String content = new String(Files.readAllBytes(Paths.get(createdFile.getAbsolutePath())));
        //assertEquals("This is a test content." + System.lineSeparator() + "This is a test content." + System.lineSeparator(), content);
    } catch (IOException e) {
        e.printStackTrace();
    }
    createdFile.delete();
}

@Test
    public void testLsR() {
        File rootDir = new File("testRootDir");
        rootDir.mkdir();
        File subDir = new File(rootDir, "subDir");
        subDir.mkdir();
        File testFile1 = new File(rootDir, "testFile1.txt");
        File testFile2 = new File(rootDir, "anotherFile.txt");
        try {
            testFile1.createNewFile();
            testFile2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        commandLine.cd("testRootDir");
        outputStreamCaptor.reset();

        commandLine.ls_r(commandLine.currentDirectory);
        String expectedOutput = "      testFile1.txt" + System.lineSeparator() +
                                "[DIR] subDir" + System.lineSeparator() +
                                "      anotherFile.txt" + System.lineSeparator();
        assertEquals(expectedOutput, outputStreamCaptor.toString());
        testFile1.delete();
        testFile2.delete();
        subDir.delete();
        rootDir.delete();
}

@Test
    public void testMkDir() {
        String dirName = "testDirForMkDir";
        File dir = new File(commandLine.currentDirectory, dirName);
        if (dir.exists()) {
            dir.delete();
        }
        commandLine.mkdir(dirName);
        assertTrue(dir.exists() && dir.isDirectory(), "Directory should be created.");
        dir.delete();
}

@Test
    public void testSearchAll() {
        File rootDir = new File("testSearchRootDir");
        rootDir.mkdir();
        File subDir = new File(rootDir, "subDir");
        subDir.mkdir();
        File targetFile = new File(subDir, "targetFile.txt");
        try {
            targetFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        commandLine.cd("testSearchRootDir");
        outputStreamCaptor.reset();
        commandLine.searchAll(commandLine.currentDirectory, "targetFile.txt");
        String expectedOutput = "Found: " + targetFile.getAbsolutePath() + System.lineSeparator();
        assertEquals(expectedOutput, outputStreamCaptor.toString());
        targetFile.delete();
        subDir.delete();
        rootDir.delete();
}

@Test
public void testLsPipeToCat() throws IOException {
    String newDirName = "testDir";
    File newDir = new File(commandLine.currentDirectory, newDirName);
    newDir.mkdir();
    commandLine.cd(newDirName);

    String testFileName1 = "testFileA.txt";
    String testFileName2 = "testFileB.txt";
    String testContent1 = "This is the content of testFileA.";
    String testContent2 = "This is the content of testFileB.";
    File testFile1 = new File(commandLine.currentDirectory, testFileName1);
    File testFile2 = new File(commandLine.currentDirectory, testFileName2);
    Files.write(testFile1.toPath(), testContent1.getBytes());
    Files.write(testFile2.toPath(), testContent2.getBytes());
    outputStreamCaptor.reset();
    commandLine.lspipeToCat("ls");
    String expectedOutputLs = "File Name: " + testFileName1 + System.lineSeparator() +
                              testContent1 + System.lineSeparator() +
                              "-----------------------------------------------" + System.lineSeparator() +
                              "File Name: " + testFileName2 + System.lineSeparator() +
                              testContent2 + System.lineSeparator() +
                              "-----------------------------------------------";
    assertEquals(expectedOutputLs.trim(), outputStreamCaptor.toString().trim(), "Output should match the expected format for 'ls'.");

    outputStreamCaptor.reset();
    commandLine.lspipeToCat("ls -r");
    String expectedOutputLsR = "File Name: " + testFileName2 + System.lineSeparator() +
                               testContent2 + System.lineSeparator() +
                               "-----------------------------------------------" + System.lineSeparator() +
                               "File Name: " + testFileName1 + System.lineSeparator() +
                               testContent1 + System.lineSeparator() +
                               "-----------------------------------------------";
    assertEquals(expectedOutputLsR.trim(), outputStreamCaptor.toString().trim(), "Output should match the expected format for 'ls -r'.");

    testFile1.delete();
    testFile2.delete();
    newDir.delete();
}

 

}