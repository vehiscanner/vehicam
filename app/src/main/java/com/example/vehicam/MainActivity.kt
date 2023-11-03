package com.example.vehicam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.vehicam.databinding.MainActivityBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException




class MainActivity : AppCompatActivity() {

    private lateinit var bindingCamera: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        bindingCamera = MainActivityBinding.inflate(layoutInflater)
        setContentView(bindingCamera.root)

        bindingCamera.btnTakePicture.isEnabled = false
        bindingCamera.btnTakeVidio.isEnabled = false
        bindingCamera.btnSaveImage.isEnabled = false

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100
            )
        } else {
            bindingCamera.btnTakePicture.isEnabled = true
            bindingCamera.btnTakeVidio.isEnabled = true
            bindingCamera.btnSaveImage.isEnabled = true
        }

        bindingCamera.btnTakePicture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 101)
        }

        bindingCamera.btnTakeVidio.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, 102)
        }
    }

    private fun getMimeType(uri: Uri?): MediaType? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
        val mimeType = mimeTypeMap.getMimeTypeFromExtension(extension)
        return mimeType?.toMediaTypeOrNull()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                101 -> {
                    val pictureUri: Bitmap? = data?.getParcelableExtra("data")
                    bindingCamera.imgView.setImageBitmap(pictureUri)
                    bindingCamera.imgView.visibility = View.VISIBLE
                    bindingCamera.videoView.visibility = View.GONE
                }
                102 -> {
                    val videoUri: Uri? = data?.data
                    bindingCamera.videoView.setVideoURI(videoUri)
                    bindingCamera.videoView.start()
                    bindingCamera.videoView.visibility = View.VISIBLE
                    bindingCamera.imgView.visibility = View.GONE
                    // Memutar vidio lagi ketika vidio selesai
                    bindingCamera.videoView.setOnCompletionListener {
                        bindingCamera.videoView.seekTo(0)
                        bindingCamera.videoView.start()
                    }
                    // Menambahkan jeda untuk memutar vidio
                    bindingCamera.videoView.setOnClickListener {
                        if (bindingCamera.videoView.isPlaying) {
                            bindingCamera.videoView.pause()
                        } else {
                            bindingCamera.videoView.start()
                        }
                    }
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindingCamera.btnTakePicture.isEnabled = true
            bindingCamera.btnTakeVidio.isEnabled = true
            bindingCamera.btnSaveImage.isEnabled = true
        }
    }
}