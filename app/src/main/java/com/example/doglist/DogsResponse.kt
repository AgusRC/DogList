package com.example.doglist

import com.google.gson.annotations.SerializedName

data class DogsResponse(    //status y message se llaman igual que en la respuesta JSON
    @SerializedName("status") var status: String,
    @SerializedName("message") var images: List<String>
    //SerializedName hace que reciba de message y despues lo guarde en image, para no usar de a huevo "message"
)