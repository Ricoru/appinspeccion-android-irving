package me.buddyoruna.appinspeccion.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.AlignContent;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.buddyoruna.appinspeccion.R;
import me.buddyoruna.appinspeccion.domain.entity.FileInspeccion;
import me.buddyoruna.appinspeccion.model.storage.MasterSession;
import me.buddyoruna.appinspeccion.ui.util.ImageUtils;
import me.buddyoruna.appinspeccion.ui.util.MediaUtil;
import me.buddyoruna.appinspeccion.ui.util.PermisosUtil;


public class ContentFotosFragment extends Fragment implements ImageViewFragment.OnImageDialogFragmentListener {

    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.content_flexbox)
    LinearLayout contentFlexbox;

    Unbinder unbinder;

    private MasterSession mMasterSession;

    private File mFileTemp;
    private MediaUtil mMediaUtil;
    private FlexboxLayout mFlexboxLayout;
    private Map<String, String> imageViewPositions = new HashMap<>();
    private Map<String, String> imageViewPositionsInver = new HashMap<>();

    public ContentFotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMasterSession = MasterSession.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fotos, container, false);
        unbinder = ButterKnife.bind(this, view);

        mMediaUtil = new MediaUtil(getActivity());

        mFlexboxLayout = new FlexboxLayout(getContext());
        mFlexboxLayout.setAlignContent(AlignContent.STRETCH);
        mFlexboxLayout.setAlignItems(AlignItems.STRETCH);
        mFlexboxLayout.setFlexWrap(FlexWrap.WRAP);
        LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp2.setMargins(0, mMediaUtil.dpToPx(10), 0, 0);
        mFlexboxLayout.setPadding(mMediaUtil.dpToPx(8), 0, 0, mMediaUtil.dpToPx(8));
        mFlexboxLayout.setLayoutParams(llp2);
        mFlexboxLayout.setFlexDirection(FlexDirection.ROW);

        LinearLayout linearLayoutFlex = new LinearLayout(getActivity());
        LinearLayout.LayoutParams llpFlex = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutFlex.setLayoutParams(llpFlex);
        linearLayoutFlex.setPadding(10, 10, 10, 10);
        linearLayoutFlex.setGravity(Gravity.CENTER);

        linearLayoutFlex.setOrientation(LinearLayout.VERTICAL);
        mFlexboxLayout.addView(linearLayoutFlex);
        contentFlexbox.addView(mFlexboxLayout);

        try {
            if (mMasterSession.values.fileListFormDynamic != null) {
                for (int x = 0; x < mMasterSession.values.fileListFormDynamic.size(); x++) {
                    setFileTemp(mMasterSession.values.fileListFormDynamic.get(x).getFile());
                    addImageFlexBox(false, x);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void addImageFlexBox(boolean isNuevo, int position) {
        if (mMasterSession.values.fileListFormDynamic == null)
            mMasterSession.values.fileListFormDynamic = new ArrayList<>();

        ImageView appCompatImageView = new ImageView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginEnd(mMediaUtil.dpToPx(4));
        params.topMargin = mMediaUtil.dpToPx(8);
        appCompatImageView.setLayoutParams(params);

        if (isNuevo) {
            position = getFileList().size() + 1;
        } else {
            position++;
        }
        mFlexboxLayout.addView(appCompatImageView, position);

        Glide.with(getActivity())
                .load(getFileTemp())
                .apply(new RequestOptions().override(170, 200).centerCrop())
                .into(appCompatImageView);

        if (isNuevo) getFileList().add(new FileInspeccion(getFileTemp()));

        imageViewPositions.put(appCompatImageView.getTag().toString(), getFileTemp().getAbsolutePath());
        imageViewPositionsInver.put(getFileTemp().getAbsolutePath(), appCompatImageView.getTag().toString());

        appCompatImageView.setOnClickListener(view -> {
            try {
                String image = imageViewPositions.get(view.getTag().toString());
                showImageDialog(image, getIndexFileList(image));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showImageDialog(String image, int position) {
        FragmentManager fragmentManager = getFragmentManager();
        ImageViewFragment imageViewFragment
                = ImageViewFragment.newInstance(image, position, true);
        imageViewFragment.setOnImageDialogFragmentListener(this);
        imageViewFragment.show(fragmentManager, "dialogImageView");
    }

    @OnClick(R.id.txtAgregarFotos)
    public void takePhoto() {
        if (!PermisosUtil.hasCameraPermission(getActivity())) {
            PermisosUtil.askForCameraPermission(getActivity());
            return;
        }

        if (!PermisosUtil.hasRealExternalPermission(getActivity())) {
            PermisosUtil.askForCameraPermission(getActivity());
            return;
        }

        try {
//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//                // Create the File where the photo should go
//                File photoFile = null;
//                try {
//                    photoFile = ImageUtils.createImageFile(getContext(), mMasterSession.values.currentUser.key);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//                if (photoFile != null) {
//                    Uri photoURI = FileProvider.getUriForFile(getContext(),
//                            "me.buddyoruna.appinspeccion.fileprovider",
//                            photoFile);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                    startActivityForResult(takePictureIntent, ImageUtils.REQUEST_CODE_CAPTURE);
//                }
//            }
            ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
                    .setToolbarColor("#FFFFFF")         //  Toolbar color
                    .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
                    .setToolbarTextColor("#212121")     //  Toolbar text color (Title and Done button)
                    .setToolbarIconColor("#212121")     //  Toolbar icon color (Back and Camera button)
                    .setProgressBarColor("#4CAF50")     //  ProgressBar color
                    .setBackgroundColor("#FFFFFF")      //  Background color
                    .setCameraOnly(false)               //  Camera mode
                    .setMultipleMode(false)              //  Select multiple images or single image
                    .setFolderMode(true)                //  Folder mode
                    .setShowCamera(true)                //  Show camera button
                    .setFolderTitle("Galería de Imagenes")           //  Folder title (works with FolderMode = true)
                    .setImageTitle("Galería")         //  Image title (works with FolderMode = false)
                    .setDoneTitle("Ok")               //  Done button title
                    .setLimitMessage("Has alcanzado el límite de selección.")    // Selection limit message
                    .setMaxSize(3)                     //  Max images can be selected
                    .setSavePath("ImagePicker")         //  Image capture folder name
                    .setSelectedImages(new ArrayList<>())          //  Selected images
                    .setAlwaysShowDoneButton(true)      //  Set always show done button in multiple mode
                    .setRequestCode(100)                //  Set request code, default Config.RC_PICK_IMAGES
                    .setKeepScreenOn(true)              //  Keep screen on when selecting images
                    .start();                           //  Start ImagePicker
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeImageFlexBox(int position) {
        try {
            //----------------------------------------------------------------
            int indexFlex = (position + 1);
            String keyPosition = imageViewPositionsInver.get(getPathFileList(position));
            String valuePosition = imageViewPositions.get(keyPosition);
            //----------------------------------------------------------------
            mFlexboxLayout.removeView(mFlexboxLayout.getFlexItemAt(indexFlex));
            mFlexboxLayout.refreshDrawableState();
            //----------------------------------------------------------------
            imageViewPositions.remove(keyPosition);
            imageViewPositionsInver.remove(valuePosition);
            //----------------------------------------------------------------
            mMasterSession.values.fileListFormDynamic.remove(position);
            //----------------------------------------------------------------
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeImagenesFlexBox() {
        //Limpiar imagenes
        mFlexboxLayout.removeAllViews();
        mFlexboxLayout.refreshDrawableState();

        LinearLayout linearLayoutFlex = new LinearLayout(getActivity());
        LinearLayout.LayoutParams llpFlex = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayoutFlex.setLayoutParams(llpFlex);
        linearLayoutFlex.setPadding(10, 10, 10, 10);
        linearLayoutFlex.setGravity(Gravity.CENTER);

        linearLayoutFlex.setOrientation(LinearLayout.VERTICAL);
        mFlexboxLayout.addView(linearLayoutFlex);
        //----------------------------------------------

        imageViewPositions = new HashMap<>();
        imageViewPositionsInver = new HashMap<>();
    }

    private File getFileTemp() {
        return mFileTemp;
    }

    private void setFileTemp(File fileTemp) {
        this.mFileTemp = fileTemp;
    }

    private int getIndexFileList(String nameFile) {
        int index = -1;
        for (int x = 0; x < mMasterSession.values.fileListFormDynamic.size(); x++) {
            if (mMasterSession.values.fileListFormDynamic.get(x).getFile().getAbsolutePath().equalsIgnoreCase(nameFile)) {
                index = x;
                break;
            }
        }
        return index;
    }

    private List<FileInspeccion> getFileList() {
        return mMasterSession.values.fileListFormDynamic;
    }

    private String getPathFileList(int position) {
        return mMasterSession.values.fileListFormDynamic.get(position).getFile().getAbsolutePath();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == ImageUtils.REQUEST_CODE_CAPTURE) {
//            if (resultCode == Activity.RESULT_OK) {
//                setFileTemp(ImageUtils.galleryAddPic(getContext()));
//                this.addImageFlexBox(true, 0);
//            }
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            for (Image item : images) {
                setFileTemp(new File(item.getPath()));
                this.addImageFlexBox(true, 0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);  // You MUST have this line to be here
        // so ImagePicker can work with fragment mode
    }

    @Override
    public void onEliminarImage(int position) {
        removeImageFlexBox(position);
    }

}