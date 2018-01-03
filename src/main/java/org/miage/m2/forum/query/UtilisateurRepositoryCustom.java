package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Utilisateur;

public interface UtilisateurRepositoryCustom {

    public Utilisateur findByPseudo(String pseudo);

}
