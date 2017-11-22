package com.zdlly.shadow.MyView;

import android.Manifest;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.zdlly.shadow.ImageSelector.ListActivity;
import com.zdlly.shadow.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_sides;
    private MyShapeView my_view;
    private File outputImage;

    private Button color_choose;
    private Button picture_take;
    private Button picture_choose;
    private ColorPickerDialog colorPickerDialog;
    private AppCompatImageView pictureImageView;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Button my_picture_choose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        edit_sides = (EditText) findViewById(R.id.edit_sides);
        Button submit = (Button) findViewById(R.id.submit);
        my_view = (MyShapeView) findViewById(R.id.my_view);
        my_view.setImageDrawable(getDrawable(R.mipmap.ic_launcher));
        submit.setOnClickListener(this);
        RadioGroup choose = (RadioGroup) findViewById(R.id.choose);
        color_choose = (Button) findViewById(R.id.color_choose);
        color_choose.setOnClickListener(this);
        picture_take = (Button) findViewById(R.id.picture_take);
        picture_take.setOnClickListener(this);
        picture_choose = (Button) findViewById(R.id.picture_choose);
        picture_choose.setOnClickListener(this);
        my_picture_choose = (Button) findViewById(R.id.my_picture_choose);
        my_picture_choose.setOnClickListener(this);
        pictureImageView.findViewById(R.id.picture);
        picture_take.setVisibility(View.GONE);
        picture_choose.setVisibility(View.GONE);
        my_picture_choose.setVisibility(View.GONE);

        colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this);
        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                my_view.setColor(color);
                my_view.invalidate();
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

    }

    @Override
    public void onClick(View v) {
        my_view.setIsReload(1);
        switch (v.getId()) {
            case R.id.submit:
                submit();
                break;
            case R.id.color_choose:
                my_view.setMode1(MyShapeView.COLOR);
                colorPickerDialog.setHexaDecimalTextColor(Color.parseColor("#ffffff"));
                colorPickerDialog.show();
                break;
            case R.id.picture_take:
                my_view.setMode1(MyShapeView.COLOR);
                outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri imageuri = Uri.fromFile(outputImage);

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);

                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.picture_choose:
                my_view.setMode1(MyShapeView.COLOR);

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
                Intent intent1 = new Intent("android.intent.action.GET_CONTENT");
                intent1.setType("image/*");
                startActivityForResult(intent1, CHOOSE_PHOTO);
                break;
            case R.id.my_picture_choose:
                my_view.setMode1(MyShapeView.COLOR);
                Intent intent2 = new Intent(MainActivity.this, ListActivity.class);
                intent2.putExtra("type", 1);
                startActivityForResult(intent2, ListActivity.RESULT_OK);
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


    private void submit() {
        String sides = edit_sides.getText().toString().trim();
        if (TextUtils.isEmpty(sides)) {
            Toast.makeText(this, "sides不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        my_view.setIsReload(1);
        my_view.setmSides(Integer.parseInt(sides));
        my_view.invalidate();
    }
}
