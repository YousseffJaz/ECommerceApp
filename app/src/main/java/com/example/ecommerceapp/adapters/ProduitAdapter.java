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
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ProduitViewHolder> {

    private Context context;
    private List<Produit> produitList;
    private List<Produit> produitListFull; // Pour la recherche
    private OnProduitClickListener listener;

    public interface OnProduitClickListener {
        void onDetailsClick(Produit produit);
        void onAddToCartClick(Produit produit);

        void onProduitClick(Produit produit);
    }

    public ProduitAdapter(Context context, List<Produit> produitList, OnProduitClickListener listener) {
        this.context = context;
        this.produitList = produitList;
        this.produitListFull = new ArrayList<>(produitList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProduitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_produit, parent, false);
        return new ProduitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProduitViewHolder holder, int position) {
        Produit produit = produitList.get(position);

        holder.textViewNom.setText(produit.getNom());
        holder.textViewPrix.setText(String.format(Locale.getDefault(), "Prix : %.2f DT", produit.getPrix()));

        // Charger l'image avec Glide
        if (produit.getImageUrl() != null && !produit.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(produit.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.imageView);
        }

        holder.buttonDetails.setOnClickListener(v -> listener.onDetailsClick(produit));
        holder.buttonAddToCart.setOnClickListener(v -> listener.onAddToCartClick(produit));
    }

    @Override
    public int getItemCount() {
        return produitList.size();
    }

    public void filter(String text) {
        produitList.clear();
        if (text.isEmpty()) {
            produitList.addAll(produitListFull);
        } else {
            text = text.toLowerCase();
            for (Produit produit : produitListFull) {
                if (produit.getNom().toLowerCase().contains(text)) {
                    produitList.add(produit);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ProduitViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewNom, textViewPrix;
        MaterialButton buttonDetails;
        ImageButton buttonAddToCart;

        public ProduitViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewProduit);
            textViewNom = itemView.findViewById(R.id.textViewNomProduit);
            textViewPrix = itemView.findViewById(R.id.textViewPrixProduit);
            buttonDetails = itemView.findViewById(R.id.buttonDetails);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }
    }
}