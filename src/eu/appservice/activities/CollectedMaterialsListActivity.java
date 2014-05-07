package eu.appservice.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import eu.appservice.BarcodeScanner;
import eu.appservice.CollectedMaterial;
import eu.appservice.Material;
import eu.appservice.R;
import eu.appservice.adapters.ArrayCollectedMaterialsAdapter;
import eu.appservice.databases.CollectedMaterialDbOpenHelper;
import eu.appservice.databases.MaterialsDbOpenHelper;
import eu.appservice.logfile.MaterialToFileSaver;

/**
 * Created by Lukasz on 27.09.13.
 * ﹕ ${PROJECT_NAME}
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class CollectedMaterialsListActivity extends ListActivity {


    //
    public static final int SIGNATURE_REQUEST = 0;
    // do testów
    final CharSequence[] alertMenu;
  //  private Set<Integer> checkedItems;
    private List<CollectedMaterial> list;
    private int signPos;
    private ArrayCollectedMaterialsAdapter indexAdapter;


    public CollectedMaterialsListActivity() {
        alertMenu = new CharSequence[]{"Usuń", "Kopiuj indeks", "Do podpisu", "Twórz QRCode"}; //, "Zmień"
    }

    private void initialize() {


        CollectedMaterialDbOpenHelper mdb = new CollectedMaterialDbOpenHelper(this.getApplicationContext());
        list = mdb.getAllPickedMaterials();
        Collections.reverse(list);


        indexAdapter = new ArrayCollectedMaterialsAdapter(CollectedMaterialsListActivity.this,
                R.layout.list_inflate, list);
        this.setListAdapter(indexAdapter);// android.R.layout.simple_list_item_multiple_choice

    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
     //   checkedItems = new HashSet<Integer>();

        ListView lv = this.getListView();
        lv.setFastScrollEnabled(true);


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0,
                                           View arg1, int arg2, long arg3) {

                try {
                    onLongListItemClickAction(arg1, arg2, arg3);
                } catch (IOException e) {

                    e.printStackTrace();
                }

                return false;
            }

        });

    }

 //--------------------------------------------------------------------------------------------------
    private void onLongListItemClickAction(View arg1, int pos, long arg3)
            throws IOException {
        //	Context myContext=this.getApplicationContext();

        this.signPos = pos;
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Menu:");
        build.setItems(alertMenu, new DialogInterface.OnClickListener() {
            //  public int signPos;

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                switch (i) {
                    case 0:

                        // boolean czyUsuwac = false;
                        AlertDialog.Builder builder = new AlertDialog.Builder(CollectedMaterialsListActivity.this);
                        final CollectedMaterial cmRemoved = list.get(signPos);
                        StringBuilder sbPmRemoved = new StringBuilder("Czy usunąć pozycję: ").append(list.size() - signPos).append("\n");
                        sbPmRemoved.append(cmRemoved.getIndex()).append("\n");
                        sbPmRemoved.append(cmRemoved.getName()).append("\n");
                        sbPmRemoved.append(cmRemoved.getCollectedQuantity()).append(" ");
                        sbPmRemoved.append(cmRemoved.getUnit()).append(" ");
                        sbPmRemoved.append(cmRemoved.getMpk()).append(" ");
                        sbPmRemoved.append(cmRemoved.getBudget()).append(" ?");

                        builder.setMessage(sbPmRemoved.toString())
                                .setCancelable(false).setPositiveButton("Tak",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        // czyUsuwac=true;
                                      //  removeFromLogFile1(signPos);
                                        removeFromLogFile(cmRemoved.getDate());
                                        updateDatabases();


                                    }
                                }).setNegativeButton("Nie",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("Usuwanie");
                        alert.show();


                        //    Vibrator vibra = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        //     vibra.vibrate(60);
                        break;

                    case 1:
                        int currentApiVersion  = android.os.Build.VERSION.SDK_INT;


                        if (currentApiVersion  < android.os.Build.VERSION_CODES.HONEYCOMB) {
                            @SuppressWarnings("deprecation")
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            clipboard.setText(list.get(signPos).getIndex());
                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("index clip", list.get(signPos).getIndex());
                            clipboard.setPrimaryClip(clip);
                        }

                        break;
                    case 2:
                        onListItemSelected(signPos);

                        break;
                    case 3:
                        encodeMaterial();
                        break;

                    default:
                        break;


                }
            }
        });

        AlertDialog alertPos = build.create();
        alertPos.show();

    }
//--------------------------------------------------------------------------------------------------
    private void encodeMaterial() {
        BarcodeScanner barcodeScanner=new BarcodeScanner(this);
        barcodeScanner.encodeData(list.get(signPos).getDataToEncodeQrcode());
    }


//--------------------------------------------------------------------------------------------------
    private void removeFromLogFile(String removedMaterialDate){
        MaterialToFileSaver mtf=new MaterialToFileSaver();
        if(mtf.removeFromLogFile(removedMaterialDate)){
            Toast.makeText(getApplicationContext(),"Pozycja usunięta!",Toast.LENGTH_LONG).show();
        }

    }

 //--------------------------------------------------------------------------------------------------
    private void onListItemSelected(int signPos) {
        indexAdapter.toggleSelection(signPos);
    }

    private boolean updateDatabases() {
        CollectedMaterialDbOpenHelper dbm = new CollectedMaterialDbOpenHelper(getApplicationContext());

        dbm.removePickedMaterialById(list.get(signPos).getId()); //remove from database
        dbm.close();


        MaterialsDbOpenHelper dba = new MaterialsDbOpenHelper(
                getApplicationContext());
        Material materialInData = dba.getMaterialByIndexAndStore(list.get(signPos).getIndex(), list.get(signPos).getStore());
        dba.updateAmount(materialInData, list.get(signPos).getCollectedQuantity());
        indexAdapter.remove(list.get(signPos)); //remove from showed listView
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SIGNATURE_REQUEST:
                if (resultCode == RESULT_OK) {
                    String signAddress = data.getStringExtra("SIGNATURE_RESULT");

                    CollectedMaterialDbOpenHelper pmd = new CollectedMaterialDbOpenHelper(getApplicationContext());
                    SparseBooleanArray selectedMaterials = indexAdapter.getSelectedItemIds();

                    for (int i = indexAdapter.getSelectedCount() - 1; i >= 0; i--) {

                        CollectedMaterial collectedMaterial = list.get(selectedMaterials.keyAt(i));
                        collectedMaterial.setSignAddress(signAddress);
                        pmd.updatePickedMaterial(collectedMaterial);//>0){

                    }
                    pmd.close();
                    Toast.makeText(getApplicationContext(), signAddress, Toast.LENGTH_LONG).show();
                    this.indexAdapter.removeSelection();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.material_list_inflate, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.schowPickedSearchActivity:
                Intent intent = new Intent(getApplicationContext(), SearchCollectedActivity.class);
                startActivity(intent);
                return true;
            case R.id.materialListInflateMenuSign:
                if (indexAdapter.getSelectedCount() > 0) {
                    Intent singIntent = new Intent(getApplicationContext(), FingerPaintActivity.class);
                    startActivityForResult(singIntent, SIGNATURE_REQUEST);
                } else {
                    Toast.makeText(getApplicationContext(), "Zaznacz pozycje do podpisu", Toast.LENGTH_LONG).show();
                }
                return true;

        }
        return super.onMenuItemSelected(featureId, item);
    }

    //  function is checking non-existent intents
    public boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.size() > 0;
    }

}

