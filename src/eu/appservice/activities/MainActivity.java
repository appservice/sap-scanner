package eu.appservice.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;

import eu.appservice.R;

//import android.support.v7.app.ActionBarActivity;

@TargetApi(Build.VERSION_CODES.BASE)
public class MainActivity extends Activity { //ActionBar

    private Button btnSettingsMainActiv;
    private Button btnRwMainActiv;
    private Button btnPzMainActiv;
    private Button btnSearchMainActiv;
    private Button btnInvMainActiv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSettingsMainActiv = (Button) findViewById(R.id.btnSettingsMainActiv);
        btnRwMainActiv = (Button) findViewById(R.id.btnRwMainActiv);
        btnPzMainActiv = (Button) findViewById(R.id.btnPzMainActiv);
        btnSearchMainActiv = (Button) findViewById(R.id.btnSearchMainActiv);
        btnInvMainActiv = (Button) findViewById(R.id.btnInvMainActiv);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
        getActionBar().hide();
        }
        createSystemFiles();
        initButtonsClick();


    }

    private void createSystemFiles() {
        if(isExternalStorageWritable()){
            File mainFolder=new File(Environment.getExternalStorageDirectory()+File.separator+getApplicationContext().getString(R.string.main_folder));
            if(!mainFolder.exists()){
                mainFolder.mkdir();
            }

            File signFolder=new File(mainFolder.getPath()+File.separator+getApplicationContext().getString(R.string.signatures_folder));
            if(!signFolder.exists()){
                signFolder.mkdir();
            }

        }

    }

    private void initButtonsClick() {
        OnClickListener btnListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnRwMainActiv:
                        showRwActivity();
                        break;
                    case R.id.btnPzMainActiv:
                        showPzActivity();
                        break;
                    case R.id.btnSearchMainActiv:
                        showSearchActivity();
                        break;
                    case R.id.btnSettingsMainActiv:
                        showPreferencesActivity();
                        break;
                    case R.id.btnInvMainActiv:
                        showInventoryActivity();
                        break;
                    default:
                        break;
                }
            }


        };
        btnRwMainActiv.setOnClickListener(btnListener);
        btnPzMainActiv.setOnClickListener(btnListener);
        btnSearchMainActiv.setOnClickListener(btnListener);
        btnSettingsMainActiv.setOnClickListener(btnListener);
        btnInvMainActiv.setOnClickListener(btnListener);

    }


    private void showInventoryActivity() {
        startActivity(new Intent(getApplicationContext(), InventoryActivity.class));
    }

    protected void showRwActivity() {
        startActivity(new Intent(getApplicationContext(), RwActivity.class));

    }

    protected void showPzActivity() {
        Intent intent = new Intent(getApplicationContext(), PzActivity.class);
        startActivity(intent);

    }

    protected void showSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivity(intent);

    }


    private void showPreferencesActivity() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Checks if external storage is available for read and write
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
