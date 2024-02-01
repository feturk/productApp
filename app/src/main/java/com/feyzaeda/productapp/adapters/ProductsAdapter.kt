package com.feyzaeda.productapp.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.feyzaeda.productapp.R
import com.feyzaeda.productapp.databinding.ItemProductsBinding
import com.feyzaeda.productapp.models.ListFragmentCommunicator
import com.feyzaeda.productapp.models.Products
import com.feyzaeda.productapp.viewmodels.RoomDbViewModel
import jp.wasabeef.glide.transformations.BlurTransformation

class ProductsAdapter(
    private var productsList: List<Products>, var context: Context,
    private val communicator: ListFragmentCommunicator,
    private val viewModel: RoomDbViewModel
) :
    RecyclerView.Adapter<ProductsAdapter.MyViewHolder>() {

    inner class MyViewHolder(binding: ItemProductsBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding: ItemProductsBinding

        init {
            this.binding = binding
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: List<Products>) {
        productsList = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = ItemProductsBinding.inflate(layoutInflater, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = productsList[position]
        val t = holder.binding
        val totalPrice = context.getString(R.string.price_dynamic, product.price.toString())
        t.txtPrice.text = totalPrice
        t.txtName.text = product.name
        t.txtBrand.text = product.brand

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

        holder.itemView.setOnClickListener {
            communicator.goToDetails(product.id!!.toInt())
        }

        val favList = viewModel.favProductsList
        favList.value!!.forEach {
            if (it.id!! == product.id?.toInt()) {
                t.imageViewFull.visibility = View.VISIBLE
                t.imageViewEmpty.visibility = View.GONE
            }
        }

        val addToCardList = viewModel.orderProductsList
        addToCardList.value!!.forEach {
            if (it.id!! == product.id?.toInt()) {
                t.btnRemoveToCard.visibility = View.VISIBLE
                t.btnAddToCard.visibility = View.GONE
            }
        }

        setOnClickListener(t.imageViewEmpty, t.imageViewEmpty, t.imageViewFull) {
            communicator.addToFavorites(product)
        }
        setOnClickListener(t.imageViewFull, t.imageViewFull, t.imageViewEmpty) {
            communicator.deleteToFavorites(product)
        }
        setOnClickListener(t.btnAddToCard, t.btnAddToCard, t.btnRemoveToCard) {
            communicator.addToOrders(product)
        }
        setOnClickListener(t.btnRemoveToCard, t.btnRemoveToCard, t.btnAddToCard) {
            communicator.deleteToOrders(product)
        }

    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    private fun setOnClickListener(
        clickView: View,
        goneView: View,
        visibleView: View,
        communicator: () -> Unit
    ) {
        clickView.setOnClickListener {
            communicator.invoke()
            goneView.visibility = View.GONE
            visibleView.visibility = View.VISIBLE
        }
    }
}