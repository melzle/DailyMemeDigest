package com.sukacita.dailymemedigest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyCreationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyCreationFragment : Fragment() {
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_creation, container, false).apply {
            val txtWarn: TextView = findViewById(R.id.txtWarn_mycreation)
            if (Global.userMemes.size > 0) {
                val lm: LinearLayoutManager = LinearLayoutManager(activity)
                val recycler: RecyclerView = this.findViewById(R.id.MemeRecyclerView)
                recycler.layoutManager = lm
                recycler.setHasFixedSize(true)
                recycler.adapter = MyCreationAdapter(requireActivity(), Global.userMemes, Global.currentUser.id)
                txtWarn.text = ""
            } else {
                txtWarn.text = "You haven't created any memes :("
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyCreationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyCreationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}