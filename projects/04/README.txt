This contains all of the Java source files.
Created by Robert C. Senkbeil (rcsvt)

NOTE: A compiled collection has been included
      in a bin folder.

NOTE: Reused distributed source code from
      CS 3114 website for heapsort (modified
      to work with a buffer pool).

-------------------------------------------
Linux Build Instructions

To build these files, type the following:
cd <Project_Directory>/
javac *.java

To run the program, type the following:
java Bindisk <file> <buffer_count> <buffer_size>

-------------------------------------------

NOTE: This program assumes that the commands
have the proper number of arguments.

NOTE: When testing this on the project 2 test file that inputted 16384 values
and then removed them, this program runs slowly, but finishes successfully.

NOTE: This program zeros out values when removing them; however, it works
exactly the same without doing this (can be done by commenting out the single
function call to clearBytes in the remove method of MemPool). I left it in
because it helps demonstrate that all of the contents were successfully removed
from the file (if requested by a command in the command file).

