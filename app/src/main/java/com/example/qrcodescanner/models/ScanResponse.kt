package com.example.qrcodescanner.models

/**
 * Data class representing the response received after scanning a URL.
 * @property code The response code indicating the status of the scan.
 * @property message A descriptive message accompanying the response code.
 */
class ScanResponse(val code: String, val message: String)