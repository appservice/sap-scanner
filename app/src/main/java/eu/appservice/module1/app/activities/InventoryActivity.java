package eu.appservice.module1.app.activities;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.TreeMap;

import eu.appservice.module1.app.CollectedMaterial;
import eu.appservice.module1.app.IntentAvailableChecker;
import eu.appservice.module1.app.Material;
import eu.appservice.R;
import eu.appservice.module1.app.ScannedMaterial;
import eu.appservice.module1.app.databases.InventoryMaterialDbOpenHelper;
import eu.appservice.module1.app.databases.MaterialsDbOpenHelper;

@TargetApi(Build.VERSION_CODES.BASE)
public class InventoryActivity extends ActionBarActivity {
    private Button btnSaveInvActiv;
    private Button btnPlaceInvActiv;
    private ImageButton imgBtnSearchInvActiv;
    private ImageButton imgBtnScanInvActiv;

    private static final int SCANER_REQUEST = 1;
    private static final int SEARCH_REQUEST = 2;

    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCES_PLACE_INV_STRING = "tvPlaceInventory";
    private TextView tvInventoryPlace;
    private static final String MY_PREFERENCES = "my_preferences";
    private TextView editTextAddPlaceDialog;
    private TextView textViewNameInvActiv;
    private TextView textViewAmountInvActiv;
    private TextView textViewUnitInvActiv;
    private TextView textViewStockInvActiv;
    private EditText textViewIndexInvActiv;
    private EditText editTextAddValueInvActiv;
    private Material materialFromDb;
    private Material material;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_inventory);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //getSupportActionBar().setTitle("Bar");
        // getSupportActionBar().
        //  getSupportActionBar().show();


        btnSaveInvActiv = (Button) findViewById(R.id.inventory_activity_btn_save);
        btnPlaceInvActiv = (Button) findViewById(R.id.inventory_activity_btn_place);
        imgBtnSearchInvActiv = (ImageButton) findViewById(R.id.inventory_activity_ibtn_search);
        imgBtnScanInvActiv = (ImageButton) findViewById(R.id.inventory_activity_ibtn_scan);

        tvInventoryPlace = (TextView) findViewById(R.id.inventory_activity_tv_place);
        textViewNameInvActiv = (TextView) findViewById(R.id.inventory_activity_tv_name);
        textViewAmountInvActiv = (TextView) findViewById(R.id.inventory_activity_tv_amount);
        textViewUnitInvActiv = (TextView) findViewById(R.id.inventory_activity_tv_unit);
        textViewStockInvActiv = (TextView) findViewById(R.id.inventory_activity_tv_stock);


        textViewIndexInvActiv = (EditText) findViewById(R.id.inventory_activity_et_index);
        editTextAddValueInvActiv = (EditText) findViewById(R.id.inventory_activity_et_value);


        sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);

        fillUiFromPreferences();

        initButtonsClick();

    }

    private void fillUiFromPreferences() {
        //get value tvPlaceInventory from sharedPreferences
        String inventoryPlaceFromTextPreferences = sharedPreferences.getString(SHARED_PREFERENCES_PLACE_INV_STRING, "");
        tvInventoryPlace.setText(inventoryPlaceFromTextPreferences);

    }

    private void initButtonsClick() {
        View.OnClickListener btnListener = new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.inventory_activity_btn_save:
                        addMaterialToInventory();
                        // new ImportMaterialsPoiEventAsyncTask(InventoryActivity.this).execute();
                        break;
                    case R.id.inventory_activity_ibtn_search:
                        showSearchActivity();
                        break;
                    case R.id.inventory_activity_ibtn_scan:
                        showScanner();
                        break;
                    case R.id.inventory_activity_btn_place:
                        showPlaceDialog();
                        break;
                    case R.id.btnShowListInvAcitv:

                        break;
                    default:
                        break;
                }
            }


        };


        btnSaveInvActiv.setOnClickListener(btnListener);
        btnPlaceInvActiv.setOnClickListener(btnListener);
        imgBtnSearchInvActiv.setOnClickListener(btnListener);
        imgBtnScanInvActiv.setOnClickListener(btnListener);

    }

    public void  showTestActivity(View v){
        Intent intent=new Intent(getApplicationContext(),TestActivity.class);
        startActivity(intent);
    }

    private void showSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra("IS_FROM_OTHER_ACTIVITY", true);
        startActivityForResult(intent, SEARCH_REQUEST);
    }


    private void showPlaceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View inflatedView = layoutInflater.inflate(R.layout.dialog_add_place, null);


        editTextAddPlaceDialog = (EditText) inflatedView.findViewById(R.id.editTextAddPlaceDialog);
        builder.setTitle("Miejsce inwentaryzacji");
        // builder.setMessage("Wprowadź miejsce inwentaryzacji");
        builder.setView(inflatedView);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                savePlaceToShardPreferences();
            }
        });
        builder.setNegativeButton("Wyjdź", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void savePlaceToShardPreferences() {
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putString(SHARED_PREFERENCES_PLACE_INV_STRING, editTextAddPlaceDialog.getText().toString());
        preferencesEditor.commit();
        fillUiFromPreferences();


    }

    /*
   * (non-Javadoc)
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCANER_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.material = new ScannedMaterial(data);
                    MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());
                    this.materialFromDb = db.getMaterialByIndexAndStore(material.getIndex(), material.getStore());
                    showMaterial();
                    if (materialFromDb == null) {
                        materialNotInDb();
                        //tvScannedMaterialIndex.setText(*//*this.material.toString() + *//*" nie isnieje w bazie!");
                    }
                }
                //if (isAutoFlashOffPreference) HtcFlashLight.flashLightOff();
                break;
            case SEARCH_REQUEST:
                if (resultCode == RESULT_OK) {
                    this.materialFromDb = (Material) data.getSerializableExtra("MATERIAL_FROM_SEARCH_ACTIVITY");
                    showMaterial();
                }
                break;


            default:
                break;
        }
    }

    private void materialNotInDb() {

        //Toast.makeText(getApplicationContext(), material.toString()+" nie istnieje w bazie danych!\n Nie dodano.", Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(
                InventoryActivity.this);

        builder.setMessage(material.toString() + "\n\nNIE ISTNIEJE W BAZIE DANYCH! ");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok",

                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // Toast.makeText(getApplicationContext(),"Nie dodano materiału do bazy",Toast.LENGTH_LONG).show();
                        dialog.cancel();


                    }
                });


        builder.show();


    }

    private void showMaterial() {
        if (this.materialFromDb != null) {
            textViewIndexInvActiv.setText(this.materialFromDb.getIndex());
            textViewNameInvActiv.setText(this.materialFromDb.getName());
            textViewUnitInvActiv.setText(this.materialFromDb.getUnit());
            textViewStockInvActiv.setText(this.materialFromDb.getStore());
            String unit = this.materialFromDb.getUnit();
            //String amount="";
            StringBuilder sb = new StringBuilder();
            MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());

            double amount = db.getMaterial(materialFromDb.getId()).getAmount();// uaktualnienie ilości
            this.materialFromDb.setAmount(amount);

            TreeMap<String, Double> mapStockAmount = (TreeMap<String, Double>) db.getMapStockAmount(this.materialFromDb.getIndex());
            for (Map.Entry<String, Double> pairs : mapStockAmount.entrySet()) {
                sb.append(pairs.getValue());
                sb.append(" ");
                sb.append(unit);
                sb.append("   skład: ");
                sb.append(pairs.getKey());
                sb.append("\n");

            }

            textViewAmountInvActiv.setText(sb.toString());
            editTextAddValueInvActiv.setText(String.valueOf(this.materialFromDb.getAmount()));

            db.close();

        } else {
            textViewIndexInvActiv.setText("");
            textViewNameInvActiv.setText("");
            textViewUnitInvActiv.setText("");
            textViewStockInvActiv.setText("");
            textViewAmountInvActiv.setText("");
        }
    }


    private void showScanner() {
        Intent barcodeScannerIntent=new Intent("com.google.zxing.client.android.SCAN");
        IntentAvailableChecker iac=new IntentAvailableChecker(getApplicationContext());

        if (iac.isIntentAvailable(barcodeScannerIntent)) {
            barcodeScannerIntent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(barcodeScannerIntent, SCANER_REQUEST);
        } else {
            Toast.makeText(getApplicationContext(), "Zainstaluj aplikację BarcodeScaner", Toast.LENGTH_LONG).show();
        }
    }

    protected void addMaterialToInventory() {
        String addedValueString = editTextAddValueInvActiv.getText().toString();
        // String mpk = editTextMpkRwActiv.getText().toString();
        //  String budget = tvBudgetValue.getText().toString();
        //  Boolean toZero = this.checkBoxZeroRwActiv.isChecked();


        if (materialFromDb != null) {
            if ((addedValueString.length() > 0)) {
                //  if (mpk.length() > 0 || budget.length() > 0) {

                MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getApplicationContext());
                Double removedValue = Double.valueOf(addedValueString);
                // this.materialFromDb.removeValue(removedValue); //uaktualnienie materiału
                //boolean isAdded = db.updateAmount(materialFromDb, removedValue * (-1));
                int removedRow = db.updateMaterial(this.materialFromDb); //uaktualnienie ilości w bazie danych
                if (removedRow > -1) {
                    Toast.makeText(getApplicationContext(), materialFromDb.toString() + "Pobrano " + removedValue + " " + this.materialFromDb.getUnit(), Toast.LENGTH_SHORT).show();


                    // writeToFile(materialFromDb, removedValue, mpk, budget, toZero);//zapis do pliku txt pobranych materiałów
                    addToInventoryMaterialDb(materialFromDb, removedValue, tvInventoryPlace.getText().toString(), "", false);// zapis do bazy danych pobranych materiałów

                    materialFromDb = null;
                    showMaterial();


                    // addedValueString.setText("");
                    // checkBoxZeroRwActiv.setChecked(false);
                    //this.sign = "";
                } else {
                    materialNotInDb();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Podaj pobieraną ilość", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Wybirz / zeskanuj pobierany materiał!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToInventoryMaterialDb(Material materialFromDatabase, Double pickingQuantity, String mpk, String budget, boolean isZero) {
        InventoryMaterialDbOpenHelper inventoryMaterialDbOpenHelper = new InventoryMaterialDbOpenHelper(getApplicationContext());

        CollectedMaterial pm = new CollectedMaterial(materialFromDatabase, pickingQuantity, mpk, budget, isZero, null, "");
        // pm.setSignAddress(this.sign);

        boolean result = inventoryMaterialDbOpenHelper.addPickedMaterial(pm);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // menu.getItem(R.id.men_inventory_add).setIcon(R.drawable.ic_add);
        getMenuInflater().inflate(R.menu.activity_inventory, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.men_inventory_add:
                item.setIcon(R.drawable.ic_add);
                return true;
        }
        return true;
    }



}
