package com.bigouz.rent.network.firebase

import androidx.core.net.toUri
import com.bigouz.rent.domain.interfaces.Repository
import com.bigouz.rent.domain.models.CollectionTypes
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.domain.models.RentType
import com.bigouz.rent.domain.models.User
import com.bigouz.rent.utils.LIMIT
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.File

private const val DATE = "date"

class RentRepository(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val storage: StorageReference = Firebase.storage.reference,
    private val limit: Long = LIMIT.toLong()
) : Repository {

    private var lastDocs = arrayOfNulls<DocumentSnapshot>(RentType.values().size)

    suspend fun getRents(rentType: RentType): List<Rent> {


        val collRef = db.collection(rentType.toString())
        collRef.addSnapshotListener { value, error -> }

        return db.collection(rentType.toString())
            .orderBy(DATE, Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnCompleteListener {
                when (rentType) {
                    RentType.SELL -> {
                        lastDocs[RentType.SELL.value] = it.result.documents.last()
                    }
                    RentType.RENT -> {
                        lastDocs[RentType.RENT.value] = it.result.documents.last()
                    }
                    RentType.RENT_ROOM -> {
                        lastDocs[RentType.RENT_ROOM.value] = it.result.documents.last()
                    }
                }
            }
            .await().toObjects(Rent::class.java)
    }

    suspend fun getNextRents(rentType: RentType): List<Rent> {
        val lastDoc: DocumentSnapshot? = when (rentType) {
            RentType.SELL -> lastDocs[RentType.SELL.value]
            RentType.RENT -> lastDocs[RentType.RENT.value]
            RentType.RENT_ROOM -> lastDocs[RentType.RENT_ROOM.value]
        }
        lastDoc!!.data
        return db.collection(rentType.toString())
            .orderBy(DATE, Query.Direction.DESCENDING)
            .startAfter(lastDoc)
            .limit(limit)
            .get()
            .addOnCompleteListener {
                if (it.result.documents.isEmpty()) return@addOnCompleteListener
                when (rentType) {
                    RentType.SELL -> {
                        lastDocs[RentType.SELL.value] = it.result.documents.last()
                    }
                    RentType.RENT -> {
                        lastDocs[RentType.RENT.value] = it.result.documents.last()
                    }
                    RentType.RENT_ROOM -> {
                        lastDocs[RentType.RENT_ROOM.value] = it.result.documents.last()
                    }
                }
            }
            .await().toObjects(Rent::class.java)
    }

    suspend fun getMyRents(uid: String): List<Rent> {
        val rents = mutableListOf<Rent>()
        rents.addAll(
            db.collection(CollectionTypes.SELL.toString())
                .whereEqualTo("ownerUid", uid)
                .get().await().toObjects(Rent::class.java)
        )
        rents.addAll(
            db.collection(CollectionTypes.RENT.toString())
                .whereEqualTo("ownerUid", uid)
                .get().await().toObjects(Rent::class.java)
        )
        rents.addAll(
            db.collection(CollectionTypes.RENT_ROOM.toString())
                .whereEqualTo("ownerUid", uid)
                .get().await().toObjects(Rent::class.java)
        )
        return rents
    }


    fun addRent(rentType: String, rent: Rent, successFun: (id: String) -> Unit) {
        val collRef = db.collection(rentType)
        collRef.add(rent).addOnCompleteListener { docRef ->
            val docId = docRef.result.id
            collRef.document(docId).update("id", docId)
            successFun.invoke(docId)
        }
    }

    fun uploadImages(list: List<File>, id: String, rentType: String) {
        for (i in list.indices) {
            val imageRef = storage.child("${id}/${i}")
            imageRef.putFile(list[i].toUri()).addOnCompleteListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    db.collection(rentType).document(id)
                        .update(
                            "listImages", FieldValue.arrayUnion(it.toString())
                        )
                }
            }
        }
    }

    fun getUser(uid: String, successFun: (User) -> Unit) {
        db.collection(CollectionTypes.USERS.toString())
            .document(uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    successFun.invoke(it.result.toObject(User::class.java)!!)
                }
            }
    }

    fun deleteRent(type: RentType,id: String, successFun: () -> Unit) {
        db.collection(type.toString())
            .document(id)
            .delete()
            .addOnCompleteListener {
                successFun.invoke()
            }
    }
}
