package com.example.qrcodescanner.models

/**
 * Data class representing the response received after reporting a URL to the server.
 * @property code The response code indicating the status of the report.
 * @property message A descriptive message accompanying the response code.
 */
class ReportResponse(var code: String, var message: String) {

    /**
     * Converts the object to its string representation.
     * @return A string representation of the object.
     */
    override fun toString(): String {
        return "ReportResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}'
    }
}
