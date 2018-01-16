package org.miage.m2.forum.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.MainSpringApplication;
import org.miage.m2.forum.config.H2JpaConfig;
import org.miage.m2.forum.config.SecurityConfig;
import org.miage.m2.forum.modele.Roles;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.RolesRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MainSpringApplication.class, H2JpaConfig.class})
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class AdministrationControllerTest {

    @Autowired
    private FilterChainProxy springSecurityFilterChain;


    private MockMvc mockMvc;

    AdministrationController administrationController;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private ProjetRepository projetRepository;

    AccountService accountService = new AccountServiceImpl();

    Roles roleUser;
    Roles roleAdmin;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    Utilisateur testUser = new Utilisateur("test@test.com", "test", "testmdp", true, null, null, null, null);
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


        user.addUserRole();
        user.setEnable(true);
        user.setAdmin(false);
        testUser.setEnable(true);
        testUser.addAdminRole();
        accountService.setUtilisateurRepository(utilisateurRepository);
        accountService.createUser(user);
        accountService.createUser(testUser);

        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        administrationController = new AdministrationController();
        administrationController.setUtilisateurRepository(utilisateurRepository);
        administrationController.setProjetRepository(projetRepository);

        this.mockMvc = MockMvcBuilders.standaloneSetup(administrationController)
                .setViewResolvers(viewResolver)
                //add config security
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * check if anonymous user is redirect to login and forbidden access for user role
     * @throws Exception
     */
    @Test
    public void testAccessDeniedUser()throws Exception{
        mockMvc.perform(get("/administration").with(anonymous()))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/administration").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/administration").with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddProject() throws Exception {
        mockMvc.perform(post("/administration/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("titre","titre")
                .param("description","description")
                .param("invite","true")
                .param("projetParent","")
                .with(user("test@test.com").roles("ADMIN"))
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successProject"))
                .andExpect(view().name("administration/index"));
    }


}
