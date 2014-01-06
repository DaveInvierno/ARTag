package com.gamejam.artag.imageproc;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.gamejam.artag.R;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public class FaceView extends View implements Camera.PreviewCallback, OnTouchListener {
    public static final int SUBSAMPLING_FACTOR = 4;

    private IplImage grayImage;
    private CvHaarClassifierCascade classifier;
    private CvMemStorage storage;
    private CvSeq faces;
    private byte[] data;
    private Bitmap mCrosshair;
    private Bitmap mGun;
    private Bitmap mGunFire;
    private boolean isFiring = false;
    
    public FaceView(Activity context) throws IOException {
        super(context);

        // Load the classifier file from Java resources.
        File classifierFile = Loader.extractResource(getClass(),
            "/res/raw/haarcascade_frontalface_alt.xml",
            context.getCacheDir(), "classifier", ".xml");
        if (classifierFile == null || classifierFile.length() <= 0) {
            throw new IOException("Could not extract the classifier file from Java resource.");
        }

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);
        classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
        classifierFile.delete();
        if (classifier.isNull()) {
            throw new IOException("Could not load the classifier file.");
        }
        storage = CvMemStorage.create();
        
        mCrosshair = BitmapFactory.decodeResource(getResources(), R.drawable.crosshair);
        mGun = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gun), 320, 320, false);
        mGunFire = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gun_fire), 320, 320, false);
        //mGun = BitmapFactory.decodeResource(getResources(), R.drawable.gun);
        setOnTouchListener(this);
    }

    public void onPreviewFrame(final byte[] data, final Camera camera) {
        try {
        	this.data = data;
            Camera.Size size = camera.getParameters().getPreviewSize();
            processImage(data, size.width, size.height);
            camera.addCallbackBuffer(data);
        } catch (RuntimeException e) {
            // The camera has probably just been released, ignore.
        }
    }

    protected void processImage(byte[] data, int width, int height) {
        // First, downsample our image and convert it into a grayscale IplImage
        int f = SUBSAMPLING_FACTOR;
        if (grayImage == null || grayImage.width() != width/f || grayImage.height() != height/f) {
            grayImage = IplImage.create(width/f, height/f, IPL_DEPTH_8U, 1);
        }
        int imageWidth  = grayImage.width();
        int imageHeight = grayImage.height();
        int dataStride = f*width;
        int imageStride = grayImage.widthStep();
        ByteBuffer imageBuffer = grayImage.getByteBuffer();
        for (int y = 0; y < imageHeight; y++) {
            int dataLine = y*dataStride;
            int imageLine = y*imageStride;
            for (int x = 0; x < imageWidth; x++) {
                imageBuffer.put(imageLine + x, data[dataLine + f*x]);
            }
        }
        
        cvClearMemStorage(storage);
        faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(20);

        String s = "FacePreview - This side up.";
        float textWidth = paint.measureText(s);
        canvas.drawText(s, (getWidth()-textWidth)/2, 20, paint);

        if (faces != null) {
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);
            float scaleX = (float)getWidth()/grayImage.width();
            float scaleY = (float)getHeight()/grayImage.height();
            int total = faces.total();
            for (int i = 0; i < total; i++) {
            	CvRect r = new CvRect(cvGetSeqElem(faces, i));
            	IplImage face = IplImage.create(r.width(), r.height(), IPL_DEPTH_8U, 1);
            	
            	int imageWidth  = r.width();
                int imageHeight = r.height();
                int dataStride = r.width();
                int imageStride = grayImage.widthStep();
                ByteBuffer imageBuffer = grayImage.getByteBuffer();
                for (int y = 0; y < imageHeight; y++) {
                    int dataLine = y*dataStride;
                    int imageLine = y*imageStride;
                    for (int x = 0; x < imageWidth; x++) {
                        imageBuffer.put(imageLine + x, data[dataLine + x]);
                    }
                }
                
                IplImage faceOutput = IplImage.create(72, 48, IPL_DEPTH_8U, 1);
                cvResize(face, faceOutput);
                
                if(FaceRecognition.getInstance().recognizeFace(faceOutput) > 60  ) {
                	paint.setColor(Color.GREEN);
                } else {
                	paint.setColor(Color.RED);
                }
                
            	
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                canvas.drawRect((x*scaleX), (y*scaleY), ((x+w)*scaleX), (y+h)*scaleY, paint);
                Log.d("FaceView", "x = " + x*scaleX + " y = " + y*scaleY + " w = " + ((x+w)*scaleX) + " h = " + (y+h)*scaleY);
            }
        }
        
        canvas.drawBitmap(mCrosshair, (canvas.getWidth() / 2) - (mCrosshair.getWidth() / 2), (canvas.getHeight() / 2) - (mCrosshair.getHeight() / 2), paint);
        if(isFiring) {
        	isFiring = false;
        	canvas.drawBitmap(mGunFire, (canvas.getWidth() / 2) + mGunFire.getWidth(), canvas.getHeight() - mGunFire.getHeight(), paint);
        } else {
        	canvas.drawBitmap(mGun, (canvas.getWidth() / 2) + mGun.getWidth(), canvas.getHeight() - mGun.getHeight(), paint);
        }
    }
	
	public void cleanUp() {
		mCrosshair.recycle();
		mGun.recycle();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d("InGameStateActivity", "Click... x = " + event.getX() + " y = " + event.getY());
		isFiring = true;
		return false;
	}
}