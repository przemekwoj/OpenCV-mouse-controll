package com.przemo.ImageProcessing.controllers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.przemo.ImageProcessing.task.RecordingClass;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Button startCameraButton;
    @FXML
    private ImageView currentFrame;
    
    private VideoCapture videoCapture;
        
    private BufferedImage image;
    
    private WritableImage writableImage;
    
    private Mat matrix;
    
    private WritableRaster raster;
    
    private DataBufferByte dataBuffer;
    
    private byte[] data;
    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
    
    // on button click
    public void startCamera(ActionEvent event) throws InterruptedException, ExecutionException
    {
    	//create object of RecoringClass which extends Task<Void> class thats provide us using background process
    	RecordingClass taskWebCam = new RecordingClass(currentFrame, videoCapture, image, writableImage, matrix, raster, dataBuffer, data);
		Thread th = new Thread(taskWebCam);
		th.setDaemon(true);
		th.start();
    	
    }
    
  
   
}
