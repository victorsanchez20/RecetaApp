package com.example.appproject.ui.home;

import static com.example.appproject.Login.servidor;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appproject.MainActivity;
import com.example.appproject.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class cambiarFoto extends Fragment implements View.OnClickListener {

    ImageView foto;
    EditText url;
    Button cambiar, actualizar;

    String id_usuario;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cambiar_foto, container, false);

        foto = rootView.findViewById(R.id.imageView);
        url = rootView.findViewById(R.id.editTextText);
        cambiar = rootView.findViewById(R.id.btnCambiar);
        actualizar = rootView.findViewById(R.id.btnActualizar);
        cambiar.setOnClickListener(this);
        actualizar.setOnClickListener(this);

        id_usuario = getActivity().getIntent().getStringExtra("id_usuario");

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v==cambiar) {
            CambiarFoto(id_usuario, url.getText().toString());
        }
        else if (v==actualizar) {
            CargarImagen();
        }
        else {
            Toast.makeText(getActivity(), "No se ha seleccionado ninguna opción", Toast.LENGTH_SHORT).show();
        }
    }

    public void CargarImagen() {
        String imageUrl = url.getText().toString();

        if (imageUrl.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingrese una URL de imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        Picasso.get()
                .load(imageUrl)
                .error(R.mipmap.ic_launcher_round) // Imagen por defecto si quieres mostrar una aún con el error
                .into(foto, new com.squareup.picasso.Callback() {
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

    private void CambiarFoto(String idUsuario, String foto) {
        String url = servidor + "cambiar_foto.php";

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("foto_empleado", foto);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.d("Respuesta", response);

                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cambiar la foto", Toast.LENGTH_LONG).show();
            }
        });
    }
}