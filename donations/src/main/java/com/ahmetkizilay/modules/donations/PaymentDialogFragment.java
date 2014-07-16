package com.ahmetkizilay.modules.donations;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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
public class PaymentDialogFragment extends DialogFragment implements PaymentViewGroup.PaymentOptionSelectionListener {

    public static final int PAYMENT_RESULT_CODE = 4426;
    private static final String PURCHASE_TYPE = "inapp";

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
    private boolean mIgnoreDev = false;

    private String[] mProductIds;
    private String mTitle;

    private PaymentViewGroup vgPayment;
    private WaitViewGroup vgWait;

    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;

    private boolean bServiceBound = false;
    private boolean bViewIsReady = false;

    private ProductInfo[] mProducts;

    private PaymentCompletedListener mCallback;

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
        this.mTitle = args.get("title") != null ? (String) args.get("title") : "Help me buy ...";
        this.mProductIds = getResources().getStringArray((this.inDevMode && !this.mIgnoreDev) ? R.array.test_product_ids : getArguments().getInt("productIds"));

        this.setupBillingService();
    }

    public void ignoreDevMode() {
        this.mIgnoreDev = true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_dialog, container);
        getDialog().setTitle(this.mTitle);
        getDialog().setCancelable(false);

        this.vgPayment = (PaymentViewGroup) view.findViewById(R.id.vgPayment);
        this.vgWait = (WaitViewGroup) view.findViewById(R.id.vgWait);
        this.vgPayment.setPaymentOptionSelectionListener(this);

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
                    Bundle skuDetails = mService.getSkuDetails(3, getActivity().getPackageName(), PURCHASE_TYPE, querySkus);
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if(response == 0) {
                        if(inDevMode && !mIgnoreDev) {
                            arrProductInfo = new ProductInfo[4];
                            arrProductInfo[0] = new ProductInfo("android.test.purchased", "an item for purchase", "$5.00");
                            arrProductInfo[1] = new ProductInfo("android.test.canceled", "an item to cancel", "$10.00");
                            arrProductInfo[2] = new ProductInfo("android.test.refunded", "an item to refund", "$15.00");
                            arrProductInfo[3] = new ProductInfo("android.test.item_unavailable", "an unavailable item", "$22.00");
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

                        mProducts = reorderProductInfo(arrProductInfo);
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

    private ProductInfo[] reorderProductInfo(ProductInfo[] unsorted) {
        ProductInfo[] sorted = new ProductInfo[unsorted.length];
        int sortedCount = 0;
        for(int i = 0; i < mProductIds.length; i += 1) {
            for(int j = 0; j < unsorted.length; j += 1) {
                if(this.mProductIds[i].equals(unsorted[j].getProductId())) {
                    sorted[sortedCount++] = unsorted[j];
                    break;
                }
            }
        }

        return sorted;
    }

    private String mDeveloperPayload = "";
    @Override
    public void onPaymentOptionSelected(final String productId) {

        this.mDeveloperPayload = RandomStringCreator.nextString(36);

        // this part should be on a separate thread...
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(),
                            productId, PURCHASE_TYPE, mDeveloperPayload);

                    // this part is about making sure the item is consumed
                    int buyIntentResponse = buyIntentBundle.getInt("RESPONSE_CODE");
                    if(buyIntentResponse == 7) {
                        String purchaseToken = "inapp:" + getActivity().getPackageName() + ":" + productId;
                        int consumeResult = mService.consumePurchase(3, getActivity().getPackageName(),
                                purchaseToken);
                        if(consumeResult != 0) {
                            throw new Exception("failed to consume product already owned");
                        }
                        else {
                            // sending buy intent once again
                            buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(),
                                    productId, PURCHASE_TYPE, mDeveloperPayload);
                            buyIntentResponse = buyIntentBundle.getInt("RESPONSE_CODE");
                            if(buyIntentResponse != 0) {
                                throw new Exception("buy intent failed after consuming");
                            }
                        }
                    }
                    else if(buyIntentResponse != 0) {
                        throw new Exception("Unexpected Buy Intent Response: " + buyIntentResponse);
                    }

                    PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                    getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(),
                            PAYMENT_RESULT_CODE,
                            new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                }
                catch(RemoteException re) {
                    Log.d("Payment-RE", re.getMessage() != null ? re.getMessage() : "");
                    makeAsyncToast("Cannot complete your request");
                } catch (IntentSender.SendIntentException e) {
                    Log.d("Payment-SIE", e.getMessage() != null ? e.getMessage() : "");
                    makeAsyncToast("Cannot complete your request");
                }
                catch(Exception e) {
                    Log.d("Payment", e.getMessage() != null ? e.getMessage() : "");
                    makeAsyncToast("Cannot complete your request");
                }
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_RESULT_CODE) {

            int responseCode = data.getIntExtra("RESPONSE_CODE", -1);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            if(resultCode != getActivity().RESULT_OK ||responseCode != 0 || purchaseData == null) {
                Toast.makeText(getActivity(), "Your payment could not be completed!", Toast.LENGTH_SHORT).show();
                return;
            }

            final PurchaseData objPurchaseData = PurchaseData.parseJSON(purchaseData);
            if(!objPurchaseData.getDeveloperPayload().equals(this.mDeveloperPayload)) {
                Toast.makeText(getActivity(), "unexpected payload value", Toast.LENGTH_SHORT).show();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                try {
                    // we don't really care about immediately consuming the product
                    // so ignoring the return value here
                    mService.consumePurchase(3, getActivity().getPackageName(), objPurchaseData.getPurchaseToken());
                }
                catch(RemoteException re) {
                    re.printStackTrace();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                dismiss();

                if(mCallback != null) {
                    mCallback.onPaymentCompleted();
                }
                }
            }).start();
        }
    }

    public void setPaymentCompletedListener(PaymentCompletedListener callback) {
        this.mCallback = callback;
    }

    public interface PaymentCompletedListener {
        public void onPaymentCompleted();
    }

    private void makeAsyncToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
