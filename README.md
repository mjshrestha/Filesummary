# Filesummary

In this project, a simple command-line utility called filesummary is developed in Java using the test driven approach.

## Concise Specification of the filesummary Utility
#### NAME:
filesummary - helps to analyze a text file.

#### SYNOPSIS
filesummary OPT <filename>
where OPT can be zero or more of
  - -a [integer]
  - -s <string>
  - (-r|-k) <string> [integer]
  - (-n)
  
#### COMMAND-LINE ARGUMENTS AND OPTIONS
  
***\<filename\>:*** the file on which the filesummary operation has to be performed.
  
***-a [integer]:*** if specified, the filesummary utility will reorder the lines in
the file, alphabetizing the lines using only their alphabetic characters . If the
optional parameter [integer] is included, it must be a positive integer, and the
utility will skip the number of characters specified on each line, alphabetizing the
lines by using the alphabetic characters after [integer] characters.

***-s \<string\>:*** if specified, the filesummary utility will output the longest
sequence in the file made up of only characters in <string> . This option is
always applied first.
  
***(-r|-k) <string> [integer]:*** if specified, the filesummary utility will
remove(-r) or keep (-k) only the last [integer] lines in the file which contain the
provided <string> . -r and -k are mutually exclusive. If the optional integer is
not specified, it will remove or keep all lines in the file which contain the provided
<string> .
  
***(-n):*** if specified, the filesummary utility will prepend the line number from
the original file to each line. This should be applied first, or second when after -s.
If none of the OPT flags is specified, filesummary will simply output the
number of words in the file, where a word is any sequence of alphanumeric
characters.

#### NOTES
○ While the last command-line parameter provided is always treated as the
filename, OPT flags can be provided in any order; though no matter the
order of the parameters, if provided, -s will be applied first and -n next .

### EXAMPLES OF USAGE
#### 1. Example 1:
filesummary file1.txt
- File content: <br>
    1 dog <br>
    2 cat <br>
- Outputs “4”.

#### 2. Example 2:
filesummary -a 2 -s “d1atc” file1.txt
 - File content: <br>
    1 dog <br>
    2 cat <br>
  - Result:
    2 cat <br>
    1 dog <br>
    (skips the first two characters and alphabetizes by “cat “ and “dog”)
  - Outputs “cat”

#### 3. Example 3:
filesummary -r “1” file1.txt
  - File content: <br>
    1 dog <br>
    2 cat <br>
  - Result:
    2 cat <br>

#### 4. Example 4:
filesummary -k “2” -s “dog” -a 3 file1.txt
   - File content: <br>
    1 dog <br>
    2 cat <br>
   - Result:
    2 cat <br>
   - Outputs “dog”
