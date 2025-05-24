package com.example.appproject.ui.Recetas;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.appproject.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RecetasDetalle extends Fragment {

    private static final String TAG = "RecetasDetalle";

    private TextView etPNombre, etPCategoria, etPDificultad, etPTiempo, etPPrecio;
    private WebView vvVideo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView INICIO");
        View view = inflater.inflate(R.layout.fragment_recetas_detalle, container, false);

        // Inicializar las vistas aquí
        etPNombre = view.findViewById(R.id.etPNombre);
        etPCategoria = view.findViewById(R.id.etPCategoria);
        etPDificultad = view.findViewById(R.id.etPDificultad);
        etPTiempo = view.findViewById(R.id.etPTiempo);
        etPPrecio = view.findViewById(R.id.etPPrecio);
        vvVideo = view.findViewById(R.id.webVideo);

        Log.d(TAG, "onCreateView FIN - Vista inflada: " + (view != null));


        return view;
    }


    public RecetasDetalle() {
        super();
        Log.d(TAG, "Constructor llamado");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate llamado");
        if (getArguments() != null) {
            String nombre = getArguments().getString("nombre");
            Log.d(TAG, "Argumento 'nombre' en onCreate: " + nombre);
            // Aquí NO actualices las vistas, aún no están creadas.
        }
        else {
            Log.w(TAG, "getArguments() es null en onCreate");
        }
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated INICIO");

        Bundle arguments = getArguments();
        if (arguments != null) {
            Log.d(TAG, "Procesando argumentos en onViewCreated...");
            etPNombre.setText(getArguments().getString("nombre", "Nombre no encontrado"));
            etPCategoria.setText(getArguments().getString("categoria", "Categoría no encontrada"));
            etPDificultad.setText(getArguments().getString("dificultad", "Dificultad no encontrada"));
            etPTiempo.setText(getArguments().getString("tiempo", "Tiempo no encontrado"));
            // Para el precio, que es double:
            double precioRecibido = getArguments().getDouble("precio", 0.0);
            etPPrecio.setText(String.valueOf(precioRecibido));


        }
        String videoUrl = getArguments().getString("video");
        if (videoUrl != null && !videoUrl.isEmpty()) {
            String videoId = extraerIdYoutube(videoUrl);
            if (videoId != null) {
                String html = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + videoId + "\" frameborder=\"0\" allowfullscreen></iframe>";
                vvVideo.getSettings().setJavaScriptEnabled(true);
                vvVideo.loadData(html, "text/html", "utf-8");
            } else {
                Log.w(TAG, "ID de video no válido");
            }
        }
    }

    private String extraerIdYoutube(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}