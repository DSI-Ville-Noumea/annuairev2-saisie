/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.noumea.mairie.annuairev2.saisie.entity;

/*
 * #%L
 * Gestion des Guest et Locality
 * %%
 * Copyright (C) 2015 - 2016 Mairie de Nouméa
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

import nc.noumea.mairie.annuairev2.saisie.config.security.TestConfig;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author barmi83
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class LocalityTest {
    
    @Test
    public void equalsContract() {
        
        Sectorisation secteurDSI = new Sectorisation();
        secteurDSI.setId(1L);
        secteurDSI.setCode4("AAAA");
        secteurDSI.setLibelle("Direction des Services de l'Information");
        secteurDSI.setSigle("DSI");
        
        Sectorisation secteurSED = new Sectorisation();
        secteurSED.setId(2L);
        secteurSED.setCode4("ABAA");
        secteurSED.setLibelle("Service Etudes et Développpement");
        secteurSED.setSigle("SED");
        secteurSED.setParent(secteurDSI);
        
        
        EqualsVerifier.forClass(Locality.class)
                .withPrefabValues(Sectorisation.class, secteurDSI, secteurSED)
                .verify();
    }
    
    @Test
    public void fullName_test(){
        
        Sectorisation secteurDSI = new Sectorisation();
        secteurDSI.setCode4("AAAA");
        secteurDSI.setLibelle("Direction des Services de l'Information");
        secteurDSI.setSigle("DSI");
        
        Locality locality = new Locality();
        locality.setNom("Salle Reunion DSI");
        locality.setLigneDirecte("+687 35.24.78");
        locality.setPoste("6547");
        locality.setService(secteurDSI);
        
        Assert.assertEquals("Salle Reunion DSI", locality.getFullName());
        
    }
    
    @Test
    public void compare_test(){
         Sectorisation secteurDSI = new Sectorisation();
        secteurDSI.setCode4("AAAA");
        secteurDSI.setLibelle("Direction des Services de l'Information");
        secteurDSI.setSigle("DSI");
        
        Sectorisation secteurSED = new Sectorisation();
        secteurSED.setCode4("ABAA");
        secteurSED.setLibelle("Service Etudes et Développpement");
        secteurSED.setSigle("SED");
        
        Locality locality = new Locality();
        locality.setNom("Salle Reunion DSI");
        locality.setLigneDirecte("+687 35.24.78");
        locality.setPoste("6547");
        locality.setService(secteurDSI);
        
        Locality locality2 = new Locality();
        locality2.setNom("Accueil DSI");
        locality2.setLigneDirecte("+687 35.24.79");
        locality2.setPoste("6548");
        locality2.setService(secteurDSI);
        
        Assert.assertTrue(locality.compareTo(locality2) > 0);
        
    }
    
     @Test
    public void getType_test(){
        Locality locality = new Locality();
        locality.setNom("Salle Reunion DSI");
        locality.setLigneDirecte("+687 35.24.78");
        locality.setPoste("6547");
        
        Assert.assertEquals(IContact.TYPE_LOCALITY, locality.getType());
        
    }
    
    
    
}
