package mops.database;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mops.Fragebogen;
import mops.Veranstaltung;
import mops.antworten.Antwort;
import mops.antworten.MultipleChoiceAntwort;
import mops.antworten.TextAntwort;
import mops.database.dto.AntwortDto;
import mops.database.dto.AuswahlDto;
import mops.database.dto.DOrganisiertVDto;
import mops.database.dto.FrageDto;
import mops.database.dto.FragebogenDto;
import mops.database.dto.SBeantwortetFDto;
import mops.database.dto.SBelegtVDto;
import mops.database.dto.StudentDto;
import mops.database.dto.VeranstaltungDto;
import mops.fragen.Auswahl;
import mops.fragen.Frage;
import mops.fragen.MultipleChoiceFrage;
import mops.fragen.TextFrage;
import mops.rollen.Dozent;
import mops.rollen.Student;
import org.springframework.stereotype.Service;

@Service
public class Translator {

  private final transient StudentenJdbcRepository studentenRepo;
  private final transient DozentenJdbcRepository dozentenRepo;

  public Translator(StudentenJdbcRepository studentenRepo, DozentenJdbcRepository dozentenRepo) {
    this.studentenRepo = studentenRepo;
    this.dozentenRepo = dozentenRepo;
  }

  public Veranstaltung loadVeranstaltung(VeranstaltungDto dto) {
    Veranstaltung.VeranstaltungBuilder veranstaltung = Veranstaltung.builder();
    veranstaltung = veranstaltung
        .veranstaltungsNr(dto.getId())
        .frageboegen(loadFrageboegen(dto.getFrageboegen()))
        .name(dto.getName())
        .dozent(loadDozent(dto.getDozenten()))
        .semester(dto.getSemester().toString())
        .studenten(loadStudentenList(dto.getStudenten()));
    return veranstaltung.build();
  }

  private Dozent loadDozent(Set<DOrganisiertVDto> dozenten) {
    // TODO Dozenten loading
    return null;
  }

  private List<Fragebogen> loadFrageboegen(Set<FragebogenDto> frageboegen) {
    return frageboegen.stream()
        .map(fragebogenDto -> loadFragebogen(fragebogenDto))
        .collect(Collectors.toList());
  }

  private Fragebogen loadFragebogen(FragebogenDto fragebogenDto) {
    Fragebogen.FragebogenBuilder fragebogen = Fragebogen.builder();
    return fragebogen
        .type(fragebogenDto.getEinheit())
        .startdatum(LocalDateTime.parse(fragebogenDto.getStartzeit()))
        .enddatum(LocalDateTime.parse(fragebogenDto.getEndzeit()))
        .abgegebeneStudierende(loadStudenten(fragebogenDto.getBearbeitetVon()))
        .bogennr(fragebogenDto.getId())
        .name(fragebogenDto.getName())
        .fragen(loadFragen(fragebogenDto.getFragen()))
        .build();
  }

  private List<Student> loadStudentenList(Set<SBelegtVDto> studenten) {
    return studenten.stream()
        .map(x -> studentenRepo.findById(x.getStudent()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(this::loadStudent)
        .collect(Collectors.toList());
  }

  private List<Student> loadStudenten(Set<SBeantwortetFDto> sBeantwortetFDtos) {
    return sBeantwortetFDtos.stream()
        .map(x -> studentenRepo.findById(x.getStudent()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(this::loadStudent)
        .collect(Collectors.toList());
  }

  private Student loadStudent(StudentDto studentDto) {
    return new Student(studentDto.getUsername());
  }

  private List<Frage> loadFragen(Set<FrageDto> fragen) {
    return fragen.stream()
        .map(frage -> loadFrage(frage))
        .collect(Collectors.toList());
  }

  private Frage loadFrage(FrageDto frage) {
    if (frage.isMultipleResponseFrage()) {
      return loadMultipleResponseFrage(frage);
    } else if (frage.isSingleResponseFrage()) {
      return loadSingleResponseFrage(frage);
    }
    return loadTextFrage(frage);
  }


  private TextFrage loadTextFrage(FrageDto frage) {
    TextFrage neueFrage = new TextFrage(frage.getId(), frage.getFragetext());
    for (AntwortDto dto : frage.getAntworten()) {
      neueFrage.addAntwort(loadTextAntwort(dto));
    }
    return neueFrage;
  }

  private TextAntwort loadTextAntwort(AntwortDto dto) {
    return new TextAntwort(dto.getId(), dto.getTextantwort());
  }

  private MultipleChoiceFrage loadMultipleResponseFrage(FrageDto frage) {
    // TODO HasMultipleResponce
    MultipleChoiceFrage neueFrage = new MultipleChoiceFrage(frage.getId(),
        frage.getFragetext());
    for (AntwortDto antwortDto : frage.getAntworten()) {
      neueFrage.addAntwort(loadMultipleChoiceAntwort(antwortDto));
    }
    for (AuswahlDto auswahlDto : frage.getAuswaehlbar()) {
      neueFrage.addChoice(loadAuswahl(auswahlDto));
    }
    return neueFrage;
  }

  private Frage loadSingleResponseFrage(FrageDto frage) {
    // TODO
    return null;
  }

  private Auswahl loadAuswahl(AuswahlDto auswahlDto) {
    return new Auswahl(auswahlDto.getId(), auswahlDto.getAuswahltext());
  }

  private MultipleChoiceAntwort loadMultipleChoiceAntwort(AntwortDto antwortDto) {
    // TODO
    return null;
  }

  public VeranstaltungDto createVeranstaltungDto(Veranstaltung veranstaltung) {
    VeranstaltungDto veranstaltungDto = VeranstaltungDto.create(veranstaltung.getName(),
        veranstaltung.getSemester());
    veranstaltungDto.setDozenten(Collections.singleton(createDOrganisiertVDto(veranstaltung.getDozent())));
    veranstaltungDto.setStudenten(veranstaltung.getStudenten().stream()
        .map(this::createSBelegtVDto)
        .collect(Collectors.toSet()));
    veranstaltungDto.setFrageboegen(veranstaltung.getFrageboegen().stream()
        .map(this::createFragebogenDto)
        .collect(Collectors.toSet()));
    return null;
  }

  private FragebogenDto createFragebogenDto(Fragebogen fragebogen) {
    FragebogenDto fragebogenDto = FragebogenDto.create(fragebogen.getName(), fragebogen.getType(),
        fragebogen.getStartdatum().toString(),
        fragebogen.getEnddatum().toString());
    for (Student student : fragebogen.getAbgegebeneStudierende()) {
      fragebogenDto.addBeantwortetVonStudent(createStudentDto(student));
    }
    for (Frage frage : fragebogen.getFragen()) {
      fragebogenDto.addFrage(createFrageDto(frage));
    }
    return fragebogenDto;
  }

  private FrageDto createFrageDto(Frage frage) {
    if (frage instanceof TextFrage) {
      return createTextFrageDto((TextFrage) frage);
    }
    return null;
  }

  private StudentDto createStudentDto(Student student) {
    return StudentDto.create(student.getUsername());
  }

  private SBelegtVDto createSBelegtVDto(Student student) {
    return new SBelegtVDto(studentenRepo.findId(student.getUsername()));
  }

  private DOrganisiertVDto createDOrganisiertVDto(Dozent dozent) {
    return new DOrganisiertVDto(dozentenRepo.findId(dozent.getUsername()));
  }

  private FrageDto createTextFrageDto(TextFrage frage) {
    FrageDto frageDto = FrageDto.createTextfrage(frage.toString());
    for (Antwort antwort : frage.getAntworten()) {
      frageDto.addAnwort(createTextAntwortDto((TextAntwort) antwort));
    }
    return frageDto;
  }

  private AntwortDto createTextAntwortDto(TextAntwort antwort) {
    return null;
  }
}