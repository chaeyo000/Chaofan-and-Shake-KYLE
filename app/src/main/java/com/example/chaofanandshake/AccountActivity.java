package com.example.chaofanandshake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    private ImageView backBtn; // Declare only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountdetails);

        backBtn = findViewById(R.id.backBtn); // Initialize after setting layout
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

 //       backBtn.setOnClickListener(new View.OnClickListener() {
 //           @Override
 //           public void onClick(View view) {
 //               onBackPressed();
 //           }
//        });
 //   }
//
//    @Override
 //   public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(AccountActivity.this, DashboardbtnActivity.class);
 //       startActivity(intent);
//        finish(); // Prevents going back to login
//    }
}


//   @Override
//   public void onBackPressed() {
//       super.onBackPressed();
//       if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//           getSupportFragmentManager().popBackStack();
//        } else {
//            finish();
//        }
//    }
//}
