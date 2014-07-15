package com.ahmetkizilay.modules.donations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by ahmetkizilay on 15.07.2014.
 */
public class ThankYouDialogFragment extends DialogFragment {

    public static ThankYouDialogFragment newInstance(String message) {
        ThankYouDialogFragment frag = new ThankYouDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        frag.setArguments(args);
        return frag;
    }

    public static ThankYouDialogFragment newInstance() {
        ThankYouDialogFragment frag = new ThankYouDialogFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        return dialog;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.thanks, container);

        if(getArguments().get("message") != null) {
            TextView tw = (TextView) view.findViewById(R.id.twThankYouMessage);
            tw.setText((String) getArguments().get("message"));
        }
        return view;
    }
}
