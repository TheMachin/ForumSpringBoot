package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.ProjectForm;
import org.miage.m2.forum.formValidation.AccessProject;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.miage.m2.forum.service.AdministrationService;
import org.miage.m2.forum.service.AdministrationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/administration")
public class AdministrationController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public static final Logger logger = LoggerFactory.getLogger(AdministrationController.class);

    @Autowired
    private ProjetRepository projetRepository;

    AdministrationService administrationService = new AdministrationServiceImpl();

    AccountService accountService = new AccountServiceImpl();

    private CurrentUserService currentUserService = new CurrentUserService();

    /**
     * send a list of all projects
     * @param model
     * @return page principale d'administration
     */
    @GetMapping(value = "")
    public String index(Principal principal, Model model){
        administrationService.setProjetRepository(projetRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        currentUserService.getCurrentNameUser(principal);
        Iterable<Projet> projets = administrationService.findAll();
        ProjectForm projectForm = new ProjectForm();
        model.addAttribute("projects",projets);
        model.addAttribute("projectForm",projectForm);
        return "administration/index";
    }

    /**
     * récupère les infos du projet à partir du titre
     * creer l'objet ProjectForm qui sera utilisé pour le formulaire
     * ajoute des informations
     *
     * @param title
     * @param model
     * @return page pour modifier le projet (titre, acces invité, description)
     */
    @RequestMapping(value = "/project/{title}", method = RequestMethod.GET)
    public String getProjectForUpdate(@PathVariable String title, Principal principal, Model model){
        administrationService.setProjetRepository(projetRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        currentUserService.getCurrentNameUser(principal);

        Projet projet = administrationService.findOne(title);
        ProjectForm projectForm = new ProjectForm();
        projectForm.setTitre(projet.getTitre());
        projectForm.setDescription(projet.getDescription());
        projectForm.setInvite(projet.isInvite());
        model.addAttribute("projectForm",projectForm);
        model.addAttribute("title", projet.getTitre());
        return "administration/update";
    }

    /**
     * récupère les infos du projet à partir du titre
     * ajoute des informations dans un objet accessProject qui sera utilisé dans un formulaire
     * @param title
     * @param model
     * @return page pour gérer qui a accès au projet
     */
    @GetMapping(value = "/access/{title}")
    public String getAccessPage(@PathVariable String title, Principal principal, Model model){
        administrationService.setProjetRepository(projetRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        currentUserService.getCurrentNameUser(principal);

        Projet projet = administrationService.findOne(title);
        AccessProject accessProject = new AccessProject();
        accessProject.setTitle(projet.getTitre());
        model.addAttribute("accessProject", accessProject);
        model.addAttribute("title", projet.getTitre());
        //ajoute la liste des utilisateurs ayant accès au projet
        model.addAttribute("access", projet.getAcces());
        return "administration/access";
    }

    /**
     * Create a noew project
     * @param projectForm
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping(value = "/")
    public String createProject(
             @Valid ProjectForm projectForm
            ,BindingResult bindingResult
            ,Principal principal
            ,Model model
    ){
        /**
         * si ya des erreurs dans le formulaire, on notifue à l'utilisateur
         */
        if(bindingResult.hasErrors()){
            Iterable<Projet> projets = administrationService.findAll();
            model.addAttribute("projects",projets);
            return "administration/index";
        }
        administrationService.setProjetRepository(projetRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        /**
         * récupere les infos de l'utilisateur qui va etre le createur du projet
         */
        Utilisateur creator = utilisateurRepository.findOne(currentUserService.getCurrentNameUser(principal));

        /**
         * créer l'objet projet
         */
        Projet projet = new Projet(projectForm.getTitre(), projectForm.getDescription(), new Date(), projectForm.isInvite(), new HashSet<>(),
                creator, new HashSet<Topic>());

        /**
         * insertion dans la bdd
         */
        Projet projetCreated = administrationService.createProject(projet);
        //echec de creation de projet
        if(projetCreated==null){
            logger.error("unable to create a project");
            model.addAttribute("failProject",true);
            return index(principal, model);
        }else{
            logger.info(projectForm.getProjetParent());
            /**
             * si ce projet est un sous projet, on l'ajoute au projet parent
             */
            Projet projetParent = administrationService.findOne(projectForm.getProjetParent());
            projetParent = administrationService.addProjectToProject(projetParent,projet);
            model.addAttribute("successProject",true);
            return index(principal, model);
        }
    }

    /**
     * Update a project
     * @param projectForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "/project/update", method = RequestMethod.POST)
    public String updateProject(
            @Valid ProjectForm projectForm
            ,BindingResult bindingResult
            ,Principal principal
            ,Model model
    ){
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        currentUserService.getCurrentNameUser(principal);
        /**
         * si ya des erreurs dans le formulaire, on notifue à l'utilisateur
         */
        if(bindingResult.hasErrors()){
            return "administration/updated";
        }
        administrationService.setProjetRepository(projetRepository);

        /**
         * récupère les infos du projets depuis la bdd
         */
        Projet projet = administrationService.findOne(projectForm.getTitre());


        /**
         * on met à jour le projet
         */
        Projet projetUpdated = administrationService.update(projet,projectForm.getTitre(),projectForm.getDescription(),projectForm.isInvite());

        /**
         * si la maj est un succes on ajoute les nouvelles infos dans le formulaire
         * sinon on notifie l'échec
         */
        if(projetUpdated!=null){
            projectForm = new ProjectForm();
            projectForm.setTitre(projetUpdated.getTitre());
            projectForm.setDescription(projetUpdated.getDescription());
            projectForm.setInvite(projetUpdated.isInvite());
            model.addAttribute("successProject",true);
            model.addAttribute("projectForm",projectForm);
            return getProjectForUpdate(projetUpdated.getTitre(), principal ,model);
        }else{

            model.addAttribute("failProject",true);
            System.out.println("fail updated");
            return getProjectForUpdate(projetUpdated.getTitre(), principal, model);
        }
    }


    /**
     * Update access of a project
     *
     * @param accessProject
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping("/access")
    public String udpdateAccess(
            @Valid AccessProject accessProject
            ,BindingResult bindingResult
            ,Model model
    ){
        administrationService.setProjetRepository(projetRepository);

        /**
         * recupere infos projet et ajoute les infos dans un model attribute
         */
        Projet projet = administrationService.findOne(accessProject.getTitle());
        model.addAttribute("title", projet.getTitre());
        model.addAttribute("access", projet.getAcces());

        /**
         * si il y a des erreurs dans le formulaire
         */
        if(bindingResult.hasErrors()){
            return "administration/access";
        }

        /**
         * Si un projet autorise les invités à y accéder, tout le monde a l'accès
         * Il est donc inutile de gérer les accès pour les utilisateurs car les droits sont les mêmes.
         */
        if(projet.isInvite()){
            model.addAttribute("errorInvite",true);
            return "administration/access";
        }

        /**
         * recupere les infos de l'utilisateur pour l'autorisé à accéder ua projet
         */
        Set<Utilisateur> userAccess = new HashSet<>();
        if(accessProject.getUserAccess()!=null){
            Utilisateur userAcces = utilisateurRepository.findByPseudo(accessProject.getUserAccess());
            if(userAcces!=null){
                userAccess.add(userAcces);
                projet = administrationService.addUserToAccess(projet,userAccess);
            }
        }

        /**
         * Même chose mais pour enlever l'acces
         */
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

    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public void setProjetRepository(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }
}
