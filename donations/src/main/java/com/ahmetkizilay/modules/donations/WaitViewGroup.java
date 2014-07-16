package com.ahmetkizilay.modules.donations;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ahmetkizilay on 13.07.2014.
 */
public class WaitViewGroup extends LinearLayout {
    public WaitViewGroup(Context context) {
        super(context);
        init();
    }

    public WaitViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedParams(context, attrs);
        init();
    }

    public WaitViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTypedParams(context, attrs);
        init();
    }

    private String mText = "please wait";

    private void initTypedParams(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaitViewGroup, 0, 0);
        try {
            this.mText = ta.getString(R.styleable.WaitViewGroup_wait_text);
        }
        finally {
            ta.recycle();
        }
    }

    private void init() {
        LayoutInflater mInflater = LayoutInflater.from(this.getContext());
        mInflater.inflate(R.layout.wait, this, true);

        TextView tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfo.setText(this.mText);
    }
}
