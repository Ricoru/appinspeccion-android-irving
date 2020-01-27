package me.buddyoruna.appinspeccion.model.storage;

import java.util.ArrayList;
import java.util.List;

import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;
import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;
import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.User;

public class SessionData {

    public double currentMyPositionLatitude = 0;
    public double currentMyPositionLongitude = 0;

    public List<TipoInspeccion> tipoInspeccionList = new ArrayList<>();
    public List<EstadoInspeccion> estadoInspeccionList = new ArrayList<>();
    public List<BuzonInicio> buzonInicioList = new ArrayList<>();
    public List<BuzonFin> buzonFinList = new ArrayList<>();
    public List<FileInspeccion> fileListFormDynamic = new ArrayList<>();
    public User currentUser;

}
