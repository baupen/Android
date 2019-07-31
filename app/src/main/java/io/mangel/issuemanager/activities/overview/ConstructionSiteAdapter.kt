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
    private var _width: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(io.mangel.issuemanager.R.layout.row_construction_site, parent, false)

        var width = this._width
        if (width == null) {
            width = (parent.measuredWidth * .66).toInt()
            _width = width
        }

        view.minimumWidth = width
        return ViewHolder(view)
    }

    private val imageLoadFailedViewByFilename = HashMap<String, ViewHolder>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val constructionSite = values[position]

        holder.titleView.text = constructionSite.name
        holder.addressView.text = constructionSite.address.toString()

        if (constructionSite.imagePath != null) {
            val loadedSuccessful = loadImageIfExists(holder, constructionSite.imagePath)

            if (!loadedSuccessful) {
                imageLoadFailedViewByFilename[constructionSite.imagePath] = holder
            }
        }

        holder.itemView.setOnClickListener {
            overview.navigate(constructionSite)
        }
    }

    private fun loadImageIfExists(holder: ViewHolder, fileName: String): Boolean {
        if (!fileService.exists(fileName)) {
            return false
        }

        val imageBytes = fileService.read(fileName)
        setImageHeight(holder.imageView)
        holder.imageView.setImageURI(imageBytes.toUri())

        return true
    }

    private fun setImageHeight(imageView: ImageView) {
        val width = _width
        if (width != null) {
            val height = (width * 0.5).toInt()

            imageView.minimumHeight = height
            imageView.maxHeight = height
        }
    }

    fun onFileChanged(fileName: String) {
        val holder = imageLoadFailedViewByFilename[fileName] ?: return
        loadImageIfExists(holder, fileName)
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.title
        val addressView: TextView = view.address
        val imageView: ImageView = view.image
    }
}