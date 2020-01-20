package me.buddyoruna.appinspeccion.repository;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;

import me.buddyoruna.appinspeccion.domain.response.Resource;

/**
 * Created by Ricoru on 17/01/18.
 */

public interface LoginRepository {

    LiveData<Resource<FirebaseUser>> loginEmail(String email, String pass);

    LiveData<Resource> isLogged();

}
