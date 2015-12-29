package nc.noumea.mairie.annuairev2.saisie.core.utility;

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


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Classe utilitaire pour gérer l'application-context spring
 */
public class ApplicationContextUtils implements ApplicationContextAware {

	private static ApplicationContext	ctx;

	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {
		ApplicationContextUtils.setApplicationContextStatic(appContext);
	}

	/**
	 * passage par une méthode statique pour éviter anomalie dans les rapports findbug/pmd
	 */
	private static void setApplicationContextStatic(ApplicationContext appContext) {
		ctx = appContext;
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public static String getActiveProfile() {
		if (ctx.getEnvironment().getActiveProfiles().length > 0)
			return ctx.getEnvironment().getActiveProfiles()[0];

		return "";
	}
}