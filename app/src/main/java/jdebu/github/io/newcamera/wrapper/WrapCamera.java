package jdebu.github.io.newcamera.wrapper;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by Glup on 19/04/16.
 */
public class WrapCamera implements Serializable {
    private FrameLayout layoutPreview;
    private ImageButton take,send;
    private ImageView imagea,imageb;

    public WrapCamera(Fragment fragment){
        layoutPreview=(FrameLayout)findViewById(fragment, "layout_preview");
        take=(ImageButton)findViewById(fragment,"take");
        send=(ImageButton)findViewById(fragment,"send");
        imagea=(ImageView)findViewById(fragment,"imagea");
        imageb=(ImageView)findViewById(fragment,"imageb");
    }
    private View findViewById(Fragment fragment, String id) {
        return fragment.getView().findViewById(
                fragment.getActivity().getResources().getIdentifier(
                        id,"id",
                        fragment.getActivity().getPackageName()
                )
        );
    }

    public FrameLayout getLayoutPreview() {return layoutPreview;}

    public ImageButton getTake() {return take;}

    public ImageView getImagea() {return imagea;}

    public ImageView getImageb() {return imageb;}

    public ImageButton getSend() {return send;}
}
