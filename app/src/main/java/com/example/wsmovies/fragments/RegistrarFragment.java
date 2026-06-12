package com.example.wsmovies.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wsmovies.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegistrarFragment extends Fragment {
    Button btnTestWS, btnGuardarPelicula;
    RequestQueue requestQueue; // Cola de solicitud
    String genero = ""; // RadioButton
    private final String URL = "http://192.168.101.65:3000/api/peliculas/"; // Endpoint
    EditText edtTitulo, edtDuracion, edtALanzamiento;
    RadioButton rbtAnimado, rbtDrama, rbtComedia, rbtAccion, rbtTerror;

    // Constructor
    public RegistrarFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registrar, container, false);
    }

    /**
     * Este método retornara TRUE cuando el formulario este listo para el registro (todos los campos tendran datos)
     * @return
     */
    private boolean readyUI() {
        boolean ready = true;

        if ( edtTitulo.getText().toString().isEmpty() ) { ready = false; };
        if ( edtDuracion.getText().toString().isEmpty() ) { ready = false; };
        if ( edtALanzamiento.getText().toString().isEmpty() ) { ready = false; };

        if ( !rbtAnimado.isChecked() && !rbtDrama.isChecked() && !rbtComedia.isChecked() && !rbtAccion.isChecked() && !rbtTerror.isChecked()) { ready = false; };

        return ready;
    }

    /**
     * Regresa la UI (formulario) a su estado original
     */
    private void resetUI() {
        edtTitulo.setText(null);
        edtDuracion.setText("");
        edtALanzamiento.setText("");

        rbtAnimado.setChecked(false);
        rbtDrama.setChecked(false);
        rbtComedia.setChecked(false);
        rbtAccion.setChecked(false);
        rbtTerror.setChecked(false);
        genero = "";
    }

    private void registrarPelicula() {
        // 0. Preparar el JSON
        // Definir que condición tiene
        genero = "";
        if ( rbtAnimado.isChecked()) { genero = "Bueno"; }
        if ( rbtDrama.isChecked()) { genero = "Regular"; }
        if ( rbtComedia.isChecked()) { genero = "Malo"; }
        if ( rbtAccion.isChecked()) { genero = "Malo"; }
        if ( rbtTerror.isChecked()) { genero = "Malo"; }


        JSONObject datosEnviar = new JSONObject();
        try {
            datosEnviar.put("titulo", edtTitulo.getText().toString()); // EditText
            datosEnviar.put("duracionmin", edtDuracion.getText().toString()); // EditText
            datosEnviar.put("alanzamiento", edtALanzamiento.getText().toString()); // EditText
            datosEnviar.put("genero", genero);
        } catch (Exception e) {
            Log.e("ErrorJSON", e.toString());
        }

        // 1. Canal de comunicación
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        // 2. Consumir el WS > Lectura de datos (JSON resultado)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                datosEnviar,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            boolean success = jsonObject.getBoolean("success");
                            String message = jsonObject.getString("message");
                            int id = jsonObject.getInt("id");

                            if ( success ) {
                                resetUI();
                                Toast.makeText(getContext(), message + " - ID: " + id, Toast.LENGTH_SHORT).show();
                                edtTitulo.requestFocus();
                            }
                            Toast.makeText(getContext(), jsonObject.toString(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("ErrorJSON", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Cuando no realiza la operación...
                        NetworkResponse response = volleyError.networkResponse;

                        // Si existe un error...
                        if ( response != null && response.data != null ) {
                            // STATUS CODE
                            int statusCode = response.statusCode;
                            // MESSAGE DETAIL
                            String errorJSON = new String(response.data);

                            // Mostrar el message (JSON) en la pantalla TOAST
                            Log.d("ErrorStatusCode", String.valueOf(statusCode));
                            Log.d("ErrorDetallado", errorJSON);
                        }
                        // Log.e("ErrorWS", volleyError.toString());
                    }
                }
        );
        // 3. Ejecución
        requestQueue.add(jsonObjectRequest);
    }

    private void testWS() {
        // ¿Qué nos devolverá la consulta / request?
        // GET (listar) => [{}, {}, {}]
        // GET (buscador) => {}
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            boolean success = jsonObject.getBoolean("success");
                            String resultado = "";

                            if (success) {
                                // JSONArrayRequest = solicitud / pedido
                                // JSONArray = contenedor
                                // Iterar la clave data = []
                                JSONArray listaPeliculas = jsonObject.getJSONArray("data");

                                // Ahora para terminar, iteramos (recorremos el JSONArray)
                                for (int i = 0; i < listaPeliculas.length(); i++) {
                                    JSONObject pelicula = listaPeliculas.getJSONObject(i);
                                    resultado += pelicula.getString("nombre") + ", ";
                                }

                                Toast.makeText(getContext(), resultado, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("ErrorJSON", "No podemos leer JSON");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ErrorWS", volleyError.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // REFERENCIAS
        btnTestWS = view.findViewById(R.id.btnTestWS);
        btnGuardarPelicula = view.findViewById(R.id.btnGuardarPelicula);

        edtTitulo = view.findViewById(R.id.edtTitulo);
        edtDuracion = view.findViewById(R.id.edtDuracion);
        edtALanzamiento = view.findViewById(R.id.edtALanzamiento);

        rbtAnimado = view.findViewById(R.id.rbtAnimado);
        rbtDrama = view.findViewById(R.id.rbtDrama);
        rbtComedia = view.findViewById(R.id.rbtComedia);
        rbtAccion = view.findViewById(R.id.rbtAccion);
        rbtTerror = view.findViewById(R.id.rbtTerror);

        btnTestWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testWS();
            }
        });

        btnGuardarPelicula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyUI()) {
                    // Confirmación de proceso
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("App Películas");
                    builder.setMessage("¿Seguro de proceder con el registro?");

                    builder.setPositiveButton("Si", (a, b) -> {
                        registrarPelicula();
                    });
                    builder.setNegativeButton("No", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(getContext(), "Complete el formulario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
