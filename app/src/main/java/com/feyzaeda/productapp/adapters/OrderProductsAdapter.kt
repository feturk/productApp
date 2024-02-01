package com.feyzaeda.productapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feyzaeda.productapp.R
import com.feyzaeda.productapp.databinding.ItemProductsBasketBinding
import com.feyzaeda.productapp.models.ProductsRoomDbOrderEntity
import com.feyzaeda.productapp.viewmodels.RoomDbViewModel

class OrderProductsAdapter(
    private var orderProductsList: MutableList<ProductsRoomDbOrderEntity>,
    var context: Context,
    private var viewModel: RoomDbViewModel
) : RecyclerView.Adapter<OrderProductsAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: ItemProductsBasketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var binding: ItemProductsBasketBinding

        init {
            this.binding = binding
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(position: Int) {
        orderProductsList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = ItemProductsBasketBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val products = orderProductsList[position]
        val t = holder.binding
        val totalPrice = context.getString(R.string.price_dynamic, products.price)
        t.titleTextView.text = products.name
        t.priceTextview.text = totalPrice
        t.txtPiece.text = products.productsPiece.toString()
        viewModel.updateTotalValues()

        Glide.with(context)
            .load("${products.image}")
            .override(1024, 768)
            .placeholder(R.drawable.loading)
            .into(t.productImageView)

        t.btnDecrease.setOnClickListener {
            if (products.productsPiece == 1) {
                viewModel.deleteOrderProducts(products)
                Toast.makeText(context, products.name + context.getString(R.string.deleted_basket), Toast.LENGTH_SHORT)
                    .show()
                removeItem(position)
                viewModel.updateTotalValues()
            } else {
                products.productsPiece--
                t.txtPiece.text = products.productsPiece.toString()
                viewModel.updateTotalValues()
                viewModel.updateOrderProducts(products)
            }
        }
        t.btnIncrease.setOnClickListener {
            products.productsPiece++
            viewModel.updateTotalValues()
            t.txtPiece.text = products.productsPiece.toString()
            viewModel.updateOrderProducts(products)
        }
    }

    override fun getItemCount(): Int {
        return orderProductsList.size
    }
}