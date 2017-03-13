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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AssessmentNotes extends AppCompatActivity {

    private String filename;
    private String courseTitle;
    private String termTitle;
    private String action;
    private Assessment thisAss;
    public static final int EDITOR_REQUEST_CODE = 191;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_share:
                shareIt();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        filename = (String) bundle.get("assessmentId");
        termTitle = (String) bundle.get("termTitle");
        courseTitle = (String) bundle.get("courseTitle");
        verifyStoragePermissions(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TextView tvAssNote = (TextView) findViewById(R.id.tvAssessmentNote);
        DBProvider provider = new DBProvider(this);
        provider.open();
        thisAss = provider.getAssessment(filename);
        provider.close();
        if(thisAss.getNote() == null)
            action = Intent.ACTION_INSERT;
        else {
            action = Intent.ACTION_EDIT;
            tvAssNote.setText(thisAss.getNote());
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        tvAssNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });
        ImageView a = (ImageView) findViewById(R.id.ivAssessmentNote);

        final String extStorageDirectory = Environment.getExternalStorageDirectory().toString()+"/wgunote";
        final File file = new File(extStorageDirectory+"/", filename+".png");
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
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri =  Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String mime = "*/*";
                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                    if (mimeTypeMap.hasExtension(
                            mimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                        mime = mimeTypeMap.getMimeTypeFromExtension(
                                mimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                    intent.setDataAndType(uri,mime);
                    startActivity(intent);
                }
            });
        }

    }
    public void shareIt() {
        final String extStorageDirectory = Environment.getExternalStorageDirectory().toString()+"/wgunote";
        final File file = new File(extStorageDirectory+"/", filename+".png");
        final TextView tvNote = (TextView) findViewById(R.id.tvNote);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        String shareBody = tvNote.getText().toString();
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
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


        AlertDialog.Builder builder = new AlertDialog.Builder(AssessmentNotes.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 7);

                } else if (options[item].equals("Choose from Gallery"))

                {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    public void addNote(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.content_addnote, null);
        final TextView tvAssNote = (TextView) findViewById(R.id.tvAssessmentNote);
        builder.setView(dialogView);
        EditText dvet = (EditText) dialogView.findViewById(R.id.etDv);
        if(!tvAssNote.getText().toString().equals("Click here to add notes")) {
        dvet.setText(tvAssNote.getText().toString());
            builder.setTitle("Edit note");}
        else {
            builder.setTitle("Add notes");
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText dvEt = (EditText) dialogView.findViewById(R.id.etDv);
                tvAssNote.setText(dvEt.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();


    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ImageView a = (ImageView) findViewById(R.id.ivAssessmentNote);
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
    private void finishedEditing() {
                TextView etNote = (TextView) findViewById(R.id.tvAssessmentNote);
                thisAss.setNote(etNote.getText().toString());
                DBProvider provider = new DBProvider(this);
                provider.open();
                Toast.makeText(this, filename, Toast.LENGTH_LONG).show();
                provider.update(thisAss.getId(), thisAss);
                setResult(RESULT_OK);
                provider.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
        finishedEditing();
        Intent intent = new Intent(this, AssessmentDetail.class);
        Bundle bundle = new Bundle();
        bundle.putString("termTitle", termTitle);
        bundle.putString("courseTitle", courseTitle);
        bundle.putString("assessmentId", thisAss.getId());

        intent.putExtras(bundle);
        startActivity(intent);

    }
}