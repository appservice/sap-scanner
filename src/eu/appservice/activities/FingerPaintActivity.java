package eu.appservice.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import eu.appservice.R;

//import android.graphics.EmbossMaskFilter;
//import android.graphics.MaskFilter;


@TargetApi(Build.VERSION_CODES.BASE)
public class FingerPaintActivity extends ActionBarActivity// implements ColorPickerDialog.OnColorChangedListener  //Graphics
{
    private String SIGN_DIR_NAME;     //implements ColorPickerDialog.OnColorChangedListener

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set full screen view
    /*    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
     /*   requestWindowFeature(Window.FEATURE_NO_TITLE);
*/
        setContentView(new MyView(this));

        SIGN_DIR_NAME=    Environment
                .getExternalStorageDirectory()
                +File.separator
                +getApplicationContext().getString(R.string.main_folder)
                +File.separator
                +getApplicationContext().getString(R.string.signatures_folder);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF0000FF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        mPaint.setStrokeWidth(4);

/*        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                0.4f, 6, 3.5f);*/
        //  mPaint.setMaskFilter(mEmboss);

        //  mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }

    private Paint mPaint;
   // private MaskFilter mEmboss;
    //private MaskFilter  mBlur;
    private Bitmap mBitmap;


  /*  public void colorChanged(int color) {
        mPaint.setColor(color);
    }*/

    @TargetApi(Build.VERSION_CODES.BASE)
    public class MyView extends View {

        // private static final float MINP = 0.25f;
        //    private static final float MAXP = 0.75f;

        //  private Bitmap  mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;

      
        
/*        public Bitmap getMBitmap() {
            return mBitmap;
		}*/

        public MyView(Context c) {
            super(c);
            initial();

        }

        @SuppressWarnings("deprecation")
        private void initial() {

            int windowWidth;
            int windowHeight;
            Point size = new Point();
            WindowManager w = getWindowManager();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                w.getDefaultDisplay().getSize(size);
                windowWidth = size.x;
                windowHeight = size.y;
            } else {
                Display d = w.getDefaultDisplay();
                windowWidth = d.getWidth();
                windowHeight = d.getHeight();
            }

            mBitmap = Bitmap.createBitmap(windowWidth, windowHeight, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.BLACK);
            mCanvas = new Canvas(mBitmap);
          /*  */
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);


            mBitmapPaint.setColor(Color.WHITE);
            mCanvas.drawRect(1, 1, mBitmap.getWidth() - 1, mBitmap.getHeight() - 1, mBitmapPaint);
            //mCanvas.drawLine(50,50,400,400,mBitmapPaint);


        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFCCCCCC);
            //  canvas.drawColor(Color.RED);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);


            // canvas.drawRect(1,1,mBitmap.getWidth()-1, mBitmap.getHeight()-1,mBitmapPaint);


            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 1;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }

    //  private static final int COLOR_MENU_ID = Menu.FIRST;
    // private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    //  private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // menu.add(0, COLOR_MENU_ID, 0, "Kolor").setShortcut('3', 'c');
        //  menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
        // menu.add(0, BLUR_MENU_ID, 0, "Smuż").setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, "Wyczyść").setShortcut('5', 'z');
        menu.add(0, SRCATOP_MENU_ID, 0, "Zapisz").setShortcut('5', 'z');

        /****   Is this the mechanism to extend with filter effects?
         Intent intent = new Intent(null, getIntent().getData());
         intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
         menu.addIntentOptions(
         Menu.ALTERNATIVE, 0,
         new ComponentName(this, NotesList.class),
         null, intent, 0, null);
         *****/
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {

            case ERASE_MENU_ID:

                setContentView(new MyView(this));

                return true;
            case SRCATOP_MENU_ID:

                String signatureResoult = "";

                try {
                    signatureResoult = saveToFile();
                } catch (IOException e) {
                    
                    e.printStackTrace();
                }
                Intent result = new Intent();
                result.putExtra("SIGNATURE_RESULT", signatureResoult);
                setResult(RESULT_OK, result);
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private String saveToFile() throws IOException {

        java.util.Calendar now = java.util.GregorianCalendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy '_'HHmmss");
        String fileName = dateFormat.format(now.getTime()) + ".png";
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
       /*     File singnFolder = new File(Environment.getExternalStorageDirectory(), SIGN_DIR_NAME);
            if (!singnFolder.exists()) {
                if (!singnFolder.mkdir()) {  //creating new folder with signatures
                    Toast.makeText(getApplicationContext(), "Wystąpił problem z utworzeniem folderu "
                            + SIGN_DIR_NAME, Toast.LENGTH_LONG).show();
                }
            }*/

            File signaturesFolder=new File(SIGN_DIR_NAME);

            File plik = new File(signaturesFolder, fileName);
            if (!plik.exists())
                if(!plik.createNewFile()) //creating file
                    Log.w("creating file", "can't create file "+fileName);

            FileOutputStream fos = new FileOutputStream(plik, true);

            mBitmap.compress(CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();
        }
        return fileName;
    }


}



