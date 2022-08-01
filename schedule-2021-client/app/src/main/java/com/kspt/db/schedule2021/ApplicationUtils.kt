package com.kspt.db.schedule2021

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import okhttp3.*
import java.text.DateFormat
import java.util.concurrent.TimeUnit


val httpClient = OkHttpClient()
    .newBuilder()
    .connectTimeout(1, TimeUnit.HOURS)
    .readTimeout(1, TimeUnit.HOURS)
    .build()

val gson: Gson = GsonBuilder()
    .serializeNulls()
    .setDateFormat(DateFormat.SHORT, DateFormat.SHORT)
    .create()

fun getApiResponse(url: String, isHeaderSet: Boolean = false, token: String): String? {
    val request = Request.Builder().url(url).apply {
        if (isHeaderSet) {
            addHeader("Authorization", "Bearer $token")
        }
    }.build()

    val response: Response

    try {
        response = httpClient.newCall(request).execute()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return response.body?.string()
}

fun loginUser(username: String, password: String): String? {
    val byte = byteArrayOf()
    val body: RequestBody = RequestBody.create(null, byte)
    val url = "http://10.0.2.2:8832/login?name=$username&password=$password"
    val request = Request.Builder().method("POST", body).url(url).build()
    val response: Response
    try {
        response = httpClient.newCall(request).execute()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return response.body?.string()
}

fun registerUser(username: String, password: String, groupName: String, role: String): String? {
    val byte = byteArrayOf()
    val body: RequestBody = RequestBody.create(null, byte)
    val url = "http://10.0.2.2:8832/register?name=$username&password=$password&groupName=$groupName&role=$role"
    val request = Request.Builder().method("POST", body).url(url).build()
    val response: Response

    try {
        response = httpClient.newCall(request).execute()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return response.body?.string()
}

fun extractAllClaims(token: String): Claims {
    return Jwts
        .parser()
        .setSigningKey("abcd")
        .parseClaimsJws(token)
        .getBody()
}

fun postDeadline(subject: String, title: String, date: String, time: String, description: String, studentId: Integer): String? {
    val byte = byteArrayOf()
    val body: RequestBody = RequestBody.create(null, byte)
    val url =
        "http://10.0.2.2:8832/deadlines/create?title=$title&description=$description&notificationTime=$time&deadlineDate=$date&subjectName=$subject&studentId=$studentId"
    val request = Request.Builder().method("POST", body).url(url).build()
    val response: Response
    try {
        response = httpClient.newCall(request).execute()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return response.body?.string()
}

fun deleteDeadline(deadlineId: Long, token: String): String? {
    val byte = byteArrayOf()
    val body: RequestBody = RequestBody.create(null, byte)
    val url = "http://10.0.2.2:8832/deadlines/delete?deadlineId=$deadlineId"
    val request = Request.Builder().method("POST", body).addHeader("Authorization", "Bearer $token").url(url).build()
    val response: Response
    try {
        response = httpClient.newCall(request).execute()
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return response.body?.string()
}

