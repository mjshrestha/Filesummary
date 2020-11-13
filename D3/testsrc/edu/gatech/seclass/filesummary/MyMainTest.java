package edu.gatech.seclass.filesummary;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MyMainTest {

    /*
    Place all  of your tests in this class, optionally using MainTest.java as an example.
    */
    private ByteArrayOutputStream outStream;
    private ByteArrayOutputStream errStream;
    private PrintStream outOrig;
    private PrintStream errOrig;
    private Charset charset = StandardCharsets.UTF_8;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        errStream = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(errStream);
        outOrig = System.out;
        errOrig = System.err;
        System.setOut(out);
        System.setErr(err);
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(outOrig);
        System.setErr(errOrig);
    }

    /*
     *  TEST UTILITIES
     */

    // Create File Utility
    private File createTmpFile() throws Exception {
        File tmpfile = temporaryFolder.newFile();
        tmpfile.deleteOnExit();
        return tmpfile;
    }

    // Write File Utility
    private File createInputFile(String input) throws Exception {
        File file =  createTmpFile();

        OutputStreamWriter fileWriter =
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);

        fileWriter.write(input);

        fileWriter.close();
        return file;
    }


    //Read File Utility
    private String getFileContent(String filename) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    /*
     * TEST FILE CONTENT
     * Assumption:
     * Special characters are also accepted in filesummary utility
     * Blank s, k and r opt strings produce errors
     */

    private static final String FILE1 = "1 dog" + System.lineSeparator() + "2 cat";
    private static final String FILE2 = "Log: 123 abc\nError: 123 xyz\nError: 567 abc\nLog: 567 abc";

    //Added the following file to check for alphanumeric and special characters and empty file
    private static final String FILE3 = "Hello!@ Is this test file?\nYes @@tester.\nThank you!!!";
    private static final String FILE4 = "Up1_and_Up2 with the white and gold!\rDown1_and_Down2 with the red and black!!";
    private static final String FILE5 = "";

    /*
     *   TEST CASES
     */

    // Purpose: Test when the length of string in opt s is one
    // Frame #: Test case 4
    // Test corrected as the usage of -s was not clear before
    @Test
    public void fileSummaryTest1() throws Exception {
        File inputFile1 = createInputFile(FILE2);

        String args[] = {"-s", "r", inputFile1.getPath()};
        Main.main(args);

        String expected1 = FILE2;

        String actual1 = getFileContent(inputFile1.getPath());

        assertEquals("The files differ!", expected1, actual1);
        assertEquals("rr", outStream.toString().trim());
    }

    // Purpose: Test case when only -r is specified without any string value
    // Frame #: Test Case 7  		<error>
    // Updated for D2 deliverable
    @Test
    public void fileSummaryTest2() throws Exception {
        File inputFile2 = createInputFile(FILE1);

        String args[]  = {"-r", inputFile2.getPath()};
        Main.main(args);

        assertEquals("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>", errStream.toString().trim());
    }

    // Purpose: Test case when both -r and -k are passed as arguments
    // Frame #: Test Case 11 		<error>
    // Updated for D2 deliverable
    @Test
    public void fileSummaryTest3() throws Exception {
        File inputFile3 = createInputFile(FILE1);

        String args[]  = {"-r", "1", "-k", "-1", inputFile3.getPath()};
        Main.main(args);

        assertEquals("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>", errStream.toString().trim());
    }

    // Purpose: Test when only opt -k is present and string length is more than one with alphanumeric characters
    // Frame #: Test Case 22 		(Key = 1.0.1.0.0.1.0.0.2.0.3.1.2.2.0.0.2.2.)
    @Test
    public void fileSummaryTest4() throws Exception {
        File inputFile4 = createInputFile(FILE2);

        String args[] = {"-k", "123", inputFile4.getPath()};
        Main.main(args);

        String expected4 = "Log: 123 abc\nError: 123 xyz";

        String actual4 = getFileContent(inputFile4.getPath());

        assertEquals("The files differ!", expected4, actual4);

    }

    // Purpose: Test when opt s and opt k are present with the string in opt s and k as alphanumeric characters
    // Frame #: Test Case 27 		(Key = 1.0.2.3.1.1.0.0.2.0.3.1.2.2.2.3.2.2.)
    // Test corrected as the usage of -s was not clear before
    @Test
    public void fileSummaryTest5() throws Exception {
        File inputFile5 = createInputFile(FILE1);

        String args[] = {"-k", "do", "-s", "23atoc", inputFile5.getPath()};
        Main.main(args);

        String expected5 = "1 dog";

        String actual5 = getFileContent(inputFile5.getPath());

        assertEquals("The files differ!", expected5, actual5);
        assertEquals("cat", outStream.toString().trim());
    }

    // Purpose: Test when opt s and opt r are present with the string in opt s and r as alphanumeric characters
    // Frame #: Test Case 29 		(Key = 1.0.2.3.1.2.3.1.1.0.0.0.2.2.2.2.2.2.)
    // Test corrected as the usage of -s was not clear before
    @Test
    public void fileSummaryTest6() throws Exception {
        File inputFile6 = createInputFile(FILE2);

        String args[] = {"-s", "567", "-r", "abc", inputFile6.getPath()};
        Main.main(args);

        String expected6 = "Error: 123 xyz";

        String actual6 = getFileContent(inputFile6.getPath());

        assertEquals("The files differ!", expected6, actual6);
        assertEquals("567", outStream.toString().trim());
    }

    // Purpose: Test case when only opt a with the integer 0 or null present
    // Frame #: Test Case 36 		(Key = 2.1.1.0.0.1.0.0.1.0.0.0.2.2.0.0.2.2.)
    // Updated for D2 deliverable
    @Test
    public void fileSummaryTest7() throws Exception {
        File inputFile7 = createInputFile(FILE2);

        String args[] = {"-a", inputFile7.getPath()};
        Main.main(args);

        String expected7 = "Error: 567 abc\nError: 123 xyz\nLog: 123 abc\nLog: 567 abc";

        String actual7 = getFileContent(inputFile7.getPath());

        assertEquals("The files differ!", expected7, actual7);
    }

    // Purpose: Test when opt a and opt k are present,
    // with the integer in opt a is 0 or null and string in k as alphanumeric characters
    // Frame #: Test Case 37 		(Key = 2.1.1.0.0.1.0.0.2.0.3.1.2.2.0.0.2.2.)
    @Test
    public void fileSummaryTest8() throws Exception {
        File inputFile8 = createInputFile(FILE1);

        String args[] = {"-a", "0", "-k", "2", inputFile8.getPath()};
        Main.main(args);

        String expected8 = "2 cat";

        String actual8 = getFileContent(inputFile8.getPath());

        assertEquals("The files differ!", expected8, actual8);
    }

    // Purpose: Test when opt a and opt r are present,
    // with the integer in opt a is 0 or null and string in r as alphanumeric characters
    // Frame #: Test Case 39 		(Key = 2.1.1.0.0.2.3.1.1.0.0.0.2.2.0.0.2.2.)
    @Test
    public void fileSummaryTest9() throws Exception {
        File inputFile9 = createInputFile(FILE2);

        String args[] = {"-r", "xyz", "-a", inputFile9.getPath()};
        Main.main(args);

        String expected9 = "Error: 567 abc\nLog: 123 abc\nLog: 567 abc";

        String actual9 = getFileContent(inputFile9.getPath());

        assertEquals("The files differ!", expected9, actual9);
    }

    // Purpose: Test when opt a and opt s are present,
    // with the integer in opt a is 0 or null and string in s as alphanumeric characters
    // Frame #: Test Case 41 		(Key = 2.1.2.3.1.1.0.0.1.0.0.0.2.2.2.1.2.2.)
    // Updated for D2 Deliverable
    @Test
    public void fileSummaryTest10() throws Exception {
        File inputFile10 = createInputFile(FILE2);

        String args[] = {"-s", "123z", "-a", inputFile10.getPath()};
        Main.main(args);

        String expected10 = "Error: 567 abc\nError: 123 xyz\nLog: 123 abc\nLog: 567 abc";

        String actual10 = getFileContent(inputFile10.getPath());

        assertEquals("The files differ!", expected10, actual10);
        assertEquals("123", outStream.toString().trim());
    }

    // Purpose: Test case when only opt a with the integer greater than 0 present
    // Frame #: Test Case 51 		(Key = 2.2.1.0.0.1.0.0.1.0.0.0.2.2.0.0.2.2.)
    @Test
    public void fileSummaryTest11() throws Exception {
        File inputFile11 = createInputFile(FILE1);

        String args[] = {"-a", "4", inputFile11.getPath()};
        Main.main(args);

        String expected11 = "1 dog" + System.lineSeparator() + "2 cat";

        String actual11 = getFileContent(inputFile11.getPath());

        assertEquals("The files differ!", expected11, actual11);
    }

    // Purpose: Test when opt a, opt s and opt k are present with integer of a greater than 0 and string in opt s and k as alphanumeric characters
    // Frame #: Test Case 57 		(Key = 2.2.2.3.1.1.0.0.2.0.3.1.2.2.2.4.2.2.)
    // Test corrected as the usage of -s was not clear before
    @Test
    public void fileSummaryTest12() throws Exception {
        File inputFile12 = createInputFile(FILE2);

        String args[] = {"-s", "or61", "-a", "5", "-k", "23", inputFile12.getPath()};
        Main.main(args);

        String expected12 = "Log: 123 abc\nError: 123 xyz";

        String actual12 = getFileContent(inputFile12.getPath());

        assertEquals("The files differ!", expected12, actual12);
        assertEquals("rror", outStream.toString().trim());
    }

    // Purpose: Test when opt a, opt s(at the end) and opt r are present with integer of a greater than 0 and string in opt s and r as alphanumeric characters
    // Frame #: Test Case 59 		(Key = 2.2.2.3.1.2.3.1.1.0.0.0.2.2.2.4.2.2.)
    // Test corrected as the usage of -s was not clear before
    @Test
    public void fileSummaryTest13() throws Exception {
        File inputFile13 = createInputFile(FILE2);

        String args[] = {"-a", "4", "-r", "ror", "-s", "3er", inputFile13.getPath()};
        Main.main(args);

        String expected13 = "Log: 123 abc\nLog: 567 abc";

        String actual13 = getFileContent(inputFile13.getPath());

        assertEquals("The files differ!", expected13, actual13);
        assertEquals("rr", outStream.toString().trim());
    }
    // Purpose: Test when opt a, opt s and opt k are present, with integer of a greater than 0,
    // string in opt s as special characters and and r as alphanumeric characters
    // Frame #: Test Case 62 		(Key = 2.2.2.3.2.1.0.0.2.0.3.1.2.2.2.4.2.2.)
    // Test corrected as the usage of -s was not clear before
    @Test
    public void fileSummaryTest14() throws Exception {
        File inputFile14 = createInputFile(FILE3);

        String args[] = {"-a", "1", "-s", "!@", "-k", "test", inputFile14.getPath()};
        Main.main(args);

        String expected14 = "Hello!@ Is this test file?\nYes @@tester.";

        String actual14 = getFileContent(inputFile14.getPath());

        assertEquals("The files differ!", expected14, actual14);
        assertEquals("!!!", outStream.toString().trim());
    }

    // Purpose: Test when opt a, opt s and opt r are present with integer of a greater than 0 and string in opt s and r as special characters
    // Frame #: Test Case 65 		(Key = 2.2.2.3.2.2.3.2.1.0.0.0.2.2.2.4.2.2.)
    // Test corrected as the usage of -s was not clear before

    // Failure Type: Test Failure - Used the same line separator used in file at the end of the file
    @Test
    public void fileSummaryTest15() throws Exception {
        File inputFile15 = createInputFile(FILE3);

        String args[] = {"-r", "@@", "-s", "?@", "-a", "2", inputFile15.getPath()};
        Main.main(args);

        //String expected15 = "Thank you!!!\nHello!@ Is this test file?";
        String expected15 = "Thank you!!!"+System.lineSeparator()+"Hello!@ Is this test file?";

        String actual15 = getFileContent(inputFile15.getPath());

        assertEquals("The files differ!", expected15, actual15);
        assertEquals("@@", outStream.toString().trim());
    }



    // Purpose: Test when opt a, with integer of a less than 0
    // Frame #: Test Case 1 		<Error>

    // Failure Type: BUG: filesummary fails when passed invalid input as the parameters of opt
    // like in this negative value for opt 'a'
    @Test
    public void fileSummaryTest16() throws Exception {
        File inputFile16 = createInputFile(FILE3);

        String args[] = {"-a", "-2", inputFile16.getPath()};
        Main.main(args);

        assertEquals("Opt a cannot be negative!!/nUsage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>", errStream.toString().trim());
    }



    // Purpose: Error when the file is empty
    // Frame #: Test Case 16 		<Error>

    // Failure Type: Corner Case - This version of filesummary doesn't generate error when file is empty but displays Null
    @Test
    public void fileSummaryTest17() throws Exception {
        File inputFile17 = createInputFile(FILE5);

        String args[] = {"-a", "0", inputFile17.getPath()};
        Main.main(args);

        //assertEquals("File is Empty!!", errStream.toString().trim());
        assertEquals("", errStream.toString().trim());
    }


    // Purpose: Test when all opt are absent and number of sequence of alphanumeric characters are calculated
    // Frame #: Test Case 21 		(Key = 1.0.1.0.0.1.0.0.1.0.0.0.2.2.0.0.2.2.)
    @Test
    public void fileSummaryTest18() throws Exception {
        File inputFile18 = createInputFile(FILE4);

        String args[] = {inputFile18.getPath()};
        Main.main(args);

        String expected18 = FILE4;

        String actual18 = getFileContent(inputFile18.getPath());

        assertEquals("The files differ!", expected18, actual18);
        assertEquals("16", outStream.toString().trim());
    }

    // Purpose: Test case when opt s(run first though placed after k) and opt k are present
    // with string in opt s as alphanumeric characters and k as special characters
    // Frame #: Test Case 28 		(Key = 1.0.2.3.1.1.0.0.2.0.3.2.2.2.2.3.2.2.)
    @Test
    public void fileSummaryTest19() throws Exception {
        File inputFile19 = createInputFile(FILE3);

        String args[] = {"-k", "!@", "-s", "Hest", inputFile19.getPath()};
        Main.main(args);

        String expected19 = "Hello!@ Is this test file?";

        String actual19 = getFileContent(inputFile19.getPath());

        assertEquals("The files differ!", expected19, actual19);
        assertEquals("teste", outStream.toString().trim());
    }

    // Purpose: Test case when opt a, opt s(run first though placed after r) and opt r are present
    // with integer of a greater than 0, string in opt r as alphanumeric characters and s as special characters
    // Frame #: Test Case 64 		(Key = 2.2.2.3.2.2.3.1.1.0.0.0.2.2.2.4.2.2.)

    // Failure Type: Test Failure - Used the same line separator used in file at the end of the file
    @Test
    public void fileSummaryTest20() throws Exception {
        File inputFile20 = createInputFile(FILE3);

        String args[] = {"-a", "2", "-r", "@@", "-s", "set hi", inputFile20.getPath()};
        Main.main(args);

        //String expected20 = "Thank you!!!\nHello!@ Is this test file?";
        String expected20 = "Thank you!!!"+System.lineSeparator()+"Hello!@ Is this test file?";

        String actual20 = getFileContent(inputFile20.getPath());

        assertEquals("The files differ!", expected20, actual20);
        assertEquals("s this test", outStream.toString().trim());
    }

    // Purpose: Test case when opt s and opt k are present
    // with string in opt k as alphanumeric characters and s as special characters
    // Frame #: Test Case 32 		(Key = 1.0.2.3.2.1.0.0.2.0.3.1.2.2.2.3.2.2.)
    @Test
    public void fileSummaryTest21() throws Exception {
        File inputFile21 = createInputFile(FILE4);

        String args[] = { "-s", "_ !", "-k", "p1", inputFile21.getPath()};
        Main.main(args);

        String expected21 = "Up1_and_Up2 with the white and gold!";

        String actual21 = getFileContent(inputFile21.getPath());

        assertEquals("The files differ!", expected21, actual21);
        assertEquals("!!", outStream.toString().trim());
    }

    // Purpose: Test case when opt s and opt k are present where
    // string in opt r and s both as special characters but r string is not present in file
    // Frame #: Test Case 35 		(Key = 1.0.2.3.2.2.3.2.1.0.0.0.2.2.2.2.2.2.)
    @Test
    public void fileSummaryTest22() throws Exception {
        File inputFile22 = createInputFile(FILE2);

        String args[] = { "-r", "@:@", "-s", "::", inputFile22.getPath()};
        Main.main(args);

        String expected22 = "Log: 123 abc\nError: 123 xyz\nError: 567 abc\nLog: 567 abc";

        String actual22 = getFileContent(inputFile22.getPath());

        assertEquals("The files differ!", expected22, actual22);
        assertEquals(":", outStream.toString().trim());
    }

    // Purpose: Test case when opt a, opt s and opt k are present where
    // string in opt r and s both as alphanumeric characters but a is equal to 0 or null
    // Frame #: Test Case 44 		(Key = 2.1.2.3.1.2.3.1.1.0.0.0.2.2.2.4.2.2.)

    // Failure Type: Test Failure - Used the same line separator used in file at the end of the file
    @Test
    public void fileSummaryTest23() throws Exception {
        File inputFile23 = createInputFile(FILE4);

        String args[] = { "-a", "-s", "doehtiw21", "-r", "Dwn1", inputFile23.getPath()};
        Main.main(args);

        //String expected23 = "Down1_and_Down2 with the red and black!!\rUp1_and_Up2 with the white and gold!";
        String expected23 = "Down1_and_Down2 with the red and black!!"+System.lineSeparator()+"Up1_and_Up2 with the white and gold!";

        String actual23 = getFileContent(inputFile23.getPath());

        assertEquals("The files differ!", expected23, actual23);
        assertEquals("white", outStream.toString().trim());
    }

    // Purpose: Test case when opt a, opt s and opt r are present where
    // string in opt r are special characters and s alphanumeric characters but a is equal to 0 or null
    // Frame #: Test Case 46 		(Key = 2.1.2.3.1.2.3.2.1.0.0.0.2.2.2.4.2.2.)

    // Failure Type: Test Failure - Used the same line separator used in file at the end of the file
    @Test
    public void fileSummaryTest24() throws Exception {
        File inputFile24 = createInputFile(FILE3);

        String args[] = { "-r", "!@", "-s", "elsioH", "-a", inputFile24.getPath()};
        Main.main(args);

        //String expected24 = "Thank you!!!\nYes @@tester.";
        String expected24 = "Thank you!!!"+System.lineSeparator()+"Yes @@tester.";

        String actual24 = getFileContent(inputFile24.getPath());

        assertEquals("The files differ!", expected24, actual24);
        assertEquals("Hello", outStream.toString().trim());
    }



    // Purpose: Test case when opt a, and opt k are present where string in opt k are special characters
    // but absent in file and a is greater than 0
    // Frame #: Test Case 53 		(Key = 2.2.1.0.0.1.0.0.2.0.3.2.2.2.0.0.2.2.)

    // Failure Type: BUG: filesummary fails when passed a search string that is not in the file
    // possibly by dereferencing index out of bound search result.
    @Test
    public void fileSummaryTest25() throws Exception {
        File inputFile25 = createInputFile(FILE4);

        String args[] = { "-k", "@@", "-a", "8", inputFile25.getPath()};
        Main.main(args);

        String expected25 = "";

        String actual25 = getFileContent(inputFile25.getPath());

        assertEquals("The files differ!", expected25, actual25);
    }




    // Purpose: Test case when opt a, opt s and opt r are present where string in opt s are alphanumeric characters,
    // opt r are special characters but absent in file and a is greater than 0 showing numbers are prioritized before alphabets
    // Test Case 60 		(Key = 2.2.2.3.1.2.3.2.1.0.0.0.2.2.2.4.2.2.)

    // Failure Type: Test Failure - Used the same line separator used in file at the end of the file
    @Test
    public void fileSummaryTest26() throws Exception {
        File inputFile26 = createInputFile(FILE4);

        String args[] = { "-r", "!@", "-s", "2ipt esUhw", "-a", "4", inputFile26.getPath()};
        Main.main(args);

        //String expected26 = "Down1_and_Down2 with the red and black!!\rUp1_and_Up2 with the white and gold!";
        String expected26 = "Down1_and_Down2 with the red and black!!"+System.lineSeparator()+"Up1_and_Up2 with the white and gold!";

        String actual26 = getFileContent(inputFile26.getPath());

        assertEquals("The files differ!", expected26, actual26);
        assertEquals("Up2 with the white", outStream.toString().trim());
    }

    // Purpose: Test case when opt s has more than one alphanumeric characters and finds more than one longest sequence of characters in the file
    // eg; "Hello" and "teste" are found but "Hello" is displayed since it appears first in the file
    // Test Case 26 		(Key = 1.0.2.3.1.1.0.0.1.0.0.0.2.2.2.0.2.2.)
    @Test
    public void fileSummaryTest27() throws Exception {
        File inputFile27 = createInputFile(FILE3);

        String args[] = {"-s", "olestH", inputFile27.getPath()};
        Main.main(args);

        String expected27 = FILE3;

        String actual27 = getFileContent(inputFile27.getPath());

        assertEquals("The files differ!", expected27, actual27);
        assertEquals("Hello", outStream.toString().trim());
    }

    // Purpose: Test case when opt s and opt k are present
    // with strings in both opt s and k include special characters and alphanumeric characters
    // not from frame: To adjust the number of test cases between 40-80, special characters and alphabets together were not considered
    @Test
    public void fileSummaryTest28() throws Exception {
        File inputFile28 = createInputFile(FILE3);

        String args[] = {"-s", "H@!est", "-k", "@test", inputFile28.getPath()};
        Main.main(args);

        String expected28 = "Yes @@tester.";

        String actual28 = getFileContent(inputFile28.getPath());

        assertEquals("The files differ!", expected28, actual28);
        assertEquals("@@teste", outStream.toString().trim());
    }

    // Purpose: Test case when opt s and opt a are present where a > 0 and
    // string in opt s include special characters and alphanumeric characters
    // not from frame: To adjust the number of test cases between 40-80, special characters and alphanumeric together were not considered

    // Failure Type: Test Failure - Used the same line separator used in file at the end of the file
    @Test
    public void fileSummaryTest29() throws Exception {
        File inputFile29 = createInputFile(FILE4);

        String args[] = {"-a", "1", "-s", "U_2danp", inputFile29.getPath()};
        Main.main(args);

        //String expected29 = "Down1_and_Down2 with the red and black!!\rUp1_and_Up2 with the white and gold!";
        String expected29 = "Down1_and_Down2 with the red and black!!"+System.lineSeparator()+"Up1_and_Up2 with the white and gold!";

        String actual29 = getFileContent(inputFile29.getPath());

        assertEquals("The files differ!", expected29, actual29);
        assertEquals("_and_Up2", outStream.toString().trim());
    }

    // Purpose: Test case when opt s(run first though placed after k), opt a and opt r are present
    // string in opt s and r include both special characters and numeric characters
    // not from frame: To adjust the number of test cases between 40-80, special characters and alphanumeric together were not considered

    // Failure Type: Test Failure - Used the same line separator used in file at the end of the file
    @Test
    public void fileSummaryTest30() throws Exception {
        File inputFile30 = createInputFile(FILE4);

        String args[] = {"-k", "1_", "-s", "1!", "-a", "6", inputFile30.getPath()};
        Main.main(args);

        //String expected30 = "Down1_and_Down2 with the red and black!!\rUp1_and_Up2 with the white and gold!";
        String expected30 = "Down1_and_Down2 with the red and black!!"+System.lineSeparator()+"Up1_and_Up2 with the white and gold!";

        String actual30 = getFileContent(inputFile30.getPath());

        assertEquals("The files differ!", expected30, actual30);
        assertEquals("!!", outStream.toString().trim());
    }




    // New Failure Type: BUG: filesummary fails when none of the opt are passed
    // to count the number of words in an empty file
    @Test
    public void fileSummaryTest33() throws Exception {
        File inputFile33 = createInputFile(FILE5);

        String args[] = {inputFile33.getPath()};
        Main.main(args);

        String actual30 = getFileContent(inputFile33.getPath());

        assertEquals("0", outStream.toString().trim());
    }



    // New Failure Type: BUG: filesummary fails when passed the integer value more than the length of the file
    // content with opt 'a' trying to sort after the provided value throwing an exception 'String index out of range'.
    @Test
    public void fileSummaryTest34() throws Exception {
        File inputFile34 = createInputFile(FILE3);

        String args[] = {"-a","100",inputFile34.getPath()};
        Main.main(args);

        String expected34 = " ";
        String actual34 = getFileContent(inputFile34.getPath());

        assertEquals("The files differ!", expected34, actual34);
   }




    // New Failure Type: BUG: filesummary fails when null is passed with opt 's' trying to find the empty sequence
    // of null throwing an exception 'Unclosed character class near index 2 [\Q\E]+  ^'.
    @Test
    public void fileSummaryTest35() throws Exception {
        File inputFile35 = createInputFile(FILE1);

        String args[] = {"-s","",inputFile35.getPath()};
        Main.main(args);

        String expected35 = "";
        String actual35 = getFileContent(inputFile35.getPath());

        assertEquals("", outStream.toString().trim());
    }



    // New Failure Type: BUG: filesummary fails when zero is passed as integer value for opt 'r' or 'k'
    // trying to remove or keep zero lines with the provided string throwing an exception 'ArrayIndexOutOfBoundsException'.
    @Test
    public void fileSummaryTest36() throws Exception {
        File inputFile36 = createInputFile(FILE1);

        String args[] = {"-r","dog","0",inputFile36.getPath()};
        Main.main(args);

        String expected36 = System.lineSeparator() + "2 cat";
        String actual36 = getFileContent(inputFile36.getPath());

        assertEquals("The files differ!", expected36, actual36);
    }



    // New Failure Type: BUG: filesummary fails when  opt 'n' used in an empty file trying to prepend the content
    // with line number throwing an exception 'ArrayIndexOutOfBoundsException'.
    @Test
    public void fileSummaryTest37() throws Exception {
        File inputFile37 = createInputFile(FILE5);

        String args[] = {"-n",inputFile37.getPath()};
        Main.main(args);

        String expected37 = "1";
        String actual37 = getFileContent(inputFile37.getPath());

        assertEquals("The files differ!", expected37, actual37);
    }




    // New Failure Type: BUG: filesummary fails when null value is passed with opt 'r' trying to
    // remove the null value from the file throwing an exception 'Method never completes'.
    @Test
    public void fileSummaryTest38() throws Exception {
        File inputFile38 = createInputFile(FILE1);

        String args[] = {"-r","",inputFile38.getPath()};
        Main.main(args);

        String expected38 = "1 dog" + System.lineSeparator() + "2 cat";
        String actual38 = getFileContent(inputFile38.getPath());

        assertEquals("The files differ!", expected38, actual38);
    }

    // New Failure Type: BUG: filesummary fails when no character sequence is passed with opt 's' trying
    // throwing an exception 'Method never completes'.
    @Test
    public void fileSummaryTest39() throws Exception {
        File inputFile39 = createInputFile(FILE3);

        String args[] = {"-s", inputFile39.getPath()};
        Main.main(args);

        String expected39 = "";
        String actual39 = getFileContent(inputFile39.getPath());

        assertEquals("", outStream.toString().trim());
    }



    // New Failure Type: BUG: filesummary fails when same opt is passed twice as parameters
    @Test
    public void fileSummaryTest40() throws Exception {
        File inputFile40 = createInputFile(FILE1);

        String args[] = {"-a", "2", "-a",inputFile40.getPath()};
        Main.main(args);

        String expected40 = "2 cat"+System.lineSeparator()+"1 dog";
        String actual40 = getFileContent(inputFile40.getPath());

        assertEquals("The files differ!", expected40, actual40);
    }

}

