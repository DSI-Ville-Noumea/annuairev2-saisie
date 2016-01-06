/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nc.noumea.mairie.annuairev2.saisie.viewmodel;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nc.noumea.mairie.annuairev2.saisie.core.security.SecurityUtil;
import nc.noumea.mairie.annuairev2.saisie.entity.Guest;
import nc.noumea.mairie.annuairev2.saisie.entity.Sectorisation;
import nc.noumea.mairie.annuairev2.saisie.entity.Utilisateur;
import nc.noumea.mairie.annuairev2.saisie.service.IGuestService;
import nc.noumea.mairie.annuairev2.saisie.service.ISectorisationService;
import nc.noumea.mairie.annuairev2.saisie.service.IUtilisateurService;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

/**
 *
 * @author barmi83
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AdminGuestViewModel extends AbstractViewModel {
    
    @WireVariable
    private IGuestService guestService;
    @WireVariable
    private ISectorisationService sectorisationService;
    @WireVariable
    private IUtilisateurService utilisateurService;
     
    private Guest selectedEntity;
    private boolean readOnly;
    private List<Sectorisation> services;
    private Utilisateur user;
    private boolean canAdmin;
       
    
    @Init
    @NotifyChange("*")
    public void initView(@ExecutionArgParam("args") Map<String, Object> args) {
        setUser(utilisateurService.findByLogin(SecurityUtil.getUser()));
        this.readOnly = (user.isGestionnaire() || user.isAdministrateur()) ? false : true ;
        this.canAdmin = user.isGestionnaire() || user.isAdministrateur();
        this.services = sectorisationService.findAll();
        
        if(args.get("idGuest") != null)
            this.selectedEntity = guestService.findById((Long)args.get("idGuest"));
        else
            this.selectedEntity = new Guest();
    }

    public Guest getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Guest guest) {
        this.selectedEntity = guest;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    public List<Sectorisation> getServices() {
        return services;
    }

    public void setServices(List<Sectorisation> services) {
        this.services = services;
    }

    public Utilisateur getUser() {
        return user;
    }

    public void setUser(Utilisateur user) {
        this.user = user;
    }

    public boolean isCanAdmin() {
        return canAdmin;
    }

    public void setCanAdmin(boolean canAdmin) {
        this.canAdmin = canAdmin;
    }
    
    
    
    
    @Command
    @NotifyChange({"selectedEntity"})
    public void  saveOrUpdateGuest(){
        selectedEntity = guestService.saveOrUpdate(selectedEntity);
        Map<String, Object> args = new HashMap<>();
        args.put("tmpTabId", "tmpGuestTab");
        BindUtils.postGlobalCommand(null, null, "refreshNewGuestTabId", args);
        this.showBottomRightNotification("Guest modifié avec succés.");
    }
    
    @Command
    @NotifyChange({"selectedEntity"})
    public void  refresh(){
        if(selectedEntity.getId() != null)
            selectedEntity = guestService.findById(selectedEntity.getId());
        else
            selectedEntity = new Guest();
        
        this.showBottomRightNotification("Modifications annulées.");
    }
    
    @Command
    public void  deleteGuest(){
        
        if(selectedEntity.getId() == null)
            BindUtils.postGlobalCommand(null, null, "closeSelectedTab", null);
        else{
            Messagebox.show("Vous allez supprimer le guest \"" + selectedEntity.getFullName()
                    + "\".\n Cliquez sur OK pour confirmer.",
                    "Supprimer un guest", Messagebox.OK |
                            Messagebox.CANCEL, Messagebox.QUESTION,
                    new EventListener() {
                        public void onEvent(Event e) {

                            if (Messagebox.ON_OK.equals(e.getName())) {
                                guestService.deleteById(selectedEntity.getId());
                                BindUtils.postGlobalCommand(null, null, "closeSelectedTab", null);
                                showBottomRightNotification("Guest supprimé avec succès.");
                            }

                        }
                    });
        }        
    }
    
    
}
