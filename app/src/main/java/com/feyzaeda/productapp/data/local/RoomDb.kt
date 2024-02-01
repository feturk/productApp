package com.feyzaeda.productapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.feyzaeda.productapp.models.ProductsRoomDbFavEntity
import com.feyzaeda.productapp.models.ProductsRoomDbOrderEntity

@Database(entities = [ProductsRoomDbFavEntity::class, ProductsRoomDbOrderEntity::class], version = 1)
abstract class RoomDb : RoomDatabase() {

    abstract fun productsFavDao(): RoomDBFavDao

    abstract fun productsOrderDao(): RoomDbOrderDao
}