# DelftAI Android Challenge: Object Detection with EfficientDet-Lite2

## Objective

Develop an Android application that captures a photo, analyzes it using the EfficientDet-Lite2 model, and displays the detected objects in an organized manner.
This should be done in **Java**.

The application is expected to work in phones with at least **Android 10**.

## Requirements

### 1. Capture Photo

- Implement a feature that allows users to capture a photo using the device's camera.
- Ensure that the captured photo is of good quality and is stored temporarily for processing.

### 2. Analyze with EfficientDet-Lite2

- Integrate the EfficientDet-Lite2 model into your application.
- Once a photo is captured, process it using the model to detect objects within the image.
- Ensure that the processing is efficient and does not cause the application to hang or crash.

### 3. Save Results

- After processing, save the results (detected objects and their confidence scores) to a local storage on the device. This could be in the form of a text file, database, or any other suitable storage format.
- Ensure that the saved data is structured and can be retrieved easily for display.

### 4. Display Detected Objects

- Create a user-friendly interface to display the detected objects from the analyzed photo.
- For each detected object, display:
    - The name of the object.
    - The confidence score (e.g., 95% confident that the object is a "dog").
- Organize the display in a structured manner (ListView, GridView, RecyclerView, etc.).
- Provide an option for users to view the original photo alongside the detected objects.

## Evaluation Criteria

- Functionality: Ensure that all the required features are implemented and working as expected.
- Code Quality: Write clean, organized, and well-documented code. Follow best practices for Android development.
- User Experience: The application should be user-friendly, intuitive, and visually appealing.
- Performance: The application should be responsive and should not have any noticeable lags, especially during the image processing phase.
- Error Handling: Handle potential errors gracefully. For instance, if the camera fails to capture an image or if the model fails to process an image.

## Submission

Provide a link to the GitHub repository containing your code.

Include a README.md in your repository with instructions on how to set up, build, and run your application.

Documentation must be included in the repository explaining your thought process. For example, implementation details explaining the decision on the architecture and the libraries that were used to accomplish the APP. Do not forget to include the phone where the code was tested.

A maximum of 1 week is provided to finish the challenge.

## Additional Data

You can find the model available here <https://tfhub.dev/tensorflow/efficientdet/lite2/detection/1> with some example code on how to perform the inference.

The tar file containing the model is also available within `model` folder.


##Design
    project contains partially MVVM archtecture with navigation component (i use the approach "dont over engineer").
        .Unlike most of android apps use multiple activities google now incourageing single activity and multiple fragments. 
        .So there are one amin activity which 2 fragments 
            1)Image Capture Fragment
                a)Image Capture Fragment is responsible to capture image (custom camera created to get more controll in future). once it capture the photo i saved the in app's document directory (not using photos app for security reasons).
                b)On complition of capturing and saving photo I pass uri of photos to other fragement(Image Processing Fragment). 
                NOTE: i am also checking user permission to access camera and availibility of camera hardware.
            2)Image Processing Fragment
                a) reading of passed uri and then set the image into image view and call the startDetection method. this fragement usign MVVM architecture due to nature of work so ViewMOdel is responsibel to talk to Object Dectection repo class (this class is interface between tensorflow and our application in case in future you want to remove tensor flow and use somehting else jsut make changes here and no need to touch whole app.) Object Dectection repo uses Some of SOLID principles. 
                b) startDetection start the processing and i show spinner to user and if we found any objects i show it in list otherwise a toast with message.
            3)Saving image and Detections
                a) as i already saved the image in app's document dir i am also saving the detected objects list into from of txt file at same place with same name. 
                    for example image name(which is timestamp + jgp) is 12345678.jpg' saved detection's file also 12345678.txt. i used this jsut for shoing you i am saving data and in future we can improve it. 
                Note) for now i am not read stored detections. 
