package com.github.mobdev778.aiadventchallenge.data.profile.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "is_selected")
    val isSelected: Boolean,
)
