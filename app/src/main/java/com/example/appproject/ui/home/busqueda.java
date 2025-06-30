package com.example.appproject.ui.home;

import static com.example.appproject.Login.servidor;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appproject.R;
import com.example.appproject.ui.Item;
import com.example.appproject.ui.Recetas.Recetas;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class busqueda extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText nombre;
    Spinner categoria, dificultad;
    Button buscar;
    ListView lista;
    int idCat = -1, idDif = -1;

    String idUsuario;
    List<Recetas> recetas = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_busqueda, container, false);
        nombre = rootview.findViewById(R.id.etBNombre);
        categoria = rootview.findViewById(R.id.spBCategoria);
        dificultad = rootview.findViewById(R.id.spBDificultad);
        buscar = rootview.findViewById(R.id.button);
        lista = rootview.findViewById(R.id.lvBusqueda);
        buscar.setOnClickListener(this);
        categoria.setOnItemSelectedListener(this);
        dificultad.setOnItemSelectedListener(this);

        ObtenerCategoria();
        ObtenerDificultad();
        ConfigurarClickListener();
        idUsuario = getActivity().getIntent().getStringExtra("id_usuario");
        return rootview;
    }

    @Override
    public void onClick(View v) {
        if (v==buscar) {
            Busqueda();

        }
    }

    public void Busqueda() {
        String url = servidor + "buscar_recetas.php";

        RequestParams params = new RequestParams();

        String receta = nombre.getText().toString().trim();

        if (!receta.isEmpty()) {
            params.put("nombreReceta", receta);
        }
        if (idCat != -1) {
            params.put("idCategoria", idCat);
        }
        if (idDif != -1) {
            params.put("idDificultad", idDif);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONArray responseArray = new JSONArray(response);

                    recetas.clear(); // Limpiar antes de agregar
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject obj = responseArray.getJSONObject(i);

                        Recetas receta = new Recetas(
                                obj.getString("idReceta"),
                                // dificultad (no viene, o deberías incluirla en PHP)
                                // categoría (igual)
                                obj.getString("nom_Dificultad"),
                                obj.getString("nom_Categoria"),
                                obj.getString("nombreReceta"),
                                obj.getString("imagenReceta"),
                                obj.getString("videoReceta"),
                                obj.getDouble("precioReceta"),
                                obj.getString("tiempoReceta"),
                                obj.getString("fechaReceta"),
                                false // aprendido (agrega en PHP si es necesario)
                        );

                        recetas.add(receta);
                    }

                    RecetasAdapter adapter = new RecetasAdapter(getActivity(), recetas);
                    lista.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error procesando la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error en la busqueda", Toast.LENGTH_SHORT).show();
            }
        });
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
                categoria.setAdapter(adapter);
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
                dificultad.setAdapter(adapter);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al obtener dificultad", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == categoria) {
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
        else if (parent == dificultad) {
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
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                navController.navigate(R.id.action_busqueda_to_recetasDetalle, bundle);
            }
        });

    }


}