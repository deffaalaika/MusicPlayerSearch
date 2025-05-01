package com.deffa.searchmodule

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Track(
    @Json(name = "trackId") val id: Long,
    @Json(name = "trackName") val title: String,
    @Json(name = "artistName") val artist: String,
    @Json(name = "previewUrl") val previewUrl: String?,
    @Json(name = "artworkUrl100") val artworkUrl: String?
) : Parcelable {
    private constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        title = parcel.readString().orEmpty(),
        artist = parcel.readString().orEmpty(),
        previewUrl = parcel.readString(),
        artworkUrl = parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(title)
        dest.writeString(artist)
        dest.writeString(previewUrl)
        dest.writeString(artworkUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track = Track(parcel)
        override fun newArray(size: Int): Array<Track?> = arrayOfNulls(size)
    }
}
