package com.zdlly.shadow.ImageSelector;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zdlly.shadow.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListActivity extends Activity {
    TextView _title_text,_r_text;
    RelativeLayout _title_bar;
    RecyclerView _rec_image_list;
    List<ImageBean> list;
    public static  final int RESULT_OK=10001;
    public int type=1;
    MyAdpter adpter;

    static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE,  //读取权限
    };
    private static final int REQUEST_CODE = 0;//请求码

    private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_selector);
        CheckPermission checkPermission = new CheckPermission(this);
        type=getIntent().getIntExtra("type",1);
        initview();
        if (checkPermission.permissionSet(PERMISSION)) {
            startPermissionActivity();
        }
        else
        {
            initData();
        }

    }

    private void initData() {

         _rec_image_list.setLayoutManager(new LinearLayoutManager(ListActivity.this));

        scanPhoto();

    }


    private void startPermissionActivity() {
        PermissionActivity.startActivityForResult(this, REQUEST_CODE, PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionActivity.PERMISSION_DENIEG) {
            finish();
        }
        else if (requestCode==RESULT_OK)
        {    if (data==null)return;
            if (data.getStringArrayListExtra("filePath")!=null)
            {
                ArrayList<String> filePath=data.getStringArrayListExtra("filePath");
                if (filePath.size()>0)
                {
                    Intent it=new Intent();
                    it.putStringArrayListExtra("filePath",filePath);
                    setResult(RESULT_OK,it);
                    finish();
                }
            }
        }
        else{ initData();}
    }
    private void initview() {
        _title_text= (TextView) findViewById(R.id.title_text);
        _r_text= (TextView) findViewById(R.id.r_text);
        _title_bar= (RelativeLayout) findViewById(R.id.title_bar);
        _rec_image_list= (RecyclerView) findViewById(R.id.rec_image_list);
        _title_text.setText("选择照片");
        _r_text.setText("取消");
        _r_text.setVisibility(View.VISIBLE);
        _r_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public  void  scanPhoto()
    {

        new Thread()
        {
            @Override
            public void run() {
                Uri imageUri= MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mresolver=ListActivity.this.getContentResolver();
                Cursor mCursor = mresolver.query(imageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

                if(mCursor == null){
                    return;
                }

                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    String parentName = new File(path).getParentFile().getName();

                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }


                handler.sendEmptyMessage(10010);
                mCursor.close();
            }
        }.start();

    }
    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
           if (msg.what==10010)
           {
            if (mGruopMap!=null) {
                list = subGroupOfImage(mGruopMap);
                adpter=new MyAdpter(ListActivity.this);
                _rec_image_list.setAdapter(adpter);
                adpter.setOnItemClickLitener(new OnItemClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        Map<String,Object> map=new HashMap<String, Object>();
                        map.put("fileName",list.get(position).getFolderName());
                        map.put("filelist",mGruopMap.get(list.get(position).getFolderName()));
                        Intent it=new Intent(ListActivity.this, DetailsActivity.class);
                        final SerializableMap myMap=new SerializableMap();
                        myMap.setMap(map);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("map", myMap);
                        it.putExtra("type",type);
                        it.putExtras(bundle);
                        startActivityForResult(it,RESULT_OK);

                    }
                });

            }
           }
        }
    };



    @Nullable
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
        if(mGruopMap.size() == 0){
            return null;
        }
        List<ImageBean> list = new ArrayList<ImageBean>();

        for (Map.Entry<String, List<String>> entry : mGruopMap.entrySet()) {
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片

            list.add(mImageBean);

        }

        return list;

    }
    public interface  OnItemClickListener
    {
        void OnItemClick(View view,int position);

    }

}
