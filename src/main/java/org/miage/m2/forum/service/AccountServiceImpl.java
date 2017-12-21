package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.rmi.CORBA.Util;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur getUtilisateurByPseudo(String pseudo) {
        return utilisateurRepository.findByPseudo(pseudo);
    }

    public boolean checkUser(Utilisateur user) {
        if (utilisateurRepository.findOne(user.getEmail()) != null || utilisateurRepository.findByPseudo(user.getPseudo()) != null) {
            return false;
        }
        return true;
    }

    public Utilisateur createUser(Utilisateur user) {
        boolean check = checkUser(user);
        if (check){
            return utilisateurRepository.save(user);
        } else {
            return null;
        }
    }


}
