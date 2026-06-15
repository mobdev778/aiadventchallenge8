package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "task_context")
data class TaskContextEntity(
    @PrimaryKey
    val id: UUID,

    @ColumnInfo(name = "task")
    val task: String,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "step")
    val step: Int,

    @ColumnInfo(name = "plan")
    val plan: String,

    @ColumnInfo(name = "done")
    val done: String,

    @ColumnInfo(name = "current")
    val current: String,
)
