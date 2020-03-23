package mops;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import mops.fragen.Frage;
import mops.fragen.MultipleResponseFrage;
import mops.fragen.SingleResponseFrage;
import mops.fragen.TextFrage;

public class DozentServiceTest {
  private final DozentService service = new DozentService();

  @Test
  public void textFrageWirdKorrektErzeugt() {
    String fragetyp = "textfrage";
    String fragetext = "Wie geht's?";

    Frage totest = service.createNeueFrageAnhandFragetyp(fragetyp, fragetext);

    assertTrue(totest instanceof TextFrage);
  }

  @Test
  public void singleResponseFrageWirdKorrektErzeugt() {
    String fragetyp = "multiplechoice";
    String fragetext = "Wie geht's?";

    Frage totest = service.createNeueFrageAnhandFragetyp(fragetyp, fragetext);

    assertTrue(totest instanceof SingleResponseFrage);
  }

  @Test
  public void multipleResponseFrageWirdKorrektErzeugt() {
    String fragetyp = "multipleresponse";
    String fragetext = "Wie geht's?";

    Frage totest = service.createNeueFrageAnhandFragetyp(fragetyp, fragetext);

    assertTrue(totest instanceof MultipleResponseFrage);
  }
}
