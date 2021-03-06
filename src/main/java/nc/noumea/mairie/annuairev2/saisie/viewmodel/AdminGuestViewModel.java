package nc.noumea.mairie.annuairev2.saisie.viewmodel;

/*
* #%L
* * Gestion des Guest et Locality
* *
* %%
* Copyright (C) 2015 - 2016 Mairie de Nouméa
* *
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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nc.noumea.mairie.annuairev2.saisie.core.security.CodeProfil;
import nc.noumea.mairie.annuairev2.saisie.core.security.SecurityUtil;
import nc.noumea.mairie.annuairev2.saisie.entity.Guest;
import nc.noumea.mairie.annuairev2.saisie.entity.Sectorisation;
import nc.noumea.mairie.annuairev2.saisie.entity.Utilisateur;
import nc.noumea.mairie.annuairev2.saisie.service.IGuestService;
import nc.noumea.mairie.annuairev2.saisie.service.ISectorisationService;
import nc.noumea.mairie.annuairev2.saisie.service.IUtilisateurService;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Constraint;
import org.zkoss.zul.Textbox;

/**
 *
 * @author barmi83
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AdminGuestViewModel extends AbstractViewModel {
    
    private static final long serialVersionUID = 2640683989228795852L;
    
    @WireVariable
    private IGuestService guestService;
    @WireVariable
    private ISectorisationService sectorisationService;
    @WireVariable
    private IUtilisateurService utilisateurService;
    
    private Guest selectedEntity;
    private boolean readOnly;
    private List<Sectorisation> services;
    private List<String> serviceNameList;
    private StringSimpleListModel servicesListModel;
    
    private Utilisateur user;
    private boolean createMode;
    
    // controle
    private CustomGuestConstraint guestConstraint;
    
    
    @Init
    @NotifyChange("*")
    public void initView(@ExecutionArgParam("args") Map<String, Object> args) {
        setUser(utilisateurService.findByLogin(SecurityUtil.getUser()));
        this.readOnly = !(user.getProfil().getNom() == CodeProfil.ADMIN ||
                user.getProfil().getNom() == CodeProfil.GESTIONNAIRE ||
                user.getProfil().getNom()== CodeProfil.GESTIONNAIRE_GUEST);
        
        this.services = sectorisationService.findAll();
        Collections.sort(services);
        
        serviceNameList = new ArrayList<>();
        for(Sectorisation serviceItem : services){
            serviceNameList.add(serviceItem.getLibelle());
        };
        Collections.sort(serviceNameList);
        servicesListModel = new StringSimpleListModel(serviceNameList);
        
        if(args.get("idGuest") != null){
            this.selectedEntity = guestService.findById((Long)args.get("idGuest"));
            this.createMode = false;
        }
        else{
            this.selectedEntity = new Guest();
            this.createMode = true;
        }
        
        setGuestConstraint(new CustomGuestConstraint());
        
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
    
    public boolean isCreateMode() {
        return createMode;
    }
    
    public void setCreateMode(boolean createMode) {
        this.createMode = createMode;
    }
    
    public List<String> getServiceNameList() {
        return serviceNameList;
    }
    
    public void setServiceNameList(List<String> serviceNameList) {
        this.serviceNameList = serviceNameList;
    }
    
    public StringSimpleListModel getServicesListModel() {
        return servicesListModel;
    }
    
    public void setServicesListModel(StringSimpleListModel servicesListModel) {
        this.servicesListModel = servicesListModel;
    }
    
    public CustomGuestConstraint getGuestConstraint() {
        return guestConstraint;
    }
    
    public void setGuestConstraint(CustomGuestConstraint guestConstraint) {
        this.guestConstraint = guestConstraint;
    }
    
    
    
    
    
    
    @Command
    @NotifyChange({"selectedEntity"})
    public void  saveOrUpdateGuest(){
        
        selectedEntity = guestService.saveOrUpdate(selectedEntity);
        Map<String, Object> args = new HashMap<>();
        args.put("tmpTabId", "tmpGuestTab");
        args.put("idGuest", selectedEntity.getId());
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
                            Messagebox.CANCEL, Messagebox.QUESTION, (Event e) -> {
                                if (Messagebox.ON_OK.equals(e.getName())) {
                                    guestService.deleteById(selectedEntity.getId());
                                    BindUtils.postGlobalCommand(null, null, "closeSelectedTab", null);
                                    showBottomRightNotification("Guest supprimé avec succès.");
                                }
                            });
        }
    }
    
    private class CustomGuestConstraint implements Constraint {
        
        @Override
        public void validate(Component comp, Object value) throws WrongValueException {
            if (comp instanceof Textbox) {
                if ("posteTxtBox".equals(comp.getId()) && value != null && !((String) value).isEmpty() && ((String) value).length() != 4 ){
                    throw new WrongValueException(comp,
                            "Le numéro de poste doit etre composé de 4 chiffes.");
                }
                else if ("mailTxtBox".equals(comp.getId())) {
                    if (value == null || ((String) value).isEmpty() || !((String) value).matches(".+@.+\\.[a-z]+")) {
                        throw new WrongValueException(comp,
                                "Adresse email invalide.");
                    }
                }
                else if("nomGuestTxt".equals(comp.getId())){
                    if (value == null || ((String) value).isEmpty()){
                        throw new WrongValueException(comp,"Champ vide non autorisé.\n Vous devez spécifier une valeur.");
                    }
                    if(((String) value).length() > Guest.NOM_MAX_LENGTH) {
                        throw new WrongValueException(comp,"Le champ doit contenir " + Guest.NOM_MAX_LENGTH+" caractères max.");
                    }
                }
                else if("prenomGuestTxt".equals(comp.getId())){
                    if (value == null || ((String) value).isEmpty()){
                        throw new WrongValueException(comp,"Champ vide non autorisé.\n Vous devez spécifier une valeur.");
                    }
                    if(((String) value).length() > Guest.PRENOM_MAX_LENGTH){
                        throw new WrongValueException(comp, "Le champ doit contenir " + Guest.PRENOM_MAX_LENGTH+" caractères max.");
                    }
                }
                else if("servCbox".equals(comp.getId())){
                    if (value == null || ((String) value).isEmpty()){
                        throw new WrongValueException(comp,"Champ vide non autorisé.\n Vous devez spécifier une valeur.");
                    }
                }
                else if("fonctionGuestTxt".equals(comp.getId())){
                    if (value == null || ((String) value).isEmpty()){
                        throw new WrongValueException(comp,"Champ vide non autorisé.\n Vous devez spécifier une valeur.");
                    }
                    if(((String) value).length() > Guest.FAX_MAX_LENGTH){
                        throw new WrongValueException(comp, "Le champ doit contenir " + Guest.FAX_MAX_LENGTH+" caractères max.");
                    }
                }
                else if("mobileGuestTxt".equals(comp.getId())){
                    
                    if(value != null && ((String) value).length() > Guest.MOBILE_MAX_LENGTH){
                        throw new WrongValueException(comp, "Le champ doit contenir " + Guest.MOBILE_MAX_LENGTH+" caractères max.");
                    }
                    
                    if (value != null && !((String) value).isEmpty()){
                        try {
                            String phone = ((String) value);
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            if(!phoneUtil.isValidNumber(phoneUtil.parse(phone, "NC"))){
                                throw new WrongValueException(comp, "Le champ doit contenir un numéro de téléphone valide ex.: +687 27.23.85 ou 272385 ou 27.23.85");
                            }
                        } catch (NumberParseException ex) {
                            throw new WrongValueException(comp, "Le champ doit contenir un numéro de téléphone valide ex.: +687 27.23.85 ou 272385 ou 27.23.85");
                        }
                    }
                    
                }
                else if("mobilePriveGuestTxt".equals(comp.getId())){
                    if(value != null && ((String) value).length() > Guest.MOBILEPRIVE_MAX_LENGTH){
                        throw new WrongValueException(comp, "Le champ doit contenir " + Guest.MOBILEPRIVE_MAX_LENGTH+" caractères max.");
                    }
                    
                    if (value != null && !((String) value).isEmpty()){
                        try {
                            String phone = ((String) value);
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            if(!phoneUtil.isValidNumber(phoneUtil.parse(phone, "NC"))){
                                throw new WrongValueException(comp, "Le champ doit contenir un numéro de téléphone valide ex.: +687 27.23.85 ou 272385 ou 27.23.85");
                            }
                        } catch (NumberParseException ex) {
                            throw new WrongValueException(comp, "Le champ doit contenir un numéro de téléphone valide ex.: +687 27.23.85 ou 272385 ou 27.23.85");
                        }
                    }
                }
                else if("telDomGuestTxt".equals(comp.getId())){
                    if(value!=null && ((String) value).length() > Guest.TELDOMICILE_MAX_LENGTH){
                        throw new WrongValueException(comp, "Le champ doit contenir " + Guest.TELDOMICILE_MAX_LENGTH+" caractères max.");
                    }
                    
                    if (value != null && !((String) value).isEmpty()){
                        
                        try {
                            String phone = ((String) value);
                            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                            if(!phoneUtil.isValidNumber(phoneUtil.parse(phone, "NC"))){
                                throw new WrongValueException(comp, "Le champ doit contenir un numéro de téléphone valide ex.: +687 27.23.85 ou 272385 ou 27.23.85");
                            }
                        } catch (NumberParseException ex) {
                            throw new WrongValueException(comp, "Le champ doit contenir un numéro de téléphone valide ex.: +687 27.23.85 ou 272385 ou 27.23.85");
                        }
                    }
                }
            }
        }
    }
    
}
