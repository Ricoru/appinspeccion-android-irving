package me.buddyoruna.appinspeccion.ui.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import me.buddyoruna.appinspeccion.R;

/**
 * Created by ricoru on 8/03/16.
 */
public class CustomDialog {

    private Activity mActivity;
    private String mMessage;
    private String mTitulo;
    private int mResource;
    private ImageView img_cargando;
    private ProgressDialog progress;

    public CustomDialog(Activity mActivity, int mResource, String mMessage) {
        this.mActivity = mActivity;
        this.mMessage = mMessage;
        this.mResource = mResource;
        this.mTitulo = mTitulo;
    }

    public CustomDialog(Activity mActivity, String mMessage, String mTitulo) {
        this.mActivity = mActivity;
        this.mMessage = mMessage;
        this.mTitulo = mTitulo;
    }

    public void showWithoutTime() {
        progress = new ProgressDialog(mActivity);
        progress.show();
        progress.setContentView(R.layout.custom_dialog_alert);
        TextView et = (TextView) progress.findViewById(R.id.txt_msj_alert_dialog);
        et.setText(mMessage);
        img_cargando = (ImageView) progress.findViewById(R.id.img_cargando);
        Glide.with(mActivity)
                .load(mResource)
                .into(img_cargando);
        progress.setCancelable(false);
    }

    public void showDialog() {
        progress = new ProgressDialog(mActivity);
        progress.show();
        progress.setContentView(R.layout.layout_custom_dialog_alert_2);
        TextView txtmensaje = (TextView) progress.findViewById(R.id.txt_mensaje);
        txtmensaje.setText(mTitulo + "\n" + mMessage);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
    }

    public void showWithTime(final int time) {
        progress = new ProgressDialog(mActivity);
        progress.show();
        progress.setContentView(R.layout.custom_dialog_alert);
        TextView et = (TextView) progress.findViewById(R.id.txt_msj_alert_dialog);
        et.setText(mMessage);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                } catch (Exception e) {
                }
                progress.dismiss();
            }
        }).start();
    }

    public void close() {
        progress.dismiss();
    }

}
