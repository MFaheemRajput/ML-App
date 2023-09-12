package com.example.ml_test_android.view_models;

import android.graphics.Bitmap;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ml_test_android.data_source.FileStorageImpl;
import com.example.ml_test_android.models.Error;
import com.example.ml_test_android.models.ObjectModel;
import com.example.ml_test_android.models.Success;
import com.example.ml_test_android.repos.ObjectDetectionRepoImpl;

import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.detector.Detection;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ImageProcessingViewModel extends ViewModel {

    private ObjectDetectionRepoImpl objectDetectionRepoImpl = null;
    private FileStorageImpl fileStorage = null;

    private final MutableLiveData<ArrayList<ObjectModel>> foundObjects = new MutableLiveData<>();
    public LiveData<ArrayList<ObjectModel>> getFoundObjects() {
        return foundObjects;
    }

    public void setFoundObjects(ArrayList<ObjectModel> data){

        this.foundObjects.postValue(data);


    }

    public ImageProcessingViewModel(){
        objectDetectionRepoImpl = new ObjectDetectionRepoImpl();

    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        objectDetectionRepoImpl = null;
    }

    public void startProcessing(Bitmap image, String imagePath){
        CompletableFuture<Pair<Error, Success<Map<String,Object>>>> resultFuture = objectDetectionRepoImpl.executeObjectDetectionAsync(image,0);
        resultFuture.thenAccept(result -> {
            // Handle the result when the asynchronous operation completes successfully.

            if(result.second != null){
                Map<String,Object> resultMap = result.second.getValue();
                ArrayList<Detection> detections = (ArrayList<Detection>) resultMap.getOrDefault("results",new Object());
                System.out.println("Received result: " + detections);
                ArrayList<ObjectModel> objects =new ArrayList<>();
                for (Detection detection : detections) {
                    for (Category category:detection.getCategories()) {
                        ObjectModel detectedObject = new ObjectModel(category.getLabel(),category.getScore()*100,0f,0f,0f,0f);
                        objects.add(detectedObject);
                    }
                }

                this.setFoundObjects(objects);
                String fileName = fileName(imagePath);
                String dirPath = dirPath(imagePath);
                if(fileName != null){
                    this.writeData(fileName + ".txt", dirPath, objects);
                }


            } else {
                // something wrong it should
            }

        }).exceptionally(ex -> {
            // Handle exceptions when the asynchronous operation fails.
            System.err.println("An error occurred: " + ex.getMessage());
            return null;
        });

    }

    private void writeData(String imageFileName, String imageFilePath, ArrayList<ObjectModel> content){
        if(imageFileName == null || imageFileName.isEmpty() || imageFilePath == null || imageFilePath.isEmpty() || content == null || content.isEmpty())
        {
            return;
        }

        fileStorage = new FileStorageImpl(imageFilePath);
        fileStorage.createFile(imageFileName,imageFilePath);
        fileStorage.writeFile(imageFileName,imageFilePath,content);

    }

    private String fileName(String imagePath){
        File file = new File(imagePath);
        String fileNameWithExtension = file.getName();

// Remove the file extension
        int lastDotIndex = fileNameWithExtension.lastIndexOf(".");
        if (lastDotIndex != -1) {
            String fileNameWithoutExtension = fileNameWithExtension.substring(0, lastDotIndex);
            return fileNameWithoutExtension;
            // Now, 'fileNameWithoutExtension' contains the file name without the extension.
        } else {
            // If there is no file extension, 'fileNameWithExtension' already contains the full name without an extension.
            return null;
        }
    }

    private String dirPath(String imagePath){
        File file = new File(imagePath);
        String directoryPath = file.getParent();
        return directoryPath;
    }


}
