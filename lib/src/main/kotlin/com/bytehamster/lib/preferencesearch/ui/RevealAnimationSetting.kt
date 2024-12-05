package com.bytehamster.lib.preferencesearch.ui

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class RevealAnimationSetting : Parcelable {
    var centerX: Int
        private set
    var centerY: Int
        private set
    var width: Int
        private set
    var height: Int
        private set
    var colorAccent: Int
        private set

    constructor(centerX: Int, centerY: Int, width: Int, height: Int, colorAccent: Int) {
        this.centerX = centerX
        this.centerY = centerY
        this.width = width
        this.height = height
        this.colorAccent = colorAccent
    }

    private constructor(`in`: Parcel) {
        centerX = `in`.readInt()
        centerY = `in`.readInt()
        width = `in`.readInt()
        height = `in`.readInt()
        colorAccent = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(
        dest: Parcel,
        flags: Int,
    ) {
        dest.writeInt(centerX)
        dest.writeInt(centerY)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeInt(colorAccent)
    }

    companion object {
        @JvmField
        val CREATOR: Creator<RevealAnimationSetting> =
            object : Creator<RevealAnimationSetting> {
                override fun createFromParcel(`in`: Parcel): RevealAnimationSetting {
                    return RevealAnimationSetting(`in`)
                }

                override fun newArray(size: Int): Array<RevealAnimationSetting?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
