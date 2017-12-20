package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Topic;
import org.miage.m2.forum.modele.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface AdministrationService {

    public Projet createProject(Projet projet);
    public Projet findOne(String title);
    public Projet addProjectToProject(Projet projet, Projet sousProjet);
    public Projet changePermissionForVisitor(Projet projet, boolean invite);
    public Projet addUserToAccess(Projet projet, Set<Utilisateur> access);
    public Projet removeUserToAccess(Projet projet, Set<Utilisateur> access);
    public Projet changeDescription(Projet projet, String description);
}
