package ru.app.services

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import java.util.HashMap

class HTTPService {
    companion object {
        fun postRequest(url: String, fields: HashMap<String?, Any?>, token: String): HttpResponse<JsonNode?>? {
            return Unirest.post(url)
                .header("token", token)
                .fields(fields)
                .asJson()
        }
    }
}