package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Utilisateur;
import org.springframework.data.repository.CrudRepository;


@org.springframework.stereotype.Repository
public interface UtilisateurRepository extends CrudRepository<Utilisateur,String> {

    public Utilisateur findByPseudo(String pseudo);
}
