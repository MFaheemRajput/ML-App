package com.example.ml_test_android.repos;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.example.ml_test_android.MLApp;
import com.example.ml_test_android.models.Error;
import com.example.ml_test_android.models.Success;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;
import org.tensorflow.lite.support.image.ops.Rot90Op;


public class ObjectDetectionRepoImpl implements ObjectDetectionRepo {

    private String modelName = "efficientdet-lite2.tflite";
    //private String modelName = "efficientdet_lite2_detection_1.tflite";
    private Float threshold = 0.10f;
    private int numThreads = 2;
    private int maxResults = 10;

    private ObjectDetector objectDetector;


    @Nullable
    public CompletableFuture<Pair<Error, Success<Map<String,Object>>>> executeObjectDetectionAsync(Bitmap image, int imageRotation){

        return CompletableFuture.supplyAsync(() -> {
            // Simulate an asynchronous operation (e.g., fetching data).
            try {
                return this.detectObjects(image, imageRotation);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                return null;
            }
        });

    }


    public ObjectDetectionRepoImpl(){
        try {
            this.setup();
        }  catch (Exception e){
            // convert into failure
            Log.e("what the Fuck", e.getLocalizedMessage());
        }

    }

    private void setup()  {
        try {
            ObjectDetector.ObjectDetectorOptions.Builder optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder().setScoreThreshold(threshold).setMaxResults(maxResults);
            BaseOptions.Builder baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads);
            optionsBuilder.setBaseOptions(baseOptionsBuilder.build());
            this.objectDetector = ObjectDetector.createFromFileAndOptions(MLApp.getInstance().getApplicationContext(), modelName, optionsBuilder.build());
            Log.e("Test", "TFLite failed to load model with error");
        } catch (IllegalStateException e) {
            Log.e("Test", "TFLite failed to load model with error: " + e.getLocalizedMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Pair<Error,Success<Map<String,Object>>> detectObjects(Bitmap image, int imageRotation) throws Exception {
        if (this.objectDetector == null) {
            try {
                setup();
            } catch (Exception e){
                // convert into failure
                return Pair.create(new Error(e.getLocalizedMessage()), null );
            }
        }

        // Inference time is the difference between the system time at the start and finish of the
        // process
        //long inferenceTime = SystemClock.uptimeMillis();

        ImageProcessor imageProcessor = new ImageProcessor.Builder().add(new Rot90Op(-imageRotation / 90)).build();

        // Preprocess the image and convert it into a TensorImage for detection.
        TensorImage tensorImage = imageProcessor.process(TensorImage.fromBitmap(image));

        List<Detection> results = this.objectDetector.detect(tensorImage);

        HashMap<String,Detection> uniqueDetectionsMap= new HashMap() ;
        for (Detection d: results) {
            Category category = d.getCategories().get(0);
            if(uniqueDetectionsMap.get(category.getLabel()) != null){
                if(uniqueDetectionsMap.get(category.getLabel()).getCategories().get(0).getScore() < category.getScore()){
                    uniqueDetectionsMap.put(category.getLabel(),d);
                }
            } else {
                uniqueDetectionsMap.put(category.getLabel(),d);
            }

        }

        //inferenceTime = SystemClock.uptimeMillis() - inferenceTime;
        ArrayList<Detection> sortedDetections = new ArrayList<Detection>(uniqueDetectionsMap.values());
        sortedDetections.sort(((Comparator<Detection>) (o1, o2) -> Float.compare(o1.getCategories().get(0).getScore(), o2.getCategories().get(0).getScore())).reversed());

        HashMap<String,Object> resultMap = new HashMap<>();
        resultMap.put("results", sortedDetections);
        resultMap.put("height", tensorImage.getHeight());
        resultMap.put("width", tensorImage.getWidth());
        Success<Map<String,Object>> outputData = new Success<>(resultMap);
        return Pair.create(null,outputData);

        }

}





