package bg.tuvarna.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
/**
 * Класът Course представя дисциплина, в която е записан студент.
 * Всеки курс се характеризира с дисциплина, оценка и статус на записване.
 */
@Data
public class Course {
    private Discipline discipline;
    private boolean isGraded;
    private double grade;
    private boolean isEnrolled;
    /**
     * Проверява дали курсът е успешно завършен от студента.
     * Курсът се счита за успешно завършен, ако студентът е записан за него,
     * той е оценен и оценката е 3.0 или по-висока.
     *
     * @return Връща true, ако курсът е успешно завършен, и false в противен случай.
     */
    @JsonIgnore
    public boolean isSuccessfullyCompleted() {
        return isEnrolled && isGraded && grade >= 3.0;
    }
}
