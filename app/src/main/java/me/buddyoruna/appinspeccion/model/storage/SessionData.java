package me.buddyoruna.appinspeccion.model.storage;

import java.util.ArrayList;
import java.util.List;

import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.User;

public class SessionData {

    public double currentMyPositionLatitude = 0;
    public double currentMyPositionLongitude = 0;

    public List<FileInspeccion> fileListFormDynamic = new ArrayList<>();
    public User currentUser;

}
