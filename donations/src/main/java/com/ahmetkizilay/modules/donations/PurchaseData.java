package com.ahmetkizilay.modules.donations;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahmetkizilay on 14.07.2014.
 */
public class PurchaseData {
    private String mOrderId;
    private String mPackageName;
    private String mProductId;
    private long mPurchaseTime;
    private int mPurchaseState;
    private String mDeveloperPayload;
    private String mPurchaseToken;

    public PurchaseData() {

    }

    public String getOrderId() {
        return mOrderId;
    }

    public void setOrderId(String mOrderId) {
        this.mOrderId = mOrderId;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getProductId() {
        return mProductId;
    }

    public void setProductId(String mProductId) {
        this.mProductId = mProductId;
    }

    public long getPurchaseTime() {
        return mPurchaseTime;
    }

    public void setPurchaseTime(long mPurchaseTime) {
        this.mPurchaseTime = mPurchaseTime;
    }

    public int getPurchaseState() {
        return mPurchaseState;
    }

    public void setPurchaseState(int mPurchaseState) {
        this.mPurchaseState = mPurchaseState;
    }

    public String getDeveloperPayload() {
        return mDeveloperPayload;
    }

    public void setDeveloperPayload(String mDeveloperPayload) {
        this.mDeveloperPayload = mDeveloperPayload;
    }

    public String getPurchaseToken() {
        return mPurchaseToken;
    }

    public void setPurchaseToken(String mPurchaseToken) {
        this.mPurchaseToken = mPurchaseToken;
    }

    public static PurchaseData parseJSON(String str) {
        try {
            JSONObject jo = new JSONObject(str);

            PurchaseData data = new PurchaseData();
            data.mOrderId = jo.getString("orderId");
            data.mPackageName = jo.getString("packageName");
            data.mProductId = jo.getString("productId");
            data.mPurchaseTime = jo.getLong("purchaseTime");
            data.mPurchaseState = jo.getInt("purchaseState");
            data.mDeveloperPayload = jo.getString("developerPayload");
            data.mPurchaseToken = jo.getString("purchaseToken");

            return data;
        } catch (JSONException e) {
            return null;
        }
    }
}
