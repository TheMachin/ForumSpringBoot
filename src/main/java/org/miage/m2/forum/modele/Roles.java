package org.miage.m2.forum.modele;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Roles {

    @Id
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<Utilisateur> utilisateurs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(Set<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }
}
