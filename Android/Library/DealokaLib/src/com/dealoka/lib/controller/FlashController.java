package com.dealoka.lib.controller;

import java.util.List;

import com.dealoka.lib.Config;
import com.dealoka.lib.Logger;

import android.hardware.Camera;

@SuppressWarnings("deprecation")
public class FlashController {
	private Camera camera;
	private Camera.Parameters camera_parameters;
	private boolean opened;
	public FlashController() {
		open();
	}
	public void open() {
		opened = safeCameraOpen();
		if(opened) {
			camera_parameters = camera.getParameters();
		}
	}
	public void close() {
		releaseCamera();
	}
	public boolean isFlashSupported() {
		if(camera_parameters == null) {
			return false;
		}
		final List<String> flash_modes = camera_parameters.getSupportedFlashModes();
		if(flash_modes == null) {
			return false;
		}
		for(final String flash_mode : flash_modes) {
			if(Camera.Parameters.FLASH_MODE_TORCH.equals(flash_mode)) {
				return true;
			}
		}
		return false;
	}
	public void flashOn() {
		if(opened) {
			final List<String> flash_modes = camera_parameters.getSupportedFlashModes();
			if(flash_modes != null && flash_modes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
				Camera.Parameters parameters = camera.getParameters();
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				camera.setParameters(parameters);
			}
			camera.startPreview();
		}
	}
	public void flashOff() {
		if(opened) {
			close();
		}
		open();
	}
	private static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		}catch(Exception ex) {
			if(Config.LOG_ENABLED) {
				Logger.d(ex.getMessage());
			}
		}
		return c;
	}
	private boolean safeCameraOpen() {
		boolean result = false;
		releaseCamera();
		camera = getCameraInstance();
		result = (camera != null);
		return result;
	}
	private void releaseCamera() {
		if(camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		opened = false;
	}
}
