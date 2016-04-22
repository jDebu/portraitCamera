package jdebu.github.io.newcamera.wrapper;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.sql.Wrapper;

/**
 * Created by Jose on 19/04/16.
 */
public class WrapSelectPrenda implements Serializable {
    private RelativeLayout layout;
    private TextView title;
    private ImageView btnBack;
    private LinearLayout wearTop;
    private LinearLayout wearBot;

    public WrapSelectPrenda(Fragment fragment){
        layout= (RelativeLayout) findViewById(fragment,"select_prenda");
        title = (TextView) findViewById(fragment,"titulo_select_prenda");
        btnBack= (ImageView) findViewById(fragment,"atras_camara2");
        wearTop = (LinearLayout) findViewById(fragment,"frame_select_superior");
        wearBot = (LinearLayout) findViewById(fragment,"frame_select_medio");
    }

    private View findViewById(Fragment fragment, String id) {
        return fragment.getView().findViewById(
                fragment.getActivity().getResources().getIdentifier(
                        id, "id",
                        fragment.getActivity().getPackageName()
                )
        );
    }

    public RelativeLayout getLayout() {return layout;}

    public TextView getTitle() {return title;}

    public ImageView getBtnBack() {return btnBack;}

    public LinearLayout getWearTop() {return wearTop;}

    public LinearLayout getWearBot() {return wearBot;}
}
