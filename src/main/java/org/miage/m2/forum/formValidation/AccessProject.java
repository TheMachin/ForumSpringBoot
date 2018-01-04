package org.miage.m2.forum.formValidation;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Utilisateur;

public class AccessProject {

    private String title;
    private String userAccess;
    private String userRemove;

    public String getUserAccess() {
        return userAccess;
    }

    public void setUserAccess(String userAccess) {
        this.userAccess = userAccess;
    }

    public String getUserRemove() {
        return userRemove;
    }

    public void setUserRemove(String userRemove) {
        this.userRemove = userRemove;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
