package org.miage.m2.forum.controller;

import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class OAuthController {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public static final Logger logger = LoggerFactory.getLogger(OAuthController.class);

    private CurrentUserService currentUserService = new CurrentUserService();

    AccountService accountService = new AccountServiceImpl();


    /**
     *
     * Récupere les informations (email) d'un utilisateur connecté avec google
     * Créer un compte dans la bdd, si il n'existe pas
     * redirige vers la page d'accueil
     *
     *
     * @param principal
     * @return
     */
    @RequestMapping("oauth/google")
    public String user(Principal principal){
        logger.info(principal.toString());
        String name = principal.getName();
        logger.info(principal.getName());
        accountService.setUtilisateurRepository(utilisateurRepository);
        currentUserService.setAccountService(accountService);
        String email = currentUserService.getEmail(principal);

        return "redirect:/";
    }

}
