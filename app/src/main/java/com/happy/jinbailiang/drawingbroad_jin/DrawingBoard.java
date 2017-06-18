package com.happy.jinbailiang.drawingbroad_jin;

/**
 * Created by Administrator on 2017/6/1.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class DrawingBoard extends android.support.v7.widget.AppCompatImageView {
    private static final float STROKE_WIDTH = 6.0F;
    private static final float HALF_STROKE_WIDTH = 3.0F;
    private final RectF dirtyRect = new RectF();
    boolean isToched = false;
    private Paint paint = new Paint();
    private Paint paint1 = new Paint();
    private Path path = new Path();
    private float lastTouchX;
    private float lastTouchY;
    private Context mContext;
    private Boolean isSaveInGallery = Boolean.valueOf(false);
    private int bgColor = 17170443;
    private String baseFilePath = "/drawingboard";
    private Canvas canvas;

    public String getBaseFilePath() {
        return this.baseFilePath;
    }

    public void setBaseFilePath(String baseFilePath) {
        this.baseFilePath = baseFilePath;
    }

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.RED);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeWidth(6.0F);
//        this.setBackgroundColor(this.getResources().getColor(17170443));
    }

    /*  public void setPenColor(int color) {
          int myColor = this.getResources().getColor(color);
          this.paint.setColor(myColor);
          this.invalidate();
      }*/

    public void setPenColor(int color) {
        this.paint.setAntiAlias(true);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeWidth(6.0F);
        if (canvas != null) {
            canvas.drawPath(this.path, this.paint);
        }
        this.invalidate();
    }

    public Bitmap getBitmap() {
        return ((BitmapDrawable) getDrawable()).getBitmap();
    }

    public void setPenColor1(int color) {
        setImageBitmap(getBitmap());
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(color);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeWidth(6.0F);

        this.invalidate();
    }

    public void setStyle(Paint.Style style) {
        this.paint.setStyle(style);
        this.invalidate();
    }

    public void setStrokeJoin(Paint.Join join) {
        this.paint.setStrokeJoin(join);
        this.invalidate();
    }

    public void setPenWidth(Float width) {
        this.paint.setStrokeWidth(width.floatValue());
        this.invalidate();
    }

    public void setCanvasColor(int bgColor) {
        this.bgColor = bgColor;
        this.setBackgroundColor(this.getResources().getColor(bgColor));
        this.invalidate();
    }

    public int getCanvasColor() {
        return this.bgColor;
    }

    public Bitmap getBitMapSignature() {
        Bitmap bitmap = null;
        if (bitmap == null) {
            int canvas = this.getWidth();
            int height = this.getHeight();
            if (canvas == 0 && height == 0) {
                canvas = 200;
                height = 200;
            }
            bitmap = Bitmap.createBitmap(canvas, height, Bitmap.Config.RGB_565);
        }

        Canvas canvas1 = new Canvas(bitmap);
        this.draw(canvas1);
        return bitmap;
    }

    public void clearBoard() {
        this.path.reset();
        this.invalidate();
        this.isToched = false;
    }

    public boolean isDraw() {
        return this.isToched;
    }

    protected void onDraw(Canvas canvas) {
      /*  this.canvas = canvas;
        this.canvas.drawPath(this.path, this.paint);
        super.onDraw(this.canvas);*/
        this.canvas = canvas;
//        this.canvas.drawBitmap(getBitmap(), getMatrix(), paint);
        super.onDraw(this.canvas);
        this.canvas.drawPath(this.path, this.paint);
    }

    private boolean isInterapt;
    public boolean isInterapt() {
        return isInterapt;
    }

    public void setInterapt(boolean interapt) {
        isInterapt = interapt;
    }
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(isInterapt);
        if(!isInterapt()){
            return true;
        }

        float eventX = event.getX();
        float eventY = event.getY();
        this.isToched = true;
        getParent().requestDisallowInterceptTouchEvent(isInterapt);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.path.moveTo(eventX, eventY);
                this.lastTouchX = eventX;
                this.lastTouchY = eventY;
                return true;
            case 1:
            case 2:
                this.resetDirtyRect(eventX, eventY);
                int historySize = event.getHistorySize();

                for (int i = 0; i < historySize; ++i) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    this.expandDirtyRect(historicalX, historicalY);
                    this.path.lineTo(historicalX, historicalY);
                }

                this.path.lineTo(eventX, eventY);
                this.invalidate((int) (this.dirtyRect.left - 3.0F), (int) (this.dirtyRect.top - 3.0F), (int) (this.dirtyRect.right + 3.0F), (int) (this.dirtyRect.bottom + 3.0F));
                this.lastTouchX = eventX;
                this.lastTouchY = eventY;
                return true;
            default:
                return true;
        }
    }


    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < this.dirtyRect.left) {
            this.dirtyRect.left = historicalX;
        } else if (historicalX > this.dirtyRect.right) {
            this.dirtyRect.right = historicalX;
        }

        if (historicalY < this.dirtyRect.top) {
            this.dirtyRect.top = historicalY;
        } else if (historicalY > this.dirtyRect.bottom) {
            this.dirtyRect.bottom = historicalY;
        }

    }

    private void resetDirtyRect(float eventX, float eventY) {
        this.dirtyRect.left = Math.min(this.lastTouchX, eventX);
        this.dirtyRect.right = Math.max(this.lastTouchX, eventX);
        this.dirtyRect.top = Math.min(this.lastTouchY, eventY);
        this.dirtyRect.bottom = Math.max(this.lastTouchY, eventY);
    }

    public boolean saveAsImageFile(String fileName, Boolean isSaveInGallery) {
        this.isSaveInGallery = isSaveInGallery;
        SaveTask task = new SaveTask();
        task.execute(new String[]{fileName});
        return true;
    }

    public static void addImageToGallery(String filePath, Context context) {
        ContentValues values = new ContentValues();
        values.put("datetaken", Long.valueOf(System.currentTimeMillis()));
        values.put("mime_type", "image/jpeg");
        values.put("_data", filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private class SaveTask extends AsyncTask<String, String, String> {
        Boolean status;
        Boolean isFileException;
        Boolean is;
        Bitmap drawnPicture;
        File file;
        ProgressDialog progressDialog;

        private SaveTask() {
            this.isFileException = Boolean.valueOf(false);
            this.file = null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.drawnPicture = DrawingBoard.this.getBitMapSignature();
            this.status = Boolean.valueOf(false);
            this.progressDialog = new ProgressDialog(DrawingBoard.this.mContext);
            this.progressDialog.setMessage("Saving");
            this.progressDialog.show();
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }

            if (DrawingBoardEventHandler.mListner != null) {
                DrawingBoardEventHandler.mListner.saveEvent((Object) null, this.status);
            }

            if (this.status.booleanValue()) {
                Toast.makeText(DrawingBoard.this.mContext, "Saved.", Toast.LENGTH_SHORT).show();
                if (DrawingBoard.this.isSaveInGallery.booleanValue()) {
                    DrawingBoard.addImageToGallery(this.file.getPath(), DrawingBoard.this.mContext);
                }
            }

        }

        protected String doInBackground(String... strings) {
            FileOutputStream out = null;
            String fname = strings[0];

            try {
                String e = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(e + DrawingBoard.this.baseFilePath);
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }

                this.file = new File(myDir, fname);
                if (this.file.exists()) {
                    this.file.delete();
                }

                out = new FileOutputStream(this.file);
                this.drawnPicture.compress(Bitmap.CompressFormat.PNG, 100, out);
                this.status = Boolean.valueOf(true);
            } catch (Exception var14) {
                var14.printStackTrace();
                this.status = Boolean.valueOf(false);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException var13) {
                    var13.printStackTrace();
                }

            }

            return null;
        }
    }
}
