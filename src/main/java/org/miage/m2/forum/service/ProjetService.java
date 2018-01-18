package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.query.ProjetRepository;

import java.util.Set;

public interface ProjetService {

    Iterable<Projet> getAll();
    Projet getOne(String titre);
    void setProjetRepository(ProjetRepository projetRepository);
}
