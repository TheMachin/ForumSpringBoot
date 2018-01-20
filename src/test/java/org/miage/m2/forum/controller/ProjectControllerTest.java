package org.miage.m2.forum.controller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.miage.m2.forum.MainSpringApplication;
import org.miage.m2.forum.config.H2JpaConfig;
import org.miage.m2.forum.config.SecurityConfig;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Roles;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.miage.m2.forum.query.RolesRepository;
import org.miage.m2.forum.query.UtilisateurRepository;
import org.miage.m2.forum.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MainSpringApplication.class, H2JpaConfig.class})
@ActiveProfiles("test")
@Import(SecurityConfig.class)
public class ProjectControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    ProjectController projectController;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RolesRepository rolesRepository;

    AccountService accountService = new AccountServiceImpl();

    Roles roleUser;
    Roles roleAdmin;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepository projetRepository;

    private ProjetService projetService = new ProjetServiceImpl();

    private AdministrationService administrationService = new AdministrationServiceImpl();


    Utilisateur testUser = new Utilisateur("test@test.com", "test", "testmdp", false, null, null, null, null);
    Utilisateur user = new Utilisateur("user@test.com", "user", "testmdp", false, null, null, null, null);
    Projet projetInvite = new Projet("invite", "invite ok", new Date(), true, new HashSet<Utilisateur>(), user, new HashSet<Topic>());
    Projet projetNotInvite = new Projet("not invite", "invite none", new Date(), false, new HashSet<Utilisateur>(), user, new HashSet<Topic>());
    Projet projetNotAccessTestUser = new Projet("not testUser", "testUser forbidden", new Date(), false, new HashSet<Utilisateur>(), user, new HashSet<Topic>());

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

        projectController = new ProjectController();
        projectController.setUtilisateurRepository(utilisateurRepository);
        accountService.setUtilisateurRepository(utilisateurRepository);
        projectController.setAccountService(accountService);
        projectController.setProjetRepository(projetRepository);
        projetService.setProjetRepository(projetRepository);
        projectController.setProjetService(projetService);
        administrationService.setProjetRepository(projetRepository);

        user.addAdminRole();
        accountService.createUser(testUser);
        accountService.createUser(user);
        projetRepository.save(projetInvite);
        projetNotInvite.addUserAccess(new HashSet<Utilisateur>(Arrays.asList(user, testUser)));
        administrationService.createProject(projetNotInvite);
        projetNotAccessTestUser.addUserAccess(new HashSet<Utilisateur>(Arrays.asList(user)));
        administrationService.createProject(projetNotAccessTestUser);





        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");


        this.mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setViewResolvers(viewResolver)
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();

    }

    @Test
    public void testInviteProject() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        Projet projet = projetRepository.findOne("invite");

        this.mockMvc.perform(get("/").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets", Matchers.hasSize(1)))
                .andExpect(view().name("index"));
    }

    @Test
    public void testInvitegGetProject() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        Projet projet = projetRepository.findOne("invite");

        this.mockMvc.perform(get("/projects/"+projetInvite.getTitre()+"/").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets", Matchers.hasSize(0)))
                .andExpect(view().name("projects"));
    }

    @Test
    public void testNotInviteProject() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        list.add(projetNotAccessTestUser);
        list.add(projetNotInvite);
        System.out.println("start test not invite project");
        this.mockMvc.perform(get("/").with(user(user.getEmail())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets",  Matchers.hasSize(3)))
                .andExpect(view().name("index"));
    }

    @Test
    public void testNotAccessTestUserProject() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        list.add(projetNotInvite);
        System.out.println("start test not invite project");
        this.mockMvc.perform(get("/").with(user(testUser.getEmail())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projets"))
                .andExpect(model().attribute("projets",  Matchers.hasSize(2)))
                .andExpect(view().name("index"));
    }

    @Test
    public void testRedirectIfNotProjectExist() throws Exception{

        List<Projet> list = new ArrayList<Projet>();
        list.add(projetInvite);
        Projet projet = projetRepository.findOne("invite");

        this.mockMvc.perform(get("/projects/moemvemo/").with(anonymous()))
                .andExpect(redirectedUrl("/"));
    }

}
