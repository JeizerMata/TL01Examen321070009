package com.example.tl01examen321070009;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends BaseAdapter {

    private Context context;
    private List<Contact> contactList;
    private List<Contact> originalList;
    private int selectedPosition = -1; // contacto seleccionado visualmente

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = new ArrayList<>(contactList);
        this.originalList = new ArrayList<>(contactList);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return contactList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        }

        Contact contact = contactList.get(position);

        TextView txtName = convertView.findViewById(R.id.txtName);
        TextView txtPhone = convertView.findViewById(R.id.txtPhone);
        ImageView imgContact = convertView.findViewById(R.id.imgContact);
        ImageButton btnLlamar = convertView.findViewById(R.id.btnLlamar);

        txtName.setText(contact.getName());
        txtPhone.setText(contact.getPhone());

        // 🧩 Mostrar imagen segura
        if (contact.getImage() == null || contact.getImage().equals("foto_default")) {
            imgContact.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            try {
                imgContact.setImageURI(Uri.parse(contact.getImage()));
            } catch (Exception e) {
                imgContact.setImageResource(R.mipmap.ic_launcher_round);
            }
        }

        // 📞 Acción del botón Llamar con confirmación
        btnLlamar.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Confirmar llamada")
                    .setMessage("¿Deseas llamar a " + contact.getName() + " al número " + contact.getPhone() + "?")
                    .setPositiveButton("Llamar", (dialogInterface, which) -> {
                        // Abrir pantalla de llamada
                        Intent intent = new Intent(context, LlamadaTL01Examen321070009.class);
                        intent.putExtra("nombre", contact.getName());
                        intent.putExtra("telefono", contact.getPhone());
                        context.startActivity(intent);
                    })
                    .setNegativeButton("Cancelar", null)
                    .create();

            dialog.show();

            // 💚 Personalizar botón "Llamar"
            Button btnLlamarDialog = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (btnLlamarDialog != null) {
                btnLlamarDialog.setTextColor(context.getResources().getColor(android.R.color.white));
                btnLlamarDialog.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            }

            // 🩶 Botón "Cancelar" gris
            Button btnCancelarDialog = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (btnCancelarDialog != null) {
                btnCancelarDialog.setTextColor(context.getResources().getColor(android.R.color.white));
                btnCancelarDialog.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        });

        // 🎨 Sombreado visual del contacto seleccionado
        if (position == selectedPosition) {
            convertView.setBackgroundColor(0xFF2A2A2A); // gris opaco
            txtName.setTextColor(0xFFFFFFFF);
        } else {
            convertView.setBackgroundColor(0xFF1E1E1E); // color base
            txtName.setTextColor(0xFFFFFFFF);
        }

        return convertView;
    }

    // 🔍 Filtro de búsqueda
    public void filter(String text) {
        contactList.clear();
        if (text.isEmpty()) {
            contactList.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (Contact c : originalList) {
                if (c.getName().toLowerCase().contains(text) || c.getPhone().contains(text)) {
                    contactList.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }

    // ✅ Selección visual del contacto
    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
