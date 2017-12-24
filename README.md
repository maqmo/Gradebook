# GradeBook
GradeBook is a program whose purpose is to demonstrate the use of an sqlite database through a Java interface using a unix based operating system. The program offers the user the ability to store students' grades for various course assignments.

## Getting Started

You can use the included SQLite library by adding the jar file to your class path and compiling the source code.

### To Compile and Execute
From the command line, enter:
```
javac -cp sqlite-jdbc-3.21.0.jar GradeBook.java CSVLoader.java DBController.java  
```
then enter:
```
java -cp sqlite-jdbc-3.21.0.jar: Gradebook
```

_Note: The above compilation command will generate the java class files directly into your **current working direcotory**_

## Program Use:

1. Enter an assignment category and give it a weight

2. Enter an assignment, if it is not an already created category, it will be rejected

3. If not done already, enter students names and id numbers.
   - Enter each student one-by-one or via .csv file with columns _id_, _name_

4. Enter scores for previously created assignment(s).
   - Enter the scores one-by-one or via .csv fiel with columsn: _id_, _score_, _assignment_
