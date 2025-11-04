package com.example.tl01examen321070009;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LlamadaTL01Examen321070009 extends AppCompatActivity {

    private String numeroTelefono;
    private String nombreContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llamada_tl01_examen321070009);

        TextView txtNombre = findViewById(R.id.txtNombreLlamada);
        TextView txtNumero = findViewById(R.id.txtNumeroLlamada);
        Button btnMarcar = findViewById(R.id.btnMarcar);
        Button btnAtras = findViewById(R.id.btnAtrasLlamada);

        // 🔹 Recibir datos desde el Intent
        nombreContacto = getIntent().getStringExtra("nombre");
        numeroTelefono = getIntent().getStringExtra("telefono");

        if (numeroTelefono == null || numeroTelefono.isEmpty()) {
            Toast.makeText(this, "No se recibió número de teléfono", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (nombreContacto == null || nombreContacto.isEmpty()) {
            nombreContacto = "Contacto desconocido";
        }

        txtNombre.setText(nombreContacto);
        txtNumero.setText("📞 " + numeroTelefono);

        // 🔙 Botón atrás
        btnAtras.setOnClickListener(v -> finish());

        // 📞 Llamar directamente (ya fue confirmado en la lista)
        btnMarcar.setOnClickListener(v -> realizarLlamada());
    }

    // 🚀 Llamada directa usando ACTION_CALL
    private void realizarLlamada() {
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + numeroTelefono));

        // ✅ Verificar permiso antes de llamar
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            startActivity(intent);
        }
    }
}
