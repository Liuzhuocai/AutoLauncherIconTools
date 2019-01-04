package com.auto.liuzuo.autoicon;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    public static final String AUTHORITY = "com.elves.launcher.settings".intern();
    public static final String TABLE_NAME = "favorites";
    private final Uri URI = Uri.parse("content://" +
            AUTHORITY + "/" + TABLE_NAME);
    public static final String SCREEN = "screen";
    public static final String CELLX = "cellX";
    public static final String CELLY = "cellY";
    public static final String INTENT = "intent";
    public static final String CONTAINER = "container";
    public static final String ITEMTYPE = "itemType";
    public static final String RANK = "rank";
    public static final String APPWIDGETID = "appWidgetId";
    public static final String ID = "_id";
    public static final String TAG = "autoIcon";

    public static final String FAVORITE = "favorite";
    public static final String PACKAGENAME = "launcher:packageName";
    public static final String CLASSNAME = "launcher:className";
    public static final String XMLSCREEN = "launcher:screen";
    public static final String X = "launcher:x";
    public static final String Y = "launcher:y";
    public static final String FOLDER = "folder";
    public static final String TITLE = "title";
    public static final String TITLE_XML= "launcher:title";
    String enter ="\r\n";


    public static final int REQUEST_PERMISSION_ALL = 0;

    TreeSet<IconBean> mIconBean = new TreeSet<IconBean>(new Comparator<IconBean>() {
        @Override
        public int compare(IconBean o1, IconBean o2) {
            int i = 100;
            int j = 10;
            return o1.screen*i+o1.cellY*j+o1.cellX-(o2.screen*i+o2.cellY*j+o2.cellX);
        }
    });
    TreeSet<IconBean> mHotseatIconBean = new TreeSet<IconBean>(new Comparator<IconBean>() {
        @Override
        public int compare(IconBean o1, IconBean o2) {
            int i = 100;
            int j = 10;
            return o1.screen*i+o1.cellY*j+o1.cellX-(o2.screen*i+o2.cellY*j+o2.cellX);
        }
    });
    TreeSet<IconBean> mFolderBean = new TreeSet<IconBean>(new Comparator<IconBean>() {
        @Override
        public int compare(IconBean o1, IconBean o2) {
            int i = 100;
            int j = 10;
            return o1.screen*i+o1.cellY*j+o1.cellX-(o2.screen*i+o2.cellY*j+o2.cellX);
        }
    });
    TreeSet<IconBean> mFolderIconBean = new TreeSet<IconBean>(new Comparator<IconBean>() {
        @Override
        public int compare(IconBean o1, IconBean o2) {
            int i = 10;
            int j = 100000;
            return o1.container*j+o1.rank-(o2.container*j+o2.rank);
        }
    });
    public static String[] sAllPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private void checkPermission(){
        List<String> noOkPermissions = new ArrayList<>();

        for (String permission : sAllPermissions) {
            if (ActivityCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_DENIED) {
                noOkPermissions.add(permission);
            }
        }
        if (noOkPermissions.size() <= 0)
            return ;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(noOkPermissions.toArray(new String[noOkPermissions.size()]), REQUEST_PERMISSION_ALL);
        }
    }
   /* private void testSetBadge(int count){
        String method = "setBadge";
        Bundle b = new Bundle();
        b.putInt("count",count);
        Uri uri = Uri.parse("content://com.android.dlauncher.badge/badge");
        Bundle bundle = getContentResolver().call(uri, method, null, b);
        if (bundle != null && bundle.getBoolean("result")) {
            Log.d("lijun", "callTest true");
        } else {
            Log.d("lijun", "callTest false");
        }
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.bottom);
        checkPermission();
        PackageManager packageManager = this.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent, 0);
        for(ResolveInfo info : resolveInfos){
            Log.d("xxxx","packageName="+info.activityInfo.packageName);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = null;
                mIconBean.clear();
                mHotseatIconBean.clear();
                mFolderBean.clear();
                mFolderIconBean.clear();
                //testSetBadge(1);
                try {
                    cursor = getContentResolver().query(URI, null, null, null, null);
                    cursor.moveToFirst();
                    String[] names = cursor.getColumnNames();
                    for(String name : names){
                        Log.d(TAG, "name="+name);
                    }
                    Log.d(TAG, "size=" + cursor.getCount());
                    do {
                        if (cursor != null) {
                            int anInt = cursor.getInt(cursor.getColumnIndex(CONTAINER));
                            int appWidgetId = cursor.getInt(cursor.getColumnIndex(APPWIDGETID));
                            int rank = -1 ;
                            int id = cursor.getInt(cursor.getColumnIndex(ID)) ;
                            String cursorString = cursor.getString(cursor.getColumnIndex(INTENT));

                            if(anInt==-101||appWidgetId > 0){
                                if(anInt==-101){
                                    IconBean iconBean = new IconBean();
                                    if(cursorString!=null){
                                        Intent intent = Intent.parseUri(cursorString, 0);
                                        ComponentName component = intent.getComponent();
                                        iconBean.className = component.getClassName();
                                        iconBean.packageName = component.getPackageName();
                                    }
                                    iconBean.cellX = cursor.getInt(cursor.getColumnIndex(CELLX));
                                    iconBean.cellY = cursor.getInt(cursor.getColumnIndex(CELLY));
                                    iconBean.screen = cursor.getInt(cursor.getColumnIndex(SCREEN));
                                    iconBean.rank = rank ;
                                    iconBean.container = anInt ;
                                    iconBean.id = id ;
                                    mHotseatIconBean.add(iconBean);
                                }

                                continue;
                            }else if(anInt > 0){
                                rank = cursor.getInt(cursor.getColumnIndex(RANK));
                            }
                            IconBean iconBean = new IconBean();
                            if(cursorString!=null){
                                Intent intent = Intent.parseUri(cursorString, 0);
                                ComponentName component = intent.getComponent();
                                iconBean.className = component.getClassName();
                                iconBean.packageName = component.getPackageName();
                            }
                            iconBean.cellX = cursor.getInt(cursor.getColumnIndex(CELLX));
                            iconBean.cellY = cursor.getInt(cursor.getColumnIndex(CELLY));
                            iconBean.screen = cursor.getInt(cursor.getColumnIndex(SCREEN));
                            iconBean.rank = rank ;
                            iconBean.container = anInt ;
                            iconBean.id = id ;
                            if(rank < 0){
                                if(cursor.getInt(cursor.getColumnIndex(ITEMTYPE))==2){
                                    iconBean.title = cursor.getString(cursor.getColumnIndex(TITLE));
                                    mFolderBean.add(iconBean);
                                }else {
                                    mIconBean.add(iconBean);
                                }
                            }else {
                                 mFolderIconBean.add(iconBean) ;
                            }
                            Log.d(TAG, "iconBean=" +iconBean.toString());
                        }
                        Log.d(TAG,"mIconBean.size()="+mIconBean.size());
                    }
                    while (cursor.moveToNext());
                  /*  cursor.moveToNext();
                    for(int i = 0 ;i < 52 ;i++){
                        cursor.moveToNext();
                        if (cursor != null ) {
                            String[] names = cursor.getColumnNames();
                            for (String name : names) {
                                int index = cursor.getColumnIndex(name);
                                Log.d("louzuo342", name + "=" + cursor.getString(index));
                            }
                        }
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                        File file = new File(Environment.getExternalStorageDirectory()+"/default_workspace.xml");
                        initSettings(file);
                        if(mHotseatIconBean.size()>0){
                            File filehs = new File(Environment.getExternalStorageDirectory()+"/dw_hotseat.xml");
                            initHotseatSettings(filehs);
                        }
                        showToast();
                    }
                }
            }
        });
    }
    public void initSettings(final File settings) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(settings);

                    XmlSerializer serializer = Xml.newSerializer();
                    serializer.setOutput(fos, "UTF-8");
                    serializer.startDocument("UTF-8", true);
                    serializer.startTag(null, "config");
                    serializer.startTag(null, "category");
                    serializer.text(enter);
                    for(IconBean bean: mIconBean ){
                        serializer.startTag(null,FAVORITE);//;
                        serializer.attribute(null,PACKAGENAME,bean.packageName);//serializer.text(enter);
                        serializer.attribute(null,CLASSNAME,bean.className);//serializer.text(enter);
                        serializer.attribute(null,XMLSCREEN, String.valueOf(bean.screen));//serializer.text(enter);
                        serializer.attribute(null,X, String.valueOf(bean.cellX));//serializer.text(enter);
                        serializer.attribute(null,Y, String.valueOf(bean.cellY));//serializer.text(enter);
                        serializer.endTag(null,FAVORITE);serializer.text(enter);//serializer.text(enter);
                    }
                    for(IconBean bean: mFolderBean ){
                        serializer.startTag(null,FOLDER);//;
                        serializer.attribute(null,TITLE_XML,bean.title);//serializer.text(enter);
                        serializer.attribute(null,XMLSCREEN, String.valueOf(bean.screen));//serializer.text(enter);
                        serializer.attribute(null,X, String.valueOf(bean.cellX));//serializer.text(enter);
                        serializer.attribute(null,Y, String.valueOf(bean.cellY));//serializer.text(enter);
                        serializer.text(enter);
                        for(IconBean beanIcon: mFolderIconBean ){
                            if(bean.id == beanIcon.container) {
                                serializer.startTag(null, FAVORITE);//;
                                serializer.attribute(null, PACKAGENAME, beanIcon.packageName);//serializer.text(enter);
                                serializer.attribute(null, CLASSNAME, beanIcon.className);//serializer.text(enter);
                                serializer.attribute(null, XMLSCREEN, String.valueOf(beanIcon.screen));//serializer.text(enter);
                                serializer.attribute(null, X, String.valueOf(beanIcon.cellX));//serializer.text(enter);
                                serializer.attribute(null, Y, String.valueOf(beanIcon.cellY));//serializer.text(enter);
                                serializer.endTag(null, FAVORITE);
                                serializer.text(enter);//serializer.text(enter);
                            }
                        }
                        serializer.endTag(null,FOLDER);serializer.text(enter);//serializer.text(enter);
                    }


                    serializer.endTag(null, "category");
                    serializer.endTag(null, "config");
                    serializer.endDocument();
                    serializer.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    public void initHotseatSettings(final File settings) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(settings);

                    XmlSerializer serializer = Xml.newSerializer();
                    serializer.setOutput(fos, "UTF-8");
                    serializer.startDocument("UTF-8", true);
                    serializer.startTag(null, "config");
                    serializer.startTag(null, "category");
                    serializer.text(enter);
                    for(IconBean bean: mHotseatIconBean ){
                        serializer.startTag(null,FAVORITE);//;
                        serializer.attribute(null,PACKAGENAME,bean.packageName);//serializer.text(enter);
                        serializer.attribute(null,CLASSNAME,bean.className);//serializer.text(enter);
                        serializer.attribute(null,XMLSCREEN, String.valueOf(bean.screen));//serializer.text(enter);
                        serializer.attribute(null,X, String.valueOf(bean.cellX));//serializer.text(enter);
                        serializer.attribute(null,Y, String.valueOf(bean.cellY));//serializer.text(enter);
                        serializer.endTag(null,FAVORITE);serializer.text(enter);//serializer.text(enter);
                    }
                    serializer.endTag(null, "category");
                    serializer.endTag(null, "config");
                    serializer.endDocument();
                    serializer.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
    private  void  showToast(){
        Toast.makeText(this,"success",Toast.LENGTH_SHORT).show();
    }
}
