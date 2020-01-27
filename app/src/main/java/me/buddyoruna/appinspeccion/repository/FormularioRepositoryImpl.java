package me.buddyoruna.appinspeccion.repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import me.buddyoruna.appinspeccion.MvpApp;
import me.buddyoruna.appinspeccion.domain.db.AppDatabase;
import me.buddyoruna.appinspeccion.domain.db.FormularioEntity;
import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;
import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;
import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.Formulario;
import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;
import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.model.mapper.FormularioMapper;
import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.ui.util.Constant;

public class FormularioRepositoryImpl implements FormularioRepository {

    private static final String rootName = "inspeccion";
    private static final String rootTipoInspeccion = "tipoInspeccion";
    private static final String rootEstadoInspeccion = "estadoInspeccion";
    private static final String rootFormulario = "formularios";
    private static final String rootBuzones = "buzones";

    private AppDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private FormularioMapper mFormularioMapper;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private MasterSession masterSession;
    private CollectionReference collectionReference;

    public FormularioRepositoryImpl(AppDatabase database) {
        mDatabase = database;
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        masterSession = MasterSession.getInstance(MvpApp.getContext());
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFormularioMapper = new FormularioMapper();
        collectionReference = mFirebaseFirestore.collection(rootName);
    }

    @Override
    public Single<Resource<String>> registrarLocal(Formulario formulario) {
        return Single.fromCallable(() -> {
            formulario.uid = mAuth.getUid();
            mDatabase.formularioDao().insert(mFormularioMapper.transforFormulario(formulario));
            return Resource.success("No hay conexión a internet, los datos del formulario se guardaron en el equipo");
        });
    }

    @Override
    public Single<List<Formulario>> updateBatchLocal(List<Formulario> formularios) {
        return Single.fromCallable(() -> {
            mDatabase.formularioDao().updateAll(mFormularioMapper.transformListFormularioAtFormularioEntity(formularios));
            return formularios;
        });
    }

    @Override
    public Completable updateBatchFotosLocal(Map<String, List<String>> mapFormulariosFiltrados) {
        return Completable.fromAction(() -> {
            for (Map.Entry<String, List<String>> entry : mapFormulariosFiltrados.entrySet()) {
                FormularioEntity formularioEntity = mDatabase.formularioDao().findKey(entry.getKey());
                if (formularioEntity != null) {
                    formularioEntity.isSycnFoto = true;
                    mDatabase.formularioDao().update(formularioEntity);
                }
            }
        });
    }

    @Override
    public Completable eliminarFormularioSincronizados() {
        return Completable.fromAction(() -> mDatabase.formularioDao().eliminarFormularioMigrados());
    }

    @Override
    public LiveData<Resource<String>> registrarFirebase(Formulario formulario) {
        final MediatorLiveData<Resource<String>> mObservableResult = new MediatorLiveData<>();
        formulario.uid = mAuth.getUid();
        collectionReference = FirebaseFirestore.getInstance().collection(rootFormulario);
        collectionReference.add(formulario).addOnSuccessListener(aVoid -> mObservableResult.setValue(Resource.success("Los Datos del formulario se guardaron exitosamente")))
                .addOnFailureListener(error -> mObservableResult.setValue(Resource.error(error.getMessage(), null)));
        return mObservableResult;
    }

    @Override
    public Observable<Resource<Formulario>> registrarPendienteEnvio(Formulario formulario) {
        return Observable.create((ObservableOnSubscribe<Resource<Formulario>>) emiter -> {
            collectionReference = FirebaseFirestore.getInstance().collection(rootFormulario);
            collectionReference.add(formulario).addOnSuccessListener(documentReference -> {
                formulario.key = documentReference.getId();
                formulario.isSycnFormulario = true;
                emiter.onNext(Resource.success(formulario));
                emiter.onComplete();
            }).addOnFailureListener(emiter::onError);
        })
        .onErrorReturn(err -> {
            err.printStackTrace();
            return Resource.error(err.getMessage(), null);
        });
    }

    @Override
    public LiveData<Map<String, Object>> obtenerPendientes() {
        return Transformations.switchMap(mDatabase.formularioDao().getFormulariosPendientes(), form -> {
            MediatorLiveData<Map<String, Object>> mObservableResult = new MediatorLiveData<>();
            List<Formulario> formularios = mFormularioMapper.transformListFormularioEntityAtFormulario(form);

            List<FileInspeccion> fileInspeccionList = new ArrayList<>();
            for (Formulario item : formularios) {
                for (FileInspeccion fileInspeccion : item.fileInspeccionList) {
                    fileInspeccion.setKey(item.key);
                    fileInspeccionList.add(fileInspeccion);
                }
            }

            Map<String, Object> mapResource = new HashMap<>();
            mapResource.put("formularios", formularios);
            mapResource.put("formulariosFotos", fileInspeccionList);
            mObservableResult.setValue(mapResource);
            return mObservableResult;
        });
    }

    @Override
    public LiveData<Resource<List<TipoInspeccion>>> loadTipoInspeccion() {
        final MediatorLiveData<Resource<List<TipoInspeccion>>> mObservableResult = new MediatorLiveData<>();
        collectionReference = FirebaseFirestore.getInstance().collection(rootTipoInspeccion);
        collectionReference.addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                mObservableResult.setValue(Resource.error(error.getMessage(), null));
                return;
            }

            List<TipoInspeccion> tipoInspeccions = new ArrayList<>();
            if (querySnapshot.isEmpty()) {
                mObservableResult.setValue(Resource.success(tipoInspeccions));
                return;
            }

            for (DocumentSnapshot doc : querySnapshot) {
                TipoInspeccion tipoInspeccion = doc.toObject(TipoInspeccion.class);
                tipoInspeccion.key = doc.getId();
                tipoInspeccions.add(tipoInspeccion);
            }

            masterSession.values.tipoInspeccionList = tipoInspeccions;
            masterSession.update();

            mObservableResult.setValue(Resource.success(tipoInspeccions));
        });
        return mObservableResult;
    }

    @Override
    public LiveData<Resource<List<EstadoInspeccion>>> loadEstadoInspeccion() {
        final MediatorLiveData<Resource<List<EstadoInspeccion>>> mObservableResult = new MediatorLiveData<>();
        collectionReference = FirebaseFirestore.getInstance().collection(rootEstadoInspeccion);
        collectionReference.addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                mObservableResult.setValue(Resource.error(error.getMessage(), null));
                return;
            }

            List<EstadoInspeccion> estadoInspeccions = new ArrayList<>();
            if (querySnapshot.isEmpty()) {
                mObservableResult.setValue(Resource.success(estadoInspeccions));
                return;
            }

            for (DocumentSnapshot doc : querySnapshot) {
                EstadoInspeccion estadoInspeccion = doc.toObject(EstadoInspeccion.class);
                estadoInspeccion.key = doc.getId();
                estadoInspeccions.add(estadoInspeccion);
            }

            Log.i("INFO", "loadEstadoInspeccion :: finish");

            masterSession.values.estadoInspeccionList = estadoInspeccions;
            masterSession.update();

            mObservableResult.setValue(Resource.success(estadoInspeccions));
        });
        return mObservableResult;
    }

    @Override
    public LiveData<Resource<List<BuzonInicio>>> loadBuzonInicio() {
        final MediatorLiveData<Resource<List<BuzonInicio>>> mObservableResult = new MediatorLiveData<>();
        collectionReference = FirebaseFirestore.getInstance().collection(rootBuzones);
        collectionReference.whereEqualTo("tipoBuzon", "inicio").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                mObservableResult.setValue(Resource.error(error.getMessage(), null));
                return;
            }

            List<BuzonInicio> buzonInicioList = new ArrayList<>();
            if (querySnapshot.isEmpty()) {
                mObservableResult.setValue(Resource.success(buzonInicioList));
                return;
            }

            for (DocumentSnapshot doc : querySnapshot) {
                BuzonInicio buzonInicio = doc.toObject(BuzonInicio.class);
                buzonInicio.key = doc.getId();
                buzonInicioList.add(buzonInicio);
            }

            masterSession.values.buzonInicioList = buzonInicioList;
            masterSession.update();

            mObservableResult.setValue(Resource.success(buzonInicioList));
        });
        return mObservableResult;
    }

    @Override
    public LiveData<Resource<List<BuzonFin>>> loadBuzonFin() {
        final MediatorLiveData<Resource<List<BuzonFin>>> mObservableResult = new MediatorLiveData<>();
        collectionReference = FirebaseFirestore.getInstance().collection(rootBuzones);
        collectionReference.whereEqualTo("tipoBuzon", "fin").addSnapshotListener((querySnapshot, error) -> {
            if (error != null) {
                mObservableResult.setValue(Resource.error(error.getMessage(), null));
                return;
            }

            List<BuzonFin> buzonFinList = new ArrayList<>();
            if (querySnapshot.isEmpty()) {
                mObservableResult.setValue(Resource.success(buzonFinList));
                return;
            }

            for (DocumentSnapshot doc : querySnapshot) {
                BuzonFin buzonFin = doc.toObject(BuzonFin.class);
                buzonFin.key = doc.getId();
                buzonFinList.add(buzonFin);
            }

            masterSession.values.buzonFinList = buzonFinList;
            masterSession.update();

            mObservableResult.setValue(Resource.success(buzonFinList));
        });

        return mObservableResult;
    }


    @Override
    public Observable<Resource<FileInspeccion>> loadFileInspeccion(FileInspeccion fileInspeccion) {
        return Observable.create((ObservableOnSubscribe<Resource<FileInspeccion>>) emiter -> {
            mStorage = FirebaseStorage.getInstance();
            mStorageRef = mStorage.getReferenceFromUrl(Constant.URL_STORAGE);
            StorageReference imagesRef = mStorageRef
                    .child(Constant.STORAGE_USER)
                    .child(fileInspeccion.getFile().getName()); //+ "_inspeccion.jpg"
            InputStream stream = new FileInputStream(fileInspeccion.getFile());
            UploadTask uploadTask = imagesRef.putStream(stream);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fileInspeccion.setPathUpload(task.getResult().toString());
                    fileInspeccion.setUpload(true);
                    emiter.onNext(Resource.success(fileInspeccion));
                    emiter.onComplete();
                } else {
                    emiter.onError(task.getException());
                }
            });
        }).onErrorReturn(err -> Resource.error(err.getMessage(), null));
    }

    @Override
    public Observable<Resource<String>> updateFotosFormulario(String key, List<String> fotos) {
        return Observable.create((ObservableOnSubscribe<Resource<String>>) emiter -> {
            collectionReference = FirebaseFirestore.getInstance().collection(rootFormulario);
            collectionReference.document(key).update("fotos", fotos).addOnSuccessListener(aVoid -> {
                emiter.onNext(Resource.success(key));
                emiter.onComplete();
            }).addOnFailureListener(emiter::onError);
        }).onErrorReturn(err -> {
            err.printStackTrace();
            return Resource.error(err.getMessage(), null);
        });
    }

}
