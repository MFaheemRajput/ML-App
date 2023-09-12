package com.example.ml_test_android.data_source;

import android.util.Log;

import com.example.ml_test_android.models.ObjectModel;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;

public class FileStorageImpl implements FileStorage{
    //private String folderName = null;
    private File root =  null;

    public FileStorageImpl(String imageFilePath){

        root = new File(imageFilePath);;
    }


    @Override
    public File createFile(String imageFileName, String imageFilePath) {
        try {
            if (!root.exists()) {
                root.mkdirs();
            }
            File dataFile = new File(root, imageFileName);
            FileWriter writer = new FileWriter(dataFile);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        };
        return root;
    }

    @Override
    public void writeFile(String imageFileName, String imageFilePath, ArrayList<ObjectModel> data) {

        File fileToWriteOn = new File(root, imageFileName);
        try{
            if (!fileToWriteOn.exists()) {
                fileToWriteOn.mkdir();
            }

                // Create a BufferedWriter
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWriteOn));

                // Serialize each object and write it to the file as a Base64-encoded string
                for (ObjectModel obj : data) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(obj);
                    objectOutputStream.close();
                    String base64String = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                    writer.write(base64String);
                    writer.newLine(); // Add a newline after each object
                }

                // Close the writer
                writer.close();

                System.out.println("Serializable objects saved to " + imageFilePath+imageFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readFile(String imageFileName, String imageFilePath) {
        return null;
    }
}
