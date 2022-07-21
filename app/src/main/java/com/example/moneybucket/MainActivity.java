package com.example.moneybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneybucket.adapters.BasketAdapter;
import com.example.moneybucket.models.Basket;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity {

    private TextView tvCurrentBalance;
    private DatabaseReference mainRef;
    private Dialog balanceDialog, basketDialog;
    private TextInputEditText edtCurrentBalance, edtBasketName, edtBasketBudget;
    private ImageButton imgBtnCloseEditBalancePopOut, imgBtnCloseCreateBasket;
    private Button btnConfirm, btnCreate;
    private CircleButton btnCreateBucket;
    private RecyclerView rvBaskets;

    private String currentBalance, basketName, basketBudget;
    private BasketAdapter basketAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentBalance = (TextView) findViewById(R.id.tvCurrentBalance);

        mainRef = FirebaseDatabase.getInstance().getReference();
        // fetch the current balance value from real-time database to the respective field
        mainRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("currentBalance")){
                    tvCurrentBalance.setText(snapshot.child("currentBalance").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // while press on the current balance amount 
        // show edit balance popup
        balanceDialog = new Dialog(this);
        tvCurrentBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditBalancePopup();
            }
        });
        
        // while press on the bucket icon button
        // display the create basket popup
        btnCreateBucket = (CircleButton) findViewById(R.id.btnCreateBucket);
        basketDialog = new Dialog(this);
        btnCreateBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateBasketPopup();
            }
        });

        // display all baskets info in recycler view
        rvBaskets = findViewById(R.id.rvBaskets);
        rvBaskets.setLayoutManager(new GridLayoutManager(this, 2));
        FirebaseRecyclerOptions<Basket> options =
                new FirebaseRecyclerOptions.Builder<Basket>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("baskets").orderByChild("basketName"), Basket.class)
                .build();
        basketAdapter = new BasketAdapter(options);
        rvBaskets.setAdapter(basketAdapter);
    }

    private void showCreateBasketPopup() {
        basketDialog.setContentView(R.layout.createbucketpopup);
        // while user press on cross-button, dismiss the popup dialog
        imgBtnCloseCreateBasket = (ImageButton) basketDialog.findViewById(R.id.imgBtnCloseCreateBasketPopOut);
        imgBtnCloseCreateBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basketDialog.dismiss();
            }
        });

        edtBasketName = (TextInputEditText) basketDialog.findViewById(R.id.edtBasketName);
        edtBasketBudget = (TextInputEditText) basketDialog.findViewById(R.id.edtBasketBudget);
        // while user press on the create button, create a new basket with respective name
        btnCreate = (Button) basketDialog.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basketName = edtBasketName.getText().toString();
                basketBudget = edtBasketBudget.getText().toString();
                if (!TextUtils.isEmpty(basketName)){
                    validateBasketName();
                }else{
                    edtBasketName.setError("Please fill in basket name");
                    return;
                }
                if (TextUtils.isEmpty(basketBudget)){
                    edtBasketBudget.setError("Please fill in your budget");
                    return;
                }

            }
        });

        basketDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        basketDialog.show();
    }

    private void validateBasketName() {
        FirebaseDatabase.getInstance().getReference().child("baskets").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(basketName)){
                    Toast.makeText(MainActivity.this, basketName+" existed!", Toast.LENGTH_SHORT).show();
                }else{
                    createBucket();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createBucket() {
        HashMap basketMap = new HashMap();
        basketMap.put("basketName", basketName);
        basketMap.put("basketBalance", "0.00");
        basketMap.put("basketBudget",basketBudget);
        FirebaseDatabase.getInstance().getReference().child("baskets").child(basketName).updateChildren(basketMap)
                .addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        basketDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Successfully created "+basketName, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                basketDialog.dismiss();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditBalancePopup() {
        balanceDialog.setContentView(R.layout.editbalancepopup);

        // while user press on cross-button, dismiss the popup dialog
        imgBtnCloseEditBalancePopOut = (ImageButton) balanceDialog.findViewById(R.id.imgBtnCloseEditBalancePopOut);
        imgBtnCloseEditBalancePopOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                balanceDialog.dismiss();
            }
        });

        edtCurrentBalance = (TextInputEditText) balanceDialog.findViewById(R.id.edtCurrentBalance);

        // while user press on confirm button, amend the current balance in database
        btnConfirm = (Button) balanceDialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check the current balance field is filled
                currentBalance = edtCurrentBalance.getText().toString();
                if (!TextUtils.isEmpty(currentBalance)){
                    updateCurrentBalance(currentBalance);
                    balanceDialog.dismiss();
                    refreshCurrentActivity();
                }else{
                    edtCurrentBalance.setError("Make sure you fill in the current balance");
                    return;
                }
            }
        });

        balanceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        balanceDialog.show();
    }

    private void refreshCurrentActivity() {
        this.recreate();
    }

    private void updateCurrentBalance(String currentBalance) {
        mainRef.child("currentBalance").setValue(currentBalance);
    }

    @Override
    protected void onStart() {
        super.onStart();
        basketAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        basketAdapter.stopListening();
    }
}