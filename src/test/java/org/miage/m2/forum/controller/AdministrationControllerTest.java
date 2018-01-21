package org.miage.m2.forum.controller;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.MainSpringApplication;
import org.miage.m2.forum.config.H2JpaConfig;
import org.miage.m2.forum.config.SecurityConfig;
import org.miage.m2.forum.formValidation.AccessProject;
import org.miage.m2.forum.formValidation.ProjectForm;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Roles;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.RolesRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.AccountService;
import org.miage.m2.forum.service.AccountServiceImpl;
import org.miage.m2.forum.service.AdministrationService;
import org.miage.m2.forum.service.AdministrationServiceImpl;
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

import java.util.Date;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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

    private AdministrationService administrationService = new AdministrationServiceImpl();

    AccountService accountService = new AccountServiceImpl();

    Roles roleUser;
    Roles roleAdmin;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    Utilisateur testUser = new Utilisateur("test@test.com", "test", "testmdp", true, null, null, null, null);
    Utilisateur user = new Utilisateur("user@test.com", "user", "testmdp", false, null, null, null, null);

    Projet projetInvite = new Projet("invite", "invite ok", new Date(), true, new HashSet<Utilisateur>(), testUser, new HashSet<Topic>());
    Projet projetNotInvite = new Projet("not invite", "invite none", new Date(), false, new HashSet<Utilisateur>(), testUser, new HashSet<Topic>());
    Projet projetNotAccessTestUser = new Projet("not testUser", "testUser forbidden", new Date(), false, new HashSet<Utilisateur>(), testUser, new HashSet<Topic>());

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
        administrationService.setProjetRepository(projetRepository);
        administrationService.createProject(projetInvite);
        administrationService.createProject(projetNotInvite);
        administrationService.createProject(projetNotAccessTestUser);

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

    /**
     * test ajout de projet
     * @throws Exception
     */
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

    /**
     * test pour vérifier si on a bien 3 projets quand un admin accede à la page d'accueil
     * @throws Exception
     */
    @Test
    public void testIndex() throws Exception{
        mockMvc.perform(get("/administration").with(user(testUser.getEmail()).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projects",  Matchers.hasSize(3)))
                .andExpect(view().name("administration/index"));;
    }

    /**
     * test pour récuperer les infos du porjet pour le mettre dans un formulaire
     * @throws Exception
     */
    @Test
    public void testGetProjectForUpdate() throws Exception{
        mockMvc.perform(get("/administration/project/"+projetNotAccessTestUser.getTitre()).with(user(testUser.getEmail()).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("projectForm", hasProperty("titre",is(projetNotAccessTestUser.getTitre()))))
                .andExpect(model().attribute("projectForm", hasProperty("description",is(projetNotAccessTestUser.getDescription()))))
                .andExpect(model().attribute("projectForm", hasProperty("invite",is(projetNotAccessTestUser.isInvite()))))
                .andExpect(view().name("administration/update"));
    }

    /**
     * test pour mettre à jour un projet
     * @throws Exception
     */
    @Test
    public void testUpdateProject() throws Exception{

        ProjectForm projectForm = new ProjectForm();
        projectForm.setTitre(projetNotInvite.getTitre());
        projectForm.setInvite(true);
        projectForm.setDescription("a great description");

        mockMvc.perform(post("/administration/project/update").with(user(testUser.getEmail()).roles("ADMIN"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("titre",projectForm.getTitre())
                .param("description",projectForm.getDescription())
                .param("invite",String.valueOf(projectForm.isInvite()))
        )
                .andExpect(status().isOk())
                .andExpect(model().attribute("projectForm", hasProperty("titre",is(projectForm.getTitre()))))
                .andExpect(model().attribute("projectForm", hasProperty("description",is(projectForm.getDescription()))))
                .andExpect(model().attribute("projectForm", hasProperty("invite",is(projectForm.isInvite()))))
                .andExpect(view().name("administration/update"));
    }

    /**
     * test de mise à jour de l'acces d; un utilisateur sur un projet
     * @throws Exception
     */
    @Test
    public void testUpdateAccessOfProject() throws Exception{

        mockMvc.perform(post("/administration/access").with(user(testUser.getEmail()).roles("ADMIN"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title",projetNotInvite.getTitre())
                .param("userAccess",testUser.getEmail())
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"))
                .andExpect(view().name("administration/access"));

        mockMvc.perform(post("/administration/access").with(user(testUser.getEmail()).roles("ADMIN"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title",projetNotInvite.getTitre())
                .param("userRemove",testUser.getEmail())
        )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"))
                .andExpect(view().name("administration/access"));

    }


}
