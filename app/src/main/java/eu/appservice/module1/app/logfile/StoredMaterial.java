package eu.appservice.module1.app.logfile;


import eu.appservice.module1.app.Material;

/**
 * Created by Lukasz on 2014-04-09.
 * ﹕ SAP Skanner
 */
public class StoredMaterial {
    private Material material;
    private String date;
    public StoredMaterial(Material material,String date){
        this.material=material;
        this.date=date;

    }

    public Material getMaterial() {
        return material;
    }
    public String getDate(){
        return date;
    }


}
