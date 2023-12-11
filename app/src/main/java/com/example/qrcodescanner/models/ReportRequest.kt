package com.example.qrcodescanner.models

/**
 * Data class representing a report request to be sent to the server.
 * @property urlCheck The URL to be checked and reported.
 */
class ReportRequest(var urlCheck: String) {

    override fun toString(): String {
        return "ReportRequest{" +
                "urlCheck='" + urlCheck + '\'' +
                '}'
    }
}
