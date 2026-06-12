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

import org.json.JSONObject;

public class BuscarFragment extends Fragment {
    EditText edtIdM, edtTituloM, edtDuracionM, edtALanzamientoM;
    Button btnBuscarM, btnActualizarM;
    RequestQueue requestQueue; // Cola de solicitud
    String genero = ""; // RadioButton
    private final String URL = "http://192.168.101.65:3000/api//"; // Endpoint
    RadioButton rbtAnimadoM, rbtDramaM, rbtComediaM, rbtAccionM, rbtTerrorM;

    // Constructor
    public BuscarFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buscar, container, false);
    }

    /**
     * Este método retornara TRUE cuando el formulario este listo para el registro (todos los campos tendran datos)
     * @return
     */
    private boolean readyUI() {
        boolean ready = true;

        if ( edtTituloM.getText().toString().isEmpty() ) { ready = false; };
        if ( edtDuracionM.getText().toString().isEmpty() ) { ready = false; };
        if ( edtALanzamientoM.getText().toString().isEmpty() ) { ready = false; };
        if ( !rbtAnimadoM.isChecked() && !rbtDramaM.isChecked() && !rbtComediaM.isChecked() && !rbtAccionM.isChecked() && !rbtTerrorM.isChecked()) { ready = false; };
        return ready;
    }

    private void buscarPelicula() {

        String id = edtIdM.getText().toString();
        String url = URL + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {

                            JSONObject data = response.getJSONObject("data");

                            edtTituloM.setText(data.getString("nombre"));
                            edtDuracionM.setText(data.getString("marca"));
                            edtALanzamientoM.setText(data.getString("descripcion"));

                            genero = data.getString("condicion");

                            rbtAnimadoM.setChecked(genero.equals("Bueno"));
                            rbtDramaM.setChecked(genero.equals("Regular"));
                            rbtComediaM.setChecked(genero.equals("Malo"));
                            rbtAccionM.setChecked(genero.equals("Manual"));
                            rbtTerrorM.setChecked(genero.equals("Eléctrica"));
                        }
                    } catch (Exception e) {
                        Log.e("ERROR", e.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ErrorWS", volleyError.toString());
                    }
                }
        );
        requestQueue.add(request);
    }

    private void actualizarPelicula() {
        String id = edtIdM.getText().toString();
        String url = URL + id;

        try {
            JSONObject body = new JSONObject();

            body.put("titulo", edtTituloM.getText().toString());
            body.put("duracionmin", edtDuracionM.getText().toString());
            body.put("alanzamiento", edtALanzamientoM.getText().toString());

            if (rbtAnimadoM.isChecked()) genero = "animado";
            if (rbtDramaM.isChecked()) genero = "drama";
            if (rbtComediaM.isChecked()) genero = "comedia";
            if (rbtAccionM.isChecked()) genero = "accion";
            if (rbtTerrorM.isChecked()) genero = "terror";

            body.put("genero", genero);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    body,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(getContext(), "Actualizado", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("ERROR", e.toString());
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

            requestQueue.add(request);

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtIdM = view.findViewById(R.id.edtIdM);
        edtTituloM = view.findViewById(R.id.edtTituloM);
        edtDuracionM = view.findViewById(R.id.edtDuracionM);
        edtALanzamientoM = view.findViewById(R.id.edtALanzamientoM);

        btnBuscarM = view.findViewById(R.id.btnBuscarM);
        btnActualizarM = view.findViewById(R.id.btnActualizarPeliculaM);

        rbtAnimadoM = view.findViewById(R.id.rbtAnimadoM);
        rbtDramaM = view.findViewById(R.id.rbtDramaM);
        rbtComediaM = view.findViewById(R.id.rbtComediaM);
        rbtAccionM = view.findViewById(R.id.rbtAccionM);
        rbtTerrorM = view.findViewById(R.id.rbtTerrorM);

        requestQueue = Volley.newRequestQueue(requireContext());

        btnBuscarM.setOnClickListener(v -> buscarPelicula());
        btnActualizarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyUI()) {
                    // Confirmación de proceso
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("App Películas");
                    builder.setMessage("¿Seguro de proceder con el registro?");

                    builder.setPositiveButton("Si", (a, b) -> {
                        actualizarPelicula();
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
