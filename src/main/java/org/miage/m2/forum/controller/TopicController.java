package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.MessageForm;
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

    @GetMapping("/projects/{title}/topics/{subject}")
    public String getMessage(@PathVariable String title, @PathVariable String subject, Principal principal, Model model) {

        projetService.setProjetRepository(projetRepository);
        topicService.setTopicRepository(topicRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);

        Projet p = projetService.getOne(title);
        Topic t = topicService.findOne(subject);
        if (p == null || t == null) {
            return "redirect:/";
        }

        List<Message> messages = new ArrayList<Message>();
        messages.addAll(t.getMessage());

        model.addAttribute("messages", messages);
        model.addAttribute("nomProjet", title);
        model.addAttribute("subject", subject);
        model.addAttribute("messageForm", new MessageForm());

        return "topics";
    }

    @PostMapping(value = "/projects/{title}/topics/{subject}")
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
        /*List<Message> messagesTopic = new ArrayList<Message>();
        messagesTopic.addAll(topic.getMessage());*/

        Message message = new Message(messageForm.getMessage(), new Date(), creator, topic);
        /*messagesTopic.add(message);
        Set<Message> setMessagesTopic = new HashSet<Message>(messagesTopic);
        topic.setMessage(setMessagesTopic);*/

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
}
