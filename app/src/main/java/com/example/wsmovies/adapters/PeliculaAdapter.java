package com.example.wsmovies.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wsmovies.R;
import com.example.wsmovies.entity.Pelicula;

import org.json.JSONObject;

import java.util.List;

public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaAdapter.ViewHolder>{

    // PASO 1
    // Contenedor de pelicula (objeto derivado de una clase)
    private List<Pelicula> listaPeliculas; // Atributo de clase
    private Context context;

    RequestQueue requestQueue;
    private final String URL = "http://192.168.101.65:3000/api//"; // Endpoint

    // PASO 2
    // Metodo contructor para la clase principal "PeliculaAdapter"
    public PeliculaAdapter(List<Pelicula> listaPeliculas, Context context){
        this.listaPeliculas = listaPeliculas;
        this.context = context;
    }

    // PASO 5
    // Backend cuál es la plantilla
    @NonNull
    @Override
    public PeliculaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaAdapter.ViewHolder(view);
    }

    // PASO 4
    // Bind (unir, vincular, asociar), View (Vista - XML), Holder (Soporte)
    @Override
    public void onBindViewHolder(@NonNull PeliculaAdapter.ViewHolder holder, int position) {
        // Todos los elementos que mostrara el RV estan almacenados en memoria (listaPeliculas)
        Pelicula pelicula = listaPeliculas.get(position);
        holder.edtTituloRV.setText(pelicula.getTitulo()); // getNombre (entity)
        holder.edtGeneroRV.setText(pelicula.getGenero()); // getDescripcion (entity)

        // Botones no traen datos, activa evento | lambda java
        holder.btnEliminarRV.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Películas");
            builder.setMessage("¿Desea eliminar la película: " + pelicula.getTitulo() + "?");
            builder.setPositiveButton("Si", (a, b) -> {
                eliminarPelicula(pelicula.getIdpelicula(), position);
            });
            builder.setNegativeButton("No", null);

            AlertDialog dialog = builder.create();
            dialog.show();

            // Algoritmo para elimnar
            Toast.makeText(context, "Eliminando ID: " + pelicula.getIdpelicula(), Toast.LENGTH_LONG).show();
        });
    }

    // Eliminación por WS
    private void eliminarPelicula(int idpelicula, int position) {
        requestQueue = Volley.newRequestQueue(context);
        String endPoint = URL + String.valueOf(idpelicula);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                endPoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        // El resultado es un JSON entonces tenemos que trabajar en un TRY
                        try {
                            if (jsonObject.getBoolean("success")) {
                                // Debemos quitar el elemento de la lista y del recycler
                                listaPeliculas.remove(position); // Se va de la lista
                                notifyItemRemoved(position); // Se va del Recycler
                                notifyItemRangeChanged(position, listaPeliculas.size()); // Actualizado estructura
                                // Notificación
                                Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e("ErrorJSON", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ErrorEliminando", volleyError.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    // PASO 6
    // El adaptador debe conocer el total de elementos
    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }

    // PASO 3
    // Acceso a los Widget del XML (plantilla)
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Referencias
        TextView edtTituloRV, edtGeneroRV;
        Button btnEliminarRV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edtTituloRV = itemView.findViewById(R.id.edtTituloRV);
            edtGeneroRV = itemView.findViewById(R.id.edtGeneroRV);
            btnEliminarRV = itemView.findViewById(R.id.btnEliminarRV);
        }
    }
}
