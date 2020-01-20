package me.buddyoruna.appinspeccion.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.buddyoruna.appinspeccion.R;
import me.buddyoruna.appinspeccion.domain.entity.User;
import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.ui.util.CustomDialog;
import me.buddyoruna.appinspeccion.ui.util.MessageUtil;
import me.buddyoruna.appinspeccion.ui.util.PermisosUtil;
import me.buddyoruna.appinspeccion.ui.util.Util;
import me.buddyoruna.appinspeccion.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.container)
    View view;

    Util util;
    CustomDialog progressDialog;
    MasterSession mMasterSession;

    LoginViewModel model;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        util = new Util(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mMasterSession = MasterSession.getInstance(getApplicationContext());
        model = ViewModelProviders.of(this).get(LoginViewModel.class);

        if (mMasterSession.values.currentUser != null) {
            irMain();
        }

        email.setText("ricoru21@live.com");
        password.setText("12345678");

        validatePermission();
    }

    @OnClick(R.id.btn_login)
    public void singIn() {
        String _email = email.getText().toString();
        final String _password = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(_password)) {
            password.setError(getString(R.string.error_field_required));
            MessageUtil.message(LoginActivity.this, getString(R.string.error_field_required));
            focusView = password;
            cancel = true;
        } else if (!util.isPasswordValid(_password)) {
            password.setError(getString(R.string.lbl_password_incorrect));
            MessageUtil.message(LoginActivity.this, getString(R.string.lbl_password_incorrect));
            focusView = password;
            cancel = true;
        }

        if (TextUtils.isEmpty(_email)) {
            email.setError(getString(R.string.error_field_required));
            MessageUtil.message(LoginActivity.this, getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!util.isEmailValid(_email)) {
            email.setError(getString(R.string.error_invalid_email));
            MessageUtil.message(LoginActivity.this, getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (cancel) {
            util.requestFocus(LoginActivity.this, focusView);
        } else {
            progressDialog = new CustomDialog(LoginActivity.this, getString(R.string.lbl_porfavor_espere),
                    getString(R.string.lbl_login_usuario));
            progressDialog.showDialog();

            model.loginEmail(_email, _password).observe(this, resource -> {
                progressDialog.close();
                if (resource.status.equals(Resource.Status.ERROR)) {
                    Snackbar.make(view, resource.message, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null)
                            .show();
                    return;
                }

                User user = new User();
                user.key = resource.data.getUid();
                mMasterSession.values.currentUser = user;
                mMasterSession.update();

                Snackbar.make(view, "Bienvenido " + resource.data.getEmail(), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null)
                        .show();

                irMain();
            });
        }
    }

    private boolean validatePermission() {
        if (!PermisosUtil.hasForAllPermission(LoginActivity.this)) {
            PermisosUtil.askForAllPermission(this);
            MessageUtil.message(LoginActivity.this,
                    "Se requiere aceptar todos los permisos solicitados para el correcto funcionamiento de la aplicación.");
            return false;
        }
        return true;
    }

    private void irMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
