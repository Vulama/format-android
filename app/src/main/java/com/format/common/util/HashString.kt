package com.format.common.util

import com.format.BuildConfig
import java.security.MessageDigest

fun hashString(input: String): String {
    val salt = BuildConfig.SALT
    // Convert salt string to byte array
    val saltBytes = salt.toByteArray()

    // Create MessageDigest instance for SHA-256
    val digest = MessageDigest.getInstance("SHA-256")

    // Add salt bytes to the beginning of the input bytes
    val inputWithSalt = saltBytes + input.toByteArray()

    // Perform the hash computation
    val hashBytes = digest.digest(inputWithSalt)

    // Convert the byte array to a hexadecimal string
    val hexString = StringBuilder()
    for (byte in hashBytes) {
        val hex = Integer.toHexString(0xff and byte.toInt())
        if (hex.length == 1) hexString.append('0')
        hexString.append(hex)
    }

    return hexString.toString()
}