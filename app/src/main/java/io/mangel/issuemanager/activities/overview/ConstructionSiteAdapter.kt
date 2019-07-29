package io.mangel.issuemanager.activities.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import io.mangel.issuemanager.models.ConstructionSite
import io.mangel.issuemanager.services.FileService
import kotlinx.android.synthetic.main.row_construction_site.view.*


class ConstructionSiteAdapter(
    private val values: List<ConstructionSite>,
    private val overview: OverviewViewModel.Overview,
    private val fileService: FileService
) : RecyclerView.Adapter<ConstructionSiteAdapter.ViewHolder>() {
    private var _width: Int? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(io.mangel.issuemanager.R.layout.row_construction_site, parent, false)

        var width = this._width;
        if (width == null) {
            width = (parent.measuredWidth * .66).toInt()
            _width = width
        }
        view.minimumWidth = width
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val constructionSite = values[position]

        holder.titleView.text = constructionSite.name
        holder.addressView.text = constructionSite.address.toString()

        if (constructionSite.imagePath != null && fileService.exists(constructionSite.imagePath)) {
            val imageBytes = fileService.read(constructionSite.imagePath)
            holder.imageView.setImageURI(imageBytes.toUri())
        }

        holder.itemView.setOnClickListener {
            overview.navigate(constructionSite)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.title
        val addressView: TextView = view.address
        val imageView: ImageView = view.image
    }
}