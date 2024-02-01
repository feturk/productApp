package com.feyzaeda.productapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.feyzaeda.productapp.R
import com.feyzaeda.productapp.databinding.FragmentProductsDetailBinding
import com.feyzaeda.productapp.models.Products
import com.feyzaeda.productapp.models.ProductsRoomDbFavEntity
import com.feyzaeda.productapp.models.ProductsRoomDbOrderEntity
import com.feyzaeda.productapp.viewmodels.RoomDbViewModel
import com.feyzaeda.productapp.viewmodels.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.BlurTransformation

@AndroidEntryPoint
class ProductDetailFragment : Fragment() {
    private lateinit var productId: String
    private lateinit var viewModel: ProductsViewModel
    private lateinit var viewModelDB: RoomDbViewModel
    private var _binding: FragmentProductsDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempViewModel: ProductsViewModel by viewModels()
        val tempViewModel2: RoomDbViewModel by viewModels()
        viewModel = tempViewModel
        viewModelDB = tempViewModel2
        productId = ProductDetailFragmentArgs.fromBundle(requireArguments()).productId.toString()
        viewModelDB.loadFavProducts()
        viewModel.productsListUpdate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelDB.loadFavProducts()
        viewModel.productsListUpdate()
        binding.imgBtnFavorite.visibility = View.VISIBLE
        binding.imgBtnFavoriteFull.visibility = View.GONE
        binding.btnAddToCard.visibility = View.VISIBLE
        binding.btnRemoveToCard.visibility = View.GONE

        viewModel.productsList.observe(viewLifecycleOwner) { listProduct ->
            listProduct.forEach { products ->
                if (products.id?.toInt() == productId.toInt()) {
                    Glide.with(requireContext())
                        .load("${products.image}")
                        .override(1024, 768)
                        .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 5)))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(binding.imgBackground.drawable) //using previous image to prevent image flickering when switching images.
                        .into(binding.imgBackground)

                    Glide.with(requireContext())
                        .load("${products.image}")
                        .override(1024, 768)
                        .placeholder(R.drawable.loading)
                        .into(binding.imgProduct)

                    binding.tvTitle.text = products.name
                    binding.tvDirector.text = products.brand
                    binding.tvProductionYear.text = products.createdAt
                    binding.tvDescription.text = products.description
                    val totalPrice = getString(R.string.price_dynamic, products.price.toString())
                    binding.tvPrice.text = totalPrice

                    binding.tvTitle.setOnClickListener {
                        findNavController().navigate(
                            ProductDetailFragmentDirections.actionProductDetailFragmentToWebViewFragment(
                                products.brand!!
                            )
                        )
                    }
                    val roomDbFavData = viewModelDB.favProductsList
                    roomDbFavData.value!!.forEach {
                        if (products.id!!.toInt() == it.id) {
                            binding.imgBtnFavorite.visibility = View.GONE
                            binding.imgBtnFavoriteFull.visibility = View.VISIBLE
                        }
                    }
                    val roomDbOrderData = viewModelDB.orderProductsList
                    roomDbOrderData.value!!.forEach {
                        if (products.id!!.toInt() == it.id) {
                            binding.btnAddToCard.visibility = View.GONE
                            binding.btnRemoveToCard.visibility = View.VISIBLE
                        }
                    }

                    binding.btnAddToCard.setOnClickListener {
                        viewModelDB.insertOrderProducts(
                            addToOrder(products, binding.btnAddToCard, binding.btnRemoveToCard)!!
                        )
                    }

                    binding.btnRemoveToCard.setOnClickListener {
                        viewModelDB.deleteOrderProducts(
                            deleteToOrder(products, binding.btnAddToCard, binding.btnRemoveToCard)!!
                        )
                    }

                    binding.imgBtnFavorite.setOnClickListener {
                        viewModelDB.insertFavProducts(
                            addToFavorites(
                                products,
                                binding.imgBtnFavorite,
                                binding.imgBtnFavoriteFull
                            )!!
                        )
                    }

                    binding.imgBtnFavoriteFull.setOnClickListener {
                        viewModelDB.deleteFavProducts(
                            deleteToFavorites(
                                products,
                                binding.imgBtnFavorite,
                                binding.imgBtnFavoriteFull
                            )!!
                        )
                    }
                }
            }
        }
        binding.tbProductDetail.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imgShopping.setOnClickListener {
            findNavController().navigate(ProductDetailFragmentDirections.actionProductDetailFragmentToOrderBasketFragment())
        }
    }

    private fun addToFavorites(
        products: Products,
        imgBtnFav: View,
        imgBtnFavFull: View
    ): ProductsRoomDbFavEntity? {
        imgBtnFav.visibility = View.GONE
        imgBtnFavFull.visibility = View.VISIBLE
        return roomDbFavEntityData(products, getString(R.string.added))
    }

    private fun deleteToFavorites(
        products: Products,
        imgBtnFav: View,
        imgBtnFavFull: View
    ): ProductsRoomDbFavEntity? {
        imgBtnFav.visibility = View.VISIBLE
        imgBtnFavFull.visibility = View.GONE
        return roomDbFavEntityData(products, getString(R.string.deleted))
    }

    private fun roomDbFavEntityData(products: Products, text: String): ProductsRoomDbFavEntity? {
        try {
            val data = ProductsRoomDbFavEntity(
                products.id?.toInt(),
                products.name,
                products.createdAt,
                products.image,
                products.price,
                products.description,
                products.model,
                products.brand
            )
            Toast.makeText(
                requireContext(),
                products.name + " " + text,
                Toast.LENGTH_LONG
            )
                .show()
            return data
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.error) + e, Toast.LENGTH_SHORT)
                .show()
        }
        return null
    }

    private fun deleteToOrder(
        products: Products,
        btnAddCard: View,
        btnRemoveCard: View
    ): ProductsRoomDbOrderEntity? {
        btnAddCard.visibility = View.VISIBLE
        btnRemoveCard.visibility = View.GONE
        return roomDbOrderEntityData(products, getString(R.string.deleted_basket))
    }

    private fun addToOrder(
        products: Products,
        btnAddCard: View,
        btnRemoveCard: View
    ): ProductsRoomDbOrderEntity? {
        btnAddCard.visibility = View.GONE
        btnRemoveCard.visibility = View.VISIBLE
        return roomDbOrderEntityData(products, getString(R.string.added_basket))
    }

    private fun roomDbOrderEntityData(
        products: Products,
        text: String
    ): ProductsRoomDbOrderEntity? {
        try {
            val data = ProductsRoomDbOrderEntity(
                products.id?.toInt(),
                products.createdAt,
                products.name,
                products.image,
                products.price,
                products.description,
                products.model,
                products.brand
            )
            Toast.makeText(
                requireContext(),
                products.name + " " + text,
                Toast.LENGTH_LONG
            )
                .show()
            return data
        } catch (e: Exception) {
            Toast.makeText(requireContext(), getString(R.string.error) + e, Toast.LENGTH_SHORT)
                .show()
        }
        return null
    }

    override fun onResume() {
        super.onResume()
        viewModelDB.loadFavProducts()
        viewModelDB.loadOrderProducts()
        viewModel.productsListUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
