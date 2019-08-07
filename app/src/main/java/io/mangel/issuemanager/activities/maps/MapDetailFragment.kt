package io.mangel.issuemanager.activities.maps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.mangel.issuemanager.R
import io.mangel.issuemanager.dummy.DummyContent
import io.mangel.issuemanager.factories.ApplicationFactory
import io.mangel.issuemanager.models.Map
import kotlinx.android.synthetic.main.activity_map_detail.*
import kotlinx.android.synthetic.main.map_detail.view.*

/**
 * A fragment representing a single Map detail screen.
 * This fragment is either contained in a [MapListActivity]
 * in two-pane mode (on tablets) or a [MapDetailActivity]
 * on handsets.
 */
class MapDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */

    private lateinit var mapId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

            mapId = it[ARG_ITEM_ID] as String
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.map_detail, container, false)

        // Show the dummy content as text in a TextView.
//        item?.let {
//            rootView.map_detail.text = it.details
//        }
        rootView.map_detail.text = mapId

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
