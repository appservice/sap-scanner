package eu.appservice.logfile;

import eu.appservice.CollectedMaterial;

/**
 * Created by Lukasz on 23.02.14.
 * ﹕ SAP Skanner
 */
public interface MaterialSaver {
    public abstract int save(CollectedMaterial collectedMaterial);
}
