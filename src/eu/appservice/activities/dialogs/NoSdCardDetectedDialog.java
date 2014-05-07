package eu.appservice.activities.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import eu.appservice.R;

/**
 * Created by Lukasz on 2014-04-03.
 * ﹕ SAP Skanner
 */
public class NoSdCardDetectedDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Zamontuj kartę pamięici SD!");
        builder.setIcon(R.drawable.ic_status_dialog_error);
        builder.setTitle("Brak karty pamięci SD");
        builder.setCancelable(false);

        builder.setNeutralButton("Ok",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        Dialog dialog=builder.create();
        return dialog;


    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}


