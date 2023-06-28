package bg.tuvarna.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/**
 * Енумерацията Program представя различните образователни програми в университета.
 * Всяка програма се характеризира с множество от дисциплини и име.
 */
public enum Program {
  /**
   * Програма "Софтуерни и интернет технологии".
   * Включва следните дисциплини: PF, SIS, WD, AUT, EL.
   */
  SIT(
      new HashSet<>(
          Arrays.asList(
              Discipline.PF, Discipline.SIS, Discipline.WD, Discipline.AUT, Discipline.EL)),
      "Software and Internet Technologies"),
  /**
   * Програма "Компютърни науки".
   * Включва следните дисциплини: PF, OOP, DS, SA, MATHS.
   */
  CS(
      new HashSet<>(
          Arrays.asList(
              Discipline.PF, Discipline.OOP, Discipline.DS, Discipline.SA, Discipline.MATHS)),
      "Computer Science"),
  /**
   * Програма "Софтуерно инженерство".
   * Включва следните дисциплини: PF, OOP, SA, DS, SQL.
   */
  SE(
      new HashSet<>(
          Arrays.asList(
              Discipline.PF, Discipline.OOP, Discipline.SA, Discipline.DS, Discipline.SQL)),
      "Software Engineering");
  private final Set<Discipline> disciplines;
  private final String name;

  Program(Set<Discipline> disciplines, String name) {
    this.disciplines = disciplines;
    this.name = name;
  }

  public Set<Discipline> getDisciplines() {
    return disciplines;
  }

  public String getName() {
    return name;
  }
}
