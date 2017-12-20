package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Utilisateur;

import org.springframework.data.jpa.repository.JpaRepository;


@org.springframework.stereotype.Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {

    public Utilisateur findByPseudo(String pseudo);
}
