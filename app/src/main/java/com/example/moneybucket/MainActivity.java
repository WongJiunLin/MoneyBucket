package com.example.moneybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView tvCurrentBalance;
    private DatabaseReference mainRef;
    private Dialog balanceDialog;
    private TextInputEditText edtCurrentBalance;
    private ImageButton imgBtnCloseEditBalancePopOut;
    private Button btnConfirm;

    private String currentBalance;

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
}