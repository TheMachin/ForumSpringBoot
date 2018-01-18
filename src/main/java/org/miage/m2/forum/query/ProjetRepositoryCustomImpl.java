package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.modele.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class ProjetRepositoryCustomImpl implements ProjetRepositoryCustom{

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Iterable<Projet> getAllChild(Projet projet) {
        return null;
    }
}
