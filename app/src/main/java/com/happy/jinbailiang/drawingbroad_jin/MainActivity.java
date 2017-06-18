package com.happy.jinbailiang.drawingbroad_jin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.happy.jinbailiang.drawingbroad_jin.databinding.ActivityMainBinding;
import com.happy.jinbailiang.lianghappylife.view.IMyAidlInterface;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_IMAGE = 200;
    private ActivityMainBinding binding;
    private Bitmap bitmap;
    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        com.suyati.telvin.drawingboard.DrawingBoard
        initView();
        AIDL_Test();
    }

    private void AIDL_Test() {
//        Intent intent = new Intent("android.intent.action.AIDLService");
//        Intent intent = new Intent("AIDLService");
//        intent.setPackage("com.happy.jinbailiang.lianghappylife.service");
        Intent intent = new Intent();
        intent.setAction("android.intent.action.AIDLService");
//        intent.setPackage(this.getPackageName());
        Intent eintent = new Intent(createExplicitFromImplicitIntent(MainActivity.this, intent));
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IMyAidlInterface iMyAidlInterface  = IMyAidlInterface.Stub.asInterface(service);
                try {
                    String a = iMyAidlInterface.aidl_change("haha");
                    Toast.makeText(MainActivity.this,a,Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(eintent ,conn, Context.BIND_AUTO_CREATE);
        startService(eintent);
    }

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        //Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        //Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        //Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        //Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        //Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }



    private void initView() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg);
        binding.drawingBoard.setImageBitmap(bitmap);
        binding.green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawingBoard.setPenColor1(Color.GREEN);
            }
        });
        binding.yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawingBoard.setPenColor1(Color.YELLOW);
            }
        });
        binding.blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawingBoard.setPenColor1(Color.BLUE);
            }
        });
  /*      binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawingBoard.saveAsImageFile("abc", true);
            }
        });*/

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.drawingBoard.clearBoard();
            }
        });

        binding.move.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                binding.drawingBoard.setInterapt(!binding.drawingBoard.isInterapt());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (binding.drawingBoard.isInterapt()) {
                        binding.move.setBackground(getDrawable(R.mipmap.pen));
                    } else {
                        binding.move.setBackground(getDrawable(R.mipmap.move));
                    }
                }
            }
        });
        /**
         * 相册
         */
        binding.album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromAlbum();
            }
        });
    }

    protected void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型  
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null ){
            Uri uri = data.getData();
            binding.drawingBoard.setImageURI(uri);
        }
    }

    @Override
    protected void onDestroy() {
        if(conn !=null){
            unbindService(conn);
        }
        super.onDestroy();
    }
}
/**
 * Customize your DrawingBoard
 * <p>
 * To change the board color
 * <p>
 * drawingBoard.setCanvasColor(android.R.color.white);
 * To change the pen color
 * <p>
 * drawingBoard.setPenColor(R.color.colorPrimary);
 * To change the pen width
 * <p>
 * drawingBoard.setPenWidth(6f);
 * ##DrawingBoard Actions
 * <p>
 * To clear the drawing on the board
 * <p>
 * drawingBoard.clearBoard();
 * To save as image file
 * <p>
 * String baseFilePath = "ProjectName/Images"; // The folder name in which your file has to be saved.
 * String fileName = "darwboard1.png"; // Your filename
 * Boolean isShownInGallery = true; // Whether to be shown in Android default gallery.
 * <p>
 * drawingBoard.setBaseFilePath(baseFilePath);
 * drawingBoard.saveAsImageFile(fileName,isShownInGallery);
 * Note: Add WRITE_EXTERNAL_STORAGE permission in manifest file.
 * <p>
 * To get Bitmap of current drawing on the board
 * <p>
 * drawingBoard.getBitMapSignature();
 * #Authors and Contributors
 * <p>
 * DrawingBoard-Android is developed by Suyati Technologies. It is written and maintained by their Android Development team.
 * <p>
 * Author:
 * <p>
 * Telvin Philips Mathew (@telvinphilipsmathew)
 * #Support or Contact
 * <p>
 * Have Suggestions? Want to give us something to do? Contact us at : support@suyati.com
 */


