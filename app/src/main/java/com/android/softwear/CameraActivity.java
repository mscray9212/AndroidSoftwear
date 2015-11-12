package com.android.softwear;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;

        import android.app.Activity;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.PixelFormat;
        import android.graphics.drawable.Drawable;
        import android.hardware.Camera;
        import android.hardware.Camera.PictureCallback;
        import android.hardware.Camera.ShutterCallback;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.util.Log;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.Toast;
        import android.graphics.Matrix;
        import android.graphics.Point;
        import android.view.Display;

        import com.squareup.picasso.Picasso;
        import com.squareup.picasso.Target;




public class CameraActivity extends Activity implements SurfaceHolder.Callback
{
    private Camera camera = null;
    int cameraCount;
    private SurfaceView cameraSurfaceView = null;
    private SurfaceHolder cameraSurfaceHolder = null;
    private boolean previewing = false;
    static Drawable drawable = null;
    RelativeLayout relativeLayout;
    String wearable;
    String TAG = "";

    private Button btnCapture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        wearable = intent.getExtras().getString("Image");
        Log.d(TAG, "wearable: " + wearable);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        relativeLayout  =   (RelativeLayout) findViewById(R.id.containerImg);
        relativeLayout.setDrawingCacheEnabled(true);
        cameraSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        cameraSurfaceHolder = cameraSurfaceView.getHolder();
        cameraSurfaceHolder.addCallback(this);

        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        Picasso.with(getApplicationContext()).load(wearable).into(imageView);


        btnCapture = (Button)findViewById(R.id.takePictureButton);
        btnCapture.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                camera.takePicture(cameraShutterCallback, cameraPictureCallbackRaw, cameraPictureCallbackJpeg);
            }
        });
    }

    ShutterCallback cameraShutterCallback = new ShutterCallback()
    {
        @Override
        public void onShutter()
        {
            // TODO Auto-generated method stub
        }
    };

    PictureCallback cameraPictureCallbackRaw = new PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            // TODO Auto-generated method stub
        }
    };

    PictureCallback cameraPictureCallbackJpeg = new PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            // TODO Auto-generated method stub
            Picasso.with(getApplicationContext()).load(wearable).into(new Target() {


                @Override
                public void onPrepareLoad(Drawable arg0) {

                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                    // TODO Create your drawable from bitmap and append where you like.
                    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
                    imageView.setImageBitmap(bitmap);
                    Drawable dynamicImage = imageView.getDrawable();
                    setDrawable(dynamicImage);
                }

                @Override
                public void onBitmapFailed(Drawable arg0) {

                }
            });

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

            // Notice that width and height are reversed
            Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            // Setting post rotate to 90
            Matrix mtx = new Matrix();
            mtx.postRotate(270);
            // Rotating Bitmap
            bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);


            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x ;
            int height = size.y;

            int wdth = 600;
            int hght = 350;
            int x = (width - wdth) / 2 ;
            int y = (height - hght) / 2;

            Canvas canvas = new Canvas(bm);
            canvas.drawBitmap(bm, 0, 0, null);

            // **** HEREEEE needs refinement  ***//
            drawable.setBounds(x, y, wdth + x , hght + y);
            drawable.draw(canvas);

            File storagePath = new File(Environment.getExternalStorageDirectory() + "/Pictures/SoftWear/");
            storagePath.mkdirs();

            File myImage = new File(storagePath, Long.toString(System.currentTimeMillis()) + ".jpg");

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.close();
            } catch(FileNotFoundException e) {
                Log.d("In Saving File", e + "");
            } catch(IOException e) {
                Log.d("In Saving File", e + "");
            }


            camera.startPreview();
            scaled.recycle();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");
            startActivity(intent);

        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder,int format, int width, int height) {
        // TODO Auto-generated method stub

        if(previewing) {

            camera.stopPreview();
            previewing = false;

        } try {

            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
            previewing = true;

        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        // Checks for the device's cameras and opens front camera, if exists
        try
        {
            //camera.setDisplayOrientation(90);
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
                Camera.getCameraInfo( camIdx, cameraInfo );
                if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                    try {
                        camera = Camera.open( camIdx );
                    } catch (RuntimeException e) {
                        Log.d("Camera FAILED to open", e + "");
                    }
                }
            }
        }
        catch(RuntimeException e)
        {
            Toast.makeText(getApplicationContext(), "Device camera  is not working properly, please try after sometime.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public Drawable getDrawable() {
        return drawable;
    }




}