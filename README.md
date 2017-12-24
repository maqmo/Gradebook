# GradeBook
GradeBook is a program whose purpose is to demonstrates the use of an sqlite database through a Java interface using a unix based operating system. The program offers teh user
the ability to store  the grades for various course assignments after entering the names of the students.

## Getting Started

You can use the included SQLite library by adding the jar file to your class path and compiling the source code.

### To Compile
From the command line, enter:
```
javac -cp sqlite-jdbc-3.21.0.jar GradeBook.java CSVLoader.java DBController.java  
```

### To Execute
```
java -cp sqlite-jdbc-3.21.0.jar: Gradebook
```

Note: The above compilation will generate the java class files into your **current working direcotory**

## Program Use:

1.Enter an assignment category and give it a weight

2.Enter an assignment, if it is not an already created category, it will be rejected

3.If not done already, enter students names and id numbers.
  - Enter each student one-by-one or via .csv file with columns _id_, _name_

4. Enter scores for previously created assignment(s).
   - Enter the scores one-by-one or via .csv fiel with columsn: _id_, _score_, _assignment_
