package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface AccountService {
    public Utilisateur getUtilisateurByPseudo(String pseudo);
    public boolean checkUser(Utilisateur user);
    public Utilisateur createUser(Utilisateur user);
    public boolean deleteUser(Utilisateur user);
    public Utilisateur modifyUser(Utilisateur user, String emailModify, String pseudoModify, String mdpModify, boolean adminModify);
    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository);
    public Utilisateur updateSuiveur(Utilisateur user);
}
