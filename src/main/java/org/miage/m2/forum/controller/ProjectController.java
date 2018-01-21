package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.ProjectForm;
import org.miage.m2.forum.formValidation.TopicForm;
import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.MessageRepository;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.TopicRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/")
public class ProjectController {

    public static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private MessageRepository messageRepository;

    private ProjetService projetService = new ProjetServiceImpl();

    TopicService topicService = new TopicServiceImpl();

    MessageService messageService = new MessageServiceImpl();

    private CurrentUserService currentUserService = new CurrentUserService();

    AccountService accountService = new AccountServiceImpl();

    /**
     * Recuperer la liste des projets pour les afficher sur la page d'accueil
     * enleve dans cette liste, les projets dont l'utilisateur ou l'internate n'a pas les acces
     *
     * @param principal
     * @param model
     * @return
     */
    @GetMapping(value = "/")
    public String index(Principal principal, Model model) {

        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);


        projetService.setProjetRepository(projetRepository);

        Iterable<Projet> projets = projetService.getAll();

        List<Projet> projetList = new ArrayList<Projet>();
        String user = currentUserService.getCurrentNameUser(principal);
        Utilisateur utilisateur = null;
        if (!user.equals(new String("anonymousUser"))) {
            utilisateur = utilisateurRepository.findOne(user);
        }
        logger.info(projets.toString());
        projetList = projectsByRights(projets, user, utilisateur);

        model.addAttribute("projets", projetList);

        return "index";
    }

    /**
     * A partir du titre du projet
     * On recupere ce projet puis on affiche ses topics et ses sous projets
     * @param title
     * @param principal
     * @param model
     * @return
     */
    @GetMapping("/projects/{title}/")
    public String getProjectChildAndTopics(@PathVariable String title, Principal principal, Model model) {

        model = getModelToProjectWithTopics(title, principal, model);

        if(model == null){
            return "redirect:/";
        }

        return "projects";
    }



    /**
     * Create a new topic
     * @param topicForm formulaire du topic pour la validation
     * @param title titre du projet
     * @param principal
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping(value = "/projects/{title}/")
    public String createTopic(@Valid TopicForm topicForm,
                              BindingResult bindingResult,
                              @PathVariable String title,
                              Principal principal ,
                              Model model) {
        /**
         * si ya des erreurs dans le formulaire, on notifue à l'utilisateur
         */
        if (bindingResult.hasErrors()) {
            model = getModelToProjectWithTopics(title, principal, model);
            /**
             * Copie les erreurs du formulaires dans un BeanPorpertyBindingResult
             */
            BeanPropertyBindingResult result2 = new BeanPropertyBindingResult(topicForm, bindingResult.getObjectName());
            for(ObjectError error: bindingResult.getGlobalErrors()) {
                result2.addError(error);
            }
            for (FieldError error: bindingResult.getFieldErrors()) {
                result2.addError(new FieldError(error.getObjectName(), error.getField(), null, error.isBindingFailure(), error.getCodes(), error.getArguments(), error.getDefaultMessage()));
            }
            model.addAllAttributes(result2.getModel());

            return "projects";
        }
        topicService.setTopicRepository(topicRepository);
        messageService.setMessageRepository(messageRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        /**
         * on récupère les infos de l'utilisateur qui va être le créateur du topic
         */
        Utilisateur creator = utilisateurRepository.findOne(currentUserService.getCurrentNameUser(principal));

        /**
         * on crée l'objet topic
         */
        Projet projet = projetService.getOne(topicForm.getProjet());
        HashSet<Message> listMessage = new HashSet<Message>();
        Topic topic = new Topic(topicForm.getTitre(), new Date(), topicForm.isInvite(), listMessage, projet, new HashSet<Utilisateur>(), new HashSet<Utilisateur>(), creator, new HashSet<Utilisateur>());

        /**
         * on crée l'objet message et on l'insère dans le topic
         */
        Message message = new Message(topicForm.getMessage(), new Date(), creator, topic);
        listMessage.add(message);
        topic.setMessage(listMessage);

        /**
         * insertion dans la bdd
         */
        Topic topicCreated = topicService.createTopic(topic);
        if (topicCreated == null) {
            logger.error("unable to create a topic");
            model.addAttribute("failTopic", true);
            return index(principal, model);
        }

        Message messageCreated = messageService.createMessage(message);
        if (messageCreated == null) {
            logger.error("unable to create a message");
            model.addAttribute("failMessage", true);
            return index(principal, model);
        }

        /**
         * l'utilisateur suit automatiquement le topic
         */

        creator = utilisateurRepository.findOne(currentUserService.getCurrentNameUser(principal));

        List<Topic> followUser = new ArrayList<Topic>();
        followUser.addAll(creator.getSuivi());

        followUser.add(topic);
        Set<Topic> setFollow = new HashSet<Topic>(followUser);
        creator.setSuivi(setFollow);

        /**
         * update dans la bdd
         */
        Utilisateur utilisateur = accountService.updateSuiveur(creator);
        if (utilisateur == null) {
            logger.error("unable to update a user");
            model.addAttribute("failUser", true);
        }

        return getProjectChildAndTopics(title, principal, model);
    }


    private Model getModelToProjectWithTopics(String title, Principal principal, Model model){

        projetService.setProjetRepository(projetRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        Projet p = projetService.getOne(title);

        /**
         * si le projet est null on retourne à la page d'accueil
         */
        if (p == null) {
            return null;
        }

        Iterable<Projet> projets = p.getSousProjet();
        Iterable<Topic> topics = p.getTopics();

        List<Projet> projetList = new ArrayList<Projet>();
        List<Topic> topicList = new ArrayList<Topic>();
        Utilisateur utilisateur = null;
        String user = "anonymousUser";
        user = currentUserService.getCurrentNameUser(principal);
        if (!user.equals(new String("anonymousUser"))) {
            utilisateur = utilisateurRepository.findOne(user);
        }

        projetList = projectsByRights(projets, user, utilisateur);
        topicList = topicsByRights(topics, user, utilisateur);

        TopicForm topicForm = new TopicForm();
        topicForm.setProjet(title);

        model.addAttribute("projets", projetList);
        model.addAttribute("topics", topicList);
        model.addAttribute("nomProjet", title);
        model.addAttribute("topicForm", new TopicForm());


        return model;
    }

    /**
     * Permet de récuperer une liste projet en fonction des droits d'acces (internaute ou utilisateur)
     * @param projets
     * @param user
     * @param utilisateur
     * @return liste de projet
     */
    private List<Projet> projectsByRights(Iterable<Projet> projets, String user, Utilisateur utilisateur) {

        List<Projet> projetList = new ArrayList<Projet>();


        for (Projet projet : projets) {
            logger.info(projet.toString());
            if (user.equals(new String("anonymousUser"))) {
                if (projet.isInvite()) {
                    projetList.add(projet);
                }
            } else {
                if (projet.isInvite()) {
                    projetList.add(projet);
                }
                if (utilisateur != null) {
                    for (Utilisateur u : projet.getAcces()) {
                        if (u.getEmail().equals(utilisateur.getEmail())) {
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

    /**
     * Permet de récuperer une liste de topic en fonction des droits d'acces (internaute, utilisateur)
     * @param topics
     * @param user
     * @param utilisateur
     * @return liste de topic
     */
    private List<Topic> topicsByRights(Iterable<Topic> topics, String user, Utilisateur utilisateur) {

        List<Topic> topicList = new ArrayList<Topic>();


        for (Topic topic : topics) {
            logger.info(topic.toString());
            if (user.equals(new String("anonymousUser"))) {
                if (topic.isInvite()) {
                    topicList.add(topic);
                }
            } else {
                if (topic.isInvite()) {
                    topicList.add(topic);
                } else {
                    if (utilisateur != null && topic.getLecture().size() == 0) {
                        topicList.add(topic);
                    }
                }
                if (utilisateur != null) {
                    for (Utilisateur u : topic.getLecture()) {
                        if (u.getEmail().equals(utilisateur.getEmail())) {
                            topicList.add(topic);
                            break;
                        }
                    }
                }
            }
        }
        logger.info(topicList.toString());
        return topicList;
    }

    public UtilisateurRepository getUtilisateurRepository() {
        return utilisateurRepository;
    }

    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public void setProjetRepository(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }

    public void setProjetService(ProjetService projetService) {
        this.projetService = projetService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public TopicRepository getTopicRepository() {
        return topicRepository;
    }

    public void setTopicRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public ProjetService getProjetService() {
        return projetService;
    }

    public TopicService getTopicService() {
        return topicService;
    }

    public void setTopicService(TopicService topicService) {
        this.topicService = topicService;
    }
}
