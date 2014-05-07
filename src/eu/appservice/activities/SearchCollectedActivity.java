package eu.appservice.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.appservice.CollectedMaterial;
import eu.appservice.R;
import eu.appservice.adapters.ArrayCollectedMaterialsAdapter;
import eu.appservice.databases.CollectedMaterialDbOpenHelper;

/**
 * Created by Lukasz on 01.10.13.
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class SearchCollectedActivity extends ActionBarActivity {
    private EditText editTextIndexSearchPickedActiv;
    //	private TextView textViewIndexSearchPickedActiv;	
    private EditText editTextNameSearchPickedActiv;
    private TextView textViewUnitSearchPickedActiv;
    private EditText editTextMpkSearchPickedActiv;
    private EditText editTextBudgetSearchPickedActiv;


    private Button btnSearchPickedActiv;

    private ArrayList<CollectedMaterial> searchedMaterials = new ArrayList<CollectedMaterial>();
    private Dialog dialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_picked);


        editTextIndexSearchPickedActiv = (EditText) findViewById(R.id.editTextIndexSearchPickedActiv);
        editTextNameSearchPickedActiv = (EditText) findViewById(R.id.editTextNameSearchPickedActiv);
        editTextMpkSearchPickedActiv = (EditText) findViewById(R.id.editTexMpkSearchPickedActiv);
        editTextBudgetSearchPickedActiv = (EditText) findViewById(R.id.editTextBudgetSearchPickedActiv);


        btnSearchPickedActiv = (Button) findViewById(R.id.btnSearchPickedActiv);




        initButtonsClick();

    }

    private void initButtonsClick() {
        View.OnClickListener btnListener = new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnSearchPickedActiv:

                        new GatDataTask().execute();
                        break;
                    default:
                        break;
                }

            }

        };
        btnSearchPickedActiv.setOnClickListener(btnListener);
    }

//-------------search Material Task----------------------------

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private class GatDataTask extends AsyncTask<Void, Void, List> {
        private CollectedMaterialDbOpenHelper materialsDb;
        protected ProgressDialog progressDialog;


        /* 
         * @see android.os.AsyncTask#onPreExecute()
         */
        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPreExecute() {
            materialsDb = new CollectedMaterialDbOpenHelper(getApplicationContext());
            btnSearchPickedActiv.setClickable(false);
            progressDialog = new ProgressDialog(SearchCollectedActivity.this);
            progressDialog.setMessage("Ładuję...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected List<CollectedMaterial> doInBackground(Void... params) {

            searchedMaterials = (ArrayList<CollectedMaterial>) materialsDb.getPickedMaterialsByIndexAndName(editTextIndexSearchPickedActiv.getText().toString(), editTextNameSearchPickedActiv.getText().toString(), editTextMpkSearchPickedActiv.getText().toString(), editTextBudgetSearchPickedActiv.getText().toString());


            return searchedMaterials;


        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onPostExecute(List result) {

            progressDialog.dismiss();

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SearchCollectedActivity.this);
            dialogBuilder.setTitle("Odnaleziono " + searchedMaterials.size() + " :");

            ListView modeList = new ListView(SearchCollectedActivity.this);
            modeList.setFastScrollEnabled(true);
            Collections.reverse(result); // reverse result (will be showed from latest to first)
            ArrayCollectedMaterialsAdapter arrayCollectedMaterialsAdapter = new ArrayCollectedMaterialsAdapter(SearchCollectedActivity.this, R.layout.list_inflate, result);

            modeList.setAdapter(arrayCollectedMaterialsAdapter);

            dialogBuilder.setView(modeList);
            dialog = dialogBuilder.create();
            dialog.show();

            btnSearchPickedActiv.setClickable(true);
            super.onPostExecute(result);

        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onCancelled()
         */
        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        @Override
        protected void onCancelled() {

            progressDialog.dismiss();

            super.onCancelled();
            btnSearchPickedActiv.setClickable(true);


        }


    }


}