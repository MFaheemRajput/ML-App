package com.example.ml_test_android.view;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ml_test_android.view_models.ImageProcessingViewModel;
import com.example.ml_test_android.MLApp;
import com.example.ml_test_android.adaptor.ObjectListAdaptor;
import com.example.ml_test_android.databinding.FragmentSecondBinding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ImageProcessingFragment extends Fragment {

    private ImageProcessingViewModel imageProcessingViewModel;
    private FragmentSecondBinding binding;
    private ObjectListAdaptor objectListAdaptor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectListAdaptor = new ObjectListAdaptor(new ArrayList<>());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));

        binding.recyclerView.setAdapter(objectListAdaptor);
        // Initialize ViewModel using ViewModelProvider
        imageProcessingViewModel = new ViewModelProvider(this).get(ImageProcessingViewModel.class);

        // Observe changes in LiveData from the ViewModel
        imageProcessingViewModel.getFoundObjects().observe(getViewLifecycleOwner(), objectModels -> {
            // Update UI with LiveData changes
            binding.progressCircular.setVisibility(View.GONE);
            if (objectModels != null && !objectModels.isEmpty()) {
                objectListAdaptor.setLocalDataSet(objectModels);
                objectListAdaptor.notifyDataSetChanged();
            } else {
                // Handle the case when there are no objects found
                makeToastInMainThread("No Object Detect");
            }
        });
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.progressCircular.setVisibility(View.VISIBLE);
        final String path = getArguments().getString("PhotoPath");
        final Uri uri = Uri.parse(path);
        setImageOnView(path);
        startDetection(path);

    }

    @SuppressLint("UnsafeOptInUsageError")
    private void startDetection(String imagePath) {
        Bitmap bitmapImage = uriToBitmap(imagePath);
        if (bitmapImage != null) {
            imageProcessingViewModel.startProcessing(bitmapImage, imagePath);
        }
    }

    private void setImageOnView(String imagePath) {
        Bitmap bitmapImage = uriToBitmap(imagePath);
        if (bitmapImage != null) {
            binding.imageView.setImageBitmap(bitmapImage);
        }
    }

    @Nullable
    private Bitmap uriToBitmap(String imageUri) {

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri);
            if (bitmap != null) {
                // You have successfully loaded the bitmap. You can use it as needed.
                // Rotate the image 90 degrees clockwise
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                return rotatedBitmap;
            } else {
                // Error handling if the bitmap cannot be loaded
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void makeToastInMainThread(String message){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
