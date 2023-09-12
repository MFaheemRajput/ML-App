package com.example.ml_test_android.repos;


import android.graphics.Bitmap;
import android.util.Pair;

import com.example.ml_test_android.models.Error;
import com.example.ml_test_android.models.Success;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

// here we will handle ML library
public interface ObjectDetectionRepo {
//    public Map<String, Float> executeObjectDetection(String imagePath);
//
public CompletableFuture<Pair<Error, Success<Map<String,Object>>>> executeObjectDetectionAsync(Bitmap image, int imageRotation);
}
