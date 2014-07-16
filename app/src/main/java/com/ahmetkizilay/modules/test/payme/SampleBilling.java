package com.ahmetkizilay.modules.test.payme;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ahmetkizilay.modules.donations.PaymentDialogFragment;
import com.ahmetkizilay.modules.donations.ThankYouDialogFragment;

import java.util.Locale;

public class SampleBilling extends FragmentActivity {

    private static final String PAYMENT_FRAGMENT_TAG = "frag-dialog";
    private static final String THANKYOU_FRAGMENT_TAG = "frag-thanks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_billing);

        Configuration config = getResources().getConfiguration();
        if (config.locale == null)
            config.locale = Locale.getDefault();

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
                PaymentDialogFragment payment = PaymentDialogFragment.getInstance(R.array.product_ids);
                payment.ignoreDevMode();
                payment.setPaymentCompletedListener(new PaymentDialogFragment.PaymentCompletedListener() {
                    @Override
                    public void onPaymentCompleted() {
                        pushToStack(ThankYouDialogFragment.newInstance("You are A.W.E.S.O.M.E!"), THANKYOU_FRAGMENT_TAG);
                    }
                });
                pushToStack(payment, PAYMENT_FRAGMENT_TAG);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // pass the request back to the fragment
        if(requestCode == PaymentDialogFragment.PAYMENT_RESULT_CODE) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(PAYMENT_FRAGMENT_TAG);
            if (fragment != null)
            {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
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
