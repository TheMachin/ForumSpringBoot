package org.miage.m2.forum.query;

import org.miage.m2.forum.modele.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

public class UtilisateurRepositoryImpl implements UtilisateurRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Utilisateur findByPseudo(String pseudo) {
        String q = "SELECT u FROM Utilisateur u WHERE u.pseudo=:pseudo";
        Query query = entityManager.createQuery(q);
        query.setParameter("pseudo",pseudo);
        List<Utilisateur> result = query.getResultList();
        if(result.isEmpty()){
            return null;
        }
        Utilisateur user = result.get(0);
        return user;
    }

    @Override
    public Utilisateur findByEmailAndPassword(String email, String password) {
        String q = "SELECT u FROM Utilisateur u WHERE u.email=:email AND u.mdp=:mdp";
        Query query = entityManager.createQuery(q);
        query.setParameter("email",email);
        query.setParameter("mdp",password);
        List<Utilisateur> result = query.getResultList();
        if(result.isEmpty()){
            return null;
        }
        Utilisateur user = result.get(0);
        return user;
    }
}
