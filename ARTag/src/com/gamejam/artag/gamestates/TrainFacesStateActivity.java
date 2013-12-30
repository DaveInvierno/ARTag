package com.gamejam.artag.gamestates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.gamejam.artag.R;

public class TrainFacesStateActivity extends Activity {
	
	private static final String TAG = "TrainFacesActivity";
	private boolean isTakePictureBtnClicked = false;
	
	private Button mTakePictureButton;
	
	private Camera mCamera;
	private SurfaceView mSurfaceView;
	
	private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			try {
	            saveImage(data, camera);
	            camera.addCallbackBuffer(data);
	        } catch (RuntimeException e) {
	            // The camera has probably just been released, ignore.
	        }
		}
	};
	
	private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
		
		public void onPictureTaken(byte[] data, Camera camera) {
			// Create a filename
			String filename = UUID.randomUUID().toString() + ".jpg";
			// Save the jpeg data to disk
			FileOutputStream os = null;
			boolean success = true;
			//File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File ("/sdcard/artag/data");
			dir.mkdirs();
			File file = new File(dir, filename);
			
			try {
				//os = openFileOutput(filename, Context.MODE_PRIVATE);
				os = new FileOutputStream(file);
				os.write(data);
			} catch (Exception e) {
				Log.e(TAG, "Error writing to file " + filename, e);
				success = false;
			} finally {
			
				try {
					if (os != null)
						os.close();
				} catch (Exception e) {
					Log.e(TAG, "Error closing file " + filename, e);
					success = false;
				}
			}
			mCamera.stopPreview();
			mCamera.startPreview();
		}
	};
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_train_faces_state);
        
        mTakePictureButton = (Button)findViewById(R.id.crime_camera_takePictureButton);
		mTakePictureButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//getActivity().finish();
				if (mCamera != null) {
					//mCamera.takePicture(null, null, mJpegCallback);
					isTakePictureBtnClicked = true;
				}
			}
		});
		
		mSurfaceView = (SurfaceView)findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		// setType() and SURFACE_TYPE_PUSH_BUFFERS are both deprecated,
		// but are required for Camera preview to work on pre-3.0 devices.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// We can no longer display on this surface, so stop the preview.
				if (mCamera != null) {
					mCamera.stopPreview();
				}
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// Tell the camera to use this surface as its preview area
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
						mCamera.setPreviewCallback(mPreviewCallback);
					}
				} catch (IOException exception) {
					Log.e(TAG, "Error setting up preview display", exception);
				}
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
				if (mCamera == null) return;
				
				// The surface has changed size; update the camera preview size
				Camera.Parameters parameters = mCamera.getParameters();
				Size s = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
				parameters.setPreviewSize(s.width, s.height);
				s = getOptimalPreviewSize(parameters.getSupportedPictureSizes(), width, height);
				parameters.setPictureSize(s.width, s.height);
				mCamera.setParameters(parameters);
				
				try {
					//mCamera.setPreviewCallback(mPreviewCallback);
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Could not start preview", e);
					mCamera.release();
					mCamera = null;
				}
				
			}
		});
		
		
    }
	
	@TargetApi(9)
	@Override
	public void onResume() {
		super.onResume();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
	
	/** A simple algorithm to get the largest size available. For a more
	* robust version, see CameraPreview.java in the ApiDemos
	* sample app from Android. */
	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		
		for (Size s : sizes) {
			int area = s.width * s.height;
			
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		
		return bestSize;
	}

	private void saveImage(byte[] data, Camera camera) {
		if(isTakePictureBtnClicked == false) return;
		
		isTakePictureBtnClicked = false;
		mTakePictureButton.setEnabled(false);
		mCamera.stopPreview();
		
		// Create a filename
		String filename = UUID.randomUUID().toString() + ".jpg";
		// Save the jpeg data to disk
		FileOutputStream os = null;
		//File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (Environment.getExternalStorageDirectory() + "/artag/data");
		dir.mkdirs();
		File file = new File(dir, filename);

		try {
			// Convert preview data to YUV
			Camera.Parameters parameters = camera.getParameters(); 
	        Size size = parameters.getPreviewSize(); 
			YuvImage image = new YuvImage(data, parameters.getPreviewFormat(), 
	                size.width, size.height, null);
			//os = openFileOutput(filename, Context.MODE_PRIVATE);
			os = new FileOutputStream(file);
			image.compressToJpeg( 
	                new Rect(0, 0, image.getWidth(), image.getHeight()), 90, 
	                os); 
		} catch (Exception e) {
			Log.e(TAG, "Error writing to file " + filename, e);
		} finally {
		
			try {
				if (os != null)
					os.close();
			} catch (Exception e) {
				Log.e(TAG, "Error closing file " + filename, e);
			}
		}
		
		mTakePictureButton.setEnabled(true);
		mCamera.startPreview();
	}
}

