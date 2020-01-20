package me.buddyoruna.appinspeccion.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import me.buddyoruna.appinspeccion.MvpApp;
import me.buddyoruna.appinspeccion.domain.entity.BuzonFin;
import me.buddyoruna.appinspeccion.domain.entity.BuzonInicio;
import me.buddyoruna.appinspeccion.domain.entity.EstadoInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.domain.entity.Formulario;
import me.buddyoruna.appinspeccion.domain.entity.TipoInspeccion;
import me.buddyoruna.appinspeccion.domain.response.Resource;
import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.ui.util.Constant;

public class FormularioRepositoryImpl implements FormularioRepository {

    private static final String rootName = "inspeccion";
    private static final String rootTipoInspeccion = "tipoInspeccion";
    private static final String rootEstadoInspeccion = "estadoInspeccion";
    private static final String rootFormulario = "formularios";
    private static final String rootBuzones = "buzones";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    DocumentReference documentReference;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;

    MasterSession masterSession;
    CollectionReference collectionReference;

    public FormularioRepositoryImpl() {
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        masterSession = MasterSession.getInstance(MvpApp.getContext());
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        collectionReference = FirebaseFirestore.getInstance().collection(rootName);
    }

    @Override
    public LiveData<Resource<String>> registrar(Formulario formulario) {
        final MediatorLiveData<Resource<String>> mObservableResult = new MediatorLiveData<>();
        formulario.uid = mAuth.getUid();
        collectionReference = FirebaseFirestore.getInstance().collection(rootFormulario);
        collectionReference.add(formulario).addOnSuccessListener(aVoid -> mObservableResult.setValue(Resource.success("Los Datos del formulario se guardarón exitosamente")))
                .addOnFailureListener(error -> {
                    mObservableResult.setValue(Resource.error(error.getMessage(), null));
                });
        return mObservableResult;
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
                    .child(fileInspeccion.getFile().getName() + "_inspeccion.jpg");
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

//        return Observable.create(it -> {
//            mStorage = FirebaseStorage.getInstance();
//            mStorageRef = mStorage.getReferenceFromUrl(Constant.URL_STORAGE);
//            StorageReference imagesRef = mStorageRef
//                    .child(Constant.STORAGE_USER)
//                    .child("inspeccion_" + mAuth.getUid() + "_img.jpg");
//            InputStream stream = new FileInputStream(file);
//            UploadTask uploadTask = imagesRef.putStream(stream);
//            uploadTask.continueWithTask(task -> {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//                // Continue with the task to get the download URL
//                return imagesRef.getDownloadUrl();
//            }).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    if (!it.isDisposed()) {
////                    iLoadFile.success(task.getResult().toString())
//                        Observable.just(Resource.success(task.getResult().toString()));
//                    }
//                } else {
////                    iLoadFile.exception(task.getException());
//                    Observable.fromCallable(() -> Resource.error(task.getException().getMessage(), null));
//                }
//            });
//        });
    }

}
