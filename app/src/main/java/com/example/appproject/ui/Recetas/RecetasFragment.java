package com.example.appproject.ui.Recetas;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appproject.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class RecetasFragment extends Fragment implements View.OnClickListener {

    final String servidor = "http://10.0.2.2/receta002/";

    ListView lista;
    Button siguiente;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recetas, container, false);

        lista = (ListView) rootView.findViewById(R.id.lvLista);
        siguiente = (Button) rootView.findViewById(R.id.btnSiguiente);
        siguiente.setOnClickListener(this);

        ConfigurarClickListener();
        ListarRecetas();




        return rootView;

    }

    public class RecetasAdapter extends BaseAdapter {
        private Context context;
        private final List<Recetas> recetasList;

        public RecetasAdapter(Context context, List<Recetas> contactList) {
            this.context = context;
            this.recetasList = contactList;
        }

        @Override
        public int getCount() { return recetasList.size(); }

        @Override
        public Object getItem(int position) { return recetasList.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.recetas_item, null);

            }

            // Obtener los elementos de la vista
            //TextView tvLId = convertView.findViewById(R.id.tvLId);
            TextView tvLNombre = convertView.findViewById(R.id.tvLNombre);
            TextView tvLPreparacion = convertView.findViewById(R.id.tvLPreparacion);
            TextView tvLCategoria = convertView.findViewById(R.id.tvLCategoria);
            TextView tvLDificultad = convertView.findViewById(R.id.tvLDificultad);
            TextView tvLPrecio = convertView.findViewById(R.id.tvLPrecio);
            ImageView ivLImagen = convertView.findViewById(R.id.ivLImagen);

            // Obtener la receta
            Recetas receta = recetasList.get(position);

            // Asignar los valores
            //tvLId.setText(receta.idreceta);
            tvLNombre.setText("Nombre: " + receta.nombre);
            tvLPreparacion.setText("Tiempo de preparacion: " + receta.tiempo_preparacion);
            tvLCategoria.setText("Categoria: " + receta.categoria);
            tvLDificultad.setText("Dificultad: " + receta.dificultad);
            tvLPrecio.setText("Precio: " + receta.precio);
            // Cargar la imagen usando Picasso - VERSIÓN CORREGIDA
            if (receta.imagen != null && !receta.imagen.isEmpty()) {
                // Asegúrate de que la URL sea completa si es una ruta relativa
                String imageUrl = receta.imagen.startsWith("http") ? receta.imagen : "http://10.0.2.2/receta001/" + receta.imagen;

                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.mipmap.ic_launcher) // Imagen mientras carga
                        .error(R.mipmap.ic_launcher_round) // Imagen si hay error
                        .into(ivLImagen); // ivLImagen debe ser tu ImageView
            } else {
                ivLImagen.setImageResource(R.mipmap.ic_launcher_round);
            }



            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == siguiente) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_gallery_to_recetasAgregar);
        }
    }

    private void ListarRecetas() {
        // Crear la URL para hacer la solicitud
        String url = servidor + "mostrar_receta.php";

        // Crear un objeto RequestParams para enviar los parámetros
        RequestParams params = new RequestParams();

        // Crear una instancia de AsyncHttpClient
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, params, new AsyncHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("Respuesta", response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<Recetas> recetas = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id = jsonObject.getString("idReceta");
                        String dificultad = jsonObject.getString("nom_Dificultad");
                        String categoria = jsonObject.getString("nom_Categoria");
                        String nombre = jsonObject.getString("nombreReceta");
                        String imagen = jsonObject.getString("imagenReceta");
                        String urlVideo = jsonObject.getString("videoReceta");
                        double precio = jsonObject.getDouble("precioReceta");
                        String tiempo = jsonObject.getString("tiempoReceta");
                        String fecha = jsonObject.getString("fechaReceta");

                        Recetas receta = new Recetas(id,
                                dificultad,
                                categoria,
                                nombre,
                                imagen,
                                urlVideo,
                                precio,
                                tiempo,
                                fecha);
                        recetas.add(receta);
                    }
                    // Crear el adaptador y asignarlo al ListView
                    RecetasAdapter adapter = new RecetasAdapter(getActivity(), recetas);
                    // Asegúrate de que tu ListView tenga el ID correcto
                    lista.setAdapter(adapter);
                }

                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al obtener recetas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ConfigurarClickListener() {
        // Configurar el click listener para el ListView
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("RecetasFragment", "onItemClick: Position " + position); // <--- LOG
                // Obtener la receta seleccionada
                Recetas recetaSeleccionada = (Recetas) parent.getItemAtPosition(position);

                // Crear bundle con los datos
                Bundle bundle = new Bundle();
                bundle.putString("id", recetaSeleccionada.idreceta);
                bundle.putString("nombre", recetaSeleccionada.nombre);
                bundle.putString("imagen", recetaSeleccionada.imagen);
                bundle.putString("video", recetaSeleccionada.video);
                bundle.putString("categoria", recetaSeleccionada.categoria);
                bundle.putString("dificultad", recetaSeleccionada.dificultad);
                bundle.putString("tiempo", recetaSeleccionada.tiempo_preparacion);
                bundle.putDouble("precio", recetaSeleccionada.precio);
                bundle.putString("fecha", recetaSeleccionada.fecha_publicacion);


                // Navegar al fragmento de detalle
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_gallery_to_recetasDetalle, bundle);
            }
        });

    }

}