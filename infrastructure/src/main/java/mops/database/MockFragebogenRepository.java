package mops.database;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import mops.Einheit;
import mops.Frage;
import mops.Fragebogen;
import mops.MultipleChoiceFrage;
import mops.TextFrage;
import mops.controllers.FragebogenRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MockFragebogenRepository implements FragebogenRepository {
  private transient List<String> fragen = new ArrayList<>(Arrays.asList("hey", "Yo", "Foo",
      "This", "Random", "Bar", "List", "Has", "Very Random", "Words", "And", "Stuff"));

  @Override
  public Fragebogen getFragebogenById(Long id) {
    List<Frage> fragenliste = new ArrayList<>();
    Frage frage1 = new MultipleChoiceFrage(1L, getRandomFrageText());
    Frage frage2 = new MultipleChoiceFrage(2L, getRandomFrageText());
    Frage frage3 = new TextFrage(3L, getRandomFrageText());
    fragenliste.add(frage1);
    fragenliste.add(frage2);
    fragenliste.add(frage3);

    Fragebogen.FragebogenBuilder fragebogen = Fragebogen.builder();
    fragebogen = fragebogen
        .startdatum(LocalDateTime.now())
        .enddatum(LocalDateTime.now().plusHours(24))
        .fragen(fragenliste)
        .professorenname("Jens Bendisposto")
        .veranstaltungsname("Softwareentwicklung im Team")
        .type(getRandomType())
        .bogennr(id);
    return fragebogen.build();
  }

  private Einheit getRandomType() {
    Random randomGenerator = new Random();
    int index = randomGenerator.nextInt(2);
    if (index == 0) {
      return Einheit.UEBUNG;
    }
    return Einheit.VORLESUNG;
  }

  private String getRandomFrageText() {
    Random randomGenerator = new Random();
    int index = randomGenerator.nextInt(fragen.size());
    return fragen.get(index);
  }

  @Override
  public List<Fragebogen> getAll() {
    List<Fragebogen> fragenliste = new ArrayList<>();
    fragenliste.add(getFragebogenById(1L));
    fragenliste.add(getFragebogenById(2L));
    fragenliste.add(getFragebogenById(3L));
    fragenliste.add(getFragebogenById(4L));
    fragenliste.add(getFragebogenById(5L));
    fragenliste.add(getFragebogenById(6L));
    fragenliste.add(getFragebogenById(7L));
    return fragenliste;
  }
}
