package com.example.tl01examen321070009;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContactDbHelper dbHelper;
    private ImageView imageViewContacto;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUriSeleccionada;
    private int contactoEditarId = -1; // ID del contacto a editar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Inicializaciones ---
        dbHelper = new ContactDbHelper(this);
        imageViewContacto = findViewById(R.id.imageViewContacto);

        Spinner spinnerPais = findViewById(R.id.spinner);
        EditText editNombre = findViewById(R.id.editTextNombre);
        EditText editTelefono = findViewById(R.id.editTextTelefono);
        EditText editNota = findViewById(R.id.editTextNota);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        Button btnVerContactos = findViewById(R.id.btnVerContactos);

        // --- 📍 Llenar Spinner con países ---
        String[] paises = new String[]{
                "Seleccionar país",
                "🇭🇳 Honduras (+504)",
                "🇬🇹 Guatemala (+502)",
                "🇸🇻 El Salvador (+503)",
                "🇳🇮 Nicaragua (+505)",
                "🇨🇷 Costa Rica (+506)",
                "🇲🇽 México (+52)",
                "🇺🇸 Estados Unidos (+1)",
                "🇵🇦 Panamá (+507)",
                "🇧🇿 Belice (+501)",
                "🇪🇸 España (+34)"
        };

        ArrayAdapter<String> adapterPaises = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paises
        );
        adapterPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPais.setAdapter(adapterPaises);

        // --- 📞 Prefijo del país (no editable) ---
        final String[] prefijoActual = {""};

        spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                switch (position) {
                    case 1: prefijoActual[0] = "+504 "; break; // Honduras
                    case 2: prefijoActual[0] = "+502 "; break; // Guatemala
                    case 3: prefijoActual[0] = "+503 "; break; // El Salvador
                    case 4: prefijoActual[0] = "+505 "; break; // Nicaragua
                    case 5: prefijoActual[0] = "+506 "; break; // Costa Rica
                    case 6: prefijoActual[0] = "+52 ";  break; // México
                    case 7: prefijoActual[0] = "+1 ";   break; // USA
                    case 8: prefijoActual[0] = "+507 "; break; // Panamá
                    case 9: prefijoActual[0] = "+501 "; break; // Belice
                    case 10: prefijoActual[0] = "+34 "; break; // España
                    default: prefijoActual[0] = "";
                }

                editTelefono.setText(prefijoActual[0]);
                editTelefono.setSelection(editTelefono.getText().length());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🧩 Evitar borrar el prefijo
        editTelefono.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!prefijoActual[0].isEmpty() && !s.toString().startsWith(prefijoActual[0])) {
                    editTelefono.setText(prefijoActual[0]);
                    editTelefono.setSelection(editTelefono.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // --- Abrir galería ---
        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // --- Ver contactos ---
        btnVerContactos.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaContactosTL01Examen321070009.class);
            startActivity(intent);
        });

        // --- Ver si venimos de "editar" ---
        Intent intent = getIntent();
        if (intent.hasExtra("editar_id")) {
            contactoEditarId = intent.getIntExtra("editar_id", -1);
            cargarDatosContacto(contactoEditarId, editNombre, editTelefono, editNota, spinnerPais, btnGuardar);
        }

        // --- Guardar / Actualizar contacto ---
        btnGuardar.setOnClickListener(v -> {
            String pais = spinnerPais.getSelectedItem().toString();
            String nombre = editNombre.getText().toString();
            String telefono = editTelefono.getText().toString();
            String nota = editNota.getText().toString();

            if (pais.equals("Seleccionar país")) {
                Toast.makeText(this, "⚠️ Debe seleccionar un país", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nombre.isEmpty()) {
                Toast.makeText(this, "⚠️ Debe escribir un nombre", Toast.LENGTH_SHORT).show();
                return;
            }
            // --- Validar teléfono ---
            if (telefono.isEmpty() || telefono.length() < (prefijoActual[0].length() + 4)) {
                Toast.makeText(this, "⚠️ Debe ingresar un número de teléfono válido", Toast.LENGTH_SHORT).show();
                editTelefono.requestFocus();
                return;
            }
            if (nota.isEmpty()) {
                Toast.makeText(this, "⚠️ Debe escribir una nota", Toast.LENGTH_SHORT).show();
                return;
            }

            String imagePath = (imageUriSeleccionada != null) ? imageUriSeleccionada.toString() : "foto_default";

            if (contactoEditarId == -1) {
                Contact contact = new Contact(pais, nombre, telefono, nota, imagePath);
                dbHelper.addContact(contact);
                Toast.makeText(this, "✅ Contacto guardado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Contact contact = new Contact(contactoEditarId, pais, nombre, telefono, nota, imagePath);
                dbHelper.updateContact(contact);
                Toast.makeText(this, "✅ Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
                contactoEditarId = -1;
                btnGuardar.setText("Guardar contacto");
            }

            editNombre.setText("");
            editTelefono.setText("");
            editNota.setText("");
            spinnerPais.setSelection(0);
            imageViewContacto.setImageResource(R.mipmap.ic_launcher_round);
            imageUriSeleccionada = null;
        });
    }

    // --- Mostrar imagen seleccionada ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriSeleccionada = data.getData();
            imageViewContacto.setImageURI(imageUriSeleccionada);
        }
    }

    // --- Cargar datos para editar ---
    private void cargarDatosContacto(int id, EditText nombre, EditText telefono, EditText nota, Spinner pais, Button btnGuardar) {
        List<Contact> contactos = dbHelper.getAllContacts();
        for (Contact c : contactos) {
            if (c.getId() == id) {
                nombre.setText(c.getName());
                telefono.setText(c.getPhone());
                nota.setText(c.getNote());

                ArrayAdapter adapter = (ArrayAdapter) pais.getAdapter();
                int position = adapter.getPosition(c.getCountry());
                pais.setSelection(position);

                imageUriSeleccionada = Uri.parse(c.getImage());
                if (!c.getImage().equals("foto_default")) {
                    imageViewContacto.setImageURI(imageUriSeleccionada);
                }
                btnGuardar.setText("Actualizar contacto");
                break;
            }
        }
    }
}
