import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.fenix.freddinho.ChatAdult
import com.fenix.freddinho.ChatChild
import com.fenix.freddinho.R

class Adapter(private val context: Context, private val dependentList: MutableList<Dependent>):
        RecyclerView.Adapter<Adapter.DependentViewHolder>() {

    val boyImg: Drawable? = ResourcesCompat
            .getDrawable(context.resources, R.drawable.profileboy, null)
    val girlImg: Drawable? = ResourcesCompat
            .getDrawable(context.resources, R.drawable.profilegirl, null)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DependentViewHolder {
        val theList = LayoutInflater.from(context)
                .inflate(R.layout.profile_dialog, parent, false)

        return DependentViewHolder(theList)
    }

    override fun onBindViewHolder(holder: DependentViewHolder, position: Int) {
        if (dependentList[position].gender == 'M'){
            holder.userPhoto.setImageDrawable(boyImg)
        }
        else{
            holder.userPhoto.setImageDrawable(girlImg)
        }

        holder.userPhoto.setOnClickListener{
            val i = Intent(context, ChatChild::class.java)
            i.putExtra("dependentName", dependentList[position].name)
            context.startActivity(i)
        }

        holder.name.text = dependentList[position].name
    }

    override fun getItemCount(): Int = dependentList.size

    inner class DependentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userPhoto: ImageButton = itemView.findViewById<ImageButton>(R.id.btn_profile)
        val name: TextView = itemView.findViewById<TextView>(R.id.user_name)
    }
}
