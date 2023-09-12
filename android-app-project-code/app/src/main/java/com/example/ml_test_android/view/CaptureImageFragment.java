package com.example.ml_test_android.view;

import static android.content.ContentValues.TAG;
import static android.view.Surface.ROTATION_90;

import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ml_test_android.MLApp;
import com.example.ml_test_android.R;
import com.example.ml_test_android.databinding.FragmentFirstBinding;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaptureImageFragment extends Fragment {

    private ExecutorService cameraExecutor = null;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    private FragmentFirstBinding binding;

    private ImageCapture imageCapture;


    private final String FILENAME_FORMAT = "yyyyMMdd_HHmmss";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the permission launcher
        this.requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), this::onPermissionRequestResult);

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        launchCameraWithPermission();
        binding.captureButton.setOnClickListener(view1 -> takePhoto());
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void launchCameraWithPermission(){
        // initial check of camera hardware and camera permission
        if(this.isRearCameraAvailable()){
            if(this.isCameraPermissionsGranted()){
                this.startCamera();
            } else {
                // ask permission to access camera
                this.requestCameraAccessPermission();
            }
        } else if(this.isFrontCameraAvailable()) {

        } else {
            // display Toast for device don't have working camera
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
        binding = null;
    }

    private boolean isRearCameraAvailable(){
        PackageManager pm = this.requireContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean isFrontCameraAvailable(){
        PackageManager pm = this.requireContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    private boolean isCameraPermissionsGranted() {
        // Check if the camera permission is granted
        if (ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, you can use the camera
            // Start camera-related operations
            return true;
        } else {
            // Permission is not granted
            return false;
        }

    }

    private void requestCameraAccessPermission(){
        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
    }

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this.requireContext());

        cameraProviderFuture.addListener(() -> {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview
                Preview preview = new Preview.Builder().setTargetRotation(ROTATION_90).build();

                preview.setSurfaceProvider(binding.imageView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                // Select back camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Unbind use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector,imageCapture, preview);
            } catch (Exception exc) {
                Log.e(TAG, "Use case binding failed", exc);
            }
        }, ContextCompat.getMainExecutor(this.requireContext()));
    }

    private void takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        if (imageCapture == null){
            return;
        }

        // Create a time-stamped name and MediaStore entry
        String name = new SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis());
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image");


        File outputDirectory = getOutputDirectory(); // This method gets your app's storage directory
        String filename = System.currentTimeMillis() + ".jpg";

        File file = new File(outputDirectory, filename);

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        // Create output options object which contains file + metadata
//        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(
//                this.requireContext().getContentResolver(),
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues)
//                .build();

        // Set up image capture listener, which is triggered after the photo has been taken
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this.requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onError(@NonNull ImageCaptureException exc) {
                        Log.e(TAG, "Photo capture failed: " + exc.getMessage(), exc);
                    }

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {

                        String savedUri = file.getAbsolutePath();
                        //gotoNextFragment(output);
                        //bundle.putString("PhotoPath", String.valueOf(output.getSavedUri()));
                        gotoNextFragment(savedUri);

                    }
                });
    }


    private File getOutputDirectory() {
        File mediaDir = new File(MLApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ML-APP");
        // Check if the directory exists; if not, create it
        if (!mediaDir.exists()) {
            if (!mediaDir.mkdirs()) {
                // Handle directory creation failure
                Log.e("TAG", "Failed to create directory");
            }
        }

        return mediaDir;
    }



    private void gotoNextFragment(String output){
        Bundle bundle = new Bundle();
        bundle.putString("PhotoPath", output);
        //bundle.putString("PhotoPath", String.valueOf(output.getSavedUri()));
        NavHostFragment.findNavController(CaptureImageFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
    }


    private void onPermissionRequestResult(boolean isGranted){
            if (isGranted) {
                // Permission is granted, you can now use the feature that requires it
                this.startCamera();
            } else {
                // Permission is denied, handle accordingly
                // You can show a message to the user or disable the feature
                makeToastInMainThread("Permission request denied");
            }

    }

    private void makeToastInMainThread(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }



}