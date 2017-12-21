package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface AccountService {
    public Utilisateur getUtilisateurByPseudo(String pseudo);
    public Utilisateur createUser(Utilisateur user);
}
