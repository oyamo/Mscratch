package yasis.apps.m_scratch;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

public class RechargeActivity extends AppCompatActivity {
    TextView recharge, carrier;
    Button rechargebtn;
    RadioButton sim1,sim2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        recharge = findViewById(R.id.code);
        carrier = findViewById(R.id.Carriertype);
        rechargebtn = findViewById(R.id.rechargebtn);
        sim1 = findViewById(R.id.line1);
        sim2 = findViewById(R.id.line2);
        Bundle bundle = getIntent().getExtras();
        final String str = bundle.getString("code");
        String carrierString = bundle.getString("Carrier", "NULL");
        recharge.setText("Scratch PIN: " + str);
        carrier.setText("Detected Carrier:" + carrierString);
        final String ussd = carrierString.equalsIgnoreCase("Airtel")? "130" : carrierString.equalsIgnoreCase("Telcom") ? "130" : "141";
        rechargebtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(!sim1.isChecked() && !sim2.isChecked()){
                    Toast.makeText(RechargeActivity.this, "Select Slot", Toast.LENGTH_SHORT).show();
                    return;
                };
                int slot = sim2.isChecked()?1:0;
                String code = "*" + ussd + "*" + str.replace(" ","") + Uri.encode("#");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + code));
                intent.putExtra("simSlot",slot);
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RechargeActivity.this,new String[]{Manifest.permission.CALL_PHONE},100);
                }else{
                    startActivity(intent);
                }

            }
        });
    }
}
