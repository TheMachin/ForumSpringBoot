package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Utilisateur;
import org.miage.m2.forum.query.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AdministrationServiceImpl implements AdministrationService {

    @Autowired
    private ProjetRepository projetRepository;

    /**
     * Créer un projet si l'utilisateur est un administrateur
     * @param projet
     * @return l'objet projet
     */
    public Projet createProject(Projet projet) {

        if(!projet.getCreators().isAdmin()){
            System.out.println("not admin");
            return null;
        }

        if(findOne(projet.getTitre())!=null){
            System.out.println("find existing project");
            return null;
        }
        Projet projet1 = projetRepository.save(projet);

        return projet1;

    }

    @Override
    public Projet update(Projet projet, String titre, String description, boolean invite) {
        if(projet==null){
            return null;
        }

        if(!projet.getTitre().equals(titre))
        {
            if(findOne(titre)==null){
                projet.setTitre(titre);
            }else{
                return null;
            }
        }

        projet.setDescription(description);
        projet.setInvite(invite);
        if(save(projet)){
            return projet;
        }
        return null;
    }

    public Projet findOne(String title){
        return projetRepository.findOne(title);
    }

    @Override
    public Iterable<Projet> findAll() {
        return projetRepository.findAll();
    }

    @Override
    public Projet addProjectToProject(Projet projet, Projet sousProjet) {

        if(projet==null){
            return null;
        }

        if(!projet.addSousProjet(sousProjet)){
            return null;
        }

        return projetRepository.save(projet);
    }

    @Override
    public Projet changePermissionForVisitor(Projet projet, boolean invite) {
        if(projet==null){
            return null;
        }

        projet.setInvite(invite);

        return projetRepository.save(projet);
    }

    @Override
    public Projet addUserToAccess(Projet projet, Set<Utilisateur> access) {

        if(projet==null){
            return null;
        }

        projet.addUserAccess(access);

        return projetRepository.save(projet);
    }

    @Override
    public Projet removeUserToAccess(Projet projet, Set<Utilisateur> access) {
        if(projet==null){
            return null;
        }

        projet.removeUserAccess(access);

        return projetRepository.save(projet);
    }


    @Override
    public Projet changeDescription(Projet projet, String description) {
        if(projet==null){
            return null;
        }

        projet.setDescription(description);

        return projetRepository.save(projet);
    }

    private boolean save(Projet projet){
        Projet p = projetRepository.save(projet);
        if(p==null){
            return false;
        }
        return true;
    }

    public void setProjetRepository(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }
}
