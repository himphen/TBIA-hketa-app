package hibernate.v2.sunshine.ui.bookmark.home.mobile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import hibernate.v2.model.Card
import hibernate.v2.sunshine.BuildConfig
import hibernate.v2.sunshine.databinding.ItemEtaAdBannerBinding
import hibernate.v2.sunshine.databinding.ItemEtaButtonGroupBinding
import hibernate.v2.sunshine.databinding.ItemEtaCardClassicBinding
import hibernate.v2.sunshine.databinding.ItemEtaCardCompactBinding
import hibernate.v2.sunshine.databinding.ItemEtaCardStandardBinding
import hibernate.v2.sunshine.ui.base.BaseViewHolder
import hibernate.v2.sunshine.ui.bookmark.EtaCardViewType
import hibernate.v2.sunshine.ui.bookmark.EtaTimeAdapter
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.BaseEtaViewHolder
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.EtaViewHolderClassic
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.EtaViewHolderCompact
import hibernate.v2.sunshine.ui.bookmark.home.mobile.view.EtaViewHolderStandard
import hibernate.v2.sunshine.ui.view.setEtaTimeFlexManager

class BookmarkHomeEtaCardAdapter(
    var type: EtaCardViewType,
    var showAdBanner: Boolean,
    val onAddButtonClick: () -> Unit,
    val onEditButtonClick: () -> Unit,
) : RecyclerView.Adapter<BaseViewHolder<out ViewBinding>>() {

    companion object {
        const val VIEW_TYPE_AD_BANNER = 0
        const val VIEW_TYPE_CONTENT = 1
        const val VIEW_TYPE_BUTTON_GROUP = 2
    }

    private var list = mutableListOf<Card.EtaCard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        BaseViewHolder<out ViewBinding> {
        return when (viewType) {
            VIEW_TYPE_AD_BANNER -> {
                EtaViewHolderAdBanner(
                    ItemEtaAdBannerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ).apply {
                    val adView = AdView(context)
                    adView.adUnitId = BuildConfig.ADMOB_BANNER_ID
                    adView.adSize = AdSize.BANNER
                    adView.loadAd(AdRequest.Builder().build())
                    viewBinding.root.addView(adView)
                }
            }
            VIEW_TYPE_CONTENT -> {
                when (type) {
                    EtaCardViewType.Classic -> EtaViewHolderClassic(
                        ItemEtaCardClassicBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    ).apply {
                        viewBinding.content.etaMinuteLl.etaTimeRv.apply {
                            setEtaTimeFlexManager()
                            adapter = EtaTimeAdapter()
                        }
                    }
                    EtaCardViewType.Compact -> EtaViewHolderCompact(
                        ItemEtaCardCompactBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                    EtaCardViewType.Standard -> EtaViewHolderStandard(
                        ItemEtaCardStandardBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                }
            }
            else -> {
                EtaViewHolderButtonGroup(
                    ItemEtaButtonGroupBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ).apply {
                    viewBinding.apply {
                        addStopButton.setOnClickListener {
                            onAddButtonClick()
                        }
                        editStopButton.setOnClickListener {
                            onEditButtonClick()
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<out ViewBinding>, position: Int) {
        if (holder is BaseEtaViewHolder) {
            val item = if (showAdBanner) {
                list[position - 1]
            } else {
                list[position]
            }
            holder.onBind(item)
        }
    }

    override fun getItemCount(): Int = if (showAdBanner) list.size + 2 else list.size + 1

    override fun getItemViewType(position: Int): Int =
        if (showAdBanner) {
            when (position) {
                0 -> VIEW_TYPE_AD_BANNER
                list.lastIndex + 2 -> VIEW_TYPE_BUTTON_GROUP
                else -> VIEW_TYPE_CONTENT
            }
        } else {
            when (position) {
                list.lastIndex + 1 -> VIEW_TYPE_BUTTON_GROUP
                else -> VIEW_TYPE_CONTENT
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(etaCardList: MutableList<Card.EtaCard>?) {
        if (etaCardList == null) return

        this.list = etaCardList
        notifyDataSetChanged()
    }

    fun replace(position: Int, etaCard: Card.EtaCard) {
        list[position] = etaCard
        if (showAdBanner) {
            notifyItemChanged(position + 1)
        } else {
            notifyItemChanged(position)
        }
    }

    inner class EtaViewHolderButtonGroup(viewBinding: ItemEtaButtonGroupBinding) :
        BaseViewHolder<ItemEtaButtonGroupBinding>(viewBinding)

    inner class EtaViewHolderAdBanner(viewBinding: ItemEtaAdBannerBinding) :
        BaseViewHolder<ItemEtaAdBannerBinding>(viewBinding)
}
