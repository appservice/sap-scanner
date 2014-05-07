package eu.appservice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import eu.appservice.Material;
import eu.appservice.R;
import eu.appservice.logfile.FlashLightOnHtc;

/**
 * Created by Lukasz on 18.02.14.
 * ﹕ SAP Skanner
 */
public class TestActivity extends ActionBarActivity implements ScanSearchFragment.OnGetMaterialFromDb {

    private Material materialFromDb;
    private TextView tvUnit, tvStock,tvMPK, tvBudget;
    private static final int MPK_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvUnit= (TextView) findViewById(R.id.tvUnitTestActivity);
        tvStock= (TextView) findViewById(R.id.tvStockTestActivity);
        tvMPK=(TextView)findViewById(R.id.tvMpkTestActivity);
        tvBudget=(TextView)findViewById(R.id.tvBudgetTestActivity);

    }

    @Override
    public void onGetMaterialFromDb(Material materialFromDb) {
       // Toast.makeText(getApplicationContext(),materialFromDb.toString(),Toast.LENGTH_LONG).show();
        this.materialFromDb=materialFromDb;
        tvUnit.setText(materialFromDb.getUnit());
        tvStock.setText(materialFromDb.getStore());
    }

    public void mpkButtonClicked(View view){
        showMpkListActivity();

    }

    private void showMpkListActivity() {
        Intent intent = new Intent(getApplicationContext(), MpkListActivity.class);
        startActivityForResult(intent, MPK_REQUEST);
    }


    public void saveButtonClicked(View view){
        if(materialFromDb!=null)
        Toast.makeText(getApplicationContext(),"zapiany:\n"+materialFromDb.toString(),Toast.LENGTH_LONG).show();
    }

 public void litButtonClicked(View view){
     Intent intent =new Intent(getApplicationContext(),CollectedMaterialsListActivity.class);
     startActivity(intent);
 }


   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

      //---------this loop is checking function onActivityResult from all fragments assigned to this activity
       for (Fragment fragment : getSupportFragmentManager().getFragments()) {
           fragment.onActivityResult(requestCode, resultCode, data);
       }


       switch(requestCode){
           case MPK_REQUEST:
               if (resultCode == RESULT_OK) {
                   String text = data.getStringExtra("MPK_BUDGET_RESULT");
                   assert text != null;

                   if (text.contains(";")) {
                       String[] budget = text.split(";");
                       this.tvMPK.setText(budget[0]);
                       this.tvBudget.setText(budget[1]);

                   }
               }
               break;

           default:
               break;
       }
    }


    /**
     * This function switch the flash light
     * @return
     */
    @Override
    public boolean onSearchRequested() {
        FlashLightOnHtc.getInstance().flashLightToggle();
        return true;
    }
}
