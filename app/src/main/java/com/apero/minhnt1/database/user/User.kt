package com.apero.minhnt1.database.user

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.apero.minhnt1.database.playlist.Playlist

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id")val userID: Int = 0,
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "password") var password: String = "",
    @ColumnInfo(name = "email") var email: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "phone_number") var phoneNumber: String = "",
    @ColumnInfo(name = "university_name") var universityName: String = "",
    @ColumnInfo(name = "self_description") var selfDescription: String = "",
    @ColumnInfo(name = "profile_picture") var profilePicture: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (userID != other.userID) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (email != other.email) return false
        if (name != other.name) return false
        if (phoneNumber != other.phoneNumber) return false
        if (universityName != other.universityName) return false
        if (selfDescription != other.selfDescription) return false
        if (!profilePicture.contentEquals(other.profilePicture)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userID
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + universityName.hashCode()
        result = 31 * result + selfDescription.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        return result
    }
}

data class UserWithPlaylists(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "creator_id"
    )
    val playlists: List<Playlist>
)