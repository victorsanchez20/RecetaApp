package com.example.appproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity implements View.OnClickListener {

    public static final String servidor = "http://10.0.2.2/receta003/";

    EditText usu, pass;
    Button ing;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usu = findViewById(R.id.editTextUsername);
        pass = findViewById(R.id.editTextPassword);
        ing = findViewById(R.id.buttonLogin);
        progress = findViewById(R.id.progressBarLogin);
        ing.setOnClickListener(this);

        //Verificar si hay sharePreferences para inicio de sesión automático
        String username = getSharedPreferences("usuario", MODE_PRIVATE).getString("username", "");
        String password = getSharedPreferences("usuario", MODE_PRIVATE).getString("password", "");
        if (!username.isEmpty() && !password.isEmpty()) {
            IniciarSesion(username, password);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonLogin) {
            String username = usu.getText().toString();
            String password = pass.getText().toString();
            IniciarSesion(username, password);
        }
    }

    private void IniciarSesion(String username, String password) {
        String url = servidor + "usuario_autentificar.php";

        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);

        progress.setVisibility(View.VISIBLE);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progress.setVisibility(View.GONE);

                String id_usuario = new String(responseBody).trim();

                if (id_usuario.equals("0")) {
                    Toast.makeText(Login.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    LimpiarCampos();
                } else {
                    Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    GrabarSharePreferences(username, password);

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.putExtra("id_usuario", id_usuario);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(Login.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void GrabarSharePreferences(String username, String password) {
        // Quiero guardar el usuario y contraseña
        getSharedPreferences("usuario", MODE_PRIVATE)
                .edit()
                .putString("username", username)
                .putString("password", password)
                .apply();
    }

    private void LimpiarCampos() {
        usu.setText("");
        pass.setText("");
    }
}