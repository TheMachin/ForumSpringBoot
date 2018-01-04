package org.miage.m2.forum.formValidation;

import org.hibernate.validator.constraints.NotEmpty;
import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Utilisateur;

public class ProjectForm {

    @NotEmpty
    private String titre;
    @NotEmpty
    private String description;
    private boolean invite;
    private String projetParent;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInvite() {
        return invite;
    }

    public void setInvite(boolean invite) {
        this.invite = invite;
    }

    public String getProjetParent() {
        return projetParent;
    }

    public void setProjetParent(String projetParent) {
        this.projetParent = projetParent;
    }
}
