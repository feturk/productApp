package com.feyzaeda.productapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favori")
data class ProductsRoomDbFavEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    var createdAt: String?,
    var name: String?,
    var image: String?,
    var price: String?,
    var description: String?,
    var model: String?,
    var brand: String?,
)