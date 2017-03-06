package com.example.mord.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Notes extends AppCompatActivity {

    private String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        filename = (String) bundle.get("termTitle") + (String) bundle.get("courseTitle");
        verifyStoragePermissions(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        ImageView a = (ImageView) findViewById(R.id.ivNote);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString()+"/wgunote";
        File file = new File(extStorageDirectory+"/", filename+".png");
        if (file.exists()) {
            Uri bitmapUri = Uri.fromFile(file);
            Bitmap bitmap = null;
            try {
                 bitmap = getBitmapFromUri(bitmapUri);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            a.setImageBitmap(bitmap);
        }
    }


    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString()+"/wgunote";
        File dir = new File(extStorageDirectory);
        OutputStream outStream = null;
        if(!dir.exists()){
            File newDir = new File(extStorageDirectory+"/");
            newDir.mkdirs();
        }
                    // String temp = null;
        File file = new File(extStorageDirectory+"/", filename+".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory+"/", filename+".png");

        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    private void selectImage() {


        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};


        AlertDialog.Builder builder = new AlertDialog.Builder(Notes.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 7);

                } else if (options[item].equals("Choose from Gallery"))

                {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageView a = (ImageView) findViewById(R.id.ivNote);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 7) {
                //h=0;
               // File f = new File(Environment.getExternalStorageDirectory().toString());


//                for (File temp : f.listFiles()) {
//
//                    if (temp.getName().equals("temp.jpg")) {
//
//                        f = temp;
//                        File photo = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
//                        //pic = photo;
//                        break;
//
//                    }
//
//                }

//                try {
//
//                    Bitmap bitmap;
//
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//
//
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//
//                            bitmapOptions);
//
//
//                    a.setImageBitmap(bitmap);
//
//
//                    String path = android.os.Environment
//
//                            .getExternalStorageDirectory()
//
//                            + File.separator
//
//                            + "Phoenix" + File.separator + "default";
//                    //p = path;
//
//                    f.delete();
//
//                    OutputStream outFile = null;
//
//                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
//
//                    try {
//
//                        outFile = new FileOutputStream(file);
//
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
//                        //pic=file;
//                        outFile.flush();
//
//                        outFile.close();
//
//
//                    } catch (FileNotFoundException e) {
//
//                        e.printStackTrace();
//
//                    } catch (IOException e) {
//
//                        e.printStackTrace();
//
//                    } catch (Exception e) {
//
//                        e.printStackTrace();
//
//                    }
//
//                } catch (Exception e) {
//
//                    e.printStackTrace();
//
//                }

                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    savebitmap(bitmap);
                    a.setImageBitmap(bitmap);

            } else if (requestCode == 2) {


                Uri selectedImage = data.getData();
                // h=1;
                //imgui = selectedImage;
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();

                int columnIndex = c.getColumnIndex(filePath[0]);

                String picturePath = c.getString(columnIndex);

                c.close();

                Log.w("path of image ", picturePath + "");
                Bitmap test = null;
try {
    test = getBitmapFromUri(selectedImage);
}
catch (IOException e) {
    e.printStackTrace();
}
//                a.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                savebitmap(test);
                Matrix matrix = new Matrix();

                matrix.postRotate(90);
                int nh = (int) ( test.getHeight() * (512.0 / test.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(test, 512, nh, true);
                Bitmap rotated = Bitmap.createBitmap(scaled, 0, 0, scaled.getWidth(), scaled.getHeight(), matrix, true );

                a.setImageBitmap(rotated);
            }

        }
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}