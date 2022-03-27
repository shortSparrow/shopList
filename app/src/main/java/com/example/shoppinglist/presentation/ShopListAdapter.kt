package com.example.shoppinglist.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.domain.ShopItem

class ShopListAdapter : RecyclerView.Adapter<ShopListAdapter.ShopItemViewHolder>() {
    var cpunt = 0

    var shopList = listOf<ShopItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onShopItemLongClickListener: ((ShopItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        Log.d("Recreate_ViewHolder", "${++cpunt}")
        val layout = when (viewType) {
            VIEW_TYPE_ENABLED -> R.layout.item_shop_enabled
            VIEW_TYPE_DISABLED -> R.layout.item_shop_disabled
            else -> throw RuntimeException("Unknown view type: $viewType")
        }

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        return ShopItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        val shopItem = shopList[position]

        holder.itemShopName.text = shopItem.name
        holder.itemShopCount.text = shopItem.count.toString()

        holder.itemView.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(shopItem)
            return@setOnLongClickListener true
        }

//        if (shopItem.enabled) {
//            holder.itemShopName.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
//        } else {
//            holder.itemShopName.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
//        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = shopList[position]
        return if (item.enabled) VIEW_TYPE_ENABLED else VIEW_TYPE_DISABLED
    }

    /**
     * ЯК ПРАЦЮЄ RECYCLER_VIEW?
     * Ми сторюємо айтеми для видимої частини + кілька зверху і зницу (ті що не видні, відправлюється в poll ViewHolder.
     * Потім ми перстаємо стоврбвати новій айтеми, а просто починаємо перевикористовувати старі. Але варто пам'ятати,
     * що значення вни стары все ще є, тож їх потрібно перезаписати.
     *
     * Коли наші ViewHolder перестають свторюватись і починаю перевикористуовуватись
     * для них будет викликатися цей метод і тут ми можемо встановити параметра за замовченням
     *
     * Коли в нас є умова для onBindViewHolder, ми або маємо писати else і вказувати там, як має себе вести
     * item коли умова не виконалась, або встановлювати параметри за замовченням
     *
     * Або ми можемо перевизначини getItemViewType, якщо в нас є кілька різних мкетів. Якщо одна умова то тип 0,
     * якщо інша, то 1, і т.д. Але не можна просто поверати position, інакше переваги RECYCLER_VIEW нівелюються,
     * бо viewHolder не буде перевикористовууватися, а будуть постійно створбватися нові
     */
//    override fun onViewRecycled(holder: ShopItemViewHolder) {
//        super.onViewRecycled(holder)
//        holder.itemShopName.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
//    }

    override fun getItemCount() = shopList.size

    inner class ShopItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemShopName: TextView = view.findViewById(R.id.itemShopName)
        val itemShopCount: TextView = view.findViewById(R.id.itemShopCount)
    }

    companion object {
        const val VIEW_TYPE_DISABLED = 0
        const val VIEW_TYPE_ENABLED = 1

        /**
         * Якщо ми перевизначили getItemViewType, то може статися так, що в пулі viewHolder у нас всі айтеми не того типу,
         * який потрібен, тож, один буде видалятися і додаватися новий, а отже це буде виникати постійно під час скролду
         * (зоч і не так часто). Це можна вирішити додавши свої значення для максимальної кількості пула кожного типу
         */
        const val MAX_POOL_SIZE = 30
    }
}
