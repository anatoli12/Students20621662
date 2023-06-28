package bg.tuvarna.models;
/**
 * Изброимият тип Discipline представя различните дисциплини в университета.
 * Всяка дисциплина се характеризира с име, индикатор дали е задължителна и година на обучение.
 */
public enum Discipline {
    AUT("Automation", false, 3),
    CO("Computer Organization", true, 2),
    DS("Data Structures", true, 2),
    EL("Electronic Engineering", true, 2),
    ENGLISH("English", true, 1),
    GS("General Science", false, 1),
    MATHS("Mathematics", true, 1),
    OOP("Object-Oriented Programming", true, 2),
    PF("Programming fundamentals", true, 1),
    PLSQL("PL/SQL", false, 3),
    PS("Problem Solving", true, 3),
    SA("System Analysis", true, 3),
    SIS("Software and Information Systems", true, 2),
    SL("System Languages", false, 2),
    SQL("SQL", true, 2),
    WD("Web Development", true, 1);
    private final String name;
    private final Boolean isMandatory;
    private final Integer year;

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
