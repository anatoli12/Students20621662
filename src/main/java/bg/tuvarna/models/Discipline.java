package bg.tuvarna.models;

public enum Discipline {
    PF("Programming fundamentals", true, 1),
    WD("Web Development", true, 1),
    EL("Electronic Engineering", true, 2),
    ENGLISH("English", true, 1),
    CO("Computer Organization", true, 2),
    AUT("Automation", false, 3),
    MATHS("Mathematics", true, 1),
    OOP("Object-Oriented Programming", true, 2),
    SIS("Software and Information Systems", true, 2),
    PS("Problem Solving", true, 3),
    GS("General Science", false, 1),
    SQL("SQL", true, 2),
    PLSQL("PL/SQL", false, 3),
    SL("System Languages", false, 2),
    DS("Data Structures", true, 2),
    SA("System Analysis", true, 3);
    private String name;
    private Boolean isMandatory;
    private Integer year;

    Discipline(String name, Boolean isMandatory, Integer year) {
        this.name = name;
        this.isMandatory = isMandatory;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public Integer getYear() {
        return year;
    }
}
