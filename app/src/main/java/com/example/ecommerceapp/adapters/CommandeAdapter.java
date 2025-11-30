package com.example.ecommerceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.models.Commande;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommandeAdapter extends RecyclerView.Adapter<CommandeAdapter.CommandeViewHolder> {

    private Context context;
    private List<Commande> commandeList;
    private SimpleDateFormat dateFormat;

    public CommandeAdapter(Context context, List<Commande> commandeList) {
        this.context = context;
        this.commandeList = commandeList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public CommandeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_commande, parent, false);
        return new CommandeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommandeViewHolder holder, int position) {
        Commande commande = commandeList.get(position);

        holder.textViewId.setText("Commande #" + (position + 1));

        if (commande.getDate() != null) {
            holder.textViewDate.setText("Date: " + dateFormat.format(commande.getDate()));
        }

        holder.textViewTotal.setText(String.format(Locale.getDefault(), "Total : %.2f DT", commande.getTotal()));
    }

    @Override
    public int getItemCount() {
        return commandeList.size();
    }

    static class CommandeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId, textViewDate, textViewTotal;

        public CommandeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.textViewCommandeId);
            textViewDate = itemView.findViewById(R.id.textViewCommandeDate);
            textViewTotal = itemView.findViewById(R.id.textViewCommandeTotal);
        }
    }
}