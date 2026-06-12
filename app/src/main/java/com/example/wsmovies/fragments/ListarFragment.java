package com.example.wsmovies.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wsmovies.R;
import com.example.wsmovies.adapters.PeliculaAdapter;
import com.example.wsmovies.entity.Pelicula;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListarFragment extends Fragment {
    RecyclerView recyclerPeliculas;
    List<Pelicula> listaPeliculas = new ArrayList<>();
    RequestQueue requestQueue;
    private final String URL = "http://192.168.101.65:3000/api/peliculas/"; // Endpoint

    public ListarFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asociar el fragment con el XML
        return inflater.inflate(R.layout.fragment_listar, container, false);
    }

    // Metodo que se ejecuta al iniciar el Fragment
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Referencia al XML
        recyclerPeliculas = view.findViewById(R.id.recyclerPeliculas);

        // Especificar la forma de renderizado (Lineal, cuadricula, etc)
        recyclerPeliculas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Traer los datos del WS > ADAPTER > RV
        obtenerDatos();
    }

    @Override
    public void onResume() {
        super.onResume();

        listaPeliculas.clear();
        obtenerDatos();
    }

    private void obtenerDatos() {
        requestQueue = Volley.newRequestQueue(requireContext().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getBoolean("success")) {
                                // Objetos es una lista(array) de JSON
                                JSONArray objetos = jsonObject.getJSONArray("data");
                                Pelicula pelicula;

                                for (int i = 0; i < objetos.length(); i++) {
                                    // Obj es un JSON
                                    JSONObject obj = objetos.getJSONObject(i);
                                    pelicula = new Pelicula();
                                    pelicula.setIdpelicula(obj.getInt("idpelicula"));
                                    pelicula.setTitulo(obj.getString("titulo"));
                                    pelicula.setGenero(obj.getString("genero"));

                                    listaPeliculas.add(pelicula);
                                }

                                // WS > ADAPTER
                                PeliculaAdapter adapter = new PeliculaAdapter(listaPeliculas, getContext());

                                // ADAPTER > RV
                                recyclerPeliculas.setAdapter(adapter);
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

}
