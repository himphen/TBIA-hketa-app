package hibernate.v2.sunshine.util

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(
    context: Context?,
    @DrawableRes drawableId: Int? = null,
    url: String? = null,
    fadeIn: Boolean = false
) {
    if (context == null || context.isDoomed()) return

    val glide = Glide.with(context)
    var glideBuilder = when {
        drawableId != null -> glide.load(drawableId)
        url != null -> glide.load(url)
        else -> return
    }

    if (fadeIn) {
        glideBuilder = glideBuilder.transition(DrawableTransitionOptions.withCrossFade())
    }

    glideBuilder
        .apply(RequestOptions()
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE))
        .into(this)
}