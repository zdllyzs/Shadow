package com.zdlly.shadow.ImageSelector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zdlly.shadow.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailsActivity extends Activity {
    TextView _title_text, _r_text, _left_text, _tv_selector_list_preview, _tv_selector_list_confirm;
    RelativeLayout _title_bar;
    RecyclerView _rec_image_list;
    Map<String, Object> map;
    List<String> fileList;
    MyAdpter2 adpter;
    String filePath = "";
    ArrayList<String> list;
    int type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_selector);
        type = getIntent().getIntExtra("type", 1);
        list = new ArrayList<>();
        initView();
        initData();
    }

    public int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.widthPixels;
    }

    public int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(outMetrics);// 给白纸设置宽高
        return outMetrics.heightPixels;
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap) bundle.get("map");
        _tv_selector_list_preview.setVisibility(View.INVISIBLE);
        if (serializableMap != null) {
            map = serializableMap.getMap();
        }
        _title_text.setText(map.get("fileName").toString());
        fileList = (List<String>) map.get("filelist");
        _rec_image_list.setLayoutManager(new GridLayoutManager(DetailsActivity.this, 3));
        adpter = new MyAdpter2(this);
        _rec_image_list.setAdapter(adpter);
        adpter.setOnItemClickLitener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                filePath = fileList.get(position);
                if ("".equals(filePath) || filePath == null) {
                    Toast.makeText(DetailsActivity.this, "请先选择一张图片", Toast.LENGTH_SHORT).show();
                } else {
                    Intent it = new Intent();
                    ArrayList<String> flielist = new ArrayList<String>();
                    flielist.add(filePath);
                    it.putStringArrayListExtra("filePath", flielist);
                    setResult(ListActivity.RESULT_OK, it);
                    finish();
                }
            }
        });
    }

    private void initView() {
        _title_text = (TextView) findViewById(R.id.title_text);
        _r_text = (TextView) findViewById(R.id.r_text);
        _title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        _rec_image_list = (RecyclerView) findViewById(R.id.rec_image_list);
        _left_text = (TextView) findViewById(R.id.left_text);
        _left_text.setText("相册");
        _left_text.setVisibility(View.VISIBLE);
        _tv_selector_list_preview = (TextView) findViewById(R.id.tv_selector_list_preview);
        _tv_selector_list_confirm = (TextView) findViewById(R.id.tv_selector_list_confirm);
        _left_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        _tv_selector_list_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1) {
                    if ("".equals(filePath) || filePath == null) {
                        Toast.makeText(DetailsActivity.this, "请先选择一张图片", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent it = new Intent();
                        ArrayList<String> flielist = new ArrayList<String>();
                        flielist.add(filePath);
                        it.putStringArrayListExtra("filePath", flielist);
                        setResult(ListActivity.RESULT_OK, it);
                        finish();
                    }
                } else {
                    if (list == null || list.size() == 0) {
                        Toast.makeText(DetailsActivity.this, "请先选择一张图片", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent it = new Intent();
                    it.putStringArrayListExtra("filePath", list);
                    setResult(ListActivity.RESULT_OK, it);
                    finish();
                }
            }
        });

    }

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);

    }

    public interface OnItemSelectkListener {
        void OnItemSelect(View view, int position, boolean isChecked);
    }

}
