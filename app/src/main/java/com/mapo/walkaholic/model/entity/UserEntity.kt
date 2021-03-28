package com.mapo.walkaholic.model.entity

import android.os.Parcel
import android.os.Parcelable

class UserEntity() : Parcelable {
    var userID: String? = null
    var userName: String? = null
    var userNick: String? = null
    var userPhone: String? = null
    var userBirth: String? = null
    var userGender: String? = null
    var userHeight: String? = null
    var userWeight: String? = null

    constructor(parcel: Parcel) : this() {
        parcel.run {
            userID = readString()
            userName = readString()
            userNick = readString()
            userPhone = readString()
            userBirth = readString()
            userGender = readString()
            userHeight = readString()
            userWeight = readString()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.run {
            writeString(this@UserEntity.userID)
            writeString(this@UserEntity.userName)
            writeString(this@UserEntity.userNick)
            writeString(this@UserEntity.userPhone)
            writeString(this@UserEntity.userBirth)
            writeString(this@UserEntity.userGender)
            writeString(this@UserEntity.userHeight)
            writeString(this@UserEntity.userWeight)
        }
    }

    companion object CREATOR : Parcelable.Creator<UserEntity> {
        override fun createFromParcel(parcel: Parcel): UserEntity {
            return UserEntity(parcel)
        }

        override fun newArray(size: Int): Array<UserEntity?> {
            return arrayOfNulls(size)
        }
    }
}