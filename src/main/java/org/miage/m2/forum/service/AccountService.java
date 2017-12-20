package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Set;

@Service
public interface AccountService {
    public Utilisateur getUtilisateurByPseudo(String pseudo);
    public boolean createUser(String email, String pseudo, String mdp, boolean admin, Set<Message> message, Set<Projet> creators, Set<Topic> listTopicCreate, Set<Topic> suivi);
}
