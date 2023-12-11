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

/**
 * Activity responsible for handling the reporting functionality.
 * Users can input a URL, and this activity sends a report request to the server.
 */
class ReportActivity : AppCompatActivity() {

    // View binding for the activity
    private lateinit var binding: ActivityReportBinding

    /**
     * Called when the activity is first created.
     * Sets up the UI and attaches a click listener to the submit button.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize view binding
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up a click listener for the submit button to trigger the report request
        binding.submitButton.setOnClickListener {
            sendRequestResponse()
        }
    }

    /**
     * Sends a report request to the server using the provided URL.
     * Handles success and failure scenarios, displaying toasts accordingly.
     */
    private fun sendRequestResponse() {
        // Extract the URL from the text input in the binding.
        val url = binding.urlReport.text.toString()
        // Validate if the URL is a network URL.
        if (URLUtil.isNetworkUrl(url)) {
            // Create a ReportRequest object with the URL.
            val reportRequest = ReportRequest(url)
            // Retrieve a ScanClient instance.
            val client = ScanClient.getClient()
            // Send the report request using Retrofit
            val call: Call<ReportResponse> =
                client.sendReport(Extensions.generateKey(), reportRequest)
            call.enqueue(object : Callback<ReportResponse> {
                // Handle the onResponse callback.
                override fun onResponse(
                    call: Call<ReportResponse>,
                    response: Response<ReportResponse>
                ) {
                    // Display a success toast.
                    if (response.isSuccessful) {
                        Toast.makeText(this@ReportActivity, "Reported success", Toast.LENGTH_LONG)
                            .show()

                        // Log the response code in case of a successful response.
                        val rp: ReportResponse? = response.body()
                        if (rp != null) {
                            Log.d("__tag", "onResponse: " + rp.code)
                        }
                    } else {
                        // Display a failure toast with the response code.
                        Toast.makeText(
                            this@ReportActivity,
                            "Reported failure. Code: " + response.code(),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }

                // Handle the onFailure callback.
                override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                    // Display a failure toast.
                    Toast.makeText(this@ReportActivity, "Reported failure", Toast.LENGTH_LONG)
                        .show()
                }
            }
            )
        } else {
            // Display a toast for an invalid network URL.
            Toast.makeText(
                this@ReportActivity,
                "Network Url is invalid (The Url must have http:// or https:// at the beginning).",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}