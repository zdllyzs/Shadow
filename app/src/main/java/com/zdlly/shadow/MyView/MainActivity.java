package com.zdlly.shadow.MyView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.zdlly.shadow.ImageSelector.ListActivity;
import com.zdlly.shadow.R;
import com.zdlly.shadow.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private File outputImage;
    private FrameLayout pictureFrame;

    private Button color_choose;
    private Button picture_take;
    private Button picture_choose;
    private ColorPickerDialog colorPickerDialog;
    private AppCompatImageView pictureImageView;
    private float beginX = 0;
    private float beginY = 0;
    private float moveX;
    private float moveY;
    private boolean isNewRect;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Button my_picture_choose;
    private MyShapeView coverView;
    private MyShapeView oldCoverView;

    private static final String TAG = "MainActivity";
    private Button select_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        RadioGroup choose = (RadioGroup) findViewById(R.id.choose);
        color_choose = (Button) findViewById(R.id.color_choose);
        color_choose.setOnClickListener(this);
        picture_take = (Button) findViewById(R.id.picture_take);
        picture_take.setOnClickListener(this);
        picture_choose = (Button) findViewById(R.id.picture_choose);
        picture_choose.setOnClickListener(this);
        my_picture_choose = (Button) findViewById(R.id.my_picture_choose);
        my_picture_choose.setOnClickListener(this);
        pictureImageView = (AppCompatImageView) findViewById(R.id.picture);
        pictureFrame = (FrameLayout) findViewById(R.id.picture_frame);
        select_text = (Button) findViewById(R.id.select_text);
        select_text.setOnClickListener(this);
        picture_take.setVisibility(View.GONE);
        picture_choose.setVisibility(View.GONE);
        my_picture_choose.setVisibility(View.GONE);

        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this);
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                coverView.setColor(color);
                coverView.invalidate();
            }
        });

        choose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.color:
                        picture_take.setVisibility(View.GONE);
                        picture_choose.setVisibility(View.GONE);
                        my_picture_choose.setVisibility(View.GONE);
                        color_choose.setVisibility(View.VISIBLE);
                        break;
                    case R.id.photo:
                        color_choose.setVisibility(View.GONE);
                        picture_take.setVisibility(View.VISIBLE);
                        picture_choose.setVisibility(View.VISIBLE);
                        my_picture_choose.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        pictureImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (!isNewRect) {
                    oldCoverView = coverView;
                    pictureFrame.removeView(oldCoverView);
                    Log.d(TAG, "onTouch: view removed!");
                }
                coverView = new MyShapeView(MainActivity.this);
                Log.d(TAG, "onTouch: view created!");
                pictureFrame.addView(coverView);
                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        isNewRect = false;
                        Log.d(TAG, "onTouch: begin down");
                        beginX = event.getX();
                        beginY = event.getY();
                        coverView.setRectLeft(beginX);
                        coverView.setRectTop(beginY);
                        coverView.setRectDown(beginY);
                        coverView.setRectRight(beginX);

                        Log.d(TAG, "onTouch: left" + beginX);
                        Log.d(TAG, "onTouch: top" + beginY);
                        coverView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        isNewRect = false;
                        Log.d(TAG, "onTouch: begin move");
                        moveX = event.getX();
                        moveY = event.getY();
                        Log.d(TAG, "onTouch: beginx" + beginX);
                        Log.d(TAG, "onTouch: beginy" + beginY);

                        coverView.setRectTop(beginY);
                        coverView.setRectLeft(beginX);
                        coverView.setRectRight(moveX);
                        coverView.setRectDown(moveY);
                        Log.d(TAG, "onTouch: recleft" + coverView.getRectLeft());
                        Log.d(TAG, "onTouch: recright" + coverView.getRectRight());
                        Log.d(TAG, "onTouch: rectop" + coverView.getRectTop());
                        Log.d(TAG, "onTouch: recdown" + coverView.getRectDown());
                        coverView.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        isNewRect = true;
                        Log.d(TAG, "onTouch: begin up");
                        coverView.setCanClick(true);
                        coverView.setRectTop(beginY);
                        coverView.setRectLeft(beginX);
                        coverView.setRectRight(moveX);
                        coverView.setRectDown(moveY);
                        coverView.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_choose:
                colorPickerDialog.setHexaDecimalTextColor(Color.parseColor("#ffffff"));
                colorPickerDialog.show();
                break;
            case R.id.picture_take:
                outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileUtils.startActionCapture(this, outputImage, TAKE_PHOTO);
                break;
            case R.id.picture_choose:
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
                intent1.setType("image/*");
                startActivityForResult(intent1, CHOOSE_PHOTO);
                break;
            case R.id.my_picture_choose:
                Intent intent2 = new Intent(MainActivity.this, ListActivity.class);
                intent2.putExtra("type", 1);
                startActivityForResult(intent2, ListActivity.RESULT_OK);
                break;
            case R.id.select_text:
                Intent intent3=new Intent(MainActivity.this,TextSelectActivity.class);
                startActivity(intent3);// TODO: 2017.11.24 新activity
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (TAKE_PHOTO):
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = BitmapFactory.decodeFile(outputImage.getPath());
                    pictureImageView.setImageBitmap(bitmap);
                }
                break;
            case (CHOOSE_PHOTO): {
                String imagePath = null;
                if (data != null) {
                    Uri uri = data.getData();
                    if (DocumentsContract.isDocumentUri(this, uri)) {
                        String docId = DocumentsContract.getDocumentId(uri);
                        assert uri != null;
                        if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                            String id = docId.split(":")[1];
                            String selection = MediaStore.Images.Media._ID + "=" + id;
                            imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                        } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public/public_downloads"), Long.valueOf(docId));
                            imagePath = getImagePath(contentUri, null);
                        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                            imagePath = getImagePath(uri, null);

                        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                            imagePath = uri.getPath();
                        }
                        displayImage(imagePath);
                    }
                }

                break;
            }
            case (10001): {
                if (data == null) return;
                ArrayList<String> filepath = data.getStringArrayListExtra("filePath");
                if (filepath != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filepath.get(0));
                    pictureImageView.setImageBitmap(bitmap);
                }


            }
            default:
                break;
        }
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            pictureImageView.setImageBitmap(bitmap);
        }

    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


}
