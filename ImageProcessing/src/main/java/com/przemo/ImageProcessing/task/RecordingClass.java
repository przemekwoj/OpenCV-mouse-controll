package com.przemo.ImageProcessing.task;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;


public class RecordingClass extends Task<Void>
{
	private ImageView currentFrame;
    
    private VideoCapture videoCapture;
        
    private BufferedImage image;
    
    private WritableImage writableImage;
    
    private Mat matrix;
    
    private WritableRaster raster;
    
    private DataBufferByte dataBuffer;
    
    private byte[] data;
    
    public RecordingClass() {}
    
    
	public RecordingClass(ImageView currentFrame, VideoCapture videoCapture, BufferedImage image,
			WritableImage writableImage, Mat matrix, WritableRaster raster, DataBufferByte dataBuffer, byte[] data) {
		super();
		this.currentFrame = currentFrame;
		this.videoCapture = videoCapture;
		this.image = image;
		this.writableImage = writableImage;
		this.matrix = matrix;
		this.raster = raster;
		this.dataBuffer = dataBuffer;
		this.data = data;
	}


	@Override
	protected Void call() throws Exception 
	{
		videoCapture = new VideoCapture(0);
		while(true)
    	//check if we connect with webcam
    	if(videoCapture.isOpened())
    	{
    		//30 frames per second
    		Thread.sleep(33);
    		// Reading the next video frame from the camera 
    		getFrame();
        	//convert color image to grey image
    		convertColor(matrix);
    		//edge detection
    		edgeDetection(matrix);
        	// Creating BuffredImage from the matrix
    		createBufferImage(matrix);
            // Creating the Writable Image
    		createWritableImg(image);
             //View Image
            currentFrame.setImage(writableImage);
    	}
	}
	
	public void getFrame()
	{
		matrix = new Mat(); 
    	videoCapture.read(matrix);
	}
	
	public void convertColor(Mat matrix)
	{
		Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_RGB2GRAY);
	}
	
	public void edgeDetection(Mat matrix)
	{
    	Imgproc.Canny(matrix, matrix, 60, 60*3);
	}
	
	public void createBufferImage(Mat matrix)
	{
		image = new BufferedImage(matrix.width(), matrix.height(), BufferedImage.TYPE_BYTE_GRAY);
        raster = image.getRaster();
        dataBuffer = (DataBufferByte) raster.getDataBuffer();
        data = dataBuffer.getData();
        matrix.get(0, 0, data);
	}
	
	public void createWritableImg(BufferedImage image)
	{
        writableImage = SwingFXUtils.toFXImage(image, null);
	}

}
