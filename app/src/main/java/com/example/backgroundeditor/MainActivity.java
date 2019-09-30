package com.example.backgroundeditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backgroundeditor.api.RetrofitClient;
import com.squareup.picasso.Picasso;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private Button btn_foto, btn_camara, nuevaFoto;
    private Spinner spinner;
    private static final int PICK_IMAGE = 12;
    private static final int CAPTURA_FOTO = 13;
    private List<Fondo> fondos;
    private EditText email;
    AlertDialog alertDialog;

    private ImageView mPhotoImageView;
    Uri uriImagen = null;
    String imageFilePath;
    String elegido;



    String[] array;
    String[] array1;


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

        final Button cancelar = findViewById(R.id.btn_cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reinciar1(v);
            }   });



        mPhotoImageView = findViewById(R.id.imageden);

        email = findViewById(R.id.editTextEmail);





    }

    protected class MyAdapterSpinner extends ArrayAdapter {

        String[] Image;
        String[] Text;

        public MyAdapterSpinner(Context context, int resource, String[] text, String[] image) {
            super(context, resource, text);
            Image = image;
            Text = text;
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.item_custom, parent, false);

            //Set Custom View
            TextView tv = (TextView)view.findViewById(R.id.textView);
            ImageView img = (ImageView) view.findViewById(R.id.imageView);

            tv.setText(Text[position]);
            Picasso.get().load(SharedPrefManager.getInstance(MainActivity.this).getRuta() + Image[position]).into(img);
           // img.setImageResource(Image[position]);

            return view;
        }

        @Override
        public View getDropDownView(int position,View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        elegido = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), elegido, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!SharedPrefManager.getInstance(this).estaConfigurado()){



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commonmenus, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.settings){

            Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
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

if(SharedPrefManager.getInstance(this).estaConfigurado()) {

    String email1 = email.getText().toString();
    File fil = FileUtil.from(this, this.uriImagen);
    File file = new Compressor(this).compressToFile(fil);


    // System.out.print("Sin Comprimir:" + fil.length());
    System.out.print("Comprimida: " + file.length());
    RequestBody rqBody = RequestBody.create(MediaType.parse("multipart/form-data*"), file);
    MultipartBody.Part imagen = MultipartBody.Part.createFormData("foto", file.getName(), rqBody);
    RequestBody emailRB = RequestBody.create(MediaType.parse("multipart/form-data*"), email1);
    RequestBody fondoRB = RequestBody.create(MediaType.parse("multipart/form-data*"), elegido);


    Call<ResponseBody> call = RetrofitClient
            .getInstance()
            .getApi()
            .nuevaFoto(imagen, emailRB, fondoRB);

    call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            showCustomDialog();
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
        }
    });
}
else{
    Toast.makeText(MainActivity.this, "Ingrese a configuración e indique la URL del servidor.", Toast.LENGTH_LONG).show();
}

    }

    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.enviada_exito, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        alertDialog = builder.create();
        alertDialog.show();
    }



    public void recargarFondos(View view){

        if(SharedPrefManager.getInstance(this).estaConfigurado()) {
            Call<FondoResponse> call = RetrofitClient.getInstance().getApi().obtenerFondos();
            call.enqueue(new Callback<FondoResponse>() {
                @Override
                public void onResponse(Call<FondoResponse> call, Response<FondoResponse> response) {
                    FondoResponse fondoResponse = response.body();
                    if (!fondoResponse.isError()) {

                        ArrayList<String> fondosa = new ArrayList<>();
                        ArrayList<String> urla = new ArrayList<>();
                        fondos = fondoResponse.getFondos();
                        for (int i = 0; i < fondos.size(); i++) {
                            String nombre = fondos.get(i).getNombre();
                            String url = fondos.get(i).getURL();

                            fondosa.add(nombre);
                            urla.add(url);
                        }

                        //Toast.makeText(MainActivity.this, fondosa.toString(), Toast.LENGTH_LONG).show();

                        List<String> list = fondosa;
                        array = list.toArray(new String[0]);

                        List<String> list1 = urla;
                        array1 = list1.toArray(new String[0]);


                        spinner = (Spinner) findViewById(R.id.spinner1);

                        MyAdapterSpinner adapter = new MyAdapterSpinner(getApplicationContext(), R.layout.item_custom, array, array1);

                        //Set Your Custom Adapter To Your Spinner
                        spinner.setAdapter(adapter);

                        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        //        R.array.numbers, android.R.layout.simple_spinner_item);
                        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(MainActivity.this);


                    }

                }

                @Override
                public void onFailure(Call<FondoResponse> call, Throwable t) {

                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();


                }
            });
        }
        else{
                Toast.makeText(MainActivity.this, "Ingrese a configuración e indique la URL del servidor.", Toast.LENGTH_LONG).show();
            }
    }

    public void reinciar(View view){

        alertDialog.dismiss();
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    public void reinciar1(View view){

        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }


}
