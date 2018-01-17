package org.miage.m2.forum.controller;

import org.apache.catalina.connector.Request;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class OAuthController {

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public static final Logger logger = LoggerFactory.getLogger(OAuthController.class);


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
        String email = getEmail(principal);
        Utilisateur utilisateur = utilisateurRepository.findOne(email);
        /**
         * user not found
         * create user
         */
        if(utilisateur == null){
            utilisateur = new Utilisateur();
            utilisateur.setEmail(email);
            utilisateur.setPseudo(name);
            utilisateur.addUserRole();
            //false pour empecher qu'il se connecte avec un login custom
            utilisateur.setEnable(false);
            utilisateurRepository.save(utilisateur);
        }

        return "redirect:/";
    }

    /***
     * get an email address from authentication
     *
     * @param principal
     * @return
     */
    private String getEmail(Principal principal){
        try {
            if (principal != null) {
                try {
                    OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
                    Authentication authentication = oAuth2Authentication.getUserAuthentication();
                    Map<String, String> details = new LinkedHashMap<>();
                    details = (Map<String, String>) authentication.getDetails();
                    logger.info("83"+details.get("email"));
                    Map<String, String> map = new LinkedHashMap<>();
                    map.put("email", details.get("email"));
                    logger.debug("details map is: {}", map);
                    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details.get("email"), "password", authentication.getAuthorities()));
                    return details.get("email");
                } catch (Exception e){
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    Object principal2 = auth.getPrincipal();
                    logger.info("92"+principal2.toString());
                    if(principal2 instanceof String){
                        logger.info("94"+principal2.toString());
                        return principal2.toString();
                    }
                    User user = (User) principal2;
                    logger.info("146 "+principal2.toString());
                    return user.getUsername();
                }
            }
        } catch (Exception e) {
            logger.error("dumping principal " + principal + "failed, exception: ", e );
        }
        return null;
    }

}
