package com.example.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ocultar barra de notificacion (status bar)
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Oculta la barra de notificaciones (status bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);

            return insets;
        });

        // cargar 3 segundos y redirigimrme hacia el login
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Implementar
                startActivity(new Intent(Inicio.this, Login.class));
                finish();
            }

        }, 3000);
    }
}