package com.bhardwaj.ui.models

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FirestoreRepository {
    private var firestore = FirebaseFirestore.getInstance()
    private val limitSize = 10L

    fun getUIList(type: String): Task<QuerySnapshot> {
        return firestore.collection(type)
            .limit(limitSize)
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .get()
    }

    fun lazyLoading(type: String, startAt: DocumentSnapshot): Task<QuerySnapshot> {
        return firestore.collection(type)
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .startAfter(startAt)
            .limit(limitSize)
            .get()
    }

    fun filterUIList(category: String, type: String): Task<QuerySnapshot> {
        return if (category == "All") {
            firestore.collection(type)
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .limit(limitSize)
                .get()
        } else {
            firestore.collection(type)
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .whereEqualTo("category", category)
                .limit(limitSize)
                .get()
        }
    }

    fun getSearchResults(type: String, query: ArrayList<String>): Task<QuerySnapshot> {
        return firestore.collection(type)
            .whereArrayContainsAny("tag", query)
            .limit(limitSize)
            .get()
    }
}