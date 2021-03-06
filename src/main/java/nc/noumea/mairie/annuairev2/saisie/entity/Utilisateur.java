package nc.noumea.mairie.annuairev2.saisie.entity;

/*
 * #%L
 * Gestion des Guest et Locality
 * %%
 * Copyright (C) 2015 Mairie de Nouméa
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.Objects;
import nc.noumea.mairie.annuairev2.saisie.core.entity.AbstractEntity;
import nc.noumea.mairie.annuairev2.saisie.core.security.Profil;

import javax.persistence.*;
import nc.noumea.mairie.annuairev2.saisie.core.security.CodeProfil;

/**
 * Created by barmi83 on 09/12/15.
 */
@Entity
@Table(name = Utilisateur.TABLENAME)
public class Utilisateur extends AbstractEntity implements Comparable<Utilisateur>{

    private static final long serialVersionUID = 3577916508493868043L;

    public static final String TABLENAME = "utilisateur";
    
    public static final String IDENTIFIANT_REGEX = "^[a-z]{5}[0-9]{2}$";
    public static final String NOM_REGEX = "\\D*";

    /** {@link #getId()} */
    private Long id;
    public static final String SEQUENCENAME_ID = "s_utilisateur";

    /** {@link #getIdentifiant()} */
    private String identifiant;
    public static final String COLUMNNAME_IDENTIFIANT = "identifiant";
    public static final String PROPERTYNAME_IDENTIFIANT = "identifiant";
    public static final int IDENTIFIANT_MAX_LENGTH = 7;

    /** {@link #getNom()} */
    private String nom;
    public static final String COLUMNNAME_NOM = "nom";
    public static final String PROPERTYNAME_NOM = "nom";
    public static final int NOM_MAX_LENGTH = 200;

    /** {@link #getPrenom()} */
    private String prenom;
    public static final String COLUMNNAME_PRENOM = "prenom";
    public static final String PROPERTYNAME_PRENOM = "prenom";
    public static final int PRENOM_MAX_LENGTH = 20;

    /** {@link #isActif()} */
    private boolean isActif;
    public static final String COLUMNNAME_ACTIF = "actif";
    public static final String PROPERTYNAME_ACTIF = "actif";

    /** {@link #getProfil()} */
    private Profil profil;
    public static final String JOIN_COLUMNNAME_PROFIL = "id_profil";

    /** {@link #getVersion()} */
    private Integer version;

    @Id
    @Column(name = COLUMNNAME_ID)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCENAME_ID)
    @SequenceGenerator(name = SEQUENCENAME_ID, sequenceName = SEQUENCENAME_ID, allocationSize = 1)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = COLUMNNAME_IDENTIFIANT)
    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    @Column(name = COLUMNNAME_NOM)
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Column(name = COLUMNNAME_PRENOM)
    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Column(name = COLUMNNAME_ACTIF)
    public boolean isActif() {
        return isActif;
    }

    public void setActif(boolean active) {
        isActif = active;
    }

    @ManyToOne
    @JoinColumn(name = JOIN_COLUMNNAME_PROFIL)
    public Profil getProfil() {
        return profil;
    }

    public void setProfil(Profil profil) {
        this.profil = profil;
    }

    /**
     * Renvoie la version hibernate de l'objet
     *
     * @return
     */
    @Version
    @Column(name = COLUMNNAME_VERSION)
    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Transient
    public String getFullName(){
        return nom + " " + prenom;
    }
    
    @Transient
    public boolean isAdministrateur(){
        return profil.getNom() == CodeProfil.ADMIN;
    }
    
    @Transient
    public boolean isGestionnaire(){
        return profil.getNom() == CodeProfil.GESTIONNAIRE || isAdministrateur();
    }
    
    @Transient
    public boolean isGestionnaireLocality(){
        return profil.getNom() == CodeProfil.GESTIONNAIRE_LOCALITY || isGestionnaire();
    }
    
    @Transient
    public boolean isGestionnaireGuest(){
        return profil.getNom() == CodeProfil.GESTIONNAIRE_GUEST || isGestionnaire();
    }
    
    @Transient
    public boolean isConsultant(){
        return profil.getNom() == CodeProfil.CONSULTANT;
    }
    
     @Override
    public int compareTo(Utilisateur o) {
	return getFullName().compareTo(o.getFullName());
    }

    @Override
    public final int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.identifiant);
        hash = 97 * hash + Objects.hashCode(this.nom);
        hash = 97 * hash + Objects.hashCode(this.prenom);
        hash = 97 * hash + (this.isActif ? 1 : 0);
        hash = 97 * hash + Objects.hashCode(this.profil);
        hash = 97 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Utilisateur)) {
            return false;
        }
        final Utilisateur other = (Utilisateur) obj;
        if (this.isActif != other.isActif) {
            return false;
        }
        if (!Objects.equals(this.identifiant, other.identifiant)) {
            return false;
        }
        if (!Objects.equals(this.nom, other.nom)) {
            return false;
        }
        if (!Objects.equals(this.prenom, other.prenom)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.profil, other.profil)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }

    
    
    
}
