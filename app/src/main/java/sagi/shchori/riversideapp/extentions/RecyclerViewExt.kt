package sagi.shchori.riversideapp.extentions

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.smoothScrollItemToMiddle(itemPosition: Int) {
    findViewHolderForLayoutPosition(itemPosition)?.itemView?.let {   itemView ->
        val middle = ((itemView.bottom - itemView.top) / 2) + itemView.top

        val containerTop = top
        val containerBottom = bottom
        val containerMiddle = (containerBottom - containerTop) / 2

        smoothScrollBy(0, middle - containerMiddle)
    }
}