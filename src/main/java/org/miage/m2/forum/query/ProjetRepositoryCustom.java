package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Projet;

public interface ProjetRepositoryCustom {

    Iterable<Projet> getAllChild(Projet projet);
}
