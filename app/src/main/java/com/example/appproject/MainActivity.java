package com.example.appproject;

import static com.example.appproject.Login.servidor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appproject.databinding.ActivityMainBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    TextView navUsername;
    TextView navEmail;
    ImageView navImage;


    String id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //--------------------------------------------------------------------
        //recibir el id_usuario del login
        id_usuario = getIntent().getStringExtra("id_usuario");

        //accerder al nav_header_main.xml
        View headerView = binding.navView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.NomUsu);
        navEmail = headerView.findViewById(R.id.EmailUsu);
        navImage = headerView.findViewById(R.id.imageUsu);


        ConsultarUsuario(id_usuario);

        //--------------------------------------------------------------------

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        /*---------------------------------------------------------------*/
        ConsultaRol(id_usuario);
        /*--------------------------------------------------------------*/
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        // Configura el listener manualmente para interceptar nav_logout
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_logout) {
                    logoutUser();
                    if (drawer != null) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    return true;
                }
                else {
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                    if (handled && drawer != null) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                    return handled;
                }
            }
        });
    }

    private void ConsultarUsuario(String idUsuario) {

        String url = servidor+"empleado_consultar.php";

        RequestParams params = new RequestParams();
        params.put("id_empleado", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);

                try {
                    // Parsear el JSON recibido
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String nom = jsonObject.getString("nom_empleado");
                        String em = jsonObject.getString("em_empleado");
                        String foto = jsonObject.getString("foto_empleado");

                        navUsername.setText(nom);
                        navEmail.setText(em);

                        Glide.with(getApplicationContext())
                                .load(foto)
                                .placeholder(R.drawable.ic_user) // Imagen por defecto mientras carga
                                .error(R.drawable.ic_error)      // Imagen si falla la carga
                                .circleCrop()
                                .into(navImage);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void logoutUser() {
        // 1. Limpia los datos de sesión del usuario.
        //    Ejemplo: Si usas SharedPreferences para guardar el ID de usuario o un token de sesión:
        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE); // Usa el nombre de tus SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("username"); // O la clave que uses para el ID de usuario
        editor.remove("password");  // O la clave para el token
        editor.putBoolean("isLoggedIn", false); // Una bandera de estado de login

        editor.apply(); // O editor.commit() si necesitas que sea síncrono

        // 2. Navega al LoginActivity.
        Intent intent = new Intent(MainActivity.this, Login.class); // Asegúrate que LoginActivity sea el nombre correcto
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia la pila de actividades
        startActivity(intent);
        finish(); // Cierra MainActivity para que el usuario no pueda volver con el botón "atrás"

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // ACTUALIZAR DATOS
        MenuItem actualizarItem = menu.findItem(R.id.action_datos);
        actualizarItem.setOnMenuItemClickListener(item -> {
            //mostrar AlertDialog para actualizar los datos
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Actualizar Datos");

            // Inflar el layout personalizado para el AlertDialog
            View viewInflated = getLayoutInflater().inflate(R.layout.dialog_change_data, null);
            // Set up the input
            final android.widget.EditText inputNom = viewInflated.findViewById(R.id.nom_pe);
            final android.widget.EditText inputApat = viewInflated.findViewById(R.id.apat_pe);
            final android.widget.EditText inputAmat = viewInflated.findViewById(R.id.amat_pe);
            final android.widget.EditText inputNdc = viewInflated.findViewById(R.id.ndc_pe);
            final android.widget.EditText inputCel = viewInflated.findViewById(R.id.cel_pe);
            final android.widget.EditText inputEm = viewInflated.findViewById(R.id.em_pe);
            builder.setView(viewInflated);

            ConsultarEmpleado(inputNom, inputApat, inputAmat, inputNdc, inputCel, inputEm);

            // Configurar los botones
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                // Aquí puedes manejar la lógica para actualizar los datos
                String nom = inputNom.getText().toString();
                String apat = inputApat.getText().toString();
                String amat = inputAmat.getText().toString();
                String ndc = inputNdc.getText().toString();
                String cel = inputCel.getText().toString();
                String em = inputEm.getText().toString();

                if (nom.isEmpty() || apat.isEmpty() || amat.isEmpty() || ndc.isEmpty() || cel.isEmpty() || em.isEmpty()) {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }

                ActualizarEmpleado(inputNom, inputApat, inputAmat, inputNdc, inputCel, inputEm, dialog);
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.show();

            return true;
        });

        // CAMBIAR PASSWORD
        MenuItem passwordItem = menu.findItem(R.id.action_password);
        passwordItem.setOnMenuItemClickListener(item -> {
            //mostrar AlertDialog para cambiar contraseña
            // Crear un AlertDialog.Builder
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Cambiar Contraseña");

            // Inflar el layout personalizado para el AlertDialog
            View viewInflated = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
            // Set up the input
            final android.widget.EditText inputOldPassword = viewInflated.findViewById(R.id.old_password);
            final android.widget.EditText inputNewPassword = viewInflated.findViewById(R.id.new_password);
            final android.widget.EditText inputConfirmPassword = viewInflated.findViewById(R.id.confirm_password);

            builder.setView(viewInflated);

            // Configurar los botones
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                dialog.dismiss();
                // Aquí puedes manejar la lógica para cambiar la contraseña
                String oldPassword = inputOldPassword.getText().toString();
                String newPassword = inputNewPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();

                // Validar que la nueva contraseña coincida con la confirmación
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                    return;
                }
                //validar que la contraseña antigua no se igual a la nueva
                if (newPassword.equals(oldPassword)) {
                    Toast.makeText(this, "La nueva contraseña no puede ser igual a la antigua", Toast.LENGTH_LONG).show();
                    return;
                }
                //los campos deben estar llenos
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show();
                    return;
                }

                CambiarContraseña(id_usuario,newPassword);

                // Por ejemplo, mostrar un Toast con las contraseñas ingresadas
                //Toast.makeText(this, "Antigua: " + oldPassword + ", Nueva: " + newPassword + ", Confirmar: " + confirmPassword, Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();

            return true;
        });

        return true;
    }

    private void CambiarContraseña(String idUsuario, String newPassword) {
        String url = servidor + "cambiar_password.php";

        RequestParams params = new RequestParams();
        params.put("id_usuario", idUsuario);
        params.put("new_password", newPassword);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    Log.d("RESPONSE", response); // Para depuración

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    String message = jsonResponse.getString("message");

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error en el formato de respuesta del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ConsultarEmpleado(EditText inputNom, EditText inputApat, EditText inputAmat,
                                   EditText inputNdc, EditText inputCel, EditText inputEm) {

        String url = servidor + "empleado_consultar.php";

        RequestParams params = new RequestParams();
        params.put("id_empleado", id_usuario); // Se usa id_usuario ya recibido al iniciar

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String nom = jsonObject.getString("nom_empleado");
                        String apat = jsonObject.getString("apat_empleado");
                        String amat = jsonObject.getString("amat_empleado");
                        String ndc = jsonObject.getString("ndc_empleado");
                        String cel = jsonObject.getString("cel_empleado");
                        String em = jsonObject.getString("em_empleado");

                        inputNom.setText(nom);
                        inputApat.setText(apat);
                        inputAmat.setText(amat);
                        inputNdc.setText(ndc);
                        inputCel.setText(cel);
                        inputEm.setText(em);
                    } else {
                        Toast.makeText(getApplicationContext(), "No se encontraron datos", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error al parsear el JSON", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ActualizarEmpleado(EditText inputNom, EditText inputApat, EditText inputAmat,
                                    EditText inputNdc, EditText inputCel, EditText inputEm,
                                    DialogInterface dialog) {

        String url = servidor + "empleado_actualizar.php";

        // Obtener los datos ingresados por el usuario
        String nom = inputNom.getText().toString().trim();
        String apat = inputApat.getText().toString().trim();
        String amat = inputAmat.getText().toString().trim();
        String ndc = inputNdc.getText().toString().trim();
        String cel = inputCel.getText().toString().trim();
        String em = inputEm.getText().toString().trim();

        // Validación rápida (opcional)
        if (nom.isEmpty() || apat.isEmpty() || amat.isEmpty() || ndc.isEmpty() || cel.isEmpty() || em.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Por favor complete todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.put("id_empleado", id_usuario);
        params.put("nom_empleado", nom);
        params.put("apat_empleado", apat);
        params.put("amat_empleado", amat);
        params.put("ndc_empleado", ndc);
        params.put("cel_empleado", cel);
        params.put("em_empleado", em);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getApplicationContext(), "Datos actualizados correctamente", Toast.LENGTH_LONG).show();
                dialog.dismiss(); // Cierra el AlertDialog
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : error.getMessage();
                Toast.makeText(getApplicationContext(), "Error de conexión: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ConsultaRol(String idUsuario) {
        String url = servidor + "mostrar_usuario.php"; // <-- Cambia esto con tu URL real

        RequestParams params = new RequestParams();
        params.put("id_empleado", idUsuario);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String json = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(json);

                    if (jsonArray.length() > 0) {
                        JSONObject obj = jsonArray.getJSONObject(0);
                        String rol = obj.getString("nombreRol");

                        // Ocultar/mostrar ítems del menú según el rol
                        Menu menu = binding.navView.getMenu();

                        if (rol.equalsIgnoreCase("empleado")) {
                            menu.findItem(R.id.nav_slideshow).setVisible(false);
                            menu.findItem(R.id.nav_agregar_receta).setVisible(false);
                            // Agrega otros .setVisible(false) si deseas ocultar más
                        } else if (rol.equalsIgnoreCase("administrador")) {
                            menu.findItem(R.id.nav_slideshow).setVisible(true);
                            menu.findItem(R.id.nav_agregar_receta).setVisible(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al leer datos del rol", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}