package com.example.tl01examen321070009;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ListaContactosTL01Examen321070009 extends AppCompatActivity {

    private ContactDbHelper dbHelper;
    private List<Contact> contactList;
    private ContactAdapter adapter;
    private ListView listView;
    private EditText editBuscar;
    private Contact contactoSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos_tl01_examen321070009);

        dbHelper = new ContactDbHelper(this);
        listView = findViewById(R.id.listViewContactos);
        editBuscar = findViewById(R.id.editBuscar);
        Button btnAtras = findViewById(R.id.btnAtras);
        Button btnCompartir = findViewById(R.id.btnCompartir);
        Button btnVerImagen = findViewById(R.id.btnVerImagen);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnEditar = findViewById(R.id.btnEditar);

        cargarContactos();

        // 🔙 Botón Atrás
        btnAtras.setOnClickListener(v -> finish());

        // 🖱 Seleccionar contacto — ahora se obtiene directamente del adaptador
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            contactoSeleccionado = (Contact) adapter.getItem(position);
            adapter.setSelectedPosition(position);
            Toast.makeText(this, "Seleccionado: " + contactoSeleccionado.getName(), Toast.LENGTH_SHORT).show();
        });

        // 🔍 Buscar contactos
        editBuscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // 📤 Compartir contacto
        btnCompartir.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto primero", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String texto = "📱 Contacto:\n" +
                    contactoSeleccionado.getName() + "\n" +
                    contactoSeleccionado.getPhone() + "\n" +
                    contactoSeleccionado.getCountry();
            shareIntent.putExtra(Intent.EXTRA_TEXT, texto);
            startActivity(Intent.createChooser(shareIntent, "Compartir contacto vía"));
        });

        // 🖼 Ver imagen del contacto
        btnVerImagen.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (contactoSeleccionado.getImage() == null || contactoSeleccionado.getImage().equals("foto_default")) {
                Toast.makeText(this, "Este contacto no tiene imagen guardada", Toast.LENGTH_SHORT).show();
                return;
            }
            mostrarImagenPopup(contactoSeleccionado.getImage());
        });

        // 🗑 Eliminar contacto
        btnEliminar.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar contacto")
                    .setMessage("¿Seguro que deseas eliminar a " + contactoSeleccionado.getName() + "?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        dbHelper.deleteContact(contactoSeleccionado.getId());
                        cargarContactos();
                        contactoSeleccionado = null;
                        Toast.makeText(this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // ✏️ Editar contacto
        btnEditar.setOnClickListener(v -> {
            if (contactoSeleccionado == null) {
                Toast.makeText(this, "Seleccione un contacto primero", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("editar_id", contactoSeleccionado.getId());
            startActivity(intent);
        });

        // ☎️ Mantener presionado → Mostrar diálogo de llamada
        listView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            contactoSeleccionado = (Contact) adapter.getItem(position);
            mostrarDialogoLlamada(contactoSeleccionado.getPhone());
            return true;
        });
    }


    private void cargarContactos() {
        contactList = dbHelper.getAllContacts();
        adapter = new ContactAdapter(this, contactList);
        listView.setAdapter(adapter);
    }

    private void mostrarImagenPopup(String imageUriString) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            ImageView imageView = new ImageView(this);
            imageView.setImageURI(Uri.parse(imageUriString));
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(20, 20, 20, 20);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            builder.setView(imageView);
            builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
            builder.show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al mostrar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoLlamada(String telefono) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Llamar contacto");
        builder.setMessage("¿Deseas llamar al número " + telefono + "?");
        builder.setPositiveButton("Llamar", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + telefono));
            startActivity(intent);
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnLlamar = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (btnLlamar != null) {
            btnLlamar.setTextColor(getResources().getColor(android.R.color.white));
            btnLlamar.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
}
