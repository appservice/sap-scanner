package eu.appservice.activities;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.TreeMap;

import eu.appservice.BarcodeScanner;
import eu.appservice.IntentAvailableChecker;
import eu.appservice.Material;
import eu.appservice.R;
import eu.appservice.ScannedMaterial;
import eu.appservice.Utils;
import eu.appservice.activities.dialogs.NoExistInDbDialog;
import eu.appservice.databases.MaterialsDbOpenHelper;
import eu.appservice.logfile.FlashLightOnHtc;

/**
 * Created by Lukasz on 18.02.14.
 * ﹕ SAP Skanner
 */
public class ScanSearchFragment extends Fragment implements View.OnClickListener{

    OnGetMaterialFromDb mCallback;


    private static final int SCANNER_REQUEST = 1;
    private static final int SEARCH_REQUEST = 2;


    private Material materialFromDb;
    private TextView tvScannedMaterialIndex;
    private TextView tvScannedMaterialName;
    private TextView tvScannedMaterialAmount;

    private boolean isSwitchedFlashLight;
    private boolean isAutoFlashOffPreference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.scan_search_framgent,container,false);
        ImageButton imgBtnSearch = (ImageButton) v.findViewById(R.id.imgBtnSearchScanSearchFragment);
        imgBtnSearch.setOnClickListener(this);

        ImageButton imgBtnScan = (ImageButton) v.findViewById(R.id.imgBtnScanSearchFragment);
        imgBtnScan.setOnClickListener(this);

        tvScannedMaterialIndex = (TextView) v.findViewById(R.id.tvIndexScanSearchFragment);
        tvScannedMaterialName = (TextView) v.findViewById(R.id.tvNameScanSearchFragment);
        tvScannedMaterialAmount = (TextView) v.findViewById(R.id.tvAmountScanSearchFragment);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBtnScanSearchFragment:
                showScanner();
                break;
            case R.id.imgBtnSearchScanSearchFragment:
               showSearchActivity();
                break;

            default:
                break;
        }
    }



    private void showSearchActivity() {
        Intent intent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
        intent.putExtra("IS_FROM_OTHER_ACTIVITY", true);
        startActivityForResult(intent, SEARCH_REQUEST);
    }

    private void showScanner() {

        BarcodeScanner barcodeScanner=new BarcodeScanner(getActivity());
        barcodeScanner.showScannerForResult(SCANNER_REQUEST);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCANNER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Material scannedMaterial = new ScannedMaterial(data);
                    MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getActivity().getApplicationContext());
                    this.materialFromDb = db.getMaterialByIndexAndStore(scannedMaterial.getIndex(), scannedMaterial.getStore());
                    showMaterial();
                    mCallback.onGetMaterialFromDb(this.materialFromDb);
                    if (materialFromDb == null) {
                        materialNotInDb(scannedMaterial);
                    }
                }
                if (isAutoFlashOffPreference) FlashLightOnHtc.getInstance().flashLightOff();
                break;
            case SEARCH_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    this.materialFromDb = (Material) data.getSerializableExtra("MATERIAL_FROM_SEARCH_ACTIVITY");
                    showMaterial();
                    mCallback.onGetMaterialFromDb(this.materialFromDb);
                }
                break;


            default:
                break;
        }
    }

    private void showMaterial() {
        if (this.materialFromDb != null) {
            tvScannedMaterialIndex.setText(this.materialFromDb.getIndex());
            tvScannedMaterialName.setText(this.materialFromDb.getName());
            String unit = this.materialFromDb.getUnit();
            StringBuilder sb = new StringBuilder();
            MaterialsDbOpenHelper db = new MaterialsDbOpenHelper(getActivity().getApplicationContext());

            double amount = db.getMaterial(materialFromDb.getId()).getAmount();// uaktualnienie ilości
            this.materialFromDb.setAmount(amount);

            TreeMap<String, Double> mapStockAmount = (TreeMap<String, Double>) db.getMapStockAmount(this.materialFromDb.getIndex());
            for (Map.Entry<String, Double> pairs : mapStockAmount.entrySet()) {
                sb.append(Utils.parse(pairs.getValue()));
                sb.append(" ");
                sb.append(unit);
                sb.append("   skład: ");
                sb.append(pairs.getKey());
                sb.append("\n");

            }


            tvScannedMaterialAmount.setText(sb.toString());
            db.close();

        } else {
            tvScannedMaterialIndex.setText("");
            tvScannedMaterialName.setText("");
            tvScannedMaterialAmount.setText("");
        }
    }
    /**
     * show Dialog when the material wont be in Database
     */
    private void materialNotInDb(Material scannedMaterial) {
        //Toast.makeText(getApplicationContext(), material.toString()+" nie istnieje w bazie danych!\n Nie dodano.", Toast.LENGTH_SHORT).show();

        NoExistInDbDialog noExistInDbDialog=new NoExistInDbDialog(scannedMaterial);
        noExistInDbDialog.show(getActivity().getSupportFragmentManager(), "NO_EXIST_IN_DB");

    }

   public interface OnGetMaterialFromDb{
       public void onGetMaterialFromDb(Material materialFromDb);
   }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnGetMaterialFromDb) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGetMaterialFromDb");
        }
    }
}
