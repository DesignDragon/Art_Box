package com.example.artbox;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;


public class camera extends Fragment {
    private TextureView textureView;
    private ImageView capture;
    protected CameraDevice cameraDevice;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Size imageDimension;
    private String cameraId;
    protected CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private ImageReader imageReader;
    private ImageView upload;
    private static final SparseIntArray ORIENTATIONS= new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_180,180);
    }


    public camera() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_camera, container, false);

        if(getActivity()!=null && getActivity() instanceof side_menu)
        {
            ((side_menu)getActivity()).getNav().setVisibility(view.GONE);
        }

        textureView= (TextureView) view.findViewById(R.id.textureView);
        assert textureView!=null;

        capture= (ImageView) view.findViewById(R.id.capture);
        assert capture!=null;
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        return view;
    }


    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice= camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice= null;
        }
    };

    final CameraCaptureSession.CaptureCallback captureCallbackListener =new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            createCameraPreview();
        }
    };
    protected void startBackgroundThread()
    {
        mBackgroundThread=new HandlerThread("camera background");
        mBackgroundThread.start();
        mBackgroundHandler= new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread()
    {
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread=null;
            mBackgroundHandler=null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void takePicture()
    {
        if (cameraDevice==null)
        {
            return;
        }
        CameraManager cameraManager= (CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try{
            CameraCharacteristics characteristics =cameraManager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpeg= null;
            if(characteristics!=null)
            {
                jpeg=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width=640;
            int height=480;
            if(jpeg!=null && 0<jpeg.length){
                width=jpeg[0].getWidth();
                height=jpeg[0].getHeight();
            }
            ImageReader imageReader= ImageReader.newInstance(width,height,ImageFormat.JPEG,1);
            List<Surface> outputSurface= new ArrayList<Surface>(2);
            outputSurface.add(imageReader.getSurface());
            outputSurface.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBiuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBiuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            int rotation= getActivity().getWindowManager().getDefaultDisplay().getRotation();
            captureBiuilder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(rotation));
            final File file=new File(Environment.getExternalStorageDirectory()+"/.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image=null;
                    try{
                        image=imageReader.acquireLatestImage();
                        ByteBuffer buffer=image.getPlanes()[0].getBuffer();
                        byte[] bytes= new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }
                    catch(FileNotFoundException e){}
                    catch (IOException e){}
                    finally{
                        if(image!=null){ image.close(); }
                    }
                }
                private void save(byte[] bytes)throws IOException{
                    OutputStream output =null;
                    try{
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if(null!=output){
                            output.close();
                        }
                    }
                }
            };
            imageReader.setOnImageAvailableListener(readerListener,mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    try{
                        session.capture(captureBiuilder.build(),captureListener,mBackgroundHandler);
                    }catch (CameraAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                }
            },mBackgroundHandler);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }


    protected void createCameraPreview()
    {
        try{
            SurfaceTexture texture= textureView.getSurfaceTexture();
            assert texture!=null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface=new Surface(texture);
            captureRequestBuilder= cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(null==cameraDevice){
                        return;
                    }
                    cameraCaptureSessions=cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getContext(),"Configuratio changed",Toast.LENGTH_SHORT).show();
                }
            },null);
        }catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }


    private void openCamera()
    {
        CameraManager manager=(CameraManager) getContext().getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId=manager.getCameraIdList()[0];
            CameraCharacteristics characteristics=manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map=characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map!=null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    protected void updatePreview()
    {
        if(null==cameraDevice){
            Log.e(TAG,"update preview err");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CameraMetadata.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    private void closeCamera(){
        if(null!=cameraDevice){
            cameraDevice.close();
            cameraDevice=null;
        }
        if(null!= imageReader){
            imageReader.close();
            imageReader=null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults[0]==PackageManager.PERMISSION_DENIED)
            {
                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable()){
            openCamera();
        }else{
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    public void onPause() {
        stopBackgroundThread();
        super.onPause();
    }
}
