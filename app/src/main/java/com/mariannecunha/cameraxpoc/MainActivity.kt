package com.mariannecunha.cameraxpoc

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private val viewFinder by lazy { findViewById<PreviewView>(R.id.view_finder) }
    private val photoButton by lazy { findViewById<Button>(R.id.camera_capture_button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
        setUpPhotoButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun setUpPhotoButton() {

        photoButton?.setOnClickListener {
            imageCapture?.let {
                takePhoto(it)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        imageCapture = ImageCapture
                .Builder()
                .build()
                .also {
                    startCamera(it, viewFinder)
                }
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasCameraPermissions(this)) {
            imageCapture = ImageCapture
                    .Builder()
                    .build()
                    .also {
                        startCamera(it, viewFinder)
                    }
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept camera permissions to use this app",
                    REQUEST_CODE_CAMERA_PERMISSION,
                    android.Manifest.permission.CAMERA
            )
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "You need to accept camera permissions to use this app",
                    REQUEST_CODE_CAMERA_PERMISSION,
                    android.Manifest.permission.CAMERA

            )
        }
    }

    companion object {
        const val REQUEST_CODE_CAMERA_PERMISSION: Int = 2021
    }
}