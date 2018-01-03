package org.miage.m2.forum.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.MainSpringApplication;
import org.miage.m2.forum.config.H2JpaConfig;
import org.miage.m2.forum.config.SecurityConfig;
import org.miage.m2.forum.formValidation.SignUpForm;
import org.miage.m2.forum.modele.*;
import org.miage.m2.forum.query.RolesRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MainSpringApplication.class, H2JpaConfig.class})
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class AccountControllerTest {

    private MockMvc mockMvc;

    AccountController accountController;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RolesRepository rolesRepository;

    AccountService accountService = new AccountServiceImpl();

    Roles roleUser;
    Roles roleAdmin;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    Utilisateur testUser = new Utilisateur("test@test.com", "test", "testmdp", false, null, null, null, null);
    Utilisateur user = new Utilisateur("user@test.com", "user", "testmdp", false, null, null, null, null);

    @Before
    public void setup() {

        if(roleAdmin==null){
            roleAdmin = new Roles();
            roleAdmin.setName("ROLE_ADMIN");
            rolesRepository.save(roleAdmin);
        }
        if(roleUser==null){
            roleUser = new Roles();
            roleUser.setName("ROLE_USER");
            rolesRepository.save(roleUser);
        }

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        accountController = new AccountController();
        accountController.setUtilisateurRepository(utilisateurRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        accountController.setAccountService(accountService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                                        .setViewResolvers(viewResolver)
                                        .build();
        accountService.createUser(user);
    }

    /**
     * Test pour vérifier si on accède à la page d'inscription
     * @throws Exception
     */
    @Test
    public void testSignUp() throws Exception{
        this.mockMvc.perform(get("/account/signup"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(view().name("signup"));
    }

    /**
     * Test pour vérifier si l'inscription s'est bien déroulé et qu'on va à la page d'authentification
     * @throws Exception
     */
    @Test
    public void testPostSignUp() throws Exception{

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setUsername(testUser.getPseudo());
        signUpForm.setEmail(testUser.getEmail());
        signUpForm.setPassword(testUser.getMdp());
        signUpForm.setConfirmPassword(testUser.getMdp());
        this.mockMvc.perform(post("/account/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email",testUser.getEmail())
                .param("username",testUser.getPseudo())
                .param("password",testUser.getMdp())
                .param("confirmPassword",testUser.getMdp())
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("signUpForm", hasProperty("email",is(signUpForm.getEmail()))))
                .andExpect(model().attribute("signUpForm", hasProperty("username",is(signUpForm.getUsername()))))
                .andExpect(model().attribute("signUpForm", hasProperty("password",is(signUpForm.getPassword()))))
                .andExpect(model().attribute("signUpForm", hasProperty("confirmPassword",is(signUpForm.getConfirmPassword()))))
                .andExpect(model().attributeExists("accountCreate"))
                .andExpect(view().name("login"))
        ;
    }

    @Test
    public void testPostSignUp_whenEmailExist() throws Exception{

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setUsername(testUser.getPseudo());
        signUpForm.setEmail(user.getEmail());
        signUpForm.setPassword(testUser.getMdp());
        signUpForm.setConfirmPassword(testUser.getMdp());
        this.mockMvc.perform(post("/account/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email",user.getEmail())
                .param("username",testUser.getPseudo())
                .param("password",testUser.getMdp())
                .param("confirmPassword",testUser.getMdp())
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("signUpForm", hasProperty("email",is(signUpForm.getEmail()))))
                .andExpect(model().attribute("signUpForm", hasProperty("username",is(signUpForm.getUsername()))))
                .andExpect(model().attribute("signUpForm", hasProperty("password",is(signUpForm.getPassword()))))
                .andExpect(model().attribute("signUpForm", hasProperty("confirmPassword",is(signUpForm.getConfirmPassword()))))
                .andExpect(model().attributeExists("fail"))
                .andExpect(view().name("signup"))
        ;
    }

    @Test
    public void when_missField_PostSignUp() throws Exception{

        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setUsername(testUser.getPseudo());
        signUpForm.setEmail(testUser.getEmail());
        signUpForm.setPassword(testUser.getMdp());
        signUpForm.setConfirmPassword(testUser.getMdp());
        this.mockMvc.perform(post("/account/signup")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email","")
                .param("username","")
                .param("password","")
                .param("confirmPassword","")
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors(
                        "signUpForm","email","username","password","confirmPassword"
                ))
                .andExpect(view().name("signup"))
        ;
    }

    @Test
    //utilisation du mock pour simuler un utilisateur connecté
    @WithMockUser(username = "user@test.com",roles = "USER_ROLE")
    public void testGoSettingView() throws Exception{
        this.mockMvc.perform(get("/account/setting"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("settingForm"))
                .andExpect(model().attribute("settingForm", hasProperty("email",is(user.getEmail()))))
                .andExpect(model().attribute("settingForm", hasProperty("username",is(user.getPseudo()))))
                .andExpect(view().name("setting"));
    }



}
