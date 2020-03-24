package mops.controllers;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import mops.DozentService;
import mops.FragebogenTemplate;
import mops.TypeChecker;
import mops.database.MockDozentenRepository;
import mops.rollen.Dozent;
import mops.security.Account;

@Controller
@RequestMapping("/feedback/dozenten/templates")
public class DozentTemplateController {
  private final DozentRepository dozenten;
  private final DozentService dozentservice;
  private final TypeChecker typechecker;

  public DozentTemplateController() {
    dozenten = new MockDozentenRepository();
    dozentservice = new DozentService();
    typechecker = new TypeChecker();
  }

  @GetMapping("")
  public String getTemplatePage(Model model, KeycloakAuthenticationToken token) {
    Dozent dozent = getDozentFromToken(token);
    model.addAttribute("templates", dozent.getTemplates());
    model.addAttribute("account", createAccountFromPrincipal(token));
    return "dozenten/templates";
  }

  @PostMapping("")
  public String neuesTemplate(String templatename, KeycloakAuthenticationToken token) {
    FragebogenTemplate template = new FragebogenTemplate(templatename);
    Dozent dozent = getDozentFromToken(token);
    dozent.addTemplate(template);
    return "redirect:/feedback/dozenten/templates/" + template.getId();
  }

  @GetMapping("/{templatenr}")
  public String templateBearbeitung(@PathVariable Long templatenr,
      KeycloakAuthenticationToken token, Model model) {
    Dozent dozent = getDozentFromToken(token);
    model.addAttribute("template", dozent.getTemplateById(templatenr));
    model.addAttribute("typechecker", typechecker);
    model.addAttribute("account", createAccountFromPrincipal(token));
    return "dozenten/edittemplate";
  }

  @PostMapping("/{templatenr}")
  public String neueFrage(@PathVariable Long templatenr, KeycloakAuthenticationToken token,
      String fragetyp, String fragetext) {
    Dozent dozent = getDozentFromToken(token);
    FragebogenTemplate template = dozent.getTemplateById(templatenr);
    template.addFrage(dozentservice.createNeueFrageAnhandFragetyp(fragetyp, fragetext));
    return "redirect:/feedback/dozenten/templates/" + templatenr;
  }

  @PostMapping("/delete/{templatenr}")
  public String deleteTemplate(@PathVariable Long templatenr, KeycloakAuthenticationToken token) {
    Dozent dozent = getDozentFromToken(token);
    dozent.deleteTemplateById(templatenr);
    return "redirect:/feedback/dozenten/templates";
  }

  private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
    KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return new Account(principal.getName(),
        principal.getKeycloakSecurityContext().getIdToken().getEmail(), null,
        token.getAccount().getRoles());
  }

  private Dozent getDozentFromToken(KeycloakAuthenticationToken token) {
    KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return dozenten.getDozentByUsername(principal.getName());
  }
}
