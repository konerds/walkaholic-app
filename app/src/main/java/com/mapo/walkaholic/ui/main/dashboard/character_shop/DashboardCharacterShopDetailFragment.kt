package com.mapo.walkaholic.ui.main.dashboard.character_shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapo.walkaholic.data.model.ItemInfo
import com.mapo.walkaholic.data.network.ApisApi
import com.mapo.walkaholic.data.network.InnerApi
import com.mapo.walkaholic.data.network.SgisApi
import com.mapo.walkaholic.data.repository.MainRepository
import com.mapo.walkaholic.databinding.FragmentDetailCharacterShopBinding
import com.mapo.walkaholic.ui.base.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DashboardCharacterShopDetailFragment(
    private val position: Int
) : BaseFragment<DashboardCharacterShopDetailViewModel, FragmentDetailCharacterShopBinding, MainRepository>() {

    val arrayListShopItem = arrayListOf<ItemInfo>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arrayListShopItem.add(ItemInfo("face", "0", "진한눈썹", "3000"))
        arrayListShopItem.add(ItemInfo("face", "1", "속눈썹펌", "3000"))
        arrayListShopItem.add(ItemInfo("face", "2", "수줍은볼", "3000"))
        arrayListShopItem.add(ItemInfo("face", "3", "발그레볼", "3000"))
        arrayListShopItem.add(ItemInfo("hair", "0", "똑딱이핀", "3000"))
        arrayListShopItem.add(ItemInfo("hair", "1", "나뭇잎컷", "3000"))
        arrayListShopItem.add(ItemInfo("hair", "2", "최준머리", "3000"))
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
                    arrayListFilteredItemInfo
                )
            }
        }
    }

    override fun getViewModel() = DashboardCharacterShopDetailViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentDetailCharacterShopBinding.inflate(inflater, container, false)

    override fun getFragmentRepository(): MainRepository {
        val jwtToken = runBlocking { userPreferences.jwtToken.first() }
        val api = remoteDataSource.buildRetrofitInnerApi(InnerApi::class.java, jwtToken)
        val apiWeather = remoteDataSource.buildRetrofitApiWeatherAPI(ApisApi::class.java)
        val apiSGIS = remoteDataSource.buildRetrofitApiSGISAPI(SgisApi::class.java)
        return MainRepository.getInstance(api, apiWeather, apiSGIS, userPreferences)
    }
}