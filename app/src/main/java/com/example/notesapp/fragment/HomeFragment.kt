package com.example.notesapp.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.Notes
import com.example.notesapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!

    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(requireActivity())[NoteViewModel::class.java]
    }

    companion object {
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        loadNotes()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            replaceFragment(NoteInteractFragment.newInstance())
        }

        binding.addBtn.setOnClickListener {
            replaceFragment(NoteInteractFragment.newInstance())
        }

        binding.insertImageBtn.setOnClickListener {
            val isNewNotes = true
            val bundle = Bundle()
            bundle.putString("insertImage", isNewNotes.toString())

            val noteInteractFragment = NoteInteractFragment()
            noteInteractFragment.arguments = bundle
            replaceFragment(noteInteractFragment)
        }

        binding.insertUrlBtn.setOnClickListener {
            openAddUrlDialog()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val adapter = noteInit()

                    noteViewModel.searchDatabase(query).observe(viewLifecycleOwner) { notes ->
                        adapter.updateData(notes)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val adapter = noteInit()

                noteViewModel.searchDatabase(newText).observe(viewLifecycleOwner) { notes ->
                    adapter.updateData(notes)
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadNotes() {
        val adapter = noteInit()

        noteViewModel.getAllNotes.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    private fun noteInit(): NoteAdapter{
        val adapter = NoteAdapter { note ->
            updateFragment(note)
        }

        binding.recyclerViewNote.setHasFixedSize(true)
        binding.recyclerViewNote.adapter = adapter
        binding.recyclerViewNote.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        return adapter
    }

    private fun updateFragment(note: Notes) {
        val bundle = Bundle()
        bundle.putParcelable("selectedNote", note)

        val noteInteractFragment = NoteInteractFragment()
        noteInteractFragment.arguments = bundle

        replaceFragment(noteInteractFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()

        fragmentTransaction
            .setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        fragmentTransaction
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    private fun openAddUrlDialog() {
        val inflater = requireActivity().layoutInflater
        val urlDialogView = inflater.inflate(R.layout.add_url_dialog, null, false)

        val cancelBtn: TextView = urlDialogView.findViewById(R.id.cancelBtn)
        val confirmAddUrlBtn: TextView = urlDialogView.findViewById(R.id.confirmAddUrlBtn)
        val urlDialogTxt: EditText = urlDialogView.findViewById(R.id.urlDialogTxt)

        val dialog = Dialog(requireActivity())
        dialog.setContentView(urlDialogView)

        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.show()

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        confirmAddUrlBtn.setOnClickListener {
            val url = urlDialogTxt.text.toString()
            if (!url.contains(".")) {
                Toast.makeText(requireContext(), "Invalid URL! ", Toast.LENGTH_LONG).show()
            } else {
                val bundle = Bundle()
                bundle.putString("insertURL", url)

                val noteInteractFragment = NoteInteractFragment()
                noteInteractFragment.arguments = bundle
                replaceFragment(noteInteractFragment)
                dialog.dismiss()
            }
        }
    }
}


