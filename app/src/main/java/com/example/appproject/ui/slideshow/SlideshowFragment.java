package com.example.appproject.ui.slideshow;

import static com.example.appproject.Login.servidor;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.appproject.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SlideshowFragment extends Fragment {

    private ListView listView;
    private ArrayList<Empleados> empleadosList;
    private TextView empleados, recetas, empleadosFaltantes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_slideshow, container, false);

        empleadosList = new ArrayList<>();
        listView = rootView.findViewById(R.id.listView);
        empleados = rootView.findViewById(R.id.tvCEmpleados);
        recetas = rootView.findViewById(R.id.tvCRecetas);
        empleadosFaltantes = rootView.findViewById(R.id.tvCFaltantes);

        ListarUsuarios();
        TotalReceta();
        TotalEmpleado();

        // Inicializar el gráfico tipo donut
        PieChart pieChart = rootView.findViewById(R.id.pieChart);

        // Crear una lista de datos (porcentaje de usuarios que terminaron y no terminaron
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(75f, "Usuarios que terminaron todas las recetas"));
        entries.add(new PieEntry(25f, "Usuarios que no terminaron todas las recetas"));

        // Crear un conjunto de datos con los valores de los usuarios
        PieDataSet dataSet = new PieDataSet(entries, "Porcentaje de usuarios");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Colores automáticos para los segmentos
        dataSet.setValueTextSize(12f);  // Tamaño de texto de los valores en el gráfico
        dataSet.setValueTextColor(R.color.black); // Color del texto (opcional)

        // Crear los datos finales del gráfico
        PieData data = new PieData(dataSet);

        // Asignar los datos del gráfico
        pieChart.setData(data);
        pieChart.invalidate(); // Refrescar el gráfico para mostrar los datos

        // Personalización del gráfico
        pieChart.setHoleRadius(40f);  // Crear el agujero en el medio (gráfico donut)
        pieChart.setTransparentCircleRadius(45f); // Radio de la parte transparente
        pieChart.setUsePercentValues(true); // Mostrar los valores en porcentaje
        pieChart.setEntryLabelColor(R.color.black); // Color de la etiqueta
        pieChart.setEntryLabelTextSize(12f); // Tamaño de texto de las etiquetas

        return rootView;
    }

    public class EmpleadosAdapter extends BaseAdapter {
        private final Context context;
        private final List<Empleados> empleadosList;

        public EmpleadosAdapter(Context context, List<Empleados> empleadosList) {
            this.context = context;
            this.empleadosList = empleadosList;
        }

        @Override
        public int getCount() { return empleadosList.size(); }
        @Override
        public Object getItem(int position) { return empleadosList.get(position); }
        @Override
        public long getItemId(int position) { return position; }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.gestionusuarios_item, null);
            }

            // Obtener los elementos de la vista
            TextView nombreCompleto = convertView.findViewById(R.id.tvGNombre);
            TextView porcentaje = convertView.findViewById(R.id.tvGPorcentaje);
            ImageView foto = convertView.findViewById(R.id.ivGfoto);
            ProgressBar barra = convertView.findViewById(R.id.pbGPorcentaje);

            // Obtener el empleado
            Empleados empleado = empleadosList.get(position);

            // Asignar los valores
            nombreCompleto.setText(empleado.getNombreEmpleado() + " " + empleado.getApellidoPat() + " " + empleado.getApellidoMat());
            porcentaje.setText(empleado.getPorcentajeEmpleado() + "%");
            barra.setProgress(empleado.getPorcentajeEmpleado()); // Asignar el porcentaje de progreso

            Glide.with(context)
                    .load(empleado.getFoto()) // Cargar la imagen desde la URL
                    .placeholder(R.drawable.ic_user) // Imagen por defecto mientras carga
                    .error(R.drawable.ic_error) // Imagen por defecto si hay un error al cargar
                    .circleCrop() // Hacer la imagen circular
                    .into(foto); // Asignar la imagen al ImageView

            return convertView;
        }
    }

    public void ListarUsuarios() {
        String url = servidor + "listar_usuarios.php";

        // Crear el cliente HTTP
        AsyncHttpClient client = new AsyncHttpClient();

        // Hacer la solicitud GET
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    // Convertir la respuesta en un objeto JSON
                    String response = new String(responseBody);
                    JSONArray usuariosArray = new JSONArray(response);

                    // Limpiar la lista antes de agregar nuevos datos
                    empleadosList.clear();
                    /*nuevo*/
                    int totalEmpleados = 0;
                    int empleadosCompletos = 0;

                    // Recorrer los usuarios y agregarlos a la lista
                    for (int i = 0; i < usuariosArray.length(); i++) {
                        JSONObject usuario = usuariosArray.getJSONObject(i);
                        int id = usuario.getInt("id_empleado");
                        String nombre = usuario.getString("nom_empleado");
                        String apellidoPat = usuario.getString("apat_empleado");
                        String apellidoMat = usuario.getString("amat_empleado");
                        String foto = usuario.getString("foto_empleado");

                        int porcentaje = usuario.getInt("porcentaje"); // Aqui deberias poner el porcentaje real de cada empleado

                        // Crear un nuevo objeto Empleados
                        Empleados nuevoEmpleado = new Empleados(id, nombre, apellidoPat, apellidoMat, porcentaje, foto);

                        // Agregar el nuevo empleado a la lista
                        empleadosList.add(nuevoEmpleado);

                        /*nuevo*/
                        totalEmpleados++;
                        if (porcentaje == 100) {
                            empleadosCompletos++;
                        }

                        empleadosFaltantes.setText(String.valueOf(totalEmpleados - empleadosCompletos));
                    }

                    // Crear el adaptador y asociarlo al ListView
                    EmpleadosAdapter adapter = new EmpleadosAdapter(getContext(), empleadosList);
                    listView.setAdapter(adapter);

                    /*nuevo*/
                    actualizarPieChart(empleadosCompletos,totalEmpleados);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesor los datos", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Mostrar un mensaje de error si la solicitud falla
                Toast.makeText(getContext(), "Error al obtener los usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPieChart(int empleadosCompletos, int totalEmpleados) {
        PieChart pieChart = getView().findViewById(R.id.pieChart);

        int empleadosIncompletos = totalEmpleados - empleadosCompletos;

        ArrayList<PieEntry> entries = new ArrayList<>();

        if (totalEmpleados > 0) {
            float porcentajeCompletos = (empleadosCompletos * 100f) / totalEmpleados;
            float porcentajeIncompletos = 100f - porcentajeCompletos;

            entries.add(new PieEntry(porcentajeCompletos, "Usuarios que terminaron todas las recetas"));
            entries.add(new PieEntry(porcentajeIncompletos, "Usuarios que no terminaron todas las recetas"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Porcentaje de usuarios");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
    }

    private void TotalReceta() {
        String url = servidor + "total_recetas.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);

                    int totalRecetas = jsonObject.getInt("total_receta");

                    recetas.setText(String.valueOf(totalRecetas));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar el total de recetas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al obtener las recetas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void TotalEmpleado() {
        String url = servidor + "total_usuarios.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject jsonObject = new JSONObject(response);

                    int totalEmpleados = jsonObject.getInt("total_empleado");

                    empleados.setText(String.valueOf(totalEmpleados));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar el total de empleados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al obtener los usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
