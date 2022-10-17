package com.example.trip.views.dialogs

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.example.trip.R
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import com.skydoves.balloon.overlay.BalloonOverlayRect

class MenuPopupCoordinateFactory: Balloon.Factory() {

    override fun create(context: Context, lifecycle: LifecycleOwner?): Balloon {
        return createBalloon(context) {
            setLayout(R.layout.layout_menu_popup_coordinate)
            setWidthRatio(WIDTH_RATIO)
            setOverlayColorResource(R.color.black_semi_transparent)
            setOverlayPadding(OVERLAY_PADDING)
            setBackgroundColorResource(R.color.white)
            setIsVisibleArrow(false)
            setCornerRadius(CORNER_RADIUS)
            setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            setIsVisibleOverlay(true)
            setOverlayShape(BalloonOverlayRect)
        }
    }



    companion object {
        private const val WIDTH_RATIO = 0.95f
        private const val OVERLAY_PADDING = 4f
        private const val CORNER_RADIUS = 20f
    }
}