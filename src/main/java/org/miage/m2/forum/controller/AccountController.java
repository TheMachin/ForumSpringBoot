package org.miage.m2.forum.controller;

import org.miage.m2.forum.formValidation.LoginForm;
import org.miage.m2.forum.formValidation.SignUpForm;
import org.miage.m2.forum.modele.Message;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.validation.Valid;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/account/")
public class AccountController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    AccountService accountService = new AccountServiceImpl();

    @GetMapping(value = "/login")
    public String login(Model model){
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @GetMapping(value = "/signup")
    public String signUp(Model model){
        model.addAttribute("signUpForm", new SignUpForm());
        return "signup";
    }

    /**
     * Création d'un nouveau compte
     * @param signUpForm
     * @param bindingResult
     * @param model
     * @return
     */
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String formSignUp(
            @Valid SignUpForm signUpForm
            ,BindingResult bindingResult
            ,Model model
    ){
        if(bindingResult.hasErrors()){
            return "signup";
        }

        accountService.setUtilisateurRepository(utilisateurRepository);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setAdmin(false);
        utilisateur.setEmail(signUpForm.getEmail());
        utilisateur.setPseudo(signUpForm.getUsername());
        utilisateur.setMdp(signUpForm.getPassword());
        accountService.createUser(utilisateur);
        return "login";
    }

    /*@RequestMapping(value = "/login", method = RequestMethod.POST)
    public String formLogin(
            @Valid LoginForm loginForm,
            BindingResult bindingResult
            ,Model model
    ) {
        System.out.println("hetre");
        if (bindingResult.hasErrors()) {
            return "login";
        }
        accountService.setUtilisateurRepository(utilisateurRepository);
        Utilisateur user = accountService.connection(loginForm.getEmail(),loginForm.getPassword());
        //échec
        if(user==null){
            return "login";
        }
        return "index";
    }*/
}
