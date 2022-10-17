package com.example.locavoreapp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddOffre extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int GALLERY_REQUEST = 9;
    private static final int CAMERA_REQUEST = 1888;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    String latitude = "";
    String longitude = "";
    EditText textE;
    EditText titreE;
    EditText prixE;
    TextView locationText;
    String titre, text, prix;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mdatabase;
    StorageReference storageReference;
    ImageButton buttonE;
    List<Uri> images;
    SelectedImagesAdapter adapter;
    DeleteSelectedImage delete;
    FirebaseUser user;
    SimpleDateFormat format;
    String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.addOffer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_add_offre);
        textE = (EditText)findViewById(R.id.texte);
        textE.setText(" "+getLifecycle().getCurrentState());
        textE.getText().clear();
        titreE = (EditText)findViewById(R.id.titre);
        titreE.setText(" "+getLifecycle().getCurrentState());
        titreE.getText().clear();
        prixE = (EditText)findViewById(R.id.prix);
        prixE.setText(" "+getLifecycle().getCurrentState());
        prixE.getText().clear();
        locationText = (TextView) findViewById(R.id.locationText);
        buttonE = (ImageButton)findViewById(R.id.addimage);
        mdatabase = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        images = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();

        delete = new DeleteSelectedImage() {
            @Override
            public void setClick(int a) {
                images.remove(a);

                adapter.notifyDataSetChanged();
            }

            public void obtainValue(Offreclass offre){
            }

            public void checkProcess(boolean finished){
            }
        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selected_images);
        adapter = new SelectedImagesAdapter(images, delete);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    public void addImage(View view){
        final CharSequence[] options = { getString(R.string.takePhoto), getString(R.string.selectPhoto),getString(R.string.cancel) };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddOffre.this);
        builder.setTitle(R.string.addPhoto);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals( getString(R.string.takePhoto))){
                    checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
                }
                else if (options[item].equals( getString(R.string.selectPhoto))){
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                }
                else if (options[item].equals( getString(R.string.cancel))){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void checkPermission(String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(AddOffre.this, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(AddOffre.this, new String[] {permission}, requestCode);
        } else {
            if(requestCode == CAMERA_PERMISSION_CODE){
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else if (requestCode == STORAGE_PERMISSION_CODE){
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
            else if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
                Intent intent = new Intent(AddOffre.this, MapsActivity.class);
                startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(AddOffre.this, getString(R.string.cameraPermDenied), Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);
            }
            else {
                Toast.makeText(AddOffre.this, getString(R.string.storagePermDenied),Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(AddOffre.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
            else{
                Toast.makeText(AddOffre.this, getString(R.string.locationPermDenied),Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getLocation(View view){
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,MY_PERMISSIONS_REQUEST_LOCATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            if(data.getClipData() != null) {
                int nb = data.getClipData().getItemCount();
                for(int i = 0; i < nb; i++) {
                    images.add(data.getClipData().getItemAt(i).getUri());
                }
            }
            else{
                images.add(data.getData());
            }
            adapter.notifyDataSetChanged();
        }
        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), photo, "Title", null);
            images.add(Uri.parse(path));
            adapter.notifyDataSetChanged();
        }
        if(requestCode == SECOND_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            latitude = data.getStringExtra("latitude");
            longitude = data.getStringExtra("longitude");
            locationText.setText(latitude+","+longitude);
        }
    }

    public void UploadImage(){
        final Offreclass offre = new Offreclass(titre, text, prix);

        if(images.size() > 0){
            int number = images.size()-1;
            for(Uri img : images){
                final StorageReference storageReference2 = storageReference.child("ImagesOffres/" + titre + "/" + titre + ((images.size()-1)-number));
                storageReference2.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){
                    }
                });
                number--;
            }
        }
        offre.nbImages = images.size();
        offre.latitude = latitude;
        offre.longitude = longitude;
        offre.userId = user.getUid();
        format = new SimpleDateFormat("dd MMMM yyyy 'à' HH:mm", Locale.FRANCE);
        date = format.format(new Date());
        offre.date = date;
        mdatabase.child("offres").child(titre).setValue(offre);
        mdatabase.child("offres").child(titre).child("ListID/"+0).setValue("");
    }
    
    public void addOffre (View view){
        titre = titreE.getText().toString();
        text = textE.getText().toString();
        prix = prixE.getText().toString();

        if(titre.equals("")){
            titreE.setError(getString(R.string.champsvide));
        }
        if(text.equals("")){
            textE.setError(getString(R.string.champsvide));
        }
        if(prix.equals("")){
            prixE.setError(getString(R.string.champsvide));
        }

        else if(!(titre.matches("")) && !(text.matches("")) && !(prix.matches(""))){
            UploadImage();
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}