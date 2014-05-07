package eu.appservice.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import eu.appservice.PlantStrucMpk;
import eu.appservice.R;
import eu.appservice.databases.CompanyStructDbOpenHelper;

/**
 * Created by Lukasz on 03.10.13.
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class MpkListActivity extends ListActivity  {


    CompanyStructDbOpenHelper db;
    List<String> wydzialyList;
    private int parentId = 0;
    //private Bundle savedInstanceState;
    private List<PlantStrucMpk> lista;
    private int longItemId;
    private String defaultBudget;
    private ArrayAdapter<String> adapter;


    private void fillData() {

        lista = db.getFactorysByParentId(parentId);

        wydzialyList = new ArrayList<String>();

        for (PlantStrucMpk plantStrucMpk : lista) {
            wydzialyList.add(plantStrucMpk.getName());
        }
        Log.w("test3", wydzialyList.toString());


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, wydzialyList);
        setListAdapter(adapter);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        defaultBudget = sf.getString("pref_default_budget", "");
        super.onCreate(savedInstanceState);
        db = new CompanyStructDbOpenHelper(this);
        lista = new ArrayList<PlantStrucMpk>();


        ListView lv = this.getListView();

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v,
                                           int pos, long id) {

                onLongListItemClickAction(pos);
                return true;
            }

        });


    }


    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        fillData();
        super.onStart();
    }


    //---------- FUNCTION ON LONG LIST ITEM CLICK
    private void onLongListItemClickAction(int pos) {
        longItemId = pos;
        dialogBuild();
        this.fillData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mpk_list, menu);

        return true;
    }


    //------- FUNCTION ON LIST ITEM CLICK-----------
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        parentId = lista.get(position).getId();

        if (lista.get(position).getValue().length() > 0 || lista.get(position).getBudget().length() > 0)//||(lista.get(position).getValue()!="Null")
        {
            Intent result = new Intent();
            //   Log.w("MPK_BUDGET_RESULT", lista.get(position).getValue() + ";" + lista.get(position).getBudget());
            result.putExtra("MPK_BUDGET_RESULT", lista.get(position).getValue() + ";" + lista.get(position).getBudget());
            setResult(RESULT_OK, result);
            db.close();
            this.finish();

        }

        this.fillData();

    }

/*    @Override
    protected void onStop() {
        super();
     // if(db)
      //  db.close();
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        if(db!=null){
            db.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_add:

                addMpkItem();

                //this.onCreate(savedInstanceState);
                adapter.notifyDataSetChanged();
                //this.fillData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * function which add the MPK Ite
     */
    private void addMpkItem() {


        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.dialog_add_mpk, null);

        //  thats add defautl budget to AutoCompleteTextView.
        final EditText mpkValue = (EditText) textEntryView.findViewById(R.id.editTextMpkValue);
        final AutoCompleteTextView mpkBudget = (AutoCompleteTextView) textEntryView.findViewById(R.id.editTextMpkBudget);


        mpkValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mpkBudget.setText(defaultBudget);

            }
        });


        //   String[] table = new String[1];
        //   table[0]=defaultBudget;
        //    ArrayAdapter<String> adapt=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,table);
        //    mpkBudget.setAdapter(adapt);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.dialogMpkTitle);
        alert.setMessage(R.string.dialogMpkMessage);
        alert.setIcon(R.drawable.ic_menu_add_mpk);
        alert.setView(textEntryView);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                EditText mpkName;


                mpkName = (EditText) textEntryView
                        .findViewById(R.id.editMpkName);


                int viewId = db.getLastId() + 1;
                db.addFactory(new PlantStrucMpk(parentId, viewId, mpkName.getText()
                        .toString(), mpkValue.getText().toString(), mpkBudget.getText().toString()));
                fillData();
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        alert.show();

    }

    private void dialogBuild() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        String[] aItems = new String[]{"Do góry", "Usuń!"};
        ab.setItems(aItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        moveUp(longItemId);
                        break;
                    case 1:
                        deleteMpkItem();
                        break;
                    default:
                        break;
                }

            }

        });
        ab.show();
    }


    private void moveUp(int pos) {

        if (pos > 0) {
            db.replaceViewId(lista.get(pos).getId(), lista.get(pos).getView_id(), lista.get(pos - 1).getId(), lista.get(pos - 1).getView_id());
            adapter.notifyDataSetChanged();
        }
    }

    //------------------function delete-------------------------------------

    /**
     *
     */
    private void deleteMpkItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy usunąć pozycję: " + lista.get(longItemId).getName() + "?")
                .setCancelable(false)
                .setPositiveButton("Tak",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                db.deleteFactory(lista.get(longItemId).getId());
                                adapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton("Nie",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        builder.show();
        Vibrator vibra = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibra.vibrate(60);


    }


}

