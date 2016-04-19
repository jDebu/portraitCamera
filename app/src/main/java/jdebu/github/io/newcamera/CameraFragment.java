package jdebu.github.io.newcamera;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import jdebu.github.io.newcamera.wrapper.WrapSelectPrenda;

/**
 * Created by Jose on 13/04/16.
 */
public class CameraFragment extends FullscreenFragment{
    private ImageView imagea,imageb;
    private int contador;
    private WrapSelectPrenda wrapSelectPrenda;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusHolder.getInstance().register(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstance){
        return inflater.inflate(R.layout.fragment,container,false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //wrapSelectPrenda = new WrapSelectPrenda(this);
        CameraPreview preview=new CameraPreview(getActivity(),this);
        FrameLayout frameLayout=(FrameLayout) getView().findViewById(R.id.preview);
        frameLayout.addView(preview);
        ImageButton take=(ImageButton)getView().findViewById(R.id.take);
        imagea = (ImageView) getView().findViewById(R.id.imagea);
        imageb = (ImageView) getView().findViewById(R.id.imageb);
        contador=1;
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clic", "event");
                BusHolder.getInstance().post(new TakePhoto());
            }
        });
        //wrapSelectPrenda.getLayout().bringToFront();
        //clickEvents();

    }

    private void clickEvents() {
        wrapSelectPrenda.getBtnBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrapSelectPrenda.getLayout().setVisibility(View.GONE);
            }
        });
        wrapSelectPrenda.getWearTop().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrapSelectPrenda.getLayout().setVisibility(View.GONE);
            }
        });
        wrapSelectPrenda.getWearBot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrapSelectPrenda.getLayout().setVisibility(View.GONE);
            }
        });
    }


    public class TakePhoto{}
    @Subscribe
    public void onSuccess(CameraPreview.SuccessSavePhoto successSavePhoto){
        switch (contador){
            case 1:
                Picasso.with(getActivity())
                        .load("file:" + successSavePhoto.result)
                        .fit()
                        .centerInside()
                        .noFade()
                        .into(imagea);
                contador=2;
                break;
            case 2:
                Picasso.with(getActivity()).
                        load("file:" + successSavePhoto.result)
                        .fit()
                        .centerInside()
                        .noFade()
                        .into(imageb);
                contador=0;
                break;
        }
    }
}
