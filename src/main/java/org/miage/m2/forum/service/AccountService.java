package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AccountService {

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
