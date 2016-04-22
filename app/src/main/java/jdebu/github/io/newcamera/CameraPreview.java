package jdebu.github.io.newcamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jose on 12/04/16.
 */
class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Preview";
    private Context context;

    SurfaceHolder mHolder;
    public Camera camera;
    private Camera.Size mSurfaceSize;
    private Camera.Size mPictureSize;
    private Bitmap source;


    CameraPreview(Context context, CameraFragment cameraFragment) {
        super(context);
        this.context=context;
        BusHolder.getInstance().register(this);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.

        if(camera == null){
            camera = Camera.open();

            camera.setDisplayOrientation(90);
            try {
                camera.setPreviewDisplay(holder);

                camera.setPreviewCallback(new Camera.PreviewCallback() {

                    public void onPreviewFrame(byte[] data, Camera arg1) {

                        CameraPreview.this.invalidate();
                    }
                });
            } catch (IOException e) {
                camera.release();
                camera = null;
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        if(camera!=null){
            camera.stopPreview();
            camera.setPreviewCallback(null);

            camera.release();
            camera = null;
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
//        parameters.setPreviewSize(w, h);
        camera.setParameters(parameters);
        setCameraPreviewSize();//no necesito ajustar con getwidth y getheight por ser full screen
        /*setPictureSize();

        List<Camera.Size> sizes=parameters.getSupportedPictureSizes();
        mPictureSize=sizes.get(0);
        mPictureSize.height=getHeight();
        mPictureSize.width=getWidth();
        mSurfaceSize=sizes.get(0);
        mSurfaceSize.height=getHeight();
        mSurfaceSize.width=getWidth();*/
        camera.startPreview();
    }

    private void setCameraPreviewSize() {
        Camera.Parameters params = camera.getParameters();
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        if (previewSizes.isEmpty()) {
            return;
        }

        Camera.Size bestSize = previewSizes.get(0);
        for (Camera.Size size : previewSizes) {
            if (size.width * size.height > bestSize.width * bestSize.height) {
                bestSize = size;
            }
        }
        mSurfaceSize = bestSize;
        params.setPreviewSize(mSurfaceSize.width, mSurfaceSize.height);
        camera.setParameters(params);
        //adjustViewSize(mSurfaceSize);

    }
    private void adjustViewSize() {
        int width = getWidth();
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        layoutParams.width = width;
        //float coefficient = (float) size.height / width;
        layoutParams.height = getHeight()-(int)(getWidth() * 0.1);
        this.setLayoutParams(layoutParams);
    }
    private void setPictureSize() {
        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size currentSize = null;
        for (Camera.Size size : sizes) {
            if (currentSize == null
                    || (size.width * size.height > currentSize.height*currentSize.width)) {
                currentSize = size;
            }
        }
        if (currentSize != null) {
            mPictureSize = currentSize;
            Log.wtf("picture", mPictureSize.width + " " + mPictureSize.height);
            params.setPictureSize(mPictureSize.width, mPictureSize.height);
            camera.setParameters(params);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint p= new Paint(Color.RED);
        Log.d(TAG, "draw");
        canvas.drawText("PREVIEW", canvas.getWidth() / 2, canvas.getHeight() / 2, p);
    }



    private void savePhoto(final Bitmap bitmap) {
        new AsyncTask<Void, Void, String>() {
            ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(context, null, "Espere...", true, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return ImageUtils.SaveImage(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
                camera.startPreview();
                SuccessSavePhoto successSavePhoto = new SuccessSavePhoto();
                successSavePhoto.success=1;
                successSavePhoto.msg="Se genero y guardo foto";
                successSavePhoto.result=aVoid;
                Log.wtf("TomaFoto:", aVoid);
                BusHolder.getInstance().post(successSavePhoto);
            }
        }.execute();
    }

    @Subscribe
    public void onTakePhoto(CameraFragment.TakePhoto takePhoto){
        Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        };
        Camera.PictureCallback pictureCallback=new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                int maxSize = 2048;
                camera.stopPreview();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(data, 0, data.length, options);

                Log.wtf("dataL",data.length+"");
                int width = options.outWidth;
                int height = options.outHeight;

                Log.wtf("options",width+" "+height+"");
                int srcSize = Math.max(width, height);
                options.inScaled=true;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = maxSize < srcSize ? 2 : 1;
                options.inJustDecodeBounds = false;
                Bitmap tmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);

                Log.wtf("optionsNew",options.outWidth+" "+options.outHeight);
                Log.wtf("screen",mSurfaceSize.height+" "+mSurfaceSize.width);
                /*int size = Math.min(options.outWidth, options.outHeight);
                float previewRatio = (float) Math.min(mSurfaceSize.width, mSurfaceSize.height)  / (float) Math.max(mSurfaceSize.width, mSurfaceSize.height);
                float cameraRatio = (float) Math.min(options.outWidth, options.outHeight) / (float)Math.max(options.outWidth, options.outHeight);;
                Log.wtf("ratios", previewRatio + " " + cameraRatio);
                */
                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                /*int length = (int) (size * (cameraRatio / previewRatio));
                int rid = Math.abs(size - length);
                Log.wtf("results",length+" "+rid);*/

                //tmp=Bitmap.createScaledBitmap(tmp,1920,1440,true);
                source = Bitmap.createBitmap(tmp,
                        0,
                        0,
                        options.outWidth,
                        options.outHeight,
                        matrix, true);

                tmp.recycle();

                savePhoto(source);

            }
        };
        try {
            camera.takePicture(shutterCallback,null,pictureCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SuccessSavePhoto{
        public int success=0;
        public String msg="No hay mensaje";
        public String result;
    }

}

