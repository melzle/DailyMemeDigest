package com.sukacita.dailymemedigest

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MEME = "meme"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var meme: Meme? = null
    private var memes: ArrayList<Meme> = arrayListOf()
    val REQUEST_UPDATE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            meme = it.getParcelable(ARG_MEME)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false).apply {
            val fab: FloatingActionButton = this.findViewById(R.id.fabAdd_homeFrag)

            fab.setOnClickListener() {
                val intent = Intent(activity, CreateMeme::class.java)
                activity?.startActivityForResult(intent, REQUEST_UPDATE)
            }

            val lm: LinearLayoutManager = LinearLayoutManager(activity)
            val recycler: RecyclerView = this.findViewById(R.id.MemeRecyclerView_homefrag)
            recycler.layoutManager = lm
            recycler.setHasFixedSize(true)

            recycler.adapter = HomeMemeAdapter(requireActivity(), Global.homeMemes, Global.currentUser.id)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(meme: Meme) =
            HomeFragment().apply {
                arguments = Bundle().apply {
//                   putParcelable(ARG_MEME, meme)
                }
            }
    }
}