package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Roles;
import org.springframework.data.repository.CrudRepository;

@org.springframework.stereotype.Repository
public interface RolesRepository extends CrudRepository<Roles, String> {
}
