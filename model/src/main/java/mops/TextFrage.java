package mops;

import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class TextFrage extends Frage {
  private String fragentext;
  private Set<TextAntwort> antworten;

  @SuppressWarnings("checkstyle:MissingJavadocMethod")
  public TextFrage(Long id, String fragentext) {
    super(id);
    this.fragentext = fragentext;
    this.antworten = new HashSet<>();
  }

  @Override
  public void addAntwort(String text) {
    TextAntwort antwort = new TextAntwort(text);
    this.antworten.add(antwort);
  }
}
