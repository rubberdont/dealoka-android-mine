package com.dealoka.lib.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.dealoka.lib.Config;
import com.dealoka.lib.GeneralCalendar;
import com.dealoka.lib.Image;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

@SuppressWarnings("deprecation")
public class CameraController {
	private Context context;
	private Camera camera;
	private CameraPreview camera_preview;
	private View camera_view;
	private View saved_master_view;
	private FrameLayout frame_layout;
	private CameraCallback camera_callback;
	private boolean opened;
	private boolean flash;
	private boolean save;
	private static int current_camera_id;
	public interface CameraCallback {
		public abstract void didGetPicture(final byte[] data);
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public CameraController(
			final Context context,
			final View master_view,
			final boolean front,
			final CameraCallback camera_callback) {
		this.context = context;
		this.saved_master_view = master_view;
		if(front) {
			current_camera_id = Camera.CameraInfo.CAMERA_FACING_FRONT;
		}else {
			current_camera_id = Camera.CameraInfo.CAMERA_FACING_BACK;
		}
		this.camera_callback = camera_callback;
		opened = safeCameraOpenInView(master_view);
	}
	public void showPreview(final FrameLayout frame_layout, final boolean flash) {
		this.frame_layout = frame_layout;
		this.flash = flash;
		if(opened) {
			camera_preview = new CameraPreview(context, camera, camera_view);
			frame_layout.addView(camera_preview);
			camera_preview.startCameraPreview();
		}
	}
	public void setCameraCallback(final CameraCallback camera_callback) {
		this.camera_callback = camera_callback;
	}
	public boolean isOpened() {
		return opened;
	}
	public void reInitiated() {
		opened = safeCameraOpenInView(saved_master_view);
	}
	public void take(final boolean save) {
		this.save = save;
		if(opened) {
			camera.takePicture(null, null, picture_callback);
		}
	}
	public void close() {
		releaseCameraAndPreview();
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void switchCamera() {
		releaseCameraAndPreview();
		if(hasMoreCamera()) {
			if(current_camera_id == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				current_camera_id = Camera.CameraInfo.CAMERA_FACING_BACK;
			}else {
				current_camera_id = Camera.CameraInfo.CAMERA_FACING_FRONT;
			}
		}
		frame_layout.removeAllViews();
		reInitiated();
		showPreview(frame_layout, flash);
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static boolean hasMoreCamera() {
		return (Camera.getNumberOfCameras() > 1);
	}
	private static Camera getCameraInstance() {
		Camera c = null;
		try {
			if(hasMoreCamera()) {
				c = openCameraWithCameraId(current_camera_id);
			}else {
				c = Camera.open();
			}
		}catch(Exception ex) {}
		return c;
	}
	private boolean safeCameraOpenInView(final View master_view) {
		boolean result = false;
		releaseCameraAndPreview();
		camera = getCameraInstance();
		camera_view = master_view;
		result = (camera != null);
		return result;
	}
	private void releaseCameraAndPreview() {
		if(camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		if(camera_preview != null){
			camera_preview.destroyDrawingCache();
			camera_preview.camera = null;
		}
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private static Camera openCameraWithCameraId(final int camera_id) {
		return Camera.open(current_camera_id);
	}
	private class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder surface_holder;
		private Camera camera;
		private Context context;
		private Camera.Size preview_size;
		private List<Camera.Size> supported_preview_sizes;
		private List<String> supported_flash_modes;
		private View camera_view;
		public CameraPreview(
				final Context context,
				final Camera camera,
				final View camera_view) {
			super(context);
			this.camera_view = camera_view;
			this.context = context;
			setCamera(camera);
			surface_holder = getHolder();
			surface_holder.addCallback(this);
			surface_holder.setKeepScreenOn(true);
			surface_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		public void startCameraPreview() {
			try {
				camera.setPreviewDisplay(surface_holder);
				camera.startPreview();
			}catch(Exception ex) {}
		}
		private void setCamera(final Camera camera) {
			this.camera = camera;
			supported_preview_sizes = camera.getParameters().getSupportedPreviewSizes();
			if(flash) {
				supported_flash_modes = camera.getParameters().getSupportedFlashModes();
				if(supported_flash_modes != null && supported_flash_modes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
					Camera.Parameters parameters = camera.getParameters();
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
					camera.setParameters(parameters);
				}
			}
			requestLayout();
		}
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(holder);
			}catch(IOException ex) {}
		}
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(camera != null) {
				camera.stopPreview();
			}
		}
		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			if(surface_holder.getSurface() == null) {
				return;
			}
			try {
				Camera.Parameters parameters = camera.getParameters();
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
				if(preview_size != null) {
					Camera.Size camera_preview_size = preview_size;
					parameters.setPreviewSize(camera_preview_size.width, camera_preview_size.height);
				}
				camera.setParameters(parameters);
				camera.startPreview();
			}catch(Exception ex) {}
		}
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
			setMeasuredDimension(width, height);
			if(supported_preview_sizes != null) {
				preview_size = getOptimalPreviewSize(supported_preview_sizes, width, height);
			}
		}
		@TargetApi(Build.VERSION_CODES.FROYO)
		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			if(changed) {
				final int width = right - left;
				final int height = bottom - top;
				int preview_width = width;
				int preview_height = height;
				if(preview_size != null) {
					Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
					switch(display.getRotation()) {
						case Surface.ROTATION_0:
							preview_width = preview_size.height;
							preview_height = preview_size.width;
							camera.setDisplayOrientation(90);
							break;
						case Surface.ROTATION_90:
							preview_width = preview_size.width;
							preview_height = preview_size.height;
							break;
						case Surface.ROTATION_180:
							preview_width = preview_size.height;
							preview_height = preview_size.width;
							break;
						case Surface.ROTATION_270:
							preview_width = preview_size.width;
							preview_height = preview_size.height;
							camera.setDisplayOrientation(180);
							break;
					}
				}
				final int scaled_child_height = preview_height * width / preview_width;
				camera_view.layout(0, height - scaled_child_height, width, height);
			}
		}
		private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
//			Camera.Size optimal_size = null;
//			final double ASPECT_TOLERANCE = 0.1;
//			double target_ratio = (double) height / width;
//			for(Camera.Size size : sizes) {
//				if(size.height != width) continue;
//				double ratio = (double) size.width / size.height;
//				if(ratio <= target_ratio + ASPECT_TOLERANCE && ratio >= target_ratio - ASPECT_TOLERANCE) {
//					optimal_size = size;
//				}
//			}
//			return optimal_size;
			Camera.Size result = null;    
			Camera.Parameters p = camera.getParameters();
			for(Camera.Size size : p.getSupportedPreviewSizes()) {
				if(size.width <= width && size.height <= height) {
					if(result == null) {
						result = size;
					}else {
						int result_area = result.width * result.height;
						int new_area = size.width * size.height;
						if(new_area > result_area) {
							result = size;
						}
					}
				}
			}
			return result;
		}
	}
	private Camera.PictureCallback picture_callback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inDither = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inTempStorage = new byte[32 * 1024];
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
			int orientation = 0;
			if(bitmap.getHeight() < bitmap.getWidth()) {
				orientation = 90;
			}else {
				orientation = 0;
			}
			Bitmap bitmap_rotate;
			if(orientation != 0) {
				Matrix matrix = new Matrix();
				matrix.postRotate(orientation);
				bitmap_rotate = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}else {
				bitmap_rotate = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
			}
			final byte[] bitmap_data = Image.getBytesFromBitmap(bitmap_rotate, 100);
			if(camera_callback != null) {
				camera_callback.didGetPicture(bitmap_data);
			}
			if(save) {
				File picture_file = getOutputMediaFile();
				if(picture_file == null) {
					return;
				}
				try {
					FileOutputStream fos = new FileOutputStream(picture_file);
					fos.write(bitmap_data);
					fos.close();
				}catch(FileNotFoundException ex) {
				}catch(IOException ex) {}
			}
			camera.startPreview();
		}
	};
	@TargetApi(Build.VERSION_CODES.FROYO)
	private File getOutputMediaFile() {
		File media_storage_dir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), Config.TAG);
		if(!media_storage_dir.exists()) {
			if(!media_storage_dir.mkdirs()) {
				return null;
			}
		}
		File media_file  = new File(media_storage_dir.getPath() + File.separator + "IMG_"+ GeneralCalendar.getUTCTimestamp() + ".jpg");
		return media_file;
	}
}
