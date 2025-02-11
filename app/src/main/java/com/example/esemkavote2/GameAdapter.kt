import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import com.example.esemkavote2.HomeActivity
import com.example.esemkavote2.R

class GameAdapter(private val context: Context, private val games: List<HomeActivity.Game>) : BaseAdapter() {

    override fun getCount(): Int = games.size
    override fun getItem(position: Int): Any = games[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_view_item_layout, parent, false)

        val nameTextView = view.findViewById<TextView>(R.id.tvTitle)
        val descriptionTextView = view.findViewById<TextView>(R.id.tvDescription)
        val authorTextView = view.findViewById<TextView>(R.id.tvAuthor)

        val game = games[position]

        nameTextView.text = game.title
        descriptionTextView.text = game.description
        authorTextView.text = "Author: ${game.author}"

        return view
    }
}
