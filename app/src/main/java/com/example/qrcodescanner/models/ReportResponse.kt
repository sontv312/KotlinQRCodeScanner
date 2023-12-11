package com.example.qrcodescanner.models

class ReportResponse(var code: String, var message: String) {

    override fun toString(): String {
        return "ReportResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}'
    }
}
