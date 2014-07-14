package com.ahmetkizilay.modules.donations;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.vending.billing.IInAppBillingService;

/**
 * Created by ahmetkizilay on 12.07.2014.
 */
public class PaymentViewGroup extends LinearLayout{

    private String mSubmitText = "Thank You!";
    private RadioGroup mRadioGroup;

    private ProductInfo[] mProducts;

    private PaymentOptionSelectionListener mCallback;

    public PaymentViewGroup(Context context) {
        super(context);
        init();
    }

    public PaymentViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PaymentViewGroup, 0, 0);
        try {
            this.mSubmitText = ta.getString(R.styleable.PaymentViewGroup_submit_text);
        }
        finally {
            ta.recycle();
        }

        init();
    }

    public PaymentViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater mInflater = LayoutInflater.from(this.getContext());
        mInflater.inflate(R.layout.payment, this, true);

        final EditText etPriceTag = (EditText) findViewById(R.id.etYourPrice);

        this.mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        if(this.isInEditMode()) {
            for(int i = 0; i < 5; i  += 1) {
                createRadioButton("an item" + i, "0.00", i);
            }
        }
        else {
            // maybe something here later
        }

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        if(this.mSubmitText != null) {
            btnSubmit.setText(this.mSubmitText);
        }
        btnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mCallback != null) {
                    mCallback.onPaymentOptionSelected(mRadioGroup.getCheckedRadioButtonId() + "");// to be fixed later
                }
            }
        });

    }


    public void setProducts(ProductInfo[] products) {
        this.mProducts = products;
        for(int i = 0; i < products.length; i += 1) {
            createRadioButton(products[i].getProductName(), products[i].getProductPrice(), i);
        }
    }

    public void setPaymentOptionSelectionListener(PaymentOptionSelectionListener callback) {
        this.mCallback = callback;
    }



    private RadioButton createRadioButton(String item, String amount, int index) {
        RadioButton rb = (RadioButton) LayoutInflater.from(this.getContext()).inflate(R.layout.option_item, null);
        rb.setText(item + " - " + amount + "");
        rb.setId(index);
        this.mRadioGroup.addView(rb);

        return rb;
    }

    public interface PaymentOptionSelectionListener {
        public void onPaymentOptionSelected(String productId);
    }
}

