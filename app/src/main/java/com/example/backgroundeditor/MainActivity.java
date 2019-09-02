package com.example.backgroundeditor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.backgroundeditor.api.RetrofitClient;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btn_foto, btn_camara, nuevaFoto;
    private static final int PICK_IMAGE = 12;
    private static final int CAPTURA_FOTO = 13;

    private ImageView mPhotoImageView;
    Uri uriImagen = null;
    String imageFilePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        Button nuevaFoto = findViewById(R.id.btn_denunciar);
        nuevaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nuevaDenuncia(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_foto = findViewById(R.id.btn_foto);
        btn_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btn_camara = findViewById(R.id.btn_camara);
        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llamarCamara();
            }
        });


        mPhotoImageView = findViewById(R.id.imageden);



    }

    private void llamarCamara() {
        if (validaPermisosCamara()) {

            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if(pictureIntent.resolveActivity(this.getPackageManager()) != null){
                //Create a file to store the image
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "fileprovider_auth", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            CAPTURA_FOTO);
                }
            }
        }
    }



    private void openGallery() {//funcion que abre la galeria
        if (validaPermisosGaleria()) {//valido que se tengan los permisos
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);//cargo el intento con el codigo 12
        }
    }

    private boolean validaPermisosGaleria() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);//los pido aca si no los tengo
            return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {//si es de la galeria
            Uri imageUri = data.getData();
            Picasso.get().load(imageUri).fit().centerCrop().into(mPhotoImageView);
            // mPhotoImageView.setImageURI(imageUri);
            this.uriImagen = imageUri;


        }
        if (resultCode == RESULT_OK && requestCode == CAPTURA_FOTO) {

            Uri imageUri = Uri.fromFile(new File(imageFilePath));
            Picasso.get().load(imageUri).fit().centerCrop().into(mPhotoImageView);
            //mPhotoImageView.setImageURI(imageUri);
            this.uriImagen = Uri.fromFile(new File(imageFilePath));

        }
    }

    private boolean validaPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2000);

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2000);
            } else {
                return true;
            }

        }
        return true;
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public void nuevaDenuncia(View v) throws IOException {


        String email = "leaguepablo16@gmail.com";


        File fil = FileUtil.from(this, this.uriImagen);
        File file = new Compressor(this).compressToFile(fil);


       // System.out.print("Sin Comprimir:" + fil.length());
        System.out.print("Comprimida: " + file.length());
        RequestBody rqBody = RequestBody.create(MediaType.parse("multipart/form-data*"), file);
        MultipartBody.Part imagen = MultipartBody.Part.createFormData("foto", file.getName(), rqBody);
        RequestBody emailRB = RequestBody.create(MediaType.parse("multipart/form-data*"), email);


        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .nuevaFoto(imagen,emailRB);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                String s = null;

                try {
                    if (response.code() == 201){
                        s = response.body().string();
                    }
                    else{
                        s = response.errorBody().string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }
}
