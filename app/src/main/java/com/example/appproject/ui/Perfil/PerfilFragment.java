package com.example.appproject.ui.Perfil;

import static com.example.appproject.Login.servidor;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appproject.R;
import com.example.appproject.ui.Recetas.Recetas;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PerfilFragment extends Fragment {
    TextView nombres, rol, correo, telefono, direccion, porce, nacimiento, dni;
    ProgressBar progreso;

    String idUsuario;
    List<Recetas> recetas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        nombres = rootView.findViewById(R.id.tvNombreP);
        rol = rootView.findViewById(R.id.tvRolP);
        correo = rootView.findViewById(R.id.tvCorreoP);
        telefono = rootView.findViewById(R.id.tvCelularP);
        direccion = rootView.findViewById(R.id.tvDireccionP);
        dni = rootView.findViewById(R.id.tvDniP);
        porce = rootView.findViewById(R.id.tvPorcentajeP);
        nacimiento = rootView.findViewById(R.id.tvNacimientoP);
        progreso = rootView.findViewById(R.id.pbProgresoP);

        idUsuario = getActivity().getIntent().getStringExtra("id_usuario");

        mostrarUsuarios(Integer.parseInt(idUsuario));
        ListarRecetas();
        return rootView;
    }


    private void mostrarUsuarios(int idEmpleado) {
        String url = servidor + "mostrar_usuario";

        RequestParams params = new RequestParams();
        params.put("id_empleado",idEmpleado);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray responseArray = new JSONArray(response);

                    if (response.length() > 0) {
                        JSONObject obj = responseArray.getJSONObject(0);

                        nombres.setText(obj.getString("nom_empleado") + " " + obj.getString("apat_empleado") + " " + obj.getString("amat_empleado"));
                        rol.setText(obj.getString("nombreRol"));
                        correo.setText(obj.getString("em_empleado"));
                        telefono.setText(obj.getString("cel_empleado"));
                        direccion.setText(obj.getString("dir_empleado"));
                        nacimiento.setText(obj.getString("fn_empleado"));
                        dni.setText(obj.getString("ndc_empleado"));

                        // Supón que agregas campo "porcentaje" en el JSON
                        int prog = obj.optInt("porcentaje", 0);
                        porce.setText(prog + "%");
                        progreso.setProgress(prog);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ListarRecetas() {
        // Crear la URL para hacer la solicitud
        String url = servidor + "mostrar_receta.php";

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

                    for (int i = 0; i < jsonArray.length(); i++) {
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
                        // Nuevo
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

                    actualizarProgreso();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error al obtener recetas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void actualizarProgreso() {
        int totalRecetas = recetas.size();
        int recetasAprendidas = 0;

        for (Recetas receta : recetas) {
            if (receta.getAprendido()) {
                recetasAprendidas++;
            }
        }
        if (totalRecetas > 0) {
            float porcentaje = (float) recetasAprendidas/totalRecetas * 100;
            progreso.setProgress((int) porcentaje);

            String textoPorcentaje = String.format("%.0f%%", porcentaje);
            porce.setText(textoPorcentaje);
        }
        else {
            progreso.setProgress(0);
            porce.setText("0%");
        }
    }
}