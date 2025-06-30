package com.example.appproject.ui.home;

import static com.example.appproject.Login.servidor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appproject.R;
import com.example.appproject.databinding.FragmentHomeBinding;
import com.example.appproject.ui.Recetas.Recetas;
import com.example.appproject.ui.Recetas.RecetasFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment implements View.OnClickListener {

    TextView bienvenida, fecha;
    Button busqueda, foto, id, sugerencias, salir;
    ListView lista;

    String idUsuario;
    List<Recetas> recetas = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        bienvenida = rootView.findViewById(R.id.tvBienvenida);
        fecha = rootView.findViewById(R.id.tvFecha);
        busqueda = rootView.findViewById(R.id.btnBusqueda);
        foto = rootView.findViewById(R.id.btnFoto);
        id = rootView.findViewById(R.id.btnId);
        sugerencias = rootView.findViewById(R.id.btnSugerencias);
        salir = rootView.findViewById(R.id.btnSalir);
        lista = rootView.findViewById(R.id.lvFaltantes);

        foto.setOnClickListener(this);
        busqueda.setOnClickListener(this);
        id.setOnClickListener(this);
        sugerencias.setOnClickListener(this);
        salir.setOnClickListener(this);

        idUsuario = getActivity().getIntent().getStringExtra("id_usuario");

        // Funciones
        Bienvenida();
        ListarRecetas();
        ConfigurarClickListener();

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

            CheckBox cbAprendida = convertView.findViewById(R.id.cbAprendido);

            // Obtener la receta
            Recetas receta = recetasList.get(position);

            // Asignar los valores
            //tvLId.setText(receta.idreceta);
            tvLNombre.setText("Nombre: " + receta.getNombre());
            tvLPreparacion.setText("Tiempo de preparacion: " + receta.getTiempo_preparacion());
            tvLCategoria.setText("Categoria: " + receta.getCategoria());
            tvLDificultad.setText("Dificultad: " + receta.getDificultad());
            tvLPrecio.setText("Precio: " + receta.getPrecio());

            // Establecerel estado del checkbox
            cbAprendida.setChecked(receta.getAprendido());

            cbAprendida.setOnCheckedChangeListener((buttonView, isChecked) -> {
                actualizarEstadoAprendida(receta.getIdreceta(), isChecked);
            });

            // Cargar la imagen usando Picasso - VERSIÓN CORREGIDA
            if (receta.getImagen() != null && !receta.getImagen().isEmpty()) {
                // Asegúrate de que la URL sea completa si es una ruta relativa
                String imageUrl = receta.getImagen().startsWith("http") ? receta.getImagen() : "http://10.0.2.2/receta001/" + receta.getImagen();

                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.mipmap.ic_launcher) // Imagen mientras carga
                        .error(R.mipmap.ic_launcher_round) // Imagen si hay error
                        .into(ivLImagen); // ivLImagen debe ser tu ImageView
            }
            else {
                ivLImagen.setImageResource(R.mipmap.ic_launcher_round);
            }


            return convertView;
        }
    }

    private void Bienvenida() {
        String url = servidor + "mostrar_usuario";
        RequestParams params = new RequestParams();
        params.put("id_empleado", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray responseArray = new JSONArray(response);

                    if (response.length() > 0) {
                        JSONObject obj = responseArray.getJSONObject(0);
                        bienvenida.setText("Bienvenido, " + obj.getString("nom_empleado") + " " + obj.getString("apat_empleado"));

                        // Formato de la fecha
                        SimpleDateFormat formato = new SimpleDateFormat("EEEE | dd/MM/yy", new Locale("es", "ES"));
                        String fechaActual = formato.format(new Date());
                        // Primera letra en mayúscula
                        fechaActual = fechaActual.substring(0, 1).toUpperCase() + fechaActual.substring(1);

                        // Mostrar la fecha en el TextView
                        fecha.setText(fechaActual);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == busqueda) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_home_to_busqueda);
        }
        else if (v == foto) {
            // NAVEGAR ENTRE FRAGMENTS
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_home_to_cambiarFoto);
        }
        else if (v == sugerencias) {

        }
        else if (v == id) {

        }
        else if (v == salir) {
            // Cerrar la app completa
            System.exit(0);
        }
        else  {
            //vacio
            Toast.makeText(getActivity(), "No se ha seleccionado ninguna opción", Toast.LENGTH_SHORT).show();
        }
    }



    private void ListarRecetas() {
        // Crear la URL para hacer la solicitud
        String url = servidor + "mostrar_receta_faltante.php";

        // Crear un objeto RequestParams para enviar los parámetros
        RequestParams params = new RequestParams();
        params.put("id_empleado", idUsuario);

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
                    recetas.clear();

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
                        boolean aprendido = jsonObject.getInt("aprendido") == 1;


                        Recetas receta = new Recetas(id,
                                dificultad,
                                categoria,
                                nombre,
                                imagen,
                                urlVideo,
                                precio,
                                tiempo,
                                fecha,
                                aprendido);
                        recetas.add(receta);
                    }
                    // Crear el adaptador y asignarlo al ListView
                    HomeFragment.RecetasAdapter adapter = new HomeFragment.RecetasAdapter(getActivity(), recetas);
                    //HomeFragment.RecetasAdapter adapter = new HomeFragment(getActivity(), recetas);
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
                bundle.putString("id", recetaSeleccionada.getIdreceta());
                bundle.putString("nombre", recetaSeleccionada.getNombre());
                bundle.putString("imagen", recetaSeleccionada.getImagen());
                bundle.putString("video", recetaSeleccionada.getVideo());
                bundle.putString("categoria", recetaSeleccionada.getCategoria());
                bundle.putString("dificultad", recetaSeleccionada.getDificultad());
                bundle.putString("tiempo", recetaSeleccionada.getTiempo_preparacion());
                bundle.putDouble("precio", recetaSeleccionada.getPrecio());
                bundle.putString("fecha", recetaSeleccionada.getFecha_publicacion());


                // Navegar al fragmento de detalle
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_home_to_recetasDetalle, bundle);
            }
        });

    }


    private void actualizarEstadoAprendida(String idReceta, boolean aprendido) {
        String url = servidor + "actualizar_aprendida.php";
        RequestParams params = new RequestParams();
        params.put("idUsuario", idUsuario);
        params.put("idReceta", idReceta);
        params.put("aprendido", aprendido ? "1" : "0");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url,params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("Response",response);
                if (response.isEmpty()) {
                    Log.e("Response","La respuesta esta vacia");
                    return;
                }

                //Toast.makeText(getActivity(), idUsuario + " " + idReceta + " " + aprendido, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");

                    if (status.equals("success")) {
                        // Si la actualización fue exitosa
                        Toast.makeText(getActivity(), "Estado actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Si hubo un error
                        Toast.makeText(getActivity(), "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al analizar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("Recetas","Error al actualizar estado");
            }
        });
    }

}