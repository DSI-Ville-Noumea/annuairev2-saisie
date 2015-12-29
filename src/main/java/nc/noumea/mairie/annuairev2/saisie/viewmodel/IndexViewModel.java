package nc.noumea.mairie.annuairev2.saisie.viewmodel;

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


import nc.noumea.mairie.annuairev2.saisie.core.security.SecurityUtil;
import nc.noumea.mairie.annuairev2.saisie.entity.Utilisateur;
import nc.noumea.mairie.annuairev2.saisie.service.IUtilisateurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Tab;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class IndexViewModel extends AbstractViewModel {

    private static final long serialVersionUID = 6154607885296755703L;

    private Logger logger = LoggerFactory.getLogger(IndexViewModel.class);

    @WireVariable
    private IUtilisateurService utilisateurService;

    private Utilisateur user;

    private List<TabModel> tabsList;
    private List<String> tabsId;
    private TabModel selectedTab;

    @Init
    @NotifyChange("*")
    public void initView() {
        tabsId = new ArrayList<>();
        tabsList = new ArrayList<>();
        selectedTab = null;
        setUser(utilisateurService.findByLogin(SecurityUtil.getUser()));
    }

    /**
     * @return the user
     */
    public Utilisateur getUser() {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(Utilisateur user) {
        this.user = user;
    }

    /**
     * @return the tabsList
     */
    public List<TabModel> getTabsList() {
        return tabsList;
    }

    /**
     * @param tabsList
     *            the tabsList to set
     */
    public void setTabsList(List<TabModel> tabsList) {
        this.tabsList = tabsList;
    }

    /**
     * @return the tabsId
     */
    public List<String> getTabsId() {
        return tabsId;
    }

    /**
     * @param tabsId
     *            the tabsId to set
     */
    public void setTabsId(List<String> tabsId) {
        this.tabsId = tabsId;
    }

    /**
     * @return the selectedTab
     */
    public TabModel getSelectedTab() {
        return selectedTab;
    }

    /**
     * @param selectedTab
     *            the selectedTab to set
     */
    public void setSelectedTab(TabModel selectedTab) {
        this.selectedTab = selectedTab;
    }


    /**************************************************************************************
     * Commandes
     **************************************************************************************/

    @Command
    @NotifyChange({ "tabsList", "selectedTab" })
    public void openGestionGuestTab(){
        openTab("gestionGuestTab", "Gestion Guest", "/includes/searchEntity.zul", true, "tab", null);
    }

    @Command
    @NotifyChange({ "tabsList", "selectedTab" })
    public void openTab(@BindingParam("id") String id, @BindingParam("label") String label,
                        @BindingParam("zulTemplate") String zulTemplate,
                        @BindingParam("closable") boolean closable,
                        @BindingParam("sclass") String sclass,
                        @BindingParam("args") Map<String, Object> args) {

        if (tabsId.contains(id)) {
            for (TabModel tabm : tabsList) {
                if (tabm.getId().equals(id)) {
                    selectedTab = tabm;
                    return;
                }
            }
            return;
        }

        tabsId.add(id);
        TabModel tm = new TabModel(id, label, zulTemplate, closable, sclass, args);
        tabsList.add(tm);
        selectedTab = tm;
    }

    @NotifyChange("selectedTab")
    @Command
    public void selectTab(@BindingParam("item") TabModel tabModel) {
        selectedTab = tabModel;
    }

    @Command
    @NotifyChange({ "tabsList", "selectedTab" })
    public void closeTab(@BindingParam("tabModel") TabModel tabModel, @BindingParam("tab") Tab tab) {
        tabsList.remove(tabModel);
        if (selectedTab != null && selectedTab.equals(tabModel)) {
            selectedTab = tabsList.get(tabsList.size() - 1);
        }
        tabsId.remove(tabModel.getId());
    }

    @Command
    @NotifyChange({ "tabsList", "selectedTab" })
    public void closeSelectedTab() {
        TabModel oldSelectedTab = selectedTab;
        tabsList.remove(oldSelectedTab);
        if (selectedTab != null) {
            selectedTab = tabsList.get(tabsList.size() - 1);
        }
        tabsId.remove(oldSelectedTab.getId());
    }

}
