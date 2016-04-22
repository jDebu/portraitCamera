package jdebu.github.io.newcamera;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import jdebu.github.io.newcamera.wrapper.WrapCamera;
import jdebu.github.io.newcamera.wrapper.WrapPreApproval;
import jdebu.github.io.newcamera.wrapper.WrapPreSend;
import jdebu.github.io.newcamera.wrapper.WrapSelectPrenda;

/**
 * Created by Jose on 13/04/16.
 */
public class CameraFragment extends FullscreenFragment{
    private int contador;
    private WrapSelectPrenda wrapSelectPrenda;
    private WrapCamera wrapCamera;
    private WrapPreApproval wrapPreApproval;
    private CameraPreview.SuccessSavePhoto successSavePhoto;
    private WrapPreSend wrapPreSend;
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
        initWraps();
        CameraPreview preview=new CameraPreview(getActivity(),this);
        wrapCamera.getLayoutPreview().addView(preview);
        contador=1;
        clickEvents();
    }

    private void initWraps() {
        wrapCamera = new WrapCamera(this);
        wrapSelectPrenda = new WrapSelectPrenda(this);
        wrapPreApproval = new WrapPreApproval(this);
        wrapPreSend = new WrapPreSend(this);
        wrapSelectPrenda.getLayout().bringToFront();
        this.setRetainInstance(true);
    }

    private void clickEvents() {
        wrapCamera.getTake().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusHolder.getInstance().post(new TakePhoto());
            }
        });
        wrapCamera.getSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreSend();

            }
        });
        wrapCamera.getImagea().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(v);
            }
        });
        wrapCamera.getImageb().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(v);
            }
        });

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
        wrapPreApproval.getLayoutApprove().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (contador) {
                    case 1:
                        Picasso.with(getActivity())
                                .load("file:" + successSavePhoto.result)
                                .fit()
                                .centerInside()
                                .noFade()
                                .into(wrapCamera.getImagea());
                        contador = 2;
                        wrapCamera.getImagea().setTag(successSavePhoto.result);
                        wrapCamera.getImagea().setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        Picasso.with(getActivity()).
                                load("file:" + successSavePhoto.result)
                                .fit()
                                .centerInside()
                                .noFade()
                                .into(wrapCamera.getImageb());
                        contador = 0;
                        wrapCamera.getImageb().setTag(successSavePhoto.result);
                        wrapCamera.getImageb().setVisibility(View.VISIBLE);

                        wrapCamera.getTake().setVisibility(View.GONE);
                        wrapCamera.getSend().setVisibility(View.VISIBLE);

                        showPreSend();
                        break;
                }
                wrapPreApproval.getLayout().setVisibility(View.GONE);
            }
        });
        wrapPreApproval.getLayoutDisapprove().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrapPreApproval.getLayout().setVisibility(View.GONE);
            }
        });
        wrapPreSend.getBtnBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wrapPreSend.getLayout().setVisibility(View.GONE);
            }
        });
        wrapPreSend.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wrapPreSend.getLayout().setVisibility(View.GONE);
            }
        });

    }

    private void showPreSend() {
        wrapPreSend.getLayout().setVisibility(View.VISIBLE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmapF = BitmapFactory.decodeFile(wrapCamera.getImagea().getTag().toString(), options);
        Bitmap bitmapP = BitmapFactory.decodeFile(wrapCamera.getImageb().getTag().toString(), options);
        wrapPreSend.getImgFront().setImageBitmap(bitmapF);
        wrapPreSend.getImgBack().setImageBitmap(bitmapP);
        wrapPreSend.getLayout().bringToFront();
    }

    private void showImage(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + v.getTag().toString()), "image/*");
        startActivity(intent);
    }


    public class TakePhoto{}
    @Subscribe
    public void onSuccess(CameraPreview.SuccessSavePhoto successSavePhoto){
        //deberia eliminar de la memoria si no lo aprueba - mejora
        //si no aprueba la foto no se deberia mostrar en la foto muestra imagea/imageb
        wrapPreApproval.getLayout().setVisibility(View.VISIBLE);
        this.successSavePhoto=successSavePhoto;
        Picasso.with(getActivity())
                .load("file:" + successSavePhoto.result)
                .fit()
                .centerInside()
                .noFade()
                .into(wrapPreApproval.getPhoto());
        wrapPreApproval.getLayout().bringToFront();
    }

}
