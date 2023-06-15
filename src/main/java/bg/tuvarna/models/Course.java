package bg.tuvarna.models;

import lombok.Data;

@Data
public class Course {
    private Discipline discipline;
    private boolean isGraded;
    private double grade;
    private boolean isEnrolled;

    public boolean isSuccessfullyCompleted() {
        return isEnrolled && isGraded && grade >= 3.0;
    }
}
