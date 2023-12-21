public class Student {
    String ID;
    String firstName;
    String lastName;
    String gender;
    String GPA;
    String level;
    String address;

    public Student(String iD, String firstName, String lastName, String gender, String gPA, String level,
            String address) {
        ID = iD;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        GPA = gPA;
        this.level = level;
        this.address = address;
    }

}
