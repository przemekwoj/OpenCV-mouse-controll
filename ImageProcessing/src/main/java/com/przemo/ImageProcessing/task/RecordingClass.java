package com.przemo.ImageProcessing.task;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import static org.opencv.photo.Photo.fastNlMeansDenoising;

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
    
    private List<MatOfPoint> contours;
    private List<MatOfPoint2f> approxCurves;
    
    private Mat hierarchy;
    
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
    		//gaussian blurring
    		//Imgproc.GaussianBlur(matrix, matrix, new Size(7,7),0);
        	//convert color image to grey image
    		convertColor(matrix);
    		//dilation white color are more prominent
    		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(13,13));
     		Imgproc.dilate(matrix, matrix, kernel);
    		//edrode - oppisite to dilation
     		kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(13,13));
      		Imgproc.erode(matrix, matrix, kernel);
      		//gaussian blurring
    		Imgproc.GaussianBlur(matrix, matrix, new Size(45,45),0);
    		Core.inRange(matrix, new Scalar(85), new Scalar(95), matrix);
    		//to zero
    		//Imgproc.threshold(matrix, matrix, 110, 255, Imgproc.THRESH_TOZERO);
    		//grey image fast denoise
    		//denoiseImage(matrix);
    		//grey to binary, just to make image more simple , less detail
    		//grey_to_binary(matrix);
    		//edge detection
    		edgeDetection(matrix);
    		//findContours
    		findContorus(matrix);
    		//circle detecting
    		detectingCircle();
    		//drawContorus
    		Imgproc.drawContours(matrix, contours, -1, new Scalar(255), -1);
        	// Creating BuffredImage from the matrix
    		createBufferImage(matrix);
            // Creating the Writable Image
    		createWritableImg(image);
             //View Image
            currentFrame.setImage(writableImage);
    	}
	}
	
	int i = 0;
	public void detectingCircle()
	{
		MatOfPoint matOfPoint = new MatOfPoint();
	    MatOfPoint2f approx = new MatOfPoint2f();
	    approxCurves = new ArrayList<MatOfPoint2f>();
	    System.out.println(contours.size() + " cntours size befor");
	    for(int i = 0 ; i<contours.size();i++)
	    {
	    	if( Imgproc.contourArea(contours.get(i)) < 65)
	    	{
	    		contours.remove(i);
	    		i--;
	    	}
	    }
	    System.out.println(contours.size() + " cntours size after");
	    
	    int j = 0;
	    List<Integer> toRemove = new ArrayList<Integer>();
		for(MatOfPoint contour: contours)
		{
			double epsilon = 0.0001*Imgproc.arcLength(new MatOfPoint2f(contours.get(j).toArray()),true);
		    Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(j).toArray()),approx,epsilon,true);
		    if(approx.width()*approx.height() < 150)
		    {
		    	toRemove.add(j);
		    }
		    j++;
		}
		
		for(int i = 0 ,index = 0; i < toRemove.size() ; i++ )
		{
			contours.remove(toRemove.get(i) - index);
			index++;
		}
		
	}


	public void filter2d() 
	{
		//create kernel , mask
		Mat ones = Mat.ones( 3, 3, matrix.depth());		
		int row = 0, col = 0;
		for(int i = 0; i<ones.height(); i++)
		{
			for(int j = 0; j<ones.width(); j++)
			{
				ones.put(i, j, 1);
			}
		}
		//2d filtering
		Imgproc.filter2D(matrix, matrix, matrix.depth(), ones);
	}


	public void denoiseImage(Mat matrix) 
	{
		fastNlMeansDenoising(matrix,matrix);
	}


	public void grey_to_binary(Mat matrix) 
	{
		Imgproc.threshold(matrix, matrix, 30, 255, Imgproc.THRESH_BINARY);
	}


	public void findContorus(Mat matrix)
	{
		contours = new ArrayList<MatOfPoint>();
		hierarchy = new Mat();
		Imgproc.findContours(matrix, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

		
		for(int i = 0; i<matrix.height(); i++)
		{
			for(int j = 0; j<matrix.width(); j++)
			{
				matrix.put(i, j, 0);
			}
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
