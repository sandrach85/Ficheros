package es.upm.miw.ficheros;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class FicherosActivity extends AppCompatActivity {

    String NOMBRE_FICHERO;
    private String RUTA_FICHERO;
    EditText lineaTexto;
    Button botonAniadir;
    TextView contenidoFichero;
    Boolean checkeado;


    @Override
    protected void onStart() {
        super.onStart();
        mostrarContenido(contenidoFichero);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros);
        this.checkeado = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tajetaSD", true);
        this.NOMBRE_FICHERO = PreferenceManager.getDefaultSharedPreferences(this).getString("nombreArchivo", "mi_fichero_miw.txt");
        RUTA_FICHERO = getExternalFilesDir(null) + "/" + NOMBRE_FICHERO;

        lineaTexto = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir = (Button) findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);

    }

    public void accionAniadir(View v) {
        if (checkeado) {
            Log.i("Boton check", "seleccionado");
            accionAniadirSD(v);
        } else {
            Log.i("Boton check", "NO seleccionado");
            accionAniadirInterna(v);
            Toast.makeText(this, "Texto añadido a TELEFONO", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, RUTA_FICHERO, Toast.LENGTH_SHORT).show();
        }
    }

    public void accionAniadirSD(View v) {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
                fos.write(lineaTexto.getText().toString().getBytes());
                fos.write('\n');
                fos.close();
                mostrarContenido(contenidoFichero);
                Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void accionAniadirInterna(View v) {

        String texto = lineaTexto.getText().toString();
        // Log.i("TEXTO PARA AÑADIR", texto);
        try {
            FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_APPEND);
            fos.write(texto.getBytes());
            fos.write('\n');
            fos.close();
            Log.i("Texto", texto);
            mostrarContenido(contenidoFichero);
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
        }

    }

    public void mostrarContenido(View textviewContenidoFichero) {
        if (checkeado) {
            Log.i("Boton check", "seleccionado");
            mostrarContenidoSD(textviewContenidoFichero);
        } else {
            Log.i("Boton check", "NO seleccionado");
            mostrarContenidoInterna(textviewContenidoFichero);
        }
    }

    public void mostrarContenidoSD(View textviewContenidoFichero) {
        boolean hayContenido = false;
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();
        contenidoFichero.setText("");
        try {
            if (fichero.exists() &&
                    estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarContenidoInterna(View textviewContenidoFichero) {
        boolean hayContenido = false;
        contenidoFichero.setText("");
        try {
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
            String linea = fin.readLine();
            while (linea != null) {
                hayContenido = true;
                contenidoFichero.append(linea + '\n');
                linea = fin.readLine();
            }
            fin.close();
            Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accionVaciar:
                borrarContenido();
                break;
            case R.id.actionPreferencias:
                Toast.makeText(this, "Has pulsado Ajustes", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(FicherosActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }

    public void borrarContenido() {
        if (checkeado) {
            Log.i("Boton check", "seleccionado Borrado SD");
            borrarContenidoSD();
        } else {
            Log.i("Boton check", "seleccionado Borrado Memoria Interna");
            borrarContenidoMemInterna();
        }
    }

    public void borrarContenidoSD() {
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText("");
                mostrarContenido(contenidoFichero);
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void borrarContenidoMemInterna() {
        try {
            FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
            fos.close();
            Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
            lineaTexto.setText("");
            mostrarContenido(contenidoFichero);
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
