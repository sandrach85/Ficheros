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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class FicherosActivity extends AppCompatActivity {

    String NOMBRE_FICHERO;
    private String RUTA_FICHERO;         /** SD card **/
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
        this.RUTA_FICHERO = PreferenceManager.getDefaultSharedPreferences(this).getString("nombreArchivo","mi_fichero_miw.txt");

        lineaTexto       = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir     = (Button)   findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);

        /** SD card **/
        // RUTA_FICHERO = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + NOMBRE_FICHERO;
        RUTA_FICHERO = getExternalFilesDir(null) + "/" + NOMBRE_FICHERO;

    }

    public void actionAniadir(View v){
        if (checkeado){
            Log.i("Boton check", "seleccionado");
            accionAniadirSD(v);
        }else{
            Log.i("Boton check", "NO seleccionado");
            accionAniadirInterna(v);
        }
    }

    /**
     * Al pulsar el botón añadir -> añadir al fichero.
     * Después de añadir -> mostrarContenido()
     *
     * @param v Botón añadir
     */
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
        Log.i("TEXTO PARA AÑADIR", texto);
        try {
            FileOutputStream fos = openFileOutput("FICHERO", Context.MODE_APPEND);
            fos.write(texto.getBytes());
            fos.close();
            mostrarContenido(contenidoFichero);
            contenidoFichero.callOnClick();
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

    }

    /**
     * Se pulsa sobre el textview -> mostrar contenido del fichero
     * Si está vacío -> mostrar un Toast
     *
     * @param textviewContenidoFichero TextView contenido del fichero
     */

    public void mostrarContenido(View textviewContenidoFichero){
        if (checkeado){
            Log.i("Boton check", "seleccionado");
            mostrarContenidoSD(textviewContenidoFichero);
            //accionAniadirSD(textviewContenidoFichero);
        }else{
            Log.i("Boton check", "NO seleccionado");
            mostrarContenidoInterna(textviewContenidoFichero);
            //accionAniadirInterna(textviewContenidoFichero);
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

    public void mostrarContenidoInterna(View textviewContenidoFichero){
        boolean hayContenido = false;
        //StringBuffer datax = new StringBuffer("");
        contenidoFichero.setText("");

        try{
            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
            String linea = fin.readLine();
            while (linea != null) {
                hayContenido = true;
                contenidoFichero.append(linea + '\n');
                linea = fin.readLine();
            }
            fin.close();
            Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");


           /* FileInputStream fis = openFileInput("FICHERO");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String readString = bufferedReader.readLine();
            while (readString != null) {
                hayContenido = true;
                datax.append(readString + '\n');
                readString = bufferedReader.readLine();
            }
            fis.close();
            contenidoFichero.append(datax.toString());*/
        }catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * Añade el menú con la opcion de vaciar el fichero
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //menu.add(Menu.NONE, 1, Menu.NONE, R.string.opcionVaciar)
        //        .setIcon(android.R.drawable.ic_menu_delete); // sólo visible android < 3.0

        // Inflador del menú: añade elementos a la action bar
        getMenuInflater().inflate(R.menu.menu, menu);


        //Preferencias
        //menu.add(Menu.NONE,1,Menu.NONE, R.string.actionPreferencias);

        return true;
       //return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // case 1:
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

    public void borrarContenido(){
        if (checkeado){
            Log.i("Boton check", "seleccionado Borrado SD");
            borrarContenidoSD();
        }else{
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
