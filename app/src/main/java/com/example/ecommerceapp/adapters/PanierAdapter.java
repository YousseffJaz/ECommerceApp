package com.example.ecommerceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.models.Produit;

import java.util.List;
import java.util.Locale;

public class PanierAdapter extends RecyclerView.Adapter<PanierAdapter.PanierViewHolder> {

    private Context context;
    private List<Produit> produitList;
    private OnPanierItemListener listener;

    public interface OnPanierItemListener {
        void onRemoveClick(Produit produit, int position);
    }

    public PanierAdapter(Context context, List<Produit> produitList, OnPanierItemListener listener) {
        this.context = context;
        this.produitList = produitList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PanierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produit, parent, false);
        return new PanierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PanierViewHolder holder, int position) {
        Produit produit = produitList.get(position);

        holder.textViewNom.setText(produit.getNom());
        holder.textViewPrix.setText(String.format(Locale.getDefault(), "%.2f DT", produit.getPrix()));

        if (produit.getImageUrl() != null && !produit.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(produit.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imageView);
        }

        // Modifier le bouton pour supprimer
        holder.buttonAction.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        holder.buttonAction.setOnClickListener(v -> listener.onRemoveClick(produit, position));

        // Cacher le bouton détails
        holder.buttonDetails.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }

    public interface OnProduitRemoveListener {
        void onProduitRemove(Produit produit, int position);
    }

    static class PanierViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewNom, textViewPrix;
        View buttonDetails;
        ImageButton buttonAction;

        public PanierViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewProduit);
            textViewNom = itemView.findViewById(R.id.textViewNomProduit);
            textViewPrix = itemView.findViewById(R.id.textViewPrixProduit);
            buttonDetails = itemView.findViewById(R.id.buttonDetails);
            buttonAction = itemView.findViewById(R.id.buttonAddToCart);
        }
    }
}