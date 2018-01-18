package org.miage.m2.forum.controller;

import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class CurrentUserService {

    public static final Logger logger = LoggerFactory.getLogger(CurrentUserService.class);

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    AccountService accountService;

    /**
     * Obtenir la session de l'utilisateur si il est connecté
     * @return email de l'utilisateur connecté
     */
    public String getCurrentNameUser(Principal principal){

        if(principal == null){
            return "anonymousUser";
        }

        String name = principal.getName();

        if(name == "anonymousUser" ){
            return "anonymousUser";
        }

        if(validate(name)){
            return name;
        }

        String email = null;
        email = getEmail(principal);

        if(email==null){
            //destroy session
            return "anonymousUser";
        }
        logger.info(email);
        Utilisateur utilisateur = accountService.getUtilisateurByPseudo(email);
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
            accountService.createUser(utilisateur);

            return email;
        }

        /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        logger.info(principal.toString());
        if(principal instanceof String){
            logger.info(principal.toString());
            return principal.toString();
        }
        User user = (User) principal;
        logger.info("146 "+principal.toString());*/

        return null;
    }

    /***
     * get an email address from authentication
     *
     * @param principal
     * @return
     */
    public String getEmail(Principal principal){
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

    private boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setUtiisateurRepository(UtilisateurRepository utiisateurRepository){
        accountService.setUtilisateurRepository(utiisateurRepository);
    }
}
