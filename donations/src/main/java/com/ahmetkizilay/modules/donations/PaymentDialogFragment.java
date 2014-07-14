package com.ahmetkizilay.modules.donations;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ahmetkizilay on 13.07.2014.
 */
public class PaymentDialogFragment extends DialogFragment {

    public static PaymentDialogFragment getInstance(int resProductIds) {
        PaymentDialogFragment frag = new PaymentDialogFragment();
        Bundle args = new Bundle();
        args.putInt("productIds", resProductIds);
        frag.setArguments(args);
        return frag;
    }

    public static PaymentDialogFragment getInstance(String title, int resProductIds) {
        PaymentDialogFragment frag = new PaymentDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("productIds", resProductIds);
        frag.setArguments(args);
        return frag;
    }

    private boolean inDevMode = false;

    private String[] mProductIds;
    private String mTitle;

    private PaymentViewGroup vgPayment;
    private WaitViewGroup vgWait;

    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;

    private boolean bServiceBound = false;
    private boolean bViewIsReady = false;

    private ProductInfo[] mProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            PackageInfo pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            int flags = pi.applicationInfo.flags;
            this.inDevMode = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
        catch(Exception exp) {}

        Bundle args = getArguments();
        this.mProductIds = getResources().getStringArray(getArguments().getInt("productIds"));
        this.mTitle = args.get("title") != null ? (String) args.get("title") : "Help me buy ...";

        this.setupBillingService();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_dialog, container);
        getDialog().setTitle(this.mTitle);
        getDialog().setCancelable(true);

        this.vgPayment = (PaymentViewGroup) view.findViewById(R.id.vgPayment);
        this.vgWait = (WaitViewGroup) view.findViewById(R.id.vgWait);

        bViewIsReady = true;

        handleProductDetails();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyBillingService();
    }

    private void destroyBillingService() {
        if (mService != null) {
            getActivity().unbindService(mServiceConn);
        }
    }

    private void setupBillingService() {
        this.mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                bServiceBound = true;
                handleProductDetails();
            }
        };

        getActivity().bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
                mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private void handleProductDetails() {
        if(!bServiceBound || !bViewIsReady) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                ArrayList<String> skuList = new ArrayList<String>();
                for(int i = 0; i < mProductIds.length; i += 1) {
                    skuList.add(mProductIds[i]);
                }

                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                ProductInfo[] arrProductInfo = null;
                try {
                    Bundle skuDetails = mService.getSkuDetails(3, getActivity().getPackageName(), "inapp", querySkus);
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if(response == 0) {
                        if(inDevMode) {
                            arrProductInfo = new ProductInfo[4];
                            arrProductInfo[0] = new ProductInfo("test_coffee", "a cup of coffee", "$2.00");
                            arrProductInfo[1] = new ProductInfo("test_sandwich", "a sandwich", "$4.00");
                            arrProductInfo[2] = new ProductInfo("test_github", "a month of Github", "$10.00");
                            arrProductInfo[3] = new ProductInfo("test_soundcloud", "a month of soundcloud", "$12.00");
                        }
                        else {
                            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                            arrProductInfo = new ProductInfo[responseList.size()];

                            int i = 0;
                            for (String thisResponse : responseList) {
                                JSONObject object = new JSONObject(thisResponse);
                                String sku = object.getString("productId");
                                String price = object.getString("price");
                                String name = object.getString("title");

                                arrProductInfo[i++] = new ProductInfo(sku, name, price);
                            }
                        }

                        mProducts = arrProductInfo;
                    }
                }
                catch(RemoteException re) {
                    re.printStackTrace();
                }
                catch(JSONException je) {
                    je.printStackTrace();
                }

                // meaning unable to reach google services for some reason
                // dismissing the fragment
                if(arrProductInfo == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(PaymentDialogFragment.this.getActivity(), "Unable to retrieve products", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });
                    return;
                }

                // trigger adding data to the payment radiogroup
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        vgPayment.setProducts(mProducts);
                        vgPayment.setVisibility(View.VISIBLE);
                        vgWait.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }
}
