package com.apero.minhnt1.database.user

import android.graphics.Bitmap
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE username = :username AND password = :password")
    suspend fun getSpecificUser(username: String, password: String) : List<User>

    @Insert
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("UPDATE user SET name = :name WHERE username = :username")
    suspend fun updateName(username: String, name: String)

    @Query("UPDATE user SET phone_number = :phoneNumber WHERE username = :username")
    suspend fun updatePhoneNumber(username: String, phoneNumber: String)

    @Query("UPDATE user SET university_name = :universityName WHERE username = :username")
    suspend fun updateUniversityName(username: String, universityName: String)

    @Query("UPDATE user SET self_description = :selfDescription WHERE username = :username")
    suspend fun updateSelfDescription(username: String, selfDescription: String)

    @Query("UPDATE user SET profile_picture = :profilePicture WHERE username = :username")
    suspend fun updateSelfDescription(username: String, profilePicture: ByteArray?)

//    @Transaction
//    @Query("SELECT * FROM User")
//    fun getUsersWithPlaylists(): List<UserWithPlaylists>
}