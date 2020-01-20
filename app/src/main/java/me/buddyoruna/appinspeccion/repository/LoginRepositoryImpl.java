package me.buddyoruna.appinspeccion.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import me.buddyoruna.appinspeccion.MvpApp;
import me.buddyoruna.appinspeccion.R;
import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.ui.util.Constant;

public class LoginRepositoryImpl implements LoginRepository {

    public static String TAG = LoginRepositoryImpl.class.getSimpleName();

    private static String rootName = "users";

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    MasterSession masterSession;
    DocumentReference documentReference;
    CollectionReference collectionReference;

    public LoginRepositoryImpl() {
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        masterSession = MasterSession.getInstance(MvpApp.getContext());
        collectionReference = FirebaseFirestore.getInstance().collection(rootName);
    }

    @Override
    public LiveData<Resource<FirebaseUser>> loginEmail(String email, String pass) {
        final MediatorLiveData<Resource<FirebaseUser>> mObservableResult = new MediatorLiveData<>();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    Log.i("INFO", "task.isSuccessfu " + task.isSuccessful());
                    if (!task.isSuccessful()) {
                        mObservableResult.setValue(Resource.error(MvpApp.getContext().getString(R.string.error_authentication), null));
                    } else {
                        mObservableResult.setValue(Resource.success(mAuth.getCurrentUser()));
                    }

                });
        return mObservableResult;
    }

    @Override
    public LiveData<Resource> isLogged() {
        final MediatorLiveData<Resource> mObservableResult = new MediatorLiveData<>();
        if (null != mUser) {
            documentReference = collectionReference.document(mUser.getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                        masterSession.values.user = document.toObject(User.class);
//                        masterSession.values.user.key = document.getId();
//                        masterSession.values.user.setHasMapInit();
//                        masterSession.update();
                        mObservableResult.setValue(Resource.success(Constant.STATUS_LOGIN_IR_HOME));
                    } else {
                        mObservableResult.setValue(Resource.success(Constant.STATUS_LOGIN_IR_CREATE_ACCOUNT));
                    }
                } else {
                    mObservableResult.setValue(Resource.error(task.getException().getMessage(), null));
                }
            });
        } else {
            mObservableResult.setValue(Resource.success(Constant.STATUS_LOGIN_IN_LOGIN));
        }
        return mObservableResult;
    }

}
