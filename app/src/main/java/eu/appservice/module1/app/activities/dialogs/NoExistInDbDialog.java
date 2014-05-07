package eu.appservice.module1.app.activities.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import eu.appservice.module1.app.Material;


/**
 * Created by Lukasz on 2014-04-03.
 * ﹕ SAP Skanner
 */
public class NoExistInDbDialog extends DialogFragment{
Material material;

    public NoExistInDbDialog(Material material){
        this.material=material;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
               getActivity());
        builder.setMessage(material.toString() + "\n\nNIE ISTNIEJE W BAZIE DANYCH! ");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                }
        );

        return builder.create();
    }
}