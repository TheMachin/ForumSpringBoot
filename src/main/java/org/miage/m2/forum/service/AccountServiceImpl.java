package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
            //user.setMdp(getSecurePassword(user.getMdp()));
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

    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Utilisateur connection(String email, String password) {
        Utilisateur user = utilisateurRepository.findByEmailAndPassword(email,getSecurePassword(password));
        return user;
    }

    private static String getSecurePassword(String passwordToHash)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
