package com.example.qrcodescanner.client;

import com.example.qrcodescanner.models.ReportRequest;
import com.example.qrcodescanner.models.ReportResponse;
import com.example.qrcodescanner.models.ScanResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface ScanApi {
    @POST("api/urlScan")
    Call<ScanResponse> sendScan(
            @Header("Authorization") String header,
            @Body String urlScan);

    @POST("api/urlRepost")
    Call<ReportResponse> sendReport(@Header("Authorization") String header,
                                    @Body ReportRequest reportRequest);
}
