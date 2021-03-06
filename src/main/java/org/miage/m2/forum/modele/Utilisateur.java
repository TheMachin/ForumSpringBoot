package org.miage.m2.forum.modele;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Utilisateur {

	@Id
	private String email;
	private String pseudo;
	private String mdp;
	//for authentification of user
	private boolean enable = true;
	private boolean admin;
	@OneToMany(mappedBy = "author")
	private Set<Message> message = new HashSet<Message>();
	@OneToMany(mappedBy = "creators")
	private Set<Projet> creators = new HashSet<Projet>();
	@OneToMany(mappedBy = "creator")
	private Set<Topic> listTopicCreate = new HashSet<Topic>();
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "suiveurs_topic",
			joinColumns = @JoinColumn(name = "utilisateur_id"),
			inverseJoinColumns = @JoinColumn(name = "topic_titre")
	)
	private Set<Topic> suivi = new HashSet<Topic>();

    //list of roles of user
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(
					name = "user_email", referencedColumnName = "email"),
			inverseJoinColumns = @JoinColumn(
					name = "role_name", referencedColumnName = "name"))
	private Set<Roles> roles = new HashSet<Roles>();

	@Autowired
	public Utilisateur(String email, String pseudo, String mdp, boolean admin, Set<Message> message, Set<Projet> creators, Set<Topic> listTopicCreate, Set<Topic> suivi) {
		addUserRole();
		this.email = email;
		this.pseudo = pseudo;
		this.mdp = mdp;
		this.enable=true;
		this.setAdmin(admin);
		this.message = message;
		this.creators = creators;
		this.listTopicCreate = listTopicCreate;
		this.suivi = suivi;
	}

	public Utilisateur() {
		addUserRole();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getMdp() {
		return mdp;
	}

	public void setMdp(String mdp) {
		this.mdp = mdp;
	}

	public boolean isAdmin() {
		Roles rolesAdmin = new Roles();
		rolesAdmin.setName("ROLE_ADMIN");
		if(roles.contains(rolesAdmin)){
			return true;
		}
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
		if(admin){
			addAdminRole();
		}else{
			removeAdminRole();
		}
	}

	public Set<Message> getMessage() {
		return message;
	}

	public void setMessage(Set<Message> message) {
		this.message = message;
	}

	public Set<Projet> getCreators() {
		return creators;
	}

	public void setCreators(Set<Projet> creators) {
		this.creators = creators;
	}


	public Set<Topic> getListTopicCreate() {
		return listTopicCreate;
	}

	public void setListTopicCreate(Set<Topic> creator) {
		this.listTopicCreate = creator;
	}

	public Set<Topic> getSuivi() {
		return suivi;
	}

	public void setSuivi(Set<Topic> suivi) {
		this.suivi = suivi;
	}

    /**
     * Add a role for user
     * @param roleName
     */
	private void addRole(String roleName){
	    //check if the set exists or to initialize it
		if(this.roles==null){
			roles = new HashSet<Roles>();
		}
		Roles role = new Roles();
		role.setName(roleName);
		if(!roles.contains(role)){
			roles.add(role);
		}
	}

	public void addUserRole(){
		addRole("ROLE_USER");
	}

	public void addAdminRole(){
		addRole("ROLE_ADMIN");
		this.enable=true;
		this.admin=true;
	}

	public void removeAdminRole(){
		if(this.roles!=null){
			Roles role = new Roles();
			role.setName("ROLE_ADMIN");
			roles.remove(role);
		}
		this.admin=false;
	}

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

	@Override
	public String toString() {
		return "Utilisateur{" +
				"email='" + email + '\'' +
				", pseudo='" + pseudo + '\'' +
				", mdp='" + mdp + '\'' +
				", enable=" + enable +
				", admin=" + admin +
				'}';
	}
}