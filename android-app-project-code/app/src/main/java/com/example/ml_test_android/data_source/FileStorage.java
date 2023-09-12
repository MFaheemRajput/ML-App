package com.example.ml_test_android.data_source;

import com.example.ml_test_android.models.ObjectModel;

import java.io.File;
import java.util.ArrayList;

public interface FileStorage {
    public File createFile(String imageFileName, String imageFilePath);
    public void writeFile(String imageFileName, String imageFilePath, ArrayList<ObjectModel> data);
    public String readFile(String imageFileName, String imageFilePath);
}
