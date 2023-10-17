package com.example.notesapp.fragment

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

        notesLoad(null, noteInit())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            floatingActionButton.setOnClickListener {
                replaceFragment(NoteInteractFragment.newInstance())
            }

            addBtn.setOnClickListener {
                replaceFragment(NoteInteractFragment.newInstance())
            }

            insertImageBtn.setOnClickListener {
                val isNewNotes = true
                val bundle = Bundle().apply {
                    putString("insertImage", isNewNotes.toString())
                }

                val noteInteractFragment = NoteInteractFragment.newInstance().apply {
                    arguments = bundle
                }
                replaceFragment(noteInteractFragment)
            }

            insertUrlBtn.setOnClickListener {
                openAddUrlDialog()
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val adapter = noteInit()
                    if (query != null) {
                        notesLoad(query, adapter)
                    } else {
                        notesLoad(null, noteInit())
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val adapter = noteInit()
                    if (newText != null) {
                        notesLoad(newText, adapter)
                    } else {
                        notesLoad(null, noteInit())
                    }
                    return true
                }
            })

            searchView.setOnCloseListener {
                searchView.clearFocus()
                notesLoad(null, noteInit())
                true
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun noteInit(): NoteAdapter {
        val adapter = NoteAdapter { note ->
            updateFragment(note)
        }

        binding.recyclerViewNote.setHasFixedSize(true)
        binding.recyclerViewNote.adapter = adapter
        binding.recyclerViewNote.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        return adapter
    }

    private fun notesLoad(key: String?, adapter: NoteAdapter) {
        if (key == null) {
            noteViewModel.getAllNotes.observe(viewLifecycleOwner) {
                adapter.updateData(it)
            }
        } else {
            noteViewModel.searchDatabase(key).observe(viewLifecycleOwner) { notes ->
                adapter.updateData(notes)
            }
        }
    }

    private fun updateFragment(note: Notes) {
        val bundle = Bundle()
        bundle.putParcelable("selectedNote", note)

        val noteInteractFragment = NoteInteractFragment.newInstance().apply {
            arguments = bundle
        }

        replaceFragment(noteInteractFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
            replace(R.id.frameLayout, fragment)
            addToBackStack(fragment.javaClass.simpleName)
            commit()
        }
    }

    private fun openAddUrlDialog() {
        val urlDialogView =
            requireActivity().layoutInflater.inflate(R.layout.add_url_dialog, null, false)

        val dialog = Dialog(requireActivity()).apply {
            setContentView(urlDialogView)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        urlDialogView.apply {
            findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
                dialog.dismiss()
            }

            findViewById<TextView>(R.id.confirmAddUrlBtn).setOnClickListener {
                val url = findViewById<EditText>(R.id.urlDialogTxt).text.toString()
                if (!url.contains(".")) {
                    Toast.makeText(requireContext(), "Invalid URL! ", Toast.LENGTH_LONG).show()
                } else {
                    val noteInteractFragment = NoteInteractFragment().apply {
                        arguments = Bundle().apply { putString("insertURL", url) }
                    }
                    replaceFragment(noteInteractFragment)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}