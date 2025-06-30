package com.example.appproject.ui.home;

import static com.example.appproject.Login.servidor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.appproject.R;
import com.example.appproject.ui.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class busqueda extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText nombre;
    Spinner categoria, dificultad;
    Button buscar;
    ListView lista;
    int idCat = -1, idDif = -1;

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

        ObtenerCategoria();
        ObtenerDificultad();

        return rootview;
    }

    @Override
    public void onClick(View v) {
        if (v==buscar) {
            //busquedaReceta();
        }
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

}