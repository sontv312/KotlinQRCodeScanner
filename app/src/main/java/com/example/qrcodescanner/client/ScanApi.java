package com.example.qrcodescanner.client;

import com.example.qrcodescanner.models.ReportRequest;
import com.example.qrcodescanner.models.ReportResponse;
import com.example.qrcodescanner.models.ScanResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Retrofit interface defining API endpoints for scanning and reporting URLs.
 */
public interface ScanApi {
    /**
     * Sends a scan request to the server.
     * @param header The authorization header.
     * @param urlScan The URL to be scanned.
     * @return A [Call] containing the response [ScanResponse].
     */
    @POST("api/urlScan")
    Call<ScanResponse> sendScan(
            @Header("Authorization") String header,
            @Body String urlScan);

    /**
     * Sends a report request to the server.
     * @param header The authorization header.
     * @param reportRequest The [ReportRequest] object containing the URL to be reported.
     * @return A [Call] containing the response [ReportResponse].
     */
    @POST("api/urlRepost")
    Call<ReportResponse> sendReport(@Header("Authorization") String header,
                                    @Body ReportRequest reportRequest);
}
