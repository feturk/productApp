package com.feyzaeda.productapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.feyzaeda.productapp.R
import com.feyzaeda.productapp.databinding.ItemProductsFavBinding
import com.feyzaeda.productapp.models.ProductsRoomDbFavEntity
import com.feyzaeda.productapp.viewmodels.RoomDbViewModel
import jp.wasabeef.glide.transformations.BlurTransformation

class FavoriteProductsAdapter(
    private var productsList: MutableList<ProductsRoomDbFavEntity>,
    var context: Context,
    private var viewModel: RoomDbViewModel,
    private val onItemClick: (Int) -> Unit
) :
    RecyclerView.Adapter<FavoriteProductsAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: ItemProductsFavBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding: ItemProductsFavBinding

        fun bind(productId: Int) {
            itemView.setOnClickListener {
                onItemClick(productId)
            }
        }

        init {
            this.binding = binding
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        productsList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val tasarim = ItemProductsFavBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(tasarim)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = productsList[position]

        val t = holder.binding
        t.titleTextView.text = product.name
        t.productionYearTextView.text = product.brand

        Glide.with(context)
            .load("${product.image}")
            .override(1024, 768)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
            .into(t.backgroundImageView)

        Glide.with(context)
            .load("${product.image}")
            .override(1024, 768)
            .placeholder(R.drawable.loading)
            .into(t.productImageView)

        t.imageViewEmpty.visibility = View.GONE
        t.imageViewFull.visibility = View.VISIBLE

        holder.bind(product.id!!)
        t.imageViewFull.setOnClickListener {
            viewModel.deleteFavProducts(product)
            Toast.makeText(context, product.name + " Favorilerden Silindi", Toast.LENGTH_SHORT)
                .show()
            removeItem(position)
        }
    }

    override fun getItemCount(): Int {
        return productsList.size
    }
}