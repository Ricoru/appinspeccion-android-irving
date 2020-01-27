package me.buddyoruna.appinspeccion.ui.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.buddyoruna.appinspeccion.R;


public class ImageViewFragment extends DialogFragment {

    private static final String ARG_PATH_IMAGE = "ARG_PATH_PHOTO";
    private static final String ARG_POSITION = "ARG_POSITION";
    private static final String ARG_BOOLEAN = "ARG_BOOLEAN";

    @BindView(R.id.img_view)
    ImageView img_view;
    @BindView(R.id.action_cancel)
    TextView action_cancel;
    @BindView(R.id.action_eliminar)
    TextView action_eliminar;

    private String mImage;
    private int mPosition;

    public static OnImageDialogFragmentListener mListener;

    public ImageViewFragment() {
    }

    public static ImageViewFragment newInstance(String mImage, int position, Boolean readOnly) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PATH_IMAGE, mImage);
        args.putInt(ARG_POSITION, position);
        args.putBoolean(ARG_BOOLEAN, readOnly);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_image_dialog, container, false);
        ButterKnife.bind(this, view);

        action_cancel.setOnClickListener(view1 -> dismiss());

        action_eliminar.setOnClickListener(view12 -> {
            mListener.onEliminarImage(mPosition);
            dismiss();
        });
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            mImage = getArguments().getString(ARG_PATH_IMAGE);
            mPosition = getArguments().getInt(ARG_POSITION);
            try {
                Glide.with(getActivity())
                        .load(mImage)
                        .apply(new RequestOptions().override(800, 800).centerCrop())
                        .into(img_view);
                Bitmap bitmap = BitmapFactory.decodeFile(mImage);
                if (bitmap.getWidth() > bitmap.getHeight()) {
                    img_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } else {
                    img_view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setOnImageDialogFragmentListener(OnImageDialogFragmentListener mListener) {
        this.mListener = mListener;
    }

    public interface OnImageDialogFragmentListener {
        void onEliminarImage(int position);
    }

}