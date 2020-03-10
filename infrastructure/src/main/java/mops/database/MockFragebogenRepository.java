package mops.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import mops.Auswahl;
import mops.Einheit;
import mops.Frage;
import mops.Fragebogen;
import mops.MultipleChoiceFrage;
import mops.SkalarFrage;
import mops.TextFrage;
import mops.controllers.FragebogenRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("Faker")
public class MockFragebogenRepository implements FragebogenRepository {
  private static final transient List<String> frage = new ArrayList<>(Arrays.asList("Was geht?",
      "Wie zufrieden sind sie mit dem Angebot?", "Random Question?"));

  private static final transient List<String> professor = new ArrayList<>(Arrays.asList("Jens Bendisposto",
      "Christian Meter", "Jan Roßbach", "Luke Skywalker"));

  private static final transient List<String> vorlesung = new ArrayList<>(Arrays.asList(
      "Professioneller Softwareentwicklung im Team", "Lineare Algebra I", "Analysis II",
      "Theoretische Informatik", "Machine Learning"));

  private static final transient List<String> uebung = new ArrayList<>(Arrays.asList("Uebung zur Linearen"
      + " Algebra", "Uebung zur Analysis", "Uebung zur Theoretischen Informatik", "Uebung zu "
      + "Machine Learning"));
  private static HashMap<Long, Fragebogen> fragebogen;

  static {
    fragebogen = new HashMap<>();
    List<Fragebogen> fragebogens = generateTenFragebogen();
    Long index = 1L;
    for (Fragebogen fragebogen1 : fragebogens) {
      fragebogen.put(index, fragebogen1);
      index++;
    }

  }

  @Override
  public void deleteFrageByIdAndFrageId(Long formId, Long frageId) {
    Fragebogen fragebogen = MockFragebogenRepository.fragebogen.get(formId);
    List<Frage> fragen = fragebogen.getFragen();
    fragen.removeIf(frage1 -> frage1.getId().equals(frageId));
    fragebogen.setFragen(fragen);
  }

  @Override
  public Fragebogen getFragebogenById(Long id) {
    return fragebogen.get(id);
  }

  private static Fragebogen getRandomFragebogen() {
    List<Frage> fragenliste = new ArrayList<>();
    Frage frage1 = generateMultipleChoice();
    Frage frage2 = generateMultipleChoice();
    Frage frage3 = new TextFrage(3L, getRandomFrage());
    fragenliste.add(frage1);
    fragenliste.add(frage2);
    fragenliste.add(frage3);
    Einheit einheit = Einheit.getRandomEinheit();
    String name;
    if (einheit == Einheit.VORLESUNG) {
      name = getRandomVorlesung();
    } else {
      name = getRandomUebung();
    }
    Fragebogen.FragebogenBuilder fragebogen = Fragebogen.builder();
    fragebogen = fragebogen
        .startdatum(LocalDateTime.now())
        .enddatum(LocalDateTime.now().plusHours(24))
        .fragen(fragenliste)
        .professorenname(getRandomProfessor())
        .veranstaltungsname(name)
        .type(einheit)
        .bogennr(1L);
    return fragebogen.build();
  }

  private static Frage generateMultipleChoice() {
    MultipleChoiceFrage frage = new MultipleChoiceFrage(1L, getRandomFrage());
    frage.addChoice(new Auswahl("1"));
    frage.addChoice(new Auswahl("2"));
    frage.addChoice(new Auswahl("3"));
    frage.addChoice(new Auswahl("4"));
    frage.addChoice(new Auswahl("5"));
    return frage;
  }

  private static String getRandomVorlesung() {
    Random randomGenerator = new Random();
    int index = randomGenerator.nextInt(vorlesung.size());
    return vorlesung.get(index);
  }

  private static String getRandomProfessor() {
    Random randomGenerator = new Random();
    int index = randomGenerator.nextInt(professor.size());
    return professor.get(index);
  }

  private static String getRandomFrage() {
    Random randomGenerator = new Random();
    int index = randomGenerator.nextInt(frage.size());
    return frage.get(index);
  }

  private static String getRandomUebung() {
    Random randomGenerator = new Random();
    int index = randomGenerator.nextInt(uebung.size());
    return uebung.get(index);
  }

  @Override
  public List<Fragebogen> getAll() {
    return List.copyOf(fragebogen.values());
  }

  private static List<Fragebogen> generateTenFragebogen() {
    List<Fragebogen> fragenliste = new ArrayList<>();
    for (long i = 1L; i < 10L; i++) {
      fragenliste.add(getRandomFragebogen());
    }
    return fragenliste;
  }

  @Override
  public List<Fragebogen> getAllContaining(String search) {
    List<Fragebogen> fragenliste = new ArrayList<>();
    for (long i = 1L; i < 10L; i++) {
      Fragebogen fragebogen = getFragebogenById(i);
      if (fragebogen.contains(search)) {
        fragenliste.add(fragebogen);
      }
    }
    return fragenliste;
  }

  @Override
  public void changeDateById(Long formId, LocalDateTime startDate, LocalDateTime endDate) {
    Fragebogen fragebogen = getFragebogenById(formId);
    fragebogen.setStartdatum(startDate);
    fragebogen.setEnddatum(endDate);
  }

  @Override
  public void addTextFrage(Long id, TextFrage frage) {
    Fragebogen fragebogen = getFragebogenById(id);
    List<Frage> fragen = fragebogen.getFragen();
    fragen.add(frage);
    fragebogen.setFragen(fragen);
  }

  @Override
  public void addSkalarFrage(Long id, SkalarFrage frage) {
    Fragebogen fragebogen = getFragebogenById(id);
    List<Frage> fragen = fragebogen.getFragen();
    fragen.add(frage);
    fragebogen.setFragen(fragen);
  }
}
