package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.myapplication.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "mParam1"
private const val ARG_PARAM2 = "mParam2"

/**
 * A simple [Fragment] subclass.
 * Use the [MessageFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageFormFragment : Fragment() {

    private var mParam1: String? = null
    private var mParam2: String? = null

//    var view: View? = null
    var is_adi_edittext: EditText? = null
    var is_aciklama_edittext: EditText? = null
    var kaydet_button: Button? = null
    var iptal_button: Button? = null
    var delete_button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mParam1 = it.getString(ARG_PARAM1)
            mParam2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_place_details, container, false)

        is_adi_edittext = view.rootView.findViewById<EditText>(R.id.is_adi_edittext)
        is_adi_edittext?.setText(mParam1)

        is_aciklama_edittext = view.rootView.findViewById<EditText>(R.id.is_aciklama_edittext)
        is_aciklama_edittext?.setText(mParam2)

        kaydet_button = view.rootView.findViewById<Button>(R.id.kaydet_button)
        iptal_button = view.rootView.findViewById<Button>(R.id.iptal_button)
        delete_button = view.rootView.findViewById<Button>(R.id.delete_button)

        if (mParam1 == null && mParam2 == null) {
            kaydet_button?.visibility = View.VISIBLE
            iptal_button?.visibility = View.VISIBLE
            delete_button?.visibility  = View.GONE
            is_adi_edittext?.setEnabled(true)
            is_aciklama_edittext?.setEnabled(true)
        } else {
            kaydet_button?.visibility = View.GONE
            delete_button?.visibility = View.VISIBLE
            iptal_button?.visibility = View.VISIBLE
            is_adi_edittext?.setEnabled(false)
            is_aciklama_edittext?.setEnabled(false)
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            MessageFormFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}