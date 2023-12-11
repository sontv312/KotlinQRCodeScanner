package com.example.qrcodescanner

import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodescanner.client.ScanClient
import com.example.qrcodescanner.databinding.ActivityReportBinding
import com.example.qrcodescanner.models.ReportRequest
import com.example.qrcodescanner.models.ReportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity : AppCompatActivity() {


    private lateinit var binding: ActivityReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.submitButton.setOnClickListener {
            sendRequestResponse()
        }
    }

    //call data : post cái report lên server
    private fun sendRequestResponse() {
        val url = binding.urlReport.text.toString()
        if (URLUtil.isNetworkUrl(url)) {
            val reportRequest = ReportRequest(url)
            val client = ScanClient.getClient()
            val call: Call<ReportResponse> =
                client.sendReport(Extensions.generateKey(), reportRequest)
            call.enqueue(object : Callback<ReportResponse> {
                override fun onResponse(
                    call: Call<ReportResponse>,
                    response: Response<ReportResponse>
                ) {
                    //ket qua trả ve
                    if (response.isSuccessful) {
                        Toast.makeText(this@ReportActivity, "Reported success", Toast.LENGTH_LONG)
                            .show()

                        val rp: ReportResponse? = response.body()
                        if (rp != null) {
                            Log.d("__tag", "onResponse: " + rp.code)
                        }
                    } else {
                        Toast.makeText(
                            this@ReportActivity,
                            "Reported failure. Code: " + response.code(),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                    Toast.makeText(this@ReportActivity, "Reported failure", Toast.LENGTH_LONG)
                        .show()
                }
            }
            )
        } else {
            Toast.makeText(
                this@ReportActivity,
                "Network Url is invalid (The Url must have http:// or https:// at the beginning).",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}