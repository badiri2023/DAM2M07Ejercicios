package com.project;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.model.Consola;
import com.project.model.Personaje;
import com.project.model.Videojuego;

public class DataManager {

    // Mapas privados para guardar los datos cargados
    private Map<String, Personaje> personajes = new HashMap<>();
    private Map<String, Videojuego> videojuegos = new HashMap<>();
    private Map<String, Consola> consolas = new HashMap<>();

    // El constructor se llama al crear el DataManager
    // y automáticamente carga todo.
    public DataManager() {
        cargarPersonajes();
        cargarVideojuegos();
        cargarConsolas();
    }

    /**
     * Carga el archivo personajes.json desde la ruta de resources.
     */
    private void cargarPersonajes() {
        // La ruta empieza con "/" (raíz de resources)
        String ruta = "/assets/data/characters.json"; 
        
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream(ruta))) {

            // Le decimos a Gson que queremos una "Lista de Personajes"
            Type listType = new TypeToken<List<Personaje>>() {}.getType();
            List<Personaje> lista = new Gson().fromJson(reader, listType);
            
            // Convertimos la Lista en un Mapa para búsquedas rápidas por nombre
            for (Personaje p : lista) {
                personajes.put(p.getNombre(), p);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar " + ruta);
            e.printStackTrace();
        }
    }

    /**
     * Carga el archivo jocs.json desde la ruta de resources.
     */
    private void cargarVideojuegos() {
        String ruta = "/assets/data/games.json";
        
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream(ruta))) {

            Type listType = new TypeToken<List<Videojuego>>() {}.getType();
            List<Videojuego> lista = new Gson().fromJson(reader, listType);
            
            for (Videojuego v : lista) {
                videojuegos.put(v.getNombre(), v);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar " + ruta);
            e.printStackTrace();
        }
    }

    /**
     * Carga el archivo consoles.json desde la ruta de resources.
     */
    private void cargarConsolas() {
        String ruta = "/assets/data/consoles.json";
        
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream(ruta))) {

            Type listType = new TypeToken<List<Consola>>() {}.getType();
            List<Consola> lista = new Gson().fromJson(reader, listType);
            
            for (Consola c : lista) {
                consolas.put(c.getNombre(), c);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar " + ruta);
            e.printStackTrace();
        }
    }

    // --- GETTERS PÚBLICOS ---
    // Métodos para que los controladores puedan "pedir" los datos.
    // Usamos Collections.unmodifiableMap para evitar que un controlador
    // modifique los datos por accidente.

    public Map<String, Personaje> getPersonajes() {
        return Collections.unmodifiableMap(personajes);
    }

    public Map<String, Videojuego> getVideojuegos() {
        return Collections.unmodifiableMap(videojuegos);
    }

    public Map<String, Consola> getConsolas() {
        return Collections.unmodifiableMap(consolas);
    }
}
