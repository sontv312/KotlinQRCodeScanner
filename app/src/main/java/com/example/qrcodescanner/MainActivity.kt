package com.example.qrcodescanner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrcodescanner.Extensions.getBitmapFromUri
import com.example.qrcodescanner.Extensions.scanQRCode
import com.example.qrcodescanner.client.ScanClient
import com.example.qrcodescanner.databinding.ActivityMainBinding
import com.example.qrcodescanner.models.ScanResponse

import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        event()
    }

    //sự kiện bấm vào các button
    private fun event() {
        binding.btnCamera.setOnClickListener {
            checkPermissionCamera(this)
        }
        binding.btnGallery.setOnClickListener {
            checkGalleryPermission()
        }
        binding.btnReport.setOnClickListener {
            startActivity(Intent(this@MainActivity, ReportActivity::class.java))
        }
    }

    private fun resetUI() {
        binding.tvResponse.text = "Response"
        binding.textResult.text = "Result"
        binding.tvResponse.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
    }

    private fun setResult(string: String) {
        binding.textResult.text = string
    }

    //xử lý kết quả trả về từ server
    private fun showResponse(scanResponse: ScanResponse) {
            when (scanResponse.code) {
                "00" -> {
                    // safe
                    binding.tvResponse.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity, R.color.safe
                        )
                    )
                    binding.tvResponse.text = "This link is safe"
                }

                "01" -> {
                    //no data
                    binding.tvResponse.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity, R.color.no_data
                        )
                    )
                    binding.tvResponse.text = "This link may be harmful"
                }

                "13" -> {
                    //waring
                    binding.tvResponse.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity, R.color.warning
                        )
                    )
                    binding.tvResponse.text = "WARNING! This link is malicious"
                }
            }
    }

    //post data lên server
    private fun postScan(value: String) {
        val client = ScanClient.getClient()
        val call: Call<ScanResponse> = client.sendScan(Extensions.generateKey(), value)

        call.enqueue(object : Callback<ScanResponse> {
            override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                if (response.isSuccessful) {
                    val scanResponse: ScanResponse? = response.body()
                    if (scanResponse != null) {
                        showResponse(scanResponse)
                    }
                } else {
                    binding.tvResponse.text = "Check the internet connection"
                }
            }
            override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                binding.tvResponse.text = "onFailure: " + t.message
            }
        })

    }

    private fun showCamera() {
        scannerLauncher.launch(Extensions.initScan())
    }

    //kiểm tra quyền camera
    private fun checkPermissionCamera(context: Context) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show()
        } else requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    //request quyền truy cập camera
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showCamera()
            }
        }

    //kiểm tra quyền truy cập gallery
    private fun checkGalleryPermission() {
        val p1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val p2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (p1 != PackageManager.PERMISSION_GRANTED || p2 != PackageManager.PERMISSION_GRANTED) {
            requestGalleryPermission()
        } else {
            getImageGallery()
        }
    }

    //request quyền truy cập gallery
    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 111
        )
    }

    //sự kiện vào chọn ảnh trong gallery
    private fun getImageGallery() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startForResult.launch(
                intent
            )
        } catch (exp: Exception) {
            Log.i("Error", exp.toString())
        }
    }


    //kq trả về khi scan bằng camera
    private val scannerLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            run {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    setResult(result.contents)
                    postScan(result.contents)
                }
            }
        }

    // ket qua tra ve khi chọn ảnh trong kho ảnh
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.data != null) {
                    val imageUri: Uri? = result.data!!.data
                    val bitmap: Bitmap? = getBitmapFromUri(this, imageUri)
                    if (bitmap != null) {
                        val it = scanQRCode(bitmap)
                        if (it != null) {
                            if (URLUtil.isNetworkUrl(it)) {
                                postScan(it)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Đây không phải đường dẫn",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            setResult(it)
                        } else {
                            resetUI()
                        }
                    } else {
                        resetUI()
                    }
                }
            }
        }
}


