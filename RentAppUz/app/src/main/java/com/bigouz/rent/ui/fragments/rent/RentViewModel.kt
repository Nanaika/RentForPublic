package com.bigouz.rent.ui.fragments.rent

import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigouz.rent.domain.models.Filter
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.domain.models.RentType
import com.bigouz.rent.domain.models.User
import com.bigouz.rent.network.firebase.RentRepository
import com.bigouz.rent.utils.FileUtils
import com.bigouz.rent.utils.Resource
import com.google.firebase.storage.ktx.storageMetadata
import id.zelory.compressor.Compressor
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class RentViewModel(
    private val repository: RentRepository = RentRepository(),
) : ViewModel() {

    var isFilterClicked = false

    var listImageUri = mutableListOf<Uri>()

    var location: Location = Location("")
    var city: String? = null
    var region: String? = null

    var filter: Filter? = null

    var propertyType = MutableLiveData(0)
    var rentType = MutableLiveData(0)

    var destFrom = MutableLiveData(0)

    var sellRents = MutableLiveData<Resource<List<Rent>>>()
    var listSell = mutableListOf<Rent>()
    var rentRents = MutableLiveData<Resource<List<Rent>>>()
    var listRent = mutableListOf<Rent>()
    var roomRents = MutableLiveData<Resource<List<Rent>>>()
    var listRoom = mutableListOf<Rent>()

    var myRents = MutableLiveData<List<Rent>>()

    init {
        getRents {
        }
    }


    fun getRents(func: (size: Int) -> Unit) {
        viewModelScope.launch {
            val size = safeCall()
            func.invoke(size)
        }
    }

    private suspend fun safeCall(): Int {
        sellRents.postValue(Resource.Loading())
        return when (RentType.valueOf(rentType.value!!)) {
            RentType.SELL -> {
                return if (listSell.isEmpty()) {
                    val res =
                        Resource.Success(repository.getRents(RentType.valueOf(rentType.value!!)!!))
                    listSell.addAll(res.data as List<Rent>)
                    sellRents.postValue(res)
                    res.data.size
                } else {
                    val res =
                        Resource.Success(repository.getNextRents(RentType.valueOf(rentType.value!!)!!))
                    listSell.addAll(res.data as List<Rent>)
                    sellRents.postValue(Resource.Success(listSell))
                    res.data.size
                }
            }
            RentType.RENT -> {
                return if (listRent.isEmpty()) {
                    val res =
                        Resource.Success(repository.getRents(RentType.valueOf(rentType.value!!)!!))
                    listRent.addAll(res.data as List<Rent>)
                    rentRents.postValue(res)
                    res.data.size
                } else {
                    val res =
                        Resource.Success(repository.getNextRents(RentType.valueOf(rentType.value!!)!!))
                    listRent.addAll(res.data as List<Rent>)
                    rentRents.postValue(Resource.Success(listRent))
                    res.data.size
                }
            }
            RentType.RENT_ROOM -> {
                return if (listRoom.isEmpty()) {
                    val res =
                        Resource.Success(repository.getRents(RentType.valueOf(rentType.value!!)!!))
                    listRoom.addAll(res.data as List<Rent>)
                    roomRents.postValue(res)
                    res.data.size
                } else {
                    val res =
                        Resource.Success(repository.getNextRents(RentType.valueOf(rentType.value!!)!!))
                    listRoom.addAll(res.data as List<Rent>)
                    roomRents.postValue(Resource.Success(listRoom))
                    res.data.size
                }
            }
            else -> {
                return 0
            }
        }
    }

    fun addRent(rent: Rent, context: Context, successFun: () -> Unit) {
        viewModelScope.launch {
            rent.rentType?.let { rentType ->
                repository.addRent(rentType, rent) { id ->
                    compressImages(listImageUri, context) { fileList ->
                        repository.uploadImages(fileList, id, rentType)
                    }
                }
            }
            successFun.invoke()
        }
    }

    fun getUser(uid: String, successFun: (User) -> Unit) {
        viewModelScope.launch {
            repository.getUser(uid) {
                successFun.invoke(it)
            }
        }
    }

    fun getMyRents(uid: String) {
        viewModelScope.launch {
            myRents.postValue(repository.getMyRents(uid))
        }
    }

    fun deleteRent(type: RentType, id: String, successFun: () -> Unit) {
        repository.deleteRent(type, id) {
            successFun.invoke()
        }
    }

    private fun compressImages(
        listUri: MutableList<Uri>,
        context: Context,
        success: (compressedImages: MutableList<File>) -> Unit
    ) {
        viewModelScope.launch {
            val compressedImages = mutableListOf<File>()
            for (i in listUri) {
                val imageFile = FileUtils().from(context, i)
                val compressedImage = Compressor.compress(context, imageFile!!)
                compressedImages.add(compressedImage)

            }
            success.invoke(compressedImages)
        }
    }
}