package com.example.appproject.ui.home;

import static com.example.appproject.Login.servidor;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appproject.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.entity.mime.Header;

public class idcard extends Fragment {

    TextView nombre, rol, fecha;
    ImageView foto, barras;

    String idUsuario;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_idcard, container, false);

        nombre = rootView.findViewById(R.id.tvCNombre);
        rol = rootView.findViewById(R.id.tvCRol);
        barras = rootView.findViewById(R.id.ivCBarras);
        fecha = rootView.findViewById(R.id.tvCFecha);
        foto = rootView.findViewById(R.id.ivcard);

        idUsuario = getActivity().getIntent().getStringExtra("id_usuario");
        mostrarUsuarios(Integer.parseInt(idUsuario));
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

                        nombre.setText(obj.getString("nom_empleado") + " " + obj.getString("apat_empleado") + " " + obj.getString("amat_empleado"));
                        rol.setText(obj.getString("nombreRol"));
                        String documento = "";
                        documento = obj.getString("ndc_empleado");

                        generarCodigoDeBarras(documento, barras);
                        String urlFoto = obj.getString("foto_empleado");

                        Date fechaActual = new Date(); // fecha de hoy
                        SimpleDateFormat formatoSalida = new SimpleDateFormat("d 'de' MMMM yyyy", new Locale("es", "ES"));
                        String fechaFormateada = formatoSalida.format(fechaActual);
                        fecha.setText(fechaFormateada);

                        Glide.with(getContext())
                                .load(urlFoto)
                                .placeholder(R.drawable.ic_user) // Imagen mientras carga
                                .error(R.drawable.ic_error)
                                .circleCrop()               // Imagen si falla la carga
                                .into(foto);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generarCodigoDeBarras(String codigo, ImageView imageView) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(codigo, BarcodeFormat.CODE_128, 400, 200);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generando código de barras", Toast.LENGTH_SHORT).show();
        }
    }

}