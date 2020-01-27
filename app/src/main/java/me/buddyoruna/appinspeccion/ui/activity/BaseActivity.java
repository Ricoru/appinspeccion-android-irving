package me.buddyoruna.appinspeccion.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.afollestad.materialdialogs.MaterialDialog;

import me.buddyoruna.appinspeccion.R;
import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.ui.contract.IBaseOnClick;
import me.buddyoruna.appinspeccion.ui.util.GPSUtil;
import me.buddyoruna.appinspeccion.ui.util.MessageUtil;
import me.buddyoruna.appinspeccion.ui.util.PermisosUtil;

public class BaseActivity extends AppCompatActivity {

    protected MaterialDialog mShowMessageDialog;
    protected MasterSession mMasterSession;
    protected Dialog mSuccessDialog;
    protected GPSUtil mGpsUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMasterSession = MasterSession.getInstance(getApplicationContext());
        mGpsUtil = new GPSUtil(BaseActivity.this);

        if (!PermisosUtil.hasLocationPermission(BaseActivity.this)) {
            MessageUtil.message(BaseActivity.this, "Es aplicación requiere permisos de GPS, por favor habilitar los permisos para continuar");
            PermisosUtil.askLocationPermission(BaseActivity.this);
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void showSuccessDialog(String title, String descripcion, IBaseOnClick iBaseOnClick) {
        mSuccessDialog = new Dialog(this);
        mSuccessDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        mSuccessDialog.setContentView(R.layout.content_dialog_success);
        mSuccessDialog.setCancelable(false);
        mSuccessDialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(mSuccessDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) mSuccessDialog.findViewById(R.id.txtTitle)).setText(title);
        if (!TextUtils.isEmpty(descripcion)) {
            ((TextView) mSuccessDialog.findViewById(R.id.txtDescripcion)).setText(descripcion);
        } else {
            mSuccessDialog.findViewById(R.id.txtDescripcion).setVisibility(View.GONE);
        }

        ((AppCompatButton) mSuccessDialog.findViewById(R.id.btnOk)).setOnClickListener(view -> {
            mSuccessDialog.dismiss();
            iBaseOnClick.onBtnClick();
        });

        mSuccessDialog.show();
        mSuccessDialog.getWindow().setAttributes(lp);
    }

    protected void showMessageDialog(String content, IBaseOnClick iBaseOnClick) {
        mShowMessageDialog = new MaterialDialog.Builder(BaseActivity.this)
                .title("Mensaje")
                .content(content)
                .titleColorRes(R.color.colorAccent)
                .widgetColorRes(R.color.colorPrimary)
                .positiveColorRes(R.color.colorPrimaryDark)
                .positiveText(R.string.action_ok)
                .onPositive((dialog, which) -> {
                    hideMessageDialog();
                    iBaseOnClick.onBtnClick();
                })
                .build();
        mShowMessageDialog.show();
    }

    protected void hideMessageDialog() {
        mShowMessageDialog.dismiss();
    }

}
