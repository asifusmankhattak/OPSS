package pbs.sme.survey.helper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import pbs.sme.survey.R;
import static androidx.core.content.res.ResourcesCompat.getColor;

public class DialogHelper {

    private Dialog alertDialog, progressDialog;


    public void showProgressDialog(Context context, String title, String msg, boolean cancelable) {
        progressDialog = new Dialog(context);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);

        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_dialog);
        ProgressBar mProgressBar = (ProgressBar) progressDialog.findViewById(R.id.progress_bar);
        //  mProgressBar.getIndeterminateDrawable().setColorFilter(context.getResources()
        // .getColor(R.color.material_blue_gray_500), PorterDuff.Mode.SRC_IN);
        TextView progressTitle = progressDialog.findViewById(R.id.progress_title);
        progressTitle.setText("" + title);
        TextView progressMsg = progressDialog.findViewById(R.id.progress_msg);
        progressMsg.setText("" + msg);
        progressMsg.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        // you can change or add this line according to your need
        mProgressBar.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawableResource(R.drawable.round_border);
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(cancelable);
        progressDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(progressDialog.getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        progressDialog.getWindow().setAttributes(layoutParams);
    }



    public void SingleClickDialogError(Context context, String title, String msg, String btn, View.OnClickListener callback) {
        alertDialog = new Dialog(context);

        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.alert_dialog);
        RelativeLayout alertTop = alertDialog.findViewById(R.id.alert_top);
        alertTop.setBackgroundColor(Color.RED);
        TextView alertTitle = alertDialog.findViewById(R.id.alert_title);
        alertTitle.setText("" + title);
        TextView alertMsg = alertDialog.findViewById(R.id.alert_msg);
        alertMsg.setText("" + msg);
        ImageView statusImg = alertDialog.findViewById(R.id.status_image);
        statusImg.setImageResource(R.drawable.ic_error);
        Button okButton = alertDialog.findViewById(R.id.ok_button);
        okButton.setText(btn);
        okButton.setBackgroundColor(Color.RED);
        okButton.setOnClickListener(callback);
        //alertDialog.setCancelable(false);
        GradientDrawable drawable = (GradientDrawable)AppCompatResources.getDrawable(context,R.drawable.round_border);
        drawable.setStroke(3, Color.RED);
        alertDialog.getWindow().setBackgroundDrawable(drawable);

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = width;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    public void hideSingleError() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }



}
