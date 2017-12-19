package org.miage.m2.forum.controller;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@RequestMapping("/account/")
public class AccountController {

    public static boolean createUser(String email, String pseudo, String mdp, boolean admin, Set<Message> message, Set<Projet> creators, Set<Topic> listTopicCreate, Set<Topic> suivi) {
        Utilisateur newUser = new Utilisateur();
        newUser.setEmail(email);
        newUser.setPseudo(pseudo);
        newUser.setMdp(mdp);
        newUser.setAdmin(admin);
        newUser.setMessage(message);
        newUser.setCreators(creators);
        newUser.setListTopicCreate(listTopicCreate);
        newUser.setSuivi(suivi);
        return true;
    }

}
