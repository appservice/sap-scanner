package eu.appservice.module1.app.logfile;

import eu.appservice.module1.app.CollectedMaterial;

/**
 * Created by Lukasz on 23.02.14.
 * ﹕ SAP Skanner
 */
public interface MaterialSaver {
    public abstract int save(CollectedMaterial collectedMaterial);
}
