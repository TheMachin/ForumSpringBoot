package org.miage.m2.forum.controller;

import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
            utilisateur.setEnable(false);
            utilisateurRepository.save(utilisateur);
        }

        return "redirect:/";
    }

    @RequestMapping(value = "/oauth/revoke-token", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            String tokenValue = authHeader.replace("Bearer", "").trim();
            OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
            tokenStore.removeAccessToken(accessToken);
        }
    }

    private String getEmail(Principal principal){
        try {
            if (principal != null) {
                OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
                Authentication authentication = oAuth2Authentication.getUserAuthentication();
                Map<String, String> details = new LinkedHashMap<>();
                details = (Map<String, String>) authentication.getDetails();
                logger.info(details.get("email"));
                Map<String, String> map = new LinkedHashMap<>();
                map.put("email", details.get("email"));
                logger.debug("details map is: {}", map);
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details.get("email"), "password", authentication.getAuthorities()));
                return details.get("email");
            }
        } catch (Exception e) {
            logger.error("dumping principal " + principal + "failed, exception: ", e );
        }
        return null;
    }

}
