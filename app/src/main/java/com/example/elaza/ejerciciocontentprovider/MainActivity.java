package com.example.elaza.ejerciciocontentprovider;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Mostramos contactos de la agenda
        mostramosContactos();


        // Base de datos
        ContentResolver CR = getContentResolver();

        // Insertamos registros
        CR.insert(Uri_CP, setVALORES(1, "Pedro", 111111111, "pedro@DB.es"));
        CR.insert(Uri_CP, setVALORES(2, "Sandra", 222222222, "sandra@DB.es"));
        CR.insert(Uri_CP, setVALORES(3, "Mar", 333333333, "mar@DB.es"));
        CR.insert(Uri_CP, setVALORES(4, "Dani", 444444444, "dani@DB.es"));

        // Recuperamos valores de la tabla
        String[] valores_recuperar = {"_id", "nombre", "telefono", "email"};
        c = CR.query(Uri_CP, valores_recuperar, null, null, null);
        c.moveToFirst();
        do {
            id = c.getInt(0);
            nombre = c.getString(1);
            telefono = c.getInt(2);
            email = c.getString(3);
            Log.d("QUERY", "" + id + ", " + nombre + ", " + telefono + ", " + email);
        } while (c.moveToNext());

        // Actualizamos un registro de la tabla
        uri = Uri.parse("content://mi.content.provider.contactos/contactos/3");
        CR.update(uri, setVALORES(3, "PPPP", 121212121, "xxxx@xxxx.es"), null, null);
        // Y lo mostramos en el log
        c = CR.query(uri, valores_recuperar, null, null, null);
        c.moveToNext();
        id = c.getInt(0);
        nombre = c.getString(1);
        telefono = c.getInt(2);
        email = c.getString(3);
        Log.d("QUERY", "" + id + ", " + nombre + ", " + telefono + ", " + email);


        // Borramos un registro
        uri = Uri.parse("content://mi.content.provider.contactos/contactos/4");
        CR.delete(uri, null, null);

    }

    /**
     * Metodo que muestra los contactos segun la SDK que tengamos. Si es una API superior a 22 pide permisos para acceder a contactos
     */
    private void mostramosContactos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            contactos();
        } else {
            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS);
                mostramosContactos();
            } else {
                if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
                    contactos();
                }
            }
        }
        return;
    }


    /**
     * Muestra los contactos y los lista en la pantalla de nuestra aplicacion
     */
    private void contactos() {
        TextView texto = (TextView) findViewById(R.id.contactos);

        Cursor c = managedQuery(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                ContactsContract.Contacts.IN_VISIBLE_GROUP, null, ContactsContract.Contacts.DISPLAY_NAME);

        while (c.moveToNext()) {
            String contactos = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            texto.append(contactos);
            texto.append("\n");
        }
    }

    /**
     * Almacenamos en un contenido de Valores todos los datos
     *
     * @param id    Id del registro
     * @param nom   Nombre del contacto
     * @param tlf   Telefono del contacto
     * @param email Email del contacto
     * @return Contenido de valores con todos los datos annadidos
     */
    private ContentValues setVALORES(int id, String nom, int tlf, String email) {
        ContentValues valores = new ContentValues();
        valores.put("_id", id);
        valores.put("nombre", nom);
        valores.put("telefono", tlf);
        valores.put("email", email);
        return valores;
    }


    // Variables
    private static final Uri Uri_CP = Uri.parse("content://mi.content.provider.contactos/contactos"); // Contenido de la autoria
    private Uri uri;
    private Cursor c;

    private int id;
    private String nombre;
    private int telefono;
    private String email;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
}