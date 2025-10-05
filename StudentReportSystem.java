// 📘 Student Report Card System in Java (CSV Version)
// Created by Tanu

import java.io.*;
import java.util.*;

class Student {
    int rollNo;
    String name;
    int physics, chemistry, maths, english, cs;
    float percentage;
    char grade;

    void calculate() {
        percentage = (physics + chemistry + maths + english + cs) / 5.0f;
        if (percentage >= 60)
            grade = 'A';
        else if (percentage >= 50)
            grade = 'B';
        else if (percentage >= 33)
            grade = 'C';
        else
            grade = 'F';
    }

    void getData(int roll) {
        Scanner sc = new Scanner(System.in);
        rollNo = roll;
        System.out.print("\nEnter the Name of Student: ");
        name = sc.nextLine();

        physics = inputMarks(sc, "Physics");
        chemistry = inputMarks(sc, "Chemistry");
        maths = inputMarks(sc, "Maths");
        english = inputMarks(sc, "English");
        cs = inputMarks(sc, "Computer Science");

        calculate();
    }

    private int inputMarks(Scanner sc, String subject) {
        int marks;
        while (true) {
            System.out.print("Enter marks in " + subject + " (0-100): ");
            if (sc.hasNextInt()) {
                marks = sc.nextInt();
                if (marks >= 0 && marks <= 100)
                    break;
            } else sc.next(); // clear invalid input
            System.out.println("Invalid input! Enter marks between 0-100.");
        }
        return marks;
    }

    void showData() {
        System.out.println("\nRoll Number: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Physics: " + physics);
        System.out.println("Chemistry: " + chemistry);
        System.out.println("Maths: " + maths);
        System.out.println("English: " + english);
        System.out.println("Computer Science: " + cs);
        System.out.println("Percentage: " + percentage);
        System.out.println("Grade: " + grade);
    }

    void showTabular() {
        System.out.printf("%-6d %-15s %-6d %-6d %-6d %-6d %-6d %-10.2f %-3c\n",
                rollNo, name, physics, chemistry, maths, english, cs, percentage, grade);
    }
}

public class StudentReportSystem {
    static final String FILE = "students.csv";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        intro();

        char ch;
        do {
            System.out.println("\n\nMAIN MENU");
            System.out.println("1. RESULT MENU");
            System.out.println("2. ENTRY/EDIT MENU");
            System.out.println("3. EXIT");
            System.out.print("Enter your choice: ");
            ch = sc.next().charAt(0);

            switch (ch) {
                case '1': resultMenu(); break;
                case '2': entryMenu(); break;
                case '3': System.out.println("Exiting..."); break;
                default: System.out.println("Invalid Choice!");
            }
        } while (ch != '3');
    }

    // ---------------- CSV-based methods ----------------

    static void writeStudent() {
        Scanner sc = new Scanner(System.in);
        int roll;
        System.out.print("Enter Roll Number: ");
        roll = sc.nextInt();

        if (findStudent(roll) != null) {
            System.out.println("⚠️ Roll number already exists!");
            return;
        }

        Student st = new Student();
        st.getData(roll);

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE, true))) {
            pw.println(st.rollNo + "," + st.name + "," + st.physics + "," + st.chemistry + "," + st.maths + ","
                    + st.english + "," + st.cs + "," + st.percentage + "," + st.grade);
            System.out.println("✅ Record Created Successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    static List<Student> loadAll() {
    List<Student> list = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
        String line;
        boolean firstLine = true;
        while ((line = br.readLine()) != null) {
            if (firstLine && line.startsWith("RollNo")) {
                firstLine = false;
                continue; // skip CSV header
            }
            String[] d = line.split(",");
            if (d.length == 9) {
                Student s = new Student();
                s.rollNo = Integer.parseInt(d[0]);
                s.name = d[1];
                s.physics = Integer.parseInt(d[2]);
                s.chemistry = Integer.parseInt(d[3]);
                s.maths = Integer.parseInt(d[4]);
                s.english = Integer.parseInt(d[5]);
                s.cs = Integer.parseInt(d[6]);
                s.percentage = Float.parseFloat(d[7]);
                s.grade = d[8].charAt(0);
                list.add(s);
            }
        }
    } catch (Exception e) { }
    return list;
}

    static Student findStudent(int roll) {
        return loadAll().stream().filter(s -> s.rollNo == roll).findFirst().orElse(null);
    }

    static void saveAll(List<Student> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Student s : list) {
                pw.println(s.rollNo + "," + s.name + "," + s.physics + "," + s.chemistry + "," + s.maths + ","
                        + s.english + "," + s.cs + "," + s.percentage + "," + s.grade);
            }
        } catch (Exception e) {
            System.out.println("Error saving file.");
        }
    }

    static void displayAll() {
        List<Student> list = loadAll();
        if (list.isEmpty()) {
            System.out.println("No records found!");
            return;
        }
        System.out.println("\nALL STUDENT RECORDS:");
        for (Student s : list) {
            s.showData();
            System.out.println("----------------------------");
        }
    }

    static void displaySpecific(int roll) {
        Student s = findStudent(roll);
        if (s == null)
            System.out.println("❌ Record not found!");
        else
            s.showData();
    }

    static void modifyStudent(int roll) {
        List<Student> list = loadAll();
        boolean found = false;
        for (Student s : list) {
            if (s.rollNo == roll) {
                s.getData(roll);
                found = true;
                break;
            }
        }
        if (found) saveAll(list);
        System.out.println(found ? "✅ Record Updated!" : "❌ Record Not Found!");
    }

    static void deleteStudent(int roll) {
        List<Student> list = loadAll();
        boolean removed = list.removeIf(s -> s.rollNo == roll);
        if (removed) saveAll(list);
        System.out.println(removed ? "🗑️ Record Deleted!" : "❌ Record Not Found!");
    }

    static void classResult() {
        List<Student> list = loadAll();
        if (list.isEmpty()) {
            System.out.println("No records to display!");
            return;
        }
        System.out.println("\nALL STUDENTS RESULT:");
        System.out.println("---------------------------------------------------------------");
        System.out.printf("%-6s %-15s %-6s %-6s %-6s %-6s %-6s %-10s %-3s\n",
                "Roll", "Name", "P", "C", "M", "E", "CS", "%age", "Grd");
        System.out.println("---------------------------------------------------------------");
        for (Student s : list) s.showTabular();
    }

    // ---------------- Menus ----------------

    static void resultMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nRESULT MENU");
        System.out.println("1. Class Result");
        System.out.println("2. Individual Report Card");
        System.out.println("3. Back to Main Menu");
        System.out.print("Enter Choice: ");
        char ch = sc.next().charAt(0);

        switch (ch) {
            case '1': classResult(); break;
            case '2':
                System.out.print("Enter Roll No: ");
                int r = sc.nextInt();
                displaySpecific(r);
                break;
            case '3': return;
            default: System.out.println("Invalid Choice!");
        }
    }

    static void entryMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nENTRY MENU");
        System.out.println("1. Create Student Record");
        System.out.println("2. Display All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Modify Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Back to Main Menu");
        System.out.print("Enter Choice: ");
        char ch = sc.next().charAt(0);

        switch (ch) {
            case '1': writeStudent(); break;
            case '2': displayAll(); break;
            case '3':
                System.out.print("Enter Roll No: ");
                int r1 = sc.nextInt();
                displaySpecific(r1);
                break;
            case '4':
                System.out.print("Enter Roll No: ");
                int r2 = sc.nextInt();
                modifyStudent(r2);
                break;
            case '5':
                System.out.print("Enter Roll No: ");
                int r3 = sc.nextInt();
                deleteStudent(r3);
                break;
            case '6': return;
            default: System.out.println("Invalid Choice!");
        }
    }

    static void intro() {
        System.out.println("\n\n\tSTUDENT REPORT CARD SYSTEM");
        System.out.println("\t---------------------------");
        System.out.println("\tMade by: Tanu (Converted from C++)\n");
    }
}
