package com.bhardwaj.ui.models

import com.bhardwaj.ui.R
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class FirestoreRepository {
    private var firestore = FirebaseFirestore.getInstance()

    fun getUIList(type: String): Task<QuerySnapshot> {
        return firestore.collection(type)
            .limit(100)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .get()
    }

    fun lazyLoading(type: String, startAt: DocumentSnapshot): Task<QuerySnapshot> {
        return firestore.collection(type)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .startAfter(startAt)
            .limit(100)
            .get()
    }

    fun filterUIList(category: String, type: String): Task<QuerySnapshot> {
        return if (category == "All") {
            firestore.collection(type)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(100)
                .get()
        } else {
            firestore.collection(type)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .whereEqualTo("category", category)
                .limit(100)
                .get()
        }
    }

    fun getSearchResults(type: String, query: ArrayList<String>): Task<QuerySnapshot> {
        return firestore.collection(type)
            .whereArrayContainsAny("tag", query)
            .limit(100)
            .get()
    }
}