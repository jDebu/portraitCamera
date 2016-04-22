package jdebu.github.io.newcamera.wrapper;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Glup on 20/04/16.
 */
public class WrapPreSend {
    private RelativeLayout layout;
    private ImageView btnBack,imgFront,imgBack;
    private Button btnSend;

    public WrapPreSend(Fragment fragment){
        layout = (RelativeLayout)findViewById(fragment,"layout_send");
        btnBack=(ImageView)findViewById(fragment,"atras");
        imgFront = (ImageView)findViewById(fragment,"previewFrontal");
        imgBack = (ImageView) findViewById(fragment,"previewPosterior");
        btnSend = (Button) findViewById(fragment,"btnEnviarPrenda");
    }

    private View findViewById(Fragment fragment, String id) {
        return fragment.getView().findViewById(
                fragment.getActivity().getResources().getIdentifier(
                        id,"id",
                        fragment.getActivity().getPackageName()
                )
        );
    }

    public RelativeLayout getLayout() {return layout;}

    public ImageView getBtnBack() {return btnBack;}

    public ImageView getImgFront() {return imgFront;}

    public ImageView getImgBack() {return imgBack;}

    public Button getBtnSend() {return btnSend;}
}
