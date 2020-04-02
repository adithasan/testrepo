package com.invisibleink.explore

import android.os.Bundle
import android.view.*
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import com.invisibleink.R
import com.invisibleink.explore.ar.ArExploreFragment
import com.invisibleink.explore.map.MapExploreFragment

class ExploreFragment(explorationModeId: Int) : Fragment() {

    enum class ExploreViewMode(val fragmentFactory: () -> Fragment, val CHILD_FRAGMENT_ID: Int) {
        MAP(::MapExploreFragment, 1),
        AR(::ArExploreFragment, 2);

        companion object {
            fun fromModeId(@IntRange(from = 0, to = 1) exploreModeId: Int): ExploreViewMode {
                return if (exploreModeId == MAP.CHILD_FRAGMENT_ID) MAP else AR
            }
        }
    }

    private var exploreViewMode = ExploreViewMode.fromModeId(explorationModeId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onStart() {
        super.onStart()
        showExploreViewMode(exploreViewMode)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.explore_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val mapExploreItem = menu.findItem(R.id.mapExploreItem)
        val arExploreItem = menu.findItem(R.id.arExploreItem)

        if (exploreViewMode == ExploreViewMode.MAP) {
            mapExploreItem.isVisible = false
            arExploreItem.isVisible = true
        } else {
            mapExploreItem.isVisible = true
            arExploreItem.isVisible = false
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.mapExploreItem -> {
            showExploreViewMode(ExploreViewMode.MAP)
            true
        }
        R.id.arExploreItem -> {
            showExploreViewMode(ExploreViewMode.AR)
            true
        }
        else ->
            super.onOptionsItemSelected(item)
    }

    private fun showExploreViewMode(exploreViewMode: ExploreViewMode) {
        this.exploreViewMode = exploreViewMode
        requireActivity().invalidateOptionsMenu()

        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, exploreViewMode.fragmentFactory.invoke())
            .commit()
    }
}