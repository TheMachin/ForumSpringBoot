package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.LoginForm;
import org.miage.m2.forum.formValidation.SettingForm;
import org.miage.m2.forum.formValidation.SignUpForm;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/")
public class AccountController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    AccountService accountService = new AccountServiceImpl();

    /**
     * Page d'authentification
     * @param model
     * @return
     */
    @GetMapping(value = "signin")
    public String login(Model model){
        model.addAttribute("loginForm", new LoginForm());
        return "signin";
    }

    /**
     * Page de création de compte
     * @param model
     * @return
     */
    @GetMapping(value = "signup")
    public String signUp(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "signup";
    }

    /**
     * page pour modifier les infos de l'utilisateur
     *
     * on récuperer l'user depuis sa session
     * on utilise l'objet settingForm pour le formulaire dans laquelle on met des informations (email, pseudo)
     * @param model
     * @return
     */
    @GetMapping(value = "account/setting")
    public String setting(Model model){

        accountService.setUtilisateurRepository(utilisateurRepository);

        String user = getCurrentNameUser();
        Utilisateur utilisateur = utilisateurRepository.findOne(user);
        SettingForm settingForm = new SettingForm();
        settingForm.setEmail(utilisateur.getEmail());
        settingForm.setUsername(utilisateur.getPseudo());

        /**
         * si l'utilisateur n'est pas activé --> connexion oauth et pas de modification de l'email et mdp
         */
        model.addAttribute("settingForm",settingForm);
        if(utilisateur.isEnable()==false){
            model.addAttribute("userOAuth",true);
        }else{
            model.addAttribute("userOAuth",false);
        }
        return "setting";
    }

    /**
     * Création d'un nouveau compte
     * @param signUpForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public String formSignUp(
            @Valid SignUpForm signUpForm
            ,BindingResult bindingResult
            ,Model model
    ){
        if(bindingResult.hasErrors()){
            return "signup";
        }

        accountService.setUtilisateurRepository(utilisateurRepository);

        /**
         * creation de l'objet utilisateur avec des informations du formulaire
         * insertion dans la bdd
         */
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setAdmin(false);
        utilisateur.setEmail(signUpForm.getEmail());
        utilisateur.setPseudo(signUpForm.getUsername());
        utilisateur.setMdp(signUpForm.getPassword());
        Utilisateur userCreate = accountService.createUser(utilisateur);

        /**
         * vérification si il a bien été enregistré
         */
        if(userCreate!=null) {
            /**
             * on le dirige vers la page d'authentification
             */
            model.addAttribute("accountCreate",true);
            return login(model);
        }
        model.addAttribute("fail",true);
        return "signup";
    }

    /**
     * Update email, username and password of user
     * if success --> update information of authentification
     * @param settingForm
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping(value = "account/setting")
    public String settingForm(
            @Valid SettingForm settingForm,
            BindingResult bindingResult,
            Model model
    ){
        logger.info("POST setting");

        accountService.setUtilisateurRepository(utilisateurRepository);


        /**
         * recuperation des infos de l; utilisateur depuis la session
         */
        String user = getCurrentNameUser();
        logger.info(user);
        Utilisateur utilisateur = utilisateurRepository.findOne(user);

        /**
         * si il a des erreurs dans le formulaire on le notifie
         */
        if(utilisateur.isEnable()==false){
            model.addAttribute("userOAuth",true);
        }else{
            model.addAttribute("userOAuth",false);
        }
        if(bindingResult.hasErrors()){
            logger.error("Error in input");
            return "setting";
        }

        /**
         * on met à jour l'utilisateur
         */
        logger.info("start update");
        Utilisateur userUpdating = accountService.modifyUser(utilisateur,settingForm.getEmail(),settingForm.getUsername(),settingForm.getPassword(),utilisateur.isAdmin());
        if(userUpdating!=null) {
            logger.info("succes of update");
            //update information of authentification
            Authentication authentication = new UsernamePasswordAuthenticationToken(userUpdating.getEmail(), userUpdating.getMdp());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            /**
             * on notifie qu'il a bien ete mis a jour
             */
            model.addAttribute("success",true);
            return "setting";
        }
        /**
         * en cas d'echec on redirige vers le formulaire avec des infos de l'utilisateur
         */
        logger.error("fail to update");
        utilisateur = utilisateurRepository.findOne(getCurrentNameUser());
        settingForm = new SettingForm();
        settingForm.setEmail(utilisateur.getEmail());
        settingForm.setUsername(utilisateur.getPseudo());
        model.addAttribute("settingForm",settingForm);
        model.addAttribute("fail",true);
        return "setting";

    }

    /**
     * Obtenir la session de l'utilisateur si il est connecté
     * @return email de l'utilisateur connecté
     */
    private String getCurrentNameUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        logger.info(principal.toString());
        if(principal instanceof String){
            logger.info(principal.toString());
            return principal.toString();
        }
        User user = (User) principal;
        logger.info("146 "+principal.toString());
        return user.getUsername();
    }

    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository){
        this.utilisateurRepository=utilisateurRepository;
    }

    public void setAccountService(AccountService accountService){
        this.accountService = accountService;
    }

}
