package io.mangel.issuemanager.activities.navigation

import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar


interface HandlesBackClick {
    // if return is true the drawer will be collapsed. Else it will stay there
    fun backWasClicked(): Boolean { return true }
}


class CustomActionBarDrawerToggle(private val parent: HandlesBackClick, private val toolbar: Toolbar,
                                  private val drawerLayout: DrawerLayout)
    : DrawerLayout.DrawerListener
{

    private val mHomeAsUpIndicator: Drawable?
    private val mSlider: DrawerArrowDrawable

    init {
        toolbar.setNavigationOnClickListener {
            customToggle()
        }

        mSlider = DrawerArrowDrawable(toolbar.context)
        mHomeAsUpIndicator = toolbar.navigationIcon
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        setPosition(Math.min(1f, Math.max(0f, slideOffset)))
    }

    override fun onDrawerClosed(drawerView: View) {
        setPosition(0f)
    }

    override fun onDrawerOpened(drawerView: View) {
        setPosition(1f)
    }

    fun syncState() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) setPosition(1f)
        else setPosition(0f)
        setActionBarUpIndicator(mSlider)
    }

    private fun customToggle() {
        val drawerLockMode = drawerLayout.getDrawerLockMode(GravityCompat.START)
        if (drawerLayout.isDrawerVisible(GravityCompat.START) && drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN) {
            val shouldBeCollapsed = parent.backWasClicked()
            if (shouldBeCollapsed) drawerLayout.closeDrawer(GravityCompat.START)
        } else if (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setPosition(position: Float) {
        mSlider.setVerticalMirror(position == 1f)
        mSlider.progress = position
    }

    private fun setActionBarUpIndicator(upDrawable: Drawable) {
        toolbar.navigationIcon = upDrawable
    }
}
