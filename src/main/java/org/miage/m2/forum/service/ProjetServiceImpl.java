package org.miage.m2.forum.service;

import org.miage.m2.forum.modele.Projet;
import org.miage.m2.forum.query.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class ProjetServiceImpl implements ProjetService{

    @Autowired
    private ProjetRepository projetRepository;


    @Override
    public Iterable<Projet> getAll() {
        return projetRepository.findAll();
    }

    @Override
    public Projet getOne(String titre) {
        return projetRepository.findOne(titre);
    }

    public void setProjetRepository(ProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }
}
