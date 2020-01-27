package me.buddyoruna.appinspeccion.model.mapper;

import java.util.ArrayList;
import java.util.List;

import me.buddyoruna.appinspeccion.domain.db.FormularioEntity;
import me.buddyoruna.appinspeccion.domain.entity.Formulario;

public class FormularioMapper {

    public List<Formulario> transformListFormularioEntityAtFormulario(List<FormularioEntity> formularioEntityList) {
        List<Formulario> formularioList = new ArrayList<>();
        if (formularioEntityList == null) return formularioList;
        for (FormularioEntity formularioEntity : formularioEntityList) {
            formularioList.add(transformFormularioEntity(formularioEntity));
        }
        return formularioList;
    }

    public List<FormularioEntity> transformListFormularioAtFormularioEntity(List<Formulario> formularioList) {
        List<FormularioEntity> formularioEntityList = new ArrayList<>();
        if (formularioList == null) return formularioEntityList;
        for (Formulario formulario : formularioList) {
            formularioEntityList.add(transforFormulario(formulario));
        }
        return formularioEntityList;
    }

    public Formulario transformFormularioEntity(FormularioEntity formularioEntity) {
        Formulario formulario = new Formulario();
        if (formularioEntity == null) return formulario;

        formulario.id = formularioEntity.m_id;
        formulario.uid = formularioEntity.uid;
        formulario.key = formularioEntity.key;
        formulario.tipoInspeccion = formularioEntity.tipoInspeccion;
        formulario.estado = formularioEntity.estado;
        formulario.distanciaEntreBuzones = formularioEntity.distanciaEntreBuzones;
        formulario.calle = formularioEntity.calle;
        formulario.fotos = formularioEntity.fotos;
        formulario.fechaHora = formularioEntity.fechaHora;
        formulario.fechaHoraStr = formularioEntity.fechaHoraStr;
        formulario.buzInicio = formularioEntity.buzInicio;
        formulario.buzFin = formularioEntity.buzFin;
        formulario.latitud = formularioEntity.latitud;
        formulario.longitudA = formularioEntity.longitudA;
        formulario.longitud = formularioEntity.longitud;
        formulario.observaciones = formularioEntity.observaciones;
        formulario.fileInspeccionList = formularioEntity.fileInspeccions;
        formulario.isSycnFormulario = formularioEntity.isSycnFormulario;
        formulario.isSycnFoto = formularioEntity.isSycnFoto;
        return formulario;
    }

    public FormularioEntity transforFormulario(Formulario formulario) {
        FormularioEntity formularioEntity = new FormularioEntity();
        if (formulario == null) return formularioEntity;

        formularioEntity.m_id = formulario.id;
        formularioEntity.key = formulario.key;
        formularioEntity.tipoInspeccion = formulario.tipoInspeccion;
        formularioEntity.estado = formulario.estado;
        formularioEntity.distanciaEntreBuzones = formulario.distanciaEntreBuzones;
        formularioEntity.calle = formulario.calle;
        formularioEntity.fotos = formulario.fotos;
        formularioEntity.fechaHora = formulario.fechaHora;
        formularioEntity.fechaHoraStr = formulario.fechaHoraStr;
        formularioEntity.buzInicio = formulario.buzInicio;
        formularioEntity.buzFin = formulario.buzFin;
        formularioEntity.latitud = formulario.latitud;
        formularioEntity.longitudA = formulario.longitudA;
        formularioEntity.longitud = formulario.longitud;
        formularioEntity.observaciones = formulario.observaciones;
        formularioEntity.fileInspeccions = formulario.fileInspeccionList;
        formularioEntity.isSycnFormulario = formulario.isSycnFormulario;
        formularioEntity.isSycnFoto = formulario.isSycnFoto;
        return formularioEntity;
    }

}
