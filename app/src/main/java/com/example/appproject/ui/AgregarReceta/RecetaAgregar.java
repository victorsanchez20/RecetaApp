package com.example.appproject.ui.AgregarReceta;



import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.appproject.R;
import com.example.appproject.ui.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class RecetaAgregar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    final String servidor = "http://10.0.2.2/receta003/";
    int idCat = -1, idDif = -1;

    private EditText nom, prep, prec, vid, img;
    private Spinner cat, dif;
    private Button agregar, cargar;
    private ImageView imagen;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_receta_agregar, container, false);

        nom = (EditText) rootView.findViewById(R.id.etNombre);
        prep = (EditText) rootView.findViewById(R.id.etPreparacion);
        prec = (EditText) rootView.findViewById(R.id.etPrecio);
        vid = (EditText) rootView.findViewById(R.id.etVideo);
        img = (EditText) rootView.findViewById(R.id.etImagen);
        cat = (Spinner) rootView.findViewById(R.id.spCategoria);
        cat.setOnItemSelectedListener(this);
        dif = (Spinner) rootView.findViewById(R.id.spDificultad);
        dif.setOnItemSelectedListener(this);
        agregar = (Button) rootView.findViewById(R.id.btnAgregar);
        agregar.setOnClickListener(this);
        cargar = (Button) rootView.findViewById(R.id.btnCargar);
        cargar.setOnClickListener(this);
        imagen = (ImageView) rootView.findViewById(R.id.ivImagen);

        ObtenerCategoria();
        ObtenerDificultad();
        return rootView;
    }

    public void ObtenerCategoria() {
        String url = servidor + "categoria_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {

                ArrayList<Item> lista = new ArrayList<>();

                lista.add(new Item(-1, "Seleccionar marca"));

                // Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cat.setAdapter(adapter);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al obtener categorias", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void ObtenerDificultad() {
        String url = servidor + "dificultad_mostrar.php";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
                ArrayList<Item> lista = new ArrayList<>();
                lista.add(new Item(-1, "Seleccionar dificultad"));

                // Respuesta del servidor
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");
                        lista.add(new Item(id, nombre));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Llena el Spinner
                ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dif.setAdapter(adapter);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al obtener dificultad", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == agregar) {
            String nombre = nom.getText().toString();
            String preparacion = prep.getText().toString();
            double precio = Double.parseDouble(prec.getText().toString());
            String imagen = img.getText().toString();
            String fecha = "";
            String video = vid.getText().toString();

            // Validaciones
            if (nombre.isEmpty() || preparacion.isEmpty() || video.isEmpty() || precio <= 0) {
                Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
            else if (idCat == -1) {
                Toast.makeText(getContext(), "Por favor, seleccione una categoría", Toast.LENGTH_SHORT).show();
            }
            else if (idDif == -1) {
                Toast.makeText(getContext(), "Por favor, seleccione una dificultad", Toast.LENGTH_SHORT).show();
            }
            else {
                RegistrarReceta(nombre,imagen, preparacion, precio, video, idCat, idDif, fecha);
            }
        }
        else if (v == cargar)
        {
            //Picasso.get().load(img.getText().toString()).into(imagen)
            CargarImagen();

        }
    }

    private void LimpiarCampos() {
        nom.setText("");
        prep.setText("");
        prec.setText("");
        vid.setText("");
        cat.setSelection(0);
        dif.setSelection(0);
        imagen.setImageResource(R.mipmap.ic_launcher_round);
        nom.requestFocus();
    }

    public void RegistrarReceta(String nombre, String imagen, String preparacion, double precio, String video, int idCat, int idDif, String fecha) {
        String url = servidor + "receta_registrar.php";

        // Crear el objeto JSON con los datos de la receta
        RequestParams params = new RequestParams();
        params.put("nombre", nombre);
        params.put("imagen",imagen);
        params.put("tiempo_preparacion", preparacion);
        params.put("precio", precio);
        params.put("video", video);
        params.put("idCat", idCat);
        params.put("idDif", idDif);
        params.put("fecha", fecha);

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                LimpiarCampos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al registrar la receta", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == cat) {
            Item selectedItem = (Item) parent.getItemAtPosition(position);

            // Verifica si es el item "Seleccionar categoria"
            if (selectedItem.id == -1) {
                // No hacer nada
            }
            else {
                int selectedId = selectedItem.id;
                String selectedName = selectedItem.nombre;
                idCat = selectedId;
            }
        }
        else if (parent == dif) {
            Item selectedItem = (Item) parent.getItemAtPosition(position);
            if (selectedItem.id == -1) {
                // No hacer nada
            }
            else {
                int selectedId = selectedItem.id;
                String selectedName = selectedItem.nombre;
                idDif = selectedId;
            }
        }
    }

    public void CargarImagen() {
        String imageUrl = img.getText().toString();

        if (imageUrl.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingrese una URL de imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        Picasso.get()
                .load(imageUrl)
                .error(R.mipmap.ic_launcher_round) // Imagen por defecto si quieres mostrar una aún con el error
                .into(imagen, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // La imagen se cargó correctamente
                        // Puedes hacer algo aquí si es necesario, como ocultar un ProgressBar
                        Toast.makeText(getContext(), "Imagen cargada con éxito", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        // Ocurrió un error al cargar la imagen
                        Toast.makeText(getContext(), "Error al cargar la imagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}