package com.ahmetkizilay.modules.donations;

/**
 * Created by ahmetkizilay on 13.07.2014.
 */
public class ProductInfo {
    private String mProductName;
    private String mProductId;
    private String mProductPrice;

    public ProductInfo(String productId, String productName, String mProductPrice) {
        this.mProductId = productId;
        this.mProductName = productName;
        this.mProductPrice = mProductPrice;
    }

    public String getProductId() {
        return this.mProductId;
    }

    public String getProductName() {
        return this.mProductName;
    }

    public String getProductPrice() {
        return this.mProductPrice;
    }
}
