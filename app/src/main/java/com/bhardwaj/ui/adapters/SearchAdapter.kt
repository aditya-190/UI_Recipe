package com.bhardwaj.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bhardwaj.ui.R
import com.bhardwaj.ui.models.UI
import com.bhardwaj.ui.view.FragmentAdobeXDDirections
import com.bhardwaj.ui.view.FragmentFigmaDirections
import com.bhardwaj.ui.view.FragmentSketchDirections
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView

class SearchAdapter(
    val mContext: Context,
    private val searchList: ArrayList<UI>,
    private val type: String
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val mcvUIContainer: MaterialCardView = view.findViewById(R.id.mcvUIContainer)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val ivImage: ImageView = view.findViewById(R.id.ivImage)
    }

    fun updateList(newUiList: ArrayList<UI>) {
        searchList.clear()
        searchList.addAll(newUiList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.layout_single_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = searchList[position]

        holder.tvTitle.text = current.title

        Glide.with(mContext)
            .setDefaultRequestOptions(RequestOptions())
            .load(current.imageUrl)
            .into(holder.ivImage)

        holder.mcvUIContainer.setOnClickListener { view ->
            when (type) {
                mContext.getString(R.string.adobeXD) -> {
                    Navigation.findNavController(view).navigate(
                        FragmentAdobeXDDirections.actionFragmentAdobeXDToFragmentSearch(current)
                    )
                }

                mContext.getString(R.string.sketch) -> {
                    Navigation.findNavController(view).navigate(
                        FragmentSketchDirections.actionFragmentSketchToFragmentSearch(current)
                    )
                }

                mContext.getString(R.string.figma) -> {
                    Navigation.findNavController(view).navigate(
                        FragmentFigmaDirections.actionFragmentFigmaToFragmentSearch(current)
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }
}