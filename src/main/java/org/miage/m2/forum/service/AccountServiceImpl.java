package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (check) {
            return utilisateurRepository.save(user);
        } else {
            return null;
        }
    }

    public boolean deleteUser(Utilisateur user) {
        boolean check = checkUser(user);
        if (!check) {
            utilisateurRepository.delete(user);
            return true;
        } else {
            return false;
        }
    }

    public Utilisateur modifyUser(Utilisateur user, String emailModify, String pseudoModify, String mdpModify, boolean adminModify) {
        Utilisateur userModify = new Utilisateur(emailModify, pseudoModify, mdpModify, adminModify, user.getMessage(), user.getCreators(), user.getListTopicCreate(), user.getSuivi());
        Utilisateur userFound;
        if (user.getEmail().equals(userModify.getEmail()) && user.getPseudo().equals(userModify.getPseudo())) {
            return utilisateurRepository.save(userModify);
        }
        if (user.getEmail().equals(userModify.getEmail()) && !user.getPseudo().equals(userModify.getPseudo())){
            userFound = getUtilisateurByPseudo(userModify.getPseudo());
            if (userFound == null){
                return utilisateurRepository.save(userModify);
            } else {
                return null;
            }
        }
        if (!user.getEmail().equals(userModify.getEmail()) && user.getPseudo().equals(userModify.getPseudo())){
            return utilisateurRepository.save(userModify);
        }
        return null;
    }

}
