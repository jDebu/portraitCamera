package jdebu.github.io.newcamera.wrapper;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.Serializable;

/**
 * Created by Glup on 19/04/16.
 */
public class WrapPreApproval implements Serializable{
    private RelativeLayout layout,layoutApprove,layoutDisapprove;
    private ImageView photo;
    public WrapPreApproval(Fragment fragment){
        layout=(RelativeLayout)findViewById(fragment,"layout");
        layoutApprove=(RelativeLayout)findViewById(fragment,"frameOk");
        layoutDisapprove=(RelativeLayout)findViewById(fragment,"frameCancel");
        photo=(ImageView)findViewById(fragment,"previewPhoto");
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

    public RelativeLayout getLayoutApprove() {return layoutApprove;}

    public RelativeLayout getLayoutDisapprove() {return layoutDisapprove;}

    public ImageView getPhoto() {return photo;}
}
