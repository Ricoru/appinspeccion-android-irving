package me.buddyoruna.appinspeccion.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.repository.LoginRepository;
import me.buddyoruna.appinspeccion.repository.LoginRepositoryImpl;

public class LoginViewModel extends ViewModel {

    private final LoginRepository repository;

    public LoginViewModel() {
        this.repository = new LoginRepositoryImpl();
    }

    public LiveData<Resource<FirebaseUser>> loginEmail(String email, String pass) {
        return repository.loginEmail(email, pass);
    }

    public LiveData<Resource> isLogged() {
        return repository.isLogged();
    }

}