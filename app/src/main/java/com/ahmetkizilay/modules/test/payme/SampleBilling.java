package com.ahmetkizilay.modules.test.payme;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahmetkizilay.modules.donations.PaymentDialogFragment;

public class SampleBilling extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_billing);

        PackageManager pm = getPackageManager();
        int version = 0;
        try {
            version = pm.getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView twVersion = (TextView) findViewById(R.id.twVersion);
        twVersion.setText("Version: " + version);
        Button btn = (Button) findViewById(R.id.btnShowBilling);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushToStack(PaymentDialogFragment.getInstance(R.array.product_ids), "frag-billing");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        this.mPayment.destroyBillingService();
    }

    private void pushToStack(DialogFragment frag, String label) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(label);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        frag.show(ft, label);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sample_billing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
