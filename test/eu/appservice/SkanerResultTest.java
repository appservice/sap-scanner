package eu.appservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SkanerResultTest extends Activity {

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        String material = "0619300020000003;Opaska zaciskowa ślimakowa  25-40.;SZT;0410";
        Intent resoult = new Intent();
        resoult.putExtra("SCAN_RESULT", material
        );
        setResult(RESULT_OK, resoult);
        this.finish();
    }

}
