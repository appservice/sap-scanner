package eu.appservice.module1.app.logfile;

/**
 * Created by Lukasz on 25.02.14.
 * ﹕ SAP Skanner
 */
public interface InterfaceObserver {

    public void update();

    public void update(InterfaceObservable o, Object arg);
}