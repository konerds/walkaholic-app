package com.mapo.walkaholic.model.entity

import android.os.Parcel
import android.os.Parcelable

class PostEntity() : Parcelable {
    var userID: String? = null
    var postNum: Long? = null
    var postTitle: String? = null
    var postBody: String? = null
    var postImage: String? = null
    var postCategory: String? = null

    constructor(parcel: Parcel) : this() {
        parcel.run {
            userID = readString()
            postNum = readLong()
            postTitle = readString()
            postBody = readString()
            postImage = readString()
            postCategory = readString()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.run {
            writeString(this@PostEntity.userID)
            writeLong(this@PostEntity.postNum as Long)
            writeString(this@PostEntity.postTitle)
            writeString(this@PostEntity.postBody)
        }
    }

    companion object CREATOR : Parcelable.Creator<PostEntity> {
        override fun createFromParcel(parcel: Parcel): PostEntity {
            return PostEntity(parcel)
        }

        override fun newArray(size: Int): Array<PostEntity?> {
            return arrayOfNulls(size)
        }
    }
}