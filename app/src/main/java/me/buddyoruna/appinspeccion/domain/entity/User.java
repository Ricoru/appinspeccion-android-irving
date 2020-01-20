package me.buddyoruna.appinspeccion.domain.entity;

import androidx.annotation.Keep;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@Keep
@IgnoreExtraProperties
public class User {

    @Exclude
    public String key;

}
