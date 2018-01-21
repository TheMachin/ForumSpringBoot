package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.FollowForm;
import org.miage.m2.forum.formValidation.MessageForm;
import org.miage.m2.forum.formValidation.ReadWriteForm;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/")
public class TopicController {

    public static final Logger logger = LoggerFactory.getLogger(TopicController.class);

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

    @GetMapping("/projects/{title}/topics/{subject}/")
    public String getMessage(@PathVariable String title, @PathVariable String subject, Principal principal, Model model) {

        projetService.setProjetRepository(projetRepository);
        topicService.setTopicRepository(topicRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        boolean readWrite = false;
        boolean follow = false;

        Projet p = projetService.getOne(title);
        Topic t = topicService.findOne(subject);
        if (p == null || t == null) {
            return "redirect:/";
        }

        List<Message> messages = new ArrayList<Message>();
        messages.addAll(t.getMessage());

        /**
         * on récupère les infos de l'utilisateur connecté
         */
        Utilisateur userConnected = utilisateurRepository.findOne(currentUserService.getCurrentNameUser(principal));

        if (userConnected != null) {
            if (t.getCreator().equals(userConnected)) {
                readWrite = true;
            }

            List<Topic> followUser = new ArrayList<Topic>();
            followUser.addAll(userConnected.getSuivi());
            for (int i = 0; i < followUser.size(); i++) {
                if(followUser.get(i).equals(t)){
                    follow = true;
                }
            }
        }


        model.addAttribute("messages", messages);
        model.addAttribute("nomProjet", title);
        model.addAttribute("subject", subject);
        model.addAttribute("messageForm", new MessageForm());
        model.addAttribute("readWrite", readWrite);
        model.addAttribute("follow", follow);
        model.addAttribute("followForm", new FollowForm());
        model.addAttribute("readWriteForm", new ReadWriteForm());

        return "topics";
    }

    @PostMapping(value = "/projects/{title}/topics/{subject}/")
    public String createMessage(@Valid MessageForm messageForm, @PathVariable String title, @PathVariable String subject, Principal principal, BindingResult bindingResult, Model model) {
        /**
         * si ya des erreurs dans le formulaire, on notifue à l'utilisateur
         */
        if (bindingResult.hasErrors()) {

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
         * on crée l'objet message et on l'insère dans le topic
         */
        Topic topic = topicRepository.findOne(messageForm.getTopic());

        Message message = new Message(messageForm.getMessage(), new Date(), creator, topic);

        /**
         * insertion dans la bdd
         */
        Message messageCreated = messageService.createMessage(message);
        if (messageCreated == null) {
            logger.error("unable to create a message");
            model.addAttribute("failMessage", true);
        }
        return getMessage(title, subject, principal, model);
    }

    @PostMapping(value = "/projects/{title}/topics/{subject}/follow")
    public String follow(@Valid FollowForm followForm, Principal principal, BindingResult bindingResult, Model model) {

        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        /**
         * on récupère les infos de l'utilisateur qui va follow
         */
        Utilisateur userConnected = utilisateurRepository.findOne(currentUserService.getCurrentNameUser(principal));

        Topic topic = topicRepository.findOne(followForm.getTopic());

        List<Topic> followUser = new ArrayList<Topic>();
        followUser.addAll(userConnected.getSuivi());

        followUser.add(topic);
        Set<Topic> setFollow = new HashSet<Topic>(followUser);
        userConnected.setSuivi(setFollow);

        /**
         * insertion dans la bdd
         */
        Utilisateur utilisateur = accountService.updateSuiveur(userConnected);
        if (utilisateur == null) {
            logger.error("unable to update a user");
            model.addAttribute("failUser", true);
        }

        return "follow";
    }

    @PostMapping(value = "/projects/{title}/topics/{subject}/unfollow")
    public String unfollow(@Valid FollowForm followForm, Principal principal, BindingResult bindingResult, Model model) {

        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        /**
         * on récupère les infos de l'utilisateur qui va unfollow
         */
        Utilisateur userConnected = utilisateurRepository.findOne(currentUserService.getCurrentNameUser(principal));

        Topic topic = topicRepository.findOne(followForm.getTopic());

        List<Topic> followUser = new ArrayList<Topic>();
        followUser.addAll(userConnected.getSuivi());

        for (int i = 0; i < followUser.size(); i++) {
            if(followUser.get(i).equals(topic)){
                followUser.remove(i);
            }
        }

        Set<Topic> setFollow = new HashSet<Topic>(followUser);
        userConnected.setSuivi(setFollow);

        /**
         * insertion dans la bdd
         */
        Utilisateur utilisateur = accountService.updateSuiveur(userConnected);
        if (utilisateur == null) {
            logger.error("unable to update a user");
            model.addAttribute("failUser", true);
        }

        return "unfollow";
    }

    @PostMapping(value = "/projects/{title}/topics/{subject}/access")
    public String modifyAccess(@Valid ReadWriteForm readWriteForm, @PathVariable String title, @PathVariable String subject, Principal principal, BindingResult bindingResult, Model model) {

        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        /**
         * on récupère les infos de l'utilisateur admin ou créateur du topic
         */
        Utilisateur userConnected = utilisateurRepository.findOne(currentUserService.getCurrentNameUser(principal));

        Topic topic = topicRepository.findOne(readWriteForm.getTopic());

        List<Utilisateur> listReadUser = new ArrayList<Utilisateur>();
        listReadUser.addAll(topic.getLecture());

        List<Utilisateur> listWriteUser = new ArrayList<Utilisateur>();
        listWriteUser.addAll(topic.getEcriture());

        Set<Utilisateur> setAddReadUser = new HashSet<>();
        Set<Utilisateur> setAddWriteUser = new HashSet<>();
        Set<Utilisateur> setRemoveReadUser = new HashSet<>();
        Set<Utilisateur> setRemoveWriteUser = new HashSet<>();

        Utilisateur userExisted;

        if(!readWriteForm.getAddReadUser().equals("")){
            if(topic.getCreator().getPseudo().equals(readWriteForm.getAddReadUser())){
                System.out.println("Le créateur ne peut pas s'ajouter en lecture.");
                return getMessage(title, subject, principal, model);
            }
            for(int i=0;i<listReadUser.size();i++){
                if(readWriteForm.getAddReadUser().equals(listReadUser.get(i).getPseudo())){
                    System.out.println("Utilisateur déjà accès en lecture.");
                    return getMessage(title, subject, principal, model);
                }
            }
            userExisted = accountService.getUtilisateurByPseudo(readWriteForm.getAddReadUser());
            if(userExisted == null){
                System.out.println("L'utilisateur n'existe pas.");
                return getMessage(title, subject, principal, model);
            }
            if(listReadUser.size() == 0){
                setAddReadUser.add(topic.getCreator());
            }
            setAddReadUser.add(userExisted);
        }




        if(!readWriteForm.getAddWriteUser().equals("")){
            if(topic.getCreator().getPseudo().equals(readWriteForm.getAddWriteUser())){
                System.out.println("Le créateur ne peut pas s'ajouter en écriture.");
                return getMessage(title, subject, principal, model);
            }
            for(int i=0;i<listWriteUser.size();i++){
                if(readWriteForm.getAddWriteUser().equals(listWriteUser.get(i).getPseudo())){
                    System.out.println("Utilisateur déjà accès en écriture.");
                    return getMessage(title, subject, principal, model);
                }
            }
            userExisted = accountService.getUtilisateurByPseudo(readWriteForm.getAddWriteUser());
            if(userExisted == null){
                System.out.println("L'utilisateur n'existe pas.");
                return getMessage(title, subject, principal, model);
            }
            if(listWriteUser.size() == 0){
                setAddWriteUser.add(topic.getCreator());
            }
            setAddWriteUser.add(userExisted);
            if (!readWriteForm.getAddReadUser().equals(readWriteForm.getAddWriteUser())) {
                boolean read = true;
                for(int i=0;i<listReadUser.size();i++){
                    if(userExisted.getPseudo().equals(listReadUser.get(i).getPseudo())){
                        read = false;
                    }
                }
                if(read){
                    setAddReadUser.add(userExisted);
                    if(listReadUser.size() == 0){
                        setAddReadUser.add(topic.getCreator());
                    }
                }
            }
        }


        if(!readWriteForm.getDeleteReadUser().equals("")){
            if(topic.getCreator().getPseudo().equals(readWriteForm.getDeleteReadUser())){
                System.out.println("Le créateur ne peut pas s'effacer en lecture.");
                return getMessage(title, subject, principal, model);
            }
            boolean find = false;
            for(int i=0;i<listReadUser.size();i++){
                if(readWriteForm.getDeleteReadUser().equals(listReadUser.get(i).getPseudo())){
                    find = true;
                }
            }
            if(!find) {
                System.out.println("Utilisateur non trouvé");
                return getMessage(title, subject, principal, model);
            }
            userExisted = accountService.getUtilisateurByPseudo(readWriteForm.getDeleteReadUser());
            if(userExisted == null){
                System.out.println("L'utilisateur n'existe pas.");
                return getMessage(title, subject, principal, model);
            }
            setRemoveReadUser.add(userExisted);
            if(setRemoveReadUser.size() == listReadUser.size()-1){
                setRemoveReadUser.add(topic.getCreator());
            }
            if (!readWriteForm.getDeleteReadUser().equals(readWriteForm.getDeleteWriteUser())) {
                boolean read = false;
                for(int i=0;i<listWriteUser.size();i++){
                    if(userExisted.getPseudo().equals(listWriteUser.get(i).getPseudo())){
                        read = true;
                    }
                }
                if(read){
                    setRemoveWriteUser.add(userExisted);
                    if(setRemoveWriteUser.size() == listWriteUser.size()-1){
                        setRemoveWriteUser.add(topic.getCreator());
                    }
                }
            }
        }


        if(!readWriteForm.getDeleteWriteUser().equals("")){
            if(topic.getCreator().getPseudo().equals(readWriteForm.getDeleteWriteUser())){
                System.out.println("Le créateur ne peut pas s'effacer en écriture.");
                return getMessage(title, subject, principal, model);
            }
            boolean find = false;
            for(int i=0;i<listWriteUser.size();i++){
                if(readWriteForm.getDeleteWriteUser().equals(listWriteUser.get(i).getPseudo())){
                    find = true;
                }
            }
            if(!find) {
                System.out.println("Utilisateur non trouvé");
                return getMessage(title, subject, principal, model);
            }
            userExisted = accountService.getUtilisateurByPseudo(readWriteForm.getDeleteWriteUser());
            if(userExisted == null){
                System.out.println("L'utilisateur n'existe pas.");
                return getMessage(title, subject, principal, model);
            }
            setRemoveWriteUser.add(userExisted);
            if(setRemoveWriteUser.size() == listWriteUser.size()-1){
                setRemoveWriteUser.add(topic.getCreator());
            }
        }

        /**
         * insertion dans la bdd
         */
        Topic topicAddReadUpdate = topicService.addUserToLecture(topic, setAddReadUser);
        if (topicAddReadUpdate == null) {
            logger.error("unable to update a topic");
            model.addAttribute("failTopic", true);
        }

        Topic topicAddWriteUpdate = topicService.addUserToEcriture(topic, setAddWriteUser);
        if (topicAddWriteUpdate == null) {
            logger.error("unable to update a topic");
            model.addAttribute("failTopic", true);
        }

        Topic topicDeleteReadUpdate = topicService.removeUserToLecture(topic, setRemoveReadUser);
        if (topicDeleteReadUpdate == null) {
            logger.error("unable to update a topic");
            model.addAttribute("failTopic", true);
        }

        Topic topicDeleteWriteUpdate = topicService.removeUserToEcriture(topic, setRemoveWriteUser);
        if (topicDeleteWriteUpdate == null) {
            logger.error("unable to update a topic");
            model.addAttribute("failTopic", true);
        }
        return "access";
    }
}
