package com.mapo.walkaholic.ui.main.dashboard.character.shop

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.R
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailCharacterShopBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import com.mapo.walkaholic.ui.base.BaseSharedFragment
import com.mapo.walkaholic.ui.base.EventObserver
import com.mapo.walkaholic.ui.base.ViewModelFactory
import com.mapo.walkaholic.ui.main.dashboard.character.CharacterItemSlotClickListener
import com.mapo.walkaholic.ui.main.dashboard.character.info.DashboardCharacterInfoViewModel
import com.mapo.walkaholic.ui.snackbar
import kotlinx.android.synthetic.main.fragment_dashboard_character_shop.view.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterShopDetailFragment(
    private val position: Int,
    private val listener : CharacterItemSlotClickListener
) : BaseSharedFragment<DashboardCharacterShopViewModel, FragmentDetailCharacterShopBinding, MainRepository>() {

    val arrayListShopItem = arrayListOf<ItemInfo>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedViewModel : DashboardCharacterShopViewModel by viewModels {
            ViewModelFactory(getFragmentRepository())
        }
        viewModel = sharedViewModel
        binding.viewModel = viewModel
        viewModel.showToastEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopDetailFragment::showToastEvent)
        )

        viewModel.showSnackbarEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopDetailFragment::showSnackbarEvent)
        )
        super.onViewCreated(view, savedInstanceState)
        arrayListShopItem.add(ItemInfo("face", "0", "진한눈썹", "3000"))
        arrayListShopItem.add(ItemInfo("face", "1", "속눈썹펌", "3000"))
        arrayListShopItem.add(ItemInfo("face", "2", "수줍은볼", "3000"))
        arrayListShopItem.add(ItemInfo("face", "3", "발그레볼", "3000"))
        arrayListShopItem.add(ItemInfo("hair", "0", "똑딱이핀", "3000"))
        arrayListShopItem.add(ItemInfo("hair", "1", "나뭇잎컷", "3000"))
        arrayListShopItem.add(ItemInfo("hair", "2", "최준머리", "3000"))
        viewModel.onClickEvent.observe(
            viewLifecycleOwner,
            EventObserver(this@DashboardCharacterShopDetailFragment::onClickEvent)
        )
        binding.dashCharacterInfoDetailRV.also {
            val linearLayoutManager = LinearLayoutManager(requireContext())
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            it.layoutManager = linearLayoutManager
            it.setHasFixedSize(true)
            it.adapter = when(position) {
                0 -> arrayListShopItem.filter { itemInfo -> itemInfo.itemType == "face" } as ArrayList<ItemInfo>
                1 -> arrayListShopItem.filter { itemInfo -> itemInfo.itemType == "hair" } as ArrayList<ItemInfo>
                else -> null
            }?.let { arrayListFilteredItemInfo ->
                DashboardCharacterShopDetailAdapter(
                    arrayListFilteredItemInfo, listener
                )
            }
        }
    }

    private fun onClickEvent(name: String) {
        when (name) {
            "walk_record" -> {
            }
            "select_clear" -> {
                Log.d(ContentValues.TAG,"Click Init Button Event")
                //binding.dashCharacterInfoDetailRV
            }
            else -> {
                null
            }
        }
    }

    private fun showToastEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
            else -> {
                Toast.makeText(
                    requireContext(),
                    contents,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showSnackbarEvent(contents: String) {
        when(contents) {
            null -> { }
            "" -> { }
            else -> {
                requireView().snackbar(contents)
            }
        }
    }

    override fun getViewModel() = DashboardCharacterShopViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailCharacterShopBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository(api, apiWeather, apiSGIS, userPreferences)
    }
}