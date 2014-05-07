package eu.appservice;


import android.content.Intent;


/**
 * @author mochelek
 */
public class ScannedMaterial extends Material {
    /**
     *
     */
    private static final long serialVersionUID = 3675525034982129770L;
    private static final int INDEX_POZ_IN_FILE = 0;
    private static final int NAME_POZ_IN_FILE = 1;
    private static final int UNIT_POZ_IN_FILE = 2;
    //private static final int AMOUNT_POZ_IN_FILE=3;
    private static final int STORE_POZ_IN_FILE = 3;
//	private static final int DESCRIPTION_POZ_IN_FILE=5;


    public ScannedMaterial(Intent dataFromScanner) {
        String dataFromScanerString = dataFromScanner.getStringExtra("SCAN_RESULT");
        if (dataFromScanerString.contains(";")) {
            String[] scanResult;
            scanResult = dataFromScanerString.split(";");
            this.setIndex(scanResult[INDEX_POZ_IN_FILE]);
            this.setName(scanResult[NAME_POZ_IN_FILE]);
            this.setUnit(scanResult[UNIT_POZ_IN_FILE]);
            //this.setAmount(Double.valueOf(scanResult[AMOUNT_POZ_IN_FILE]));
            this.setStore(scanResult[STORE_POZ_IN_FILE]);
            //this.setDescription(scanResult[DESCRIPTION_POZ_IN_FILE]);
        }

    }


}
