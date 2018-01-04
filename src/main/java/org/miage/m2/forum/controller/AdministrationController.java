package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.ProjectForm;
import org.miage.m2.forum.formValidation.AccessProject;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AdministrationService;
import org.miage.m2.forum.service.AdministrationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/administration")
public class AdministrationController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepository projetRepository;

    AdministrationService administrationService = new AdministrationServiceImpl();

    @GetMapping(value = "")
    public String index(Model model){
        administrationService.setProjetRepository(projetRepository);
        Iterable<Projet> projets = administrationService.findAll();
        ProjectForm projectForm = new ProjectForm();
        model.addAttribute("projects",projets);
        model.addAttribute("projectForm",projectForm);
        return "administration/index";
    }

    @RequestMapping(value = "/project/{title}", method = RequestMethod.GET)
    public String getProjectForUpdate(@PathVariable String title, Model model){
        administrationService.setProjetRepository(projetRepository);
        Projet projet = administrationService.findOne(title);
        ProjectForm projectForm = new ProjectForm();
        projectForm.setTitre(projet.getTitre());
        projectForm.setDescription(projet.getDescription());
        projectForm.setInvite(projet.isInvite());
        model.addAttribute("projectForm",projectForm);
        model.addAttribute("title", projet.getTitre());
        return "administration/update";
    }

    @GetMapping(value = "/access/{title}")
    public String getAccessPage(@PathVariable String title, Model model){
        administrationService.setProjetRepository(projetRepository);
        Projet projet = administrationService.findOne(title);
        AccessProject accessProject = new AccessProject();
        accessProject.setTitle(projet.getTitre());
        model.addAttribute("accessProject", accessProject);
        model.addAttribute("title", projet.getTitre());
        return "administration/access";
    }

    @PostMapping(value = "/")
    public String createProject(
             @Valid ProjectForm projectForm
            ,BindingResult bindingResult
            ,Model model
    ){
        if(bindingResult.hasErrors()){
            Iterable<Projet> projets = administrationService.findAll();
            model.addAttribute("projects",projets);
            return "administration/index";
        }
        administrationService.setProjetRepository(projetRepository);
        Utilisateur creator = utilisateurRepository.findOne(getCurrentNameUser());
        Projet projet = new Projet(projectForm.getTitre(), projectForm.getDescription(), new Date(), projectForm.isInvite(), new HashSet<>(),
                creator, new HashSet<Topic>());

        Projet projetCreated = administrationService.createProject(projet);
        //echec de creation de projet
        if(projetCreated==null){
            model.addAttribute("failProject",true);
            return index(model);
        }else{
            System.out.println(projectForm.getProjetParent());
            Projet projetParent = administrationService.findOne(projectForm.getProjetParent());
            projetParent = administrationService.addProjectToProject(projetParent,projet);
            model.addAttribute("successProject",true);
            return index(model);
        }
    }

    @RequestMapping(value = "/project/update", method = RequestMethod.POST)
    public String updateProject(
            @Valid ProjectForm projectForm
            ,BindingResult bindingResult
            ,Model model
    ){
        if(bindingResult.hasErrors()){
            return "administration/updated";
        }
        administrationService.setProjetRepository(projetRepository);
        Utilisateur creator = utilisateurRepository.findOne(getCurrentNameUser());
        Projet projet = administrationService.findOne(projectForm.getTitre());

        Projet projetUpdated = administrationService.update(projet,projectForm.getTitre(),projectForm.getDescription(),projectForm.isInvite());
        if(projetUpdated!=null){
            projectForm = new ProjectForm();
            projectForm.setTitre(projetUpdated.getTitre());
            projectForm.setDescription(projetUpdated.getDescription());
            projectForm.setInvite(projetUpdated.isInvite());
            model.addAttribute("successProject",true);
            model.addAttribute("projectForm",projectForm);
            return getProjectForUpdate(projetUpdated.getTitre(),model);
        }else{
            model.addAttribute("failProject",true);
            System.out.println("fail updated");
            return getProjectForUpdate(projetUpdated.getTitre(),model);
        }
    }


    @PostMapping("/access")
    public String udpdateAccess(
            @Valid AccessProject accessProject
            ,BindingResult bindingResult
            ,Model model
    ){
        administrationService.setProjetRepository(projetRepository);
        Projet projet = administrationService.findOne(accessProject.getTitle());
        model.addAttribute("title", projet.getTitre());
        if(bindingResult.hasErrors()){
            return "administration/access";
        }


        if(projet.isInvite()){
            model.addAttribute("errorInvite",true);
            return "administration/access";
        }
        Set<Utilisateur> userAccess = new HashSet<>();
        if(accessProject.getUserAccess()!=null){
            Utilisateur userAcces = utilisateurRepository.findByPseudo(accessProject.getUserAccess());
            if(userAcces!=null){
                userAccess.add(userAcces);
                projet = administrationService.addUserToAccess(projet,userAccess);
            }
        }
        Set<Utilisateur> userRemoves = new HashSet<>();
        if(accessProject.getUserRemove()!=null){
            Utilisateur userRemove = utilisateurRepository.findByPseudo(accessProject.getUserRemove());
            if(userRemove!=null){
                userRemoves.add(userRemove);
                projet = administrationService.removeUserToAccess(projet,userRemoves);
            }
        }

        if(projet == null){
            model.addAttribute("error",true);
            return "administration/access";
        }

        model.addAttribute("success",true);

        return "administration/access";
    }

    /**
     * Obtenir la session de l'utilisateur si il est connecté
     * @return email de l'utilisateur connecté
     */
    private String getCurrentNameUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if(principal instanceof String){
            return principal.toString();
        }
        User user = (User) principal;
        return user.getUsername();
    }

}
