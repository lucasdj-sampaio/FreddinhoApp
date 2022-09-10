package com.fenix.freddinho

import android.app.Activity
import com.bumptech.glide.Glide
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

/**
 * Created by VMac on 17/11/16.
 */
class ChatAdapter(private val messageArrayList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected var activity: Activity? = null
    private val SELF = 100

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View

        // view type is to identify where to render the chat message
        // left or right
        itemView = if (viewType == SELF) {
            // self message
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_self, parent, false)
        } else {
            // WatBot message
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_watson, parent, false)
        }
        return ViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageArrayList[position]
        return if (message.id != null && message.id == "1") {
            SELF
        } else position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageArrayList[position]
        when (message.type) {
            Message.Type.TEXT -> (holder as ViewHolder).message.text =
                ConvertNewLineHtmlToKotlin(message.message.toString())
            Message.Type.IMAGE -> {
                (holder as ViewHolder).message.visibility = View.GONE
                val iv = holder.image
                Glide
                    .with(iv!!.context)
                    .load(message.url)
                    .into(iv)
            }
        }
    }

    private fun ConvertNewLineHtmlToKotlin(message: String) : String {
        return message.replace("<br> <br> ", "\n")
    }

    override fun getItemCount(): Int {
        return messageArrayList.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        var message: TextView
        var image: ImageView? = null

        init {
            message = itemView.findViewById<View>(R.id.message) as TextView
            image = itemView.findViewById<View>(R.id.image) as ImageView?

            //TODO: Uncomment this if you want to use a custom Font
            /*String customFont = "Montserrat-Regular.ttf";
            Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), customFont);
            message.setTypeface(typeface);*/
        }
    }
}