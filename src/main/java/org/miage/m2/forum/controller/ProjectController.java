package org.miage.m2.forum.controller;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.miage.m2.forum.service.ProjetService;
import org.miage.m2.forum.service.ProjetServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class ProjectController {

    public static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepository projetRepository;

    private ProjetService projetService = new ProjetServiceImpl();

    private CurrentUserService currentUserService = new CurrentUserService();

    AccountService accountService = new AccountServiceImpl();

    /**
     * Recuperer la liste des projets pour les afficher sur la page d'accueil
     * enleve dans cette liste, les projets dont l'utilisateur ou l'internate n'a pas les acces
     * @param principal
     * @param model
     * @return
     */
    @GetMapping(value="/")
    public String index(Principal principal, Model model){

        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);


        projetService.setProjetRepository(projetRepository);

        Iterable<Projet> projets = projetService.getAll();

        List<Projet> projetList = new ArrayList<Projet>();
        String user = currentUserService.getCurrentNameUser(principal);
        Utilisateur utilisateur=null;
        if(!user.equals(new String("anonymousUser"))){
            utilisateur = utilisateurRepository.findOne(user);
        }
        logger.info(projets.toString());
        projetList = projectsByRights(projets, user, utilisateur);

        model.addAttribute("projets",projetList);

        return "index";
    }

    @GetMapping("/projects/{title}/")
    public String getProjectChildAndTopics(@PathVariable String title, Principal principal, Model model){

        projetService.setProjetRepository(projetRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        Projet p = projetService.getOne(title);

        if(p == null){
            return "redirect:/";
        }

        Iterable<Projet> projets = p.getSousProjet();

        List<Projet> projetList = new ArrayList<Projet>();
        Utilisateur utilisateur = null;
        String user =  "anonymousUser";
        user = currentUserService.getCurrentNameUser(principal);
        if (!user.equals(new String("anonymousUser"))) {
            utilisateur = utilisateurRepository.findOne(user);
        }

        logger.info(projets.toString());
        projetList = projectsByRights(projets, user, utilisateur);

        model.addAttribute("projets",projetList);

        return "projects";
    }

    private List<Projet> projectsByRights(Iterable<Projet> projets, String user, Utilisateur utilisateur){

        List<Projet> projetList = new ArrayList<Projet>();


        for(Projet projet : projets){
            logger.info(projet.toString());
            if(user.equals(new String("anonymousUser"))){
                if(projet.isInvite()){
                    projetList.add(projet);
                }
            }else{
                if(projet.isInvite()){
                    projetList.add(projet);
                }
                if(utilisateur!=null){
                    for(Utilisateur u : projet.getAcces()){
                        if(u.getEmail().equals(utilisateur.getEmail())){
                            projetList.add(projet);
                            break;
                        }
                    }
                }
            }
        }
        logger.info(projetList.toString());
        return projetList;
    }

    public UtilisateurRepository getUtilisateurRepository() {
        return utilisateurRepository;
    }

    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public ProjetRepository getProjetRepository() {
        return projetRepository;
    }

    public void setProjetRepository(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }

    public ProjetService getProjetService() {
        return projetService;
    }

    public void setProjetService(ProjetService projetService) {
        this.projetService = projetService;
    }

    public CurrentUserService getCurrentUserService() {
        return currentUserService;
    }

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
