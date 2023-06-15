package bg.tuvarna.models;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Program {
  SIT(
      new HashSet<>(
          Arrays.asList(
              Discipline.PF, Discipline.SIS, Discipline.WD, Discipline.AUT, Discipline.EL)),
      "Software and Internet Technologies"),
  CS(
      new HashSet<>(
          Arrays.asList(
              Discipline.PF, Discipline.OOP, Discipline.DS, Discipline.SA, Discipline.MATHS)),
      "Computer Science"),
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
