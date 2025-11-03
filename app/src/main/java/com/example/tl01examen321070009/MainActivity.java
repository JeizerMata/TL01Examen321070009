package com.example.tl01examen321070009;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

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
    }

    // --- CÓDIGO PARA GUARDAR CONTACTOS ---
    ContactDbHelper dbHelper = new ContactDbHelper(this);

    // Referencias a tus campos (asegúrate que los IDs coincidan con los del XML)
    Spinner spinnerPais = findViewById(R.id.spinner);
    EditText editNombre = findViewById(R.id.editTextNombre);
    EditText editTelefono = findViewById(R.id.editTextTelefono);
    EditText editNota = findViewById(R.id.editTextNota);
    Button btnGuardar = findViewById(R.id.btnGuardar);

// Acción del botón Guardar
btnGuardar.setOnClickListener(v -> {
        String pais = spinnerPais.getSelectedItem().toString();
        String nombre = editNombre.getText().toString();
        String telefono = editTelefono.getText().toString();
        String nota = editNota.getText().toString();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el contacto y guardarlo
        Contact contact = new Contact(pais, nombre, telefono, nota, "foto_default");
        dbHelper.addContact(contact);

        Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show();

        // Limpiar los campos
        editNombre.setText("");
        editTelefono.setText("");
        editNota.setText("");
    });

}