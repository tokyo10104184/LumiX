package com.project.lumina.client.constructors

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.project.lumina.client.R

enum class CheatCategory(
    @DrawableRes val iconResId: Int,
    @StringRes val labelResId: Int
) {

    Combat(
        iconResId = R.drawable.ic_sword_cross_black_24dp,
        labelResId = R.string.combat
    ),
    Motion(
        iconResId = R.drawable.ic_run_black_24dp,
        labelResId = R.string.motion
    ),
    World(
        iconResId = R.drawable.ic_cube_outline_black_24dp,
                labelResId = R.string.world
    ),
    Visual(
        iconResId = R.drawable.ic_camera_outline_black_24dp,
        labelResId = R.string.visual
    ),
    Misc(
        iconResId = R.drawable.ic_key_variant_black_24dp,
        labelResId = R.string.misc
    ),
   /* Effect(
        iconResId = R.drawable.select_24,
        labelResId = R.string.effect
    ),*/
    Config(
        iconResId = R.drawable.ic_cloud_upload_black_24dp,
        labelResId = R.string.config
    )
}