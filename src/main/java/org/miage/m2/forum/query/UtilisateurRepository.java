package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Utilisateur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;

public interface UtilisateurRepository extends CrudRepository<Utilisateur,String> {


}
