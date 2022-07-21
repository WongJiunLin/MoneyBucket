package com.example.moneybucket.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneybucket.R;
import com.example.moneybucket.models.Basket;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.w3c.dom.Text;

public class BasketAdapter extends FirebaseRecyclerAdapter<Basket, BasketAdapter.myViewHolder> {
    public BasketAdapter(@NonNull FirebaseRecyclerOptions<Basket> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BasketAdapter.myViewHolder holder, int position, @NonNull Basket model) {
        holder.tvBasketName.setText(model.getBasketName());
        holder.tvBasketBalance.setText(model.getBasketBalance());
    }

    @NonNull
    @Override
    public BasketAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_cardview, parent, false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView tvBasketBalance;
        TextView tvBasketName;
        CardView cvBasket;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBasketName = (TextView) itemView.findViewById(R.id.tvBasketName);
            tvBasketBalance = (TextView) itemView.findViewById(R.id.tvBasketBalance);
            cvBasket = (CardView) itemView.findViewById(R.id.cvBasket);
        }
    }
}
