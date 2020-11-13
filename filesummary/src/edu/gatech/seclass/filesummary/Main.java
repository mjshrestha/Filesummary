package edu.gatech.seclass.filesummary;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class Main {
    /*
    Empty Main class for Individual Project.
     */
    static LinkedList<String> myL = new LinkedList<String>();
    static LinkedList<String> myL2 = new LinkedList();
    static String sOpt = "";
    static String rOpt = "";
    static String kOpt = "";
    static String nOpt = "";
    static int rOrkInt;

    static String fileName = "";
    static int indexA;
    private static Charset charset = StandardCharsets.UTF_8;
    static String endF;


    public static void main(String[] args) {
        // Empty Skeleton Method - Implement this.
        int nArgs = args.length;//get the total number of arguments
        if (nArgs <= 0) {
            usage();
            return;//stop the program
        } else {
            fileName = args[nArgs - 1];//file is always the last argument
            if (!readFiles(fileName))//read the file and load string to the array
                return;
        }
        //need to check if args contains and if it does we need to process it first
        if (nArgs == 1)//read file summary if only one option given
            // a method to read file summary
            System.out.print(readSummary());
        else if (!validOptions(args)) {//check if there is no option given
            usage();
            return;
        } else {
            boolean hasS = Arrays.asList(args).contains("-s");//checks if args contains s option or not
            if (hasS) {
                sOpt = getOptString("-s", args);
                if (sOpt == null || sOpt.length() == 0) {
                    usage();
                    return;
                } else
                    System.out.print(sOption());
            }
            boolean hasN = Arrays.asList(args).contains("-n");
            if (hasN) {
                nOption(fileName);
            }
            boolean hasK = Arrays.asList(args).contains("-k");
            boolean hasR = Arrays.asList(args).contains("-r");
            if (hasK && hasR) {
                usage();
                return;
            }
            if (hasK) {
                kOpt = getOptString("-k", args);
                if (kOpt == null) {
                    usage();
                    return;
                }
                else if (rOrkInt < 0) { //if it is negative, it is error
                    negativeOptError();
                    return;
                }
                else
                    kOption(fileName, rOrkInt);

            } else if (hasR) {
                rOpt = getOptString("-r", args); //rOpt is global
                if (rOpt == null || rOpt.length() == 0) {
                    usage();
                    return;
                }
                else if (rOrkInt < 0) { //if it is negative, it is error
                    negativeOptError();
                    return;
                }
                else
                    rOption(fileName, rOrkInt);
            }
            boolean hasA = Arrays.asList(args).contains("-a");
            if (hasA) {
                String aOpt = getOptString("-a", args);
                if (aOpt == null) // if no option for a so we set index to be 0 for the first position
                    indexA = 0;
                else if (aOpt.matches("-?\\d+")) {
                    indexA = Integer.parseInt(aOpt);
                    if (indexA < 0) { //if it is negative, it is error
                        negativeOptError();
                        return;
                    }
                } else
                    indexA = 0;
                optionA(fileName);
            }
        }
        myL.clear();
    }

    //error to see if there is no required options
    public static boolean validOptions(String arr[]) {
        for (String inp : arr) {
            if (inp.equals("-a") || inp.equals("-s") || inp.equals("-k") || inp.equals("-r") || inp.equals("-n"))
                return true;
        }
        return false;
    }

    //Read summary to count the number of words in the file
    public static int readSummary() {
        int cnt = 0;
        for (String s : myL) {
            String temp = s.trim();
            String[] wordsIn_temp = temp.split("[^A-Za-z0-9]");//split with other than alphanumeric characters
            cnt += wordsIn_temp.length;
        }
        return cnt;
    }

    // Usage Error
    public static void usage() {
        System.err.print("Usage: filesummary [-a [int]] [-r string [int] | -k string [int]] [-s string] [-n] <filename>");
    }

    // No file Error
    public static void noFileError() {
        System.err.print("File Not Found");
    }

    // Empty file Error
    public static void emptyFileError() {
        System.err.print("File is Empty!!");
    }

    // Empty file Error
    public static void negativeOptError() {
        System.err.print("Opt a cannot be negative!!/n");
        usage();
    }

    // Get the value of Opt
    public static String getOptString(String s, String arr[]) {
        rOrkInt = 0;
        String opt = null;
        int optParInd = -1;
        int nArgs = arr.length - 1;//arr.length is always the file so option should be before it
        for (int i = 0; i < nArgs; i++)//arr.length -2 since arr.length - 1 is always the text file
        {
            if (arr[i].equals(s)) {
                optParInd = i; // found the position of the index
                break;
            }
        }
        int optionsInd = optParInd + 1;//let us check the next parameter
        if (optionsInd != nArgs && optParInd != -1)// means found a valid parameter for option
        {
            String optT = arr[optionsInd];
            if (s.equals("-r") || s.equals("-k")) {
                //if(optT.equals("")) {
                opt = optT;
                if (isInteger(arr[++optionsInd])) {
                    rOrkInt = Integer.parseInt(arr[optionsInd]);
                }
            }
            else if (s.equals("-a")) {
                if (isInteger(optT))
                    opt = optT;

            }
            else {
                opt = optT;
            }

        }

        return opt;
    }

    // Read File Utility
    private static boolean readFiles(String filename) {
        myL.clear();//reset it as it is readmultiple times
        myL2.clear();
        String content = null;
        File f = new File(filename);
        if (!f.exists()) {
            noFileError();
            return false;
        }

        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), charset);
            if (content.trim().isEmpty()) {
                emptyFileError();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int len = 0;
        endF = getLineSeparatorRegx(content);
        String endFNot = "[^" + endF + "]";//converting to a regular expression
        String[] splitEnds = content.split(endFNot);
        for (String s : splitEnds) {
            if (!s.equals(""))
                myL2.add(s);
        }
        myL2.add(System.lineSeparator());//at the end there is no line separator so manually adding here

        endF = "[" + endF + "]";//converting to a regular expression
        String[] split = content.split(endF);
        for (String s : split) {
            if (!s.equals(""))
                myL.add(s);
        }
        return true;
    }

    //method to check if a value is integer
    public static boolean isInteger(String strNum) {

        if (strNum.matches("-?\\d+")) //&& !strNum.matches("[-]"))
            return true;
        else return false;
    }

    // Opt -k Utility
    public static void kOption(String file, int num) {
        readFiles(fileName);
        //int addedCnt = 0;
        LinkedList<String> temp = new LinkedList<>();
        for (String line : myL) {
            if (line.contains(kOpt))
                temp.add(line);
            //addedCnt++;
        }
        myL = temp;//reset to myL since we can use it in another option

        if (num == 0 || num > myL.size())//if num is zero, means that there is no num option so we need to get all of lines
            num = myL.size();
        writeFile(file, myL, myL.size() - num, myL.size());//write the last from num to size
    }

    // Opt -r Utility
    public static void rOption(String file, int num) {
        readFiles(fileName);

        LinkedList<String> temp = new LinkedList<>();
        LinkedList<String> tempEnds = new LinkedList<>();

        int addedCnt = 0;
        for (int i = 0; i < myL.size(); i++) {
            if (!myL.get(i).contains(rOpt)) {
                temp.add(myL.get(i));
                tempEnds.add(myL2.get(i));//these two need to work together now
                addedCnt++;
            }
        }
        myL = temp;//reset to myL since we can use it in another option
        myL2 = tempEnds;

        if (num == 0 || num > myL.size() || addedCnt == myL.size())//if num is zero, means that there is no num option so we need to get all of lines
            num = myL.size();

        writeFile(file, myL, myL.size() - num, myL.size());//write the last from num to size
    }

    // Check if opt character of s is in the string
    public static boolean isCharInSOpt(Character c) {
        return sOpt.contains(Character.toString(c));
    }

    // Opt -s Utility
    public static String sOption() {
        readFiles(fileName);
        String longestS = "";
        int longestCnt = -1;
        int cnt = 0;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < myL.size(); i++) {//check all content in the list
            String temp = myL.get(i) + endF;
            for (int j = 0; j < temp.length(); j++) {//check all the content of each list
                if (isCharInSOpt(temp.charAt(j))) {
                    cnt++;
                    sb.append(temp.charAt(j));
                } else {
                    cnt = 0;
                    sb = new StringBuilder();
                }

                if (longestCnt < cnt) {
                    longestCnt = cnt;
                    longestS = sb.toString();
                }
            }
        }
        return longestS;
    }

    public static char getNextChar(String str, int ind) {
        char o1c = '\0';//initialize to 0
        for (int i = ind; i < str.length(); i++) {

            if (Character.isLetter(str.charAt(i))) {
                o1c = str.charAt(i);
                break;
            }

        }
        return o1c;
    }

    public static void optionA(String file) {
        readFiles(fileName);

        Collections.sort(myL, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int indO1 = indexA;
                int indO2 = indexA;
                o1 = o1.toLowerCase();
                o2 = o2.toLowerCase();
                char o1c = getNextChar(o1, indO1++);
                char o2c = getNextChar(o2, indO2++);
                while (o1c == o2c && indO1 < o1.length() - 1 && indO2 < o2.length() - 1) {
                    while (true && indO1 < o1.length()) {
                        if (Character.isLetter(o1.charAt(++indO1))) {
                            o1c = o1.charAt(indO1);
                            break;
                        } //indO1++;
                    }
                    while (true && indO2 < o2.length()) {
                        if (Character.isLetter(o2.charAt(++indO2))) {
                            o2c = o2.charAt(indO2);
                            break;
                        } //indO2++;
                    }

                }
                if (o1c < o2c)
                    return -1;
                else if (o1c > o2c)
                    return 1;
                else
                    return 0;


            }
        });
        writeFile(file, myL, 0, myL.size());
    }

    public static void nOption(String file) {
        readFiles(fileName);
        for (int i = 0; i < myL.size(); i++) {
            myL.set(i, (i + 1) + myL.get(i));
        }
        writeFile(file, myL, 0, myL.size());
    }

    // Write File Utility
    public static void writeFile(String file, LinkedList<String> writeList, int min, int max) {
        try {
            BufferedWriter writer1 = new BufferedWriter(new FileWriter(file, false));
            //clear file
            writer1.write("");
            writer1.close();

            //Writing sorted lines into output file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            int cntEndOfList = 0;
            for (int i = min; i < max; i++) {
                cntEndOfList++;
                writer.write(writeList.get(i));
                if (cntEndOfList != (max - min))//do not write at the end of the line//if(cntEndOfList!=writeList.size())
                {
                    //writer.append(endF);//(System.getProperty("line.separator"));
                    writer.append(myL2.get(i));//(System.getProperty("line.separator"));
                }
            }
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    // check if Line separator
    public static boolean isLineSeparator(String ch) {
        if (ch.equals("\r") || ch.equals("\n") || ch.equals("\r\n") || ch.equals(System.lineSeparator())) {
            return true;
        }
        return false;

    }*/

    // Find Line separator
    public static String getLineSeparator(String str) {
        String lineBreak;
        if (str.matches("(?s).*(\\r\\n).*")) {
            lineBreak = "\r\n";
        } else if (str.matches("(?s).*(\\n).*")) {
            lineBreak = "\n";
        } else if (str.matches("(?s).*(\\r).*")) {
            lineBreak = "\r";
        } else if (str.matches(System.lineSeparator())) {
            lineBreak = System.lineSeparator();
        } else {
            lineBreak = "\n";
        }
        return lineBreak;
    }

    public static String getLineSeparatorRegx(String str) {
        String lineBreak = "";
        if (str.matches("(?s).*(\\r\\n).*")) {
            lineBreak += "\r\n";
        }
        if (str.matches("(?s).*(\\n).*")) {
            lineBreak += "\n";
        }
        if (str.matches("(?s).*(\\r).*")) {
            lineBreak += "\r";
        }
        if (str.matches(System.lineSeparator())) {
            lineBreak += getLineSeparator(str);
        }
        else lineBreak += "\n";
        return lineBreak;
    }
}