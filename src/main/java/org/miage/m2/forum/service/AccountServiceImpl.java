package org.miage.m2.forum.service;

import org.miage.m2.forum.controller.AccountController;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Utilisateur getUtilisateurByPseudo(String pseudo) {
        return utilisateurRepository.findByPseudo(pseudo);
    }

    public static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    /**
     * Check if user exist with this email and this username
     * @param user
     * @return
     */
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
            user.setEnable(true);
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

    /**
     * Modifie un utilisateur
     * on vérifie si on accepte ou non les modifications avant de les sauvegarder
     * @param user utilisateur à modifier (sans les modifs)
     * @param emailModify
     * @param pseudoModify
     * @param mdpModify
     * @param adminModify
     * @return utilisateur avec modification
     */
    public Utilisateur modifyUser(Utilisateur user, String emailModify, String pseudoModify, String mdpModify, boolean adminModify) {
        Utilisateur userModify = new Utilisateur(emailModify, pseudoModify, mdpModify, adminModify, user.getMessage(), user.getCreators(), user.getListTopicCreate(), user.getSuivi());

        //if user does'nt change password
        if(mdpModify!=null) {
            if (mdpModify.isEmpty()) {
                user.setMdp(user.getMdp());
            }
        }
        user.setAdmin(adminModify);
        Utilisateur userFound;
        //Si l'adresse mail et le pseudo n'ont pas été modifié
        if (user.getEmail().equals(userModify.getEmail()) && user.getPseudo().equals(userModify.getPseudo())) {
            logger.info("update user password");
            return utilisateurRepository.save(user);
        }
        //Sinon on vérifie si le pseudo a changé et qu'il est disponible
        if(!user.getPseudo().equals(userModify.getPseudo())) {
            userFound = getUtilisateurByPseudo(userModify.getPseudo());
            if (userFound == null) {
                logger.info("update user pseudo");
                user.setPseudo(pseudoModify);
            }else{
                logger.info("fail");
                return null;
            }
        }
        return utilisateurRepository.save(user);
    }

    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
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

    public Utilisateur updateSuiveur(Utilisateur user){
        return utilisateurRepository.save(user);
    }
}
