package eu.appservice.activities.tasks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.apache.poi.hssf.eventusermodel.AbortableHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFUserException;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import eu.appservice.Material;
import eu.appservice.databases.MaterialsDbOpenHelper;
import eu.appservice.logfile.InterfaceObservable;
import eu.appservice.logfile.InterfaceObserver;


/**
 * Created by Lukasz on 13.10.13.
 * ﹕ SAP Skanner
 */
public class ImportMaterialsEventListener extends AbortableHSSFListener implements HSSFListener ,InterfaceObservable{

    private SSTRecord sstrec;
    private LabelSSTRecord lrec;
    private Context myContext;
    private MaterialsDbOpenHelper db;
    private Material material;
    private SQLiteDatabase sqldb;
    private int lastNumRow;
    private int sheetNumber;
    private List<InterfaceObserver> observers;


    public ImportMaterialsEventListener(Context context) {
        this.myContext = context;
        this.sheetNumber = 0;
        this.observers=new ArrayList<InterfaceObserver>();


    }

    /**
     * This method listens for incoming records and handles them as required.
     *
     * @param record The record that was found while reading.
     */
    public void processRecord(Record record) {

    }

    @Override
    public short abortableProcessRecord(Record record) throws HSSFUserException {
        //Log.i("recordSize",String.valueOf(record.getRecordSize()));


        switch (record.getSid()) {
            // the BOFRecord can represent either the beginning of a sheet or the workbook
            case BOFRecord.sid:
                BOFRecord bof = (BOFRecord) record;



                if (bof.getType() == bof.TYPE_WORKBOOK) {
                    System.out.println("Encountered workbook");
                    // assigned to the class level member
                } else if (bof.getType() == bof.TYPE_WORKSHEET) {
                    ++sheetNumber;
                    System.out.println("Encountered sheet reference");
                    System.out.println(sheetNumber);

                    if(sheetNumber==1){
                        db = new MaterialsDbOpenHelper(myContext);
                        db.dropTableCpglStore();
                        sqldb = db.getWritableDatabase();
                        sqldb.beginTransaction();


                    }

                }
                break;


            case BoundSheetRecord.sid:

                BoundSheetRecord bsr = (BoundSheetRecord) record;

              //  System.out.println("New sheet named: " + bsr.getSheetname());
                break;
/*                case RowRecord.sid:
                    RowRecord rowrec = (RowRecord) record;
                    System.out.println("Row found, first column at "
                            + rowrec.getFirstCol() + " last column at " + rowrec.getLastCol());
                    break;*/

            case SSTRecord.sid:
                //if(sheetNumber==1)
                this.sstrec = (SSTRecord) record;

                break;
            case LabelSSTRecord.sid:
                if (sheetNumber == 1) {
                    lrec = (LabelSSTRecord) record;


                    if (lrec.getRow() > 0) {


                        switch (lrec.getColumn()) {


                            case 0:
                                material = new Material();
                                material.setIndex(sstrec.getString(lrec.getSSTIndex()).getString());
                                break;
                            case 1:
                                material.setName(sstrec.getString(lrec.getSSTIndex()).getString().replace(";", ",").replace("�", "ó"));
                                break;
                            case 2:
                                material.setUnit(sstrec.getString(lrec.getSSTIndex()).getString());
                                break;
                            case 3:
                                closeDatabase();
                                throw new HSSFUserException("Kolumna 4 musi zawierać ilości!\nSprawdź kolumnę 4 wiersz: "+(getNumberRow()+1));

                            case 4:
                                material.setStore(sstrec.getString(lrec.getSSTIndex()).getString());
                                //System.out.println(lrec.getRow() + ": " + material.toString());
                                long rowNumberInDatabase=db.addMaterialsFromExcel(material, sqldb);
                                if(rowNumberInDatabase==-1){
                                    closeDatabase();


                                    throw new HSSFUserException();
                                    //return 1;
                                }

                                break;
                            default:

                                break;

                        }
                    }
                }

                break;
            case NumberRecord.sid:
                if (sheetNumber == 1) {
                    NumberRecord numrec = (NumberRecord) record;

                    if (numrec.getColumn() == 3) {
                        material.setAmount(numrec.getValue());
                    }

                    lastNumRow = numrec.getRow() + 1;
                    notifyObservers();


                }
                break;


            case EOFRecord.sid:
                if (sheetNumber == 1) {
                    EOFRecord endOfSheet = (EOFRecord) record;

                    //System.out.println("koniec " + getNumberRow());
                    sqldb.setTransactionSuccessful();
                    sqldb.endTransaction();
                    sqldb.close();
                    db.close();
                    return 1; //brake the function when first sheet is wrote in database
                }
                break;

        }
        return 0;
    }

    private void closeDatabase(){

        sqldb.endTransaction();
        sqldb.close();
        db.close();
    }

    public int getNumberRow() {


        return lastNumRow;
    }

  /*  public int getLastNumRow(){
        return lastNumRow;
    }*/
    @Override
    public void addObserver(InterfaceObserver iObserver) {
        observers.add(iObserver);

    }

    @Override
    public void deleteObserver(InterfaceObserver iObserver) {
        observers.remove(iObserver);
    }

    @Override
    public void notifyObservers() {
        notifyObservers(null);

    }

    @Override
    public void notifyObservers(Object arg) {
        for(InterfaceObserver o:observers){
            o.update(this,arg);
        }

    }
}