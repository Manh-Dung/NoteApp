package com.example.notesapp.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.data.FragmentViewModel
import com.example.notesapp.data.NoteViewModel
import com.example.notesapp.data.Notes
import com.example.notesapp.databinding.FragmentNoteInteractBinding
import java.text.SimpleDateFormat
import java.util.Date


class NoteInteractFragment : Fragment(), NoteBottomSheet.ButtonClickListener {
    private var _binding: FragmentNoteInteractBinding? = null
    private val binding
        get() = _binding!!

    private val noteViewModel: NoteViewModel by lazy {
        ViewModelProvider(requireActivity())[NoteViewModel::class.java]
    }

    private val fragmentViewModel: FragmentViewModel by lazy {
        ViewModelProvider(requireActivity())[FragmentViewModel::class.java]
    }

    private val contract = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        binding.imageView.setImageURI(it)
        binding.imageView.tag = it.toString()

        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        it?.let { requireActivity().contentResolver.takePersistableUriPermission(it, takeFlags) }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NoteInteractFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteInteractBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNewNote()
        checkInsertImage()
        checkInsertURL()

        binding.backBtn.setOnClickListener {
            replaceFragment(HomeFragment.newInstance())
        }

        binding.openBottomSheetBtn.setOnClickListener {
            NoteBottomSheet().show(
                this@NoteInteractFragment.childFragmentManager,
                "BottomSheetDialog"
            )
        }

        binding.deleteImgBtn.setOnClickListener {
            binding.imageLayout.visibility = View.GONE
            binding.imageView.tag = ""
        }

        binding.deleteUrlBtn.setOnClickListener {
            binding.urlLayout.visibility = View.GONE
            binding.urlTxt.text = ""
        }

        binding.urlTxt.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://" + binding.urlTxt.text.toString())
            startActivity(intent)
        }

        fragmentViewModel.color.observe(viewLifecycleOwner) {
            val colorDrawable = ColorDrawable(Color.parseColor(it))
            binding.noteBackground.background = colorDrawable
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentViewModel.setSharedData("Waiting")
    }

//    private fun checkNewNote() {
//        val selectedNote = arguments?.parcelable("selectedNote") as? Notes
//        if (selectedNote != null) {
//            binding.titleTxt.setText(selectedNote.title)
//            binding.dateTimeTxt.text = selectedNote.dateTime
//            if (selectedNote.subTitle != "") {
//                binding.subTitleTxt.setText(selectedNote.subTitle)
//            }
//            binding.textTxt.setText(selectedNote.text)
//
//            val colorDrawable = ColorDrawable(Color.parseColor(selectedNote.color))
//            binding.noteBackground.background = colorDrawable
//
//            if (selectedNote.link != "") {
//                binding.urlLayout.visibility = View.VISIBLE
//                binding.urlTxt.text = selectedNote.link
//            }
//            if (selectedNote.image != "") {
//                binding.imageLayout.visibility = View.VISIBLE
//                binding.imageView.setImageURI(selectedNote.image.toUri())
//            }
//            fragmentViewModel.setDeleteConfirm(false)
//        } else {
//            val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//            val curTime = format.format(Date())
//            binding.dateTimeTxt.text = curTime
//            fragmentViewModel.setDeleteConfirm(true)
//        }
//
//        binding.confirmBtn.setOnClickListener {
//            if (selectedNote != null) {
//                updateNote(selectedNote)
//            } else {
//                insertNote()
//            }
//        }
//
//        fragmentViewModel.sharedData.observe(viewLifecycleOwner) {
//            if (it == "Confirm") {
//                deleteNote(selectedNote)
//            }
//        }
//    }

    private fun checkNewNote() {
        val selectedNote = arguments?.parcelable("selectedNote") as? Notes

        selectedNote?.let { note ->
            with(binding) {
                titleTxt.setText(note.title)
                dateTimeTxt.text = note.dateTime
                subTitleTxt.setText(note.subTitle.takeIf { it.isNotEmpty() })
                textTxt.setText(note.text)

                noteBackground.background = ColorDrawable(Color.parseColor(note.color))

                urlLayout.visibility = if (note.link.isNotEmpty()) View.VISIBLE else View.GONE
                urlTxt.text = note.link

                imageLayout.visibility = if (note.image.isNotEmpty()) View.VISIBLE else View.GONE
                imageView.setImageURI(note.image.toUri())
            }

            fragmentViewModel.setDeleteConfirm(false)

            binding.confirmBtn.setOnClickListener { updateNote(note) }
        } ?: run {
            val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val curTime = format.format(Date())
            binding.dateTimeTxt.text = curTime
            fragmentViewModel.setDeleteConfirm(true)

            binding.confirmBtn.setOnClickListener { insertNote() }
        }

        fragmentViewModel.sharedData.observe(viewLifecycleOwner) {
            if (it == "Confirm") {
                deleteNote(selectedNote)
            }
        }
    }

    private fun checkInsertImage() {
        arguments?.getString("insertImage")?.toBoolean()?.let {
            contract.launch(arrayOf("image/*"))
            binding.imageLayout.visibility = View.VISIBLE
        }
    }

    private fun checkInsertURL() {
        arguments?.getString("insertURL")?.let { url ->
            binding.urlLayout.visibility = View.VISIBLE
            binding.urlTxt.text = url
        }
    }

    private fun insertNote() {
        val title = binding.titleTxt.text.toString()
        val subTitle = binding.subTitleTxt.text.toString()
        val dateTime = binding.dateTimeTxt.text.toString()
        val text = binding.textTxt.text.toString()

        if (inputCheck(title) && inputCheck(text)) {
            var color = "#3F3F3F"
            fragmentViewModel.color.observe(viewLifecycleOwner) { color = it }

            val uri = if (binding.imageLayout.visibility == View.VISIBLE) {
                binding.imageView.tag.toString()
            } else {
                ""
            }

            val url: String = if (binding.urlLayout.visibility == View.VISIBLE) {
                binding.urlTxt.text.toString()
            } else {
                ""
            }

            val notes = Notes(
                null,
                title,
                subTitle,
                dateTime,
                text,
                uri,
                url,
                color
            )

            noteViewModel.insertNote(notes)
            Toast.makeText(requireContext(), "Added ", Toast.LENGTH_LONG).show()
            replaceFragment(HomeFragment.newInstance())
        } else {
            if (!inputCheck(title)) {
                Toast.makeText(
                    requireContext(),
                    "Please fill out the Title field!",
                    Toast.LENGTH_LONG
                ).show()
            }
            if (!inputCheck(text)) {
                Toast.makeText(
                    requireContext(),
                    "Please fill out the Text field!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateNote(notes: Notes?) {
        notes?.let { note ->
            val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val curTime = format.format(Date())
            binding.dateTimeTxt.text = curTime

            note.title = binding.titleTxt.text.toString()
            note.subTitle = binding.subTitleTxt.text.toString()

            note.dateTime = binding.dateTimeTxt.text.toString()
            note.text = binding.textTxt.text.toString()

            note.image = if (binding.imageLayout.visibility == View.VISIBLE) {
                binding.imageView.tag.toString()
            } else {
                ""
            }

            note.link = if (binding.urlLayout.visibility == View.VISIBLE) {
                binding.urlTxt.text.toString()
            } else {
                ""
            }

            fragmentViewModel.color.observe(viewLifecycleOwner) { note.color = it }

            if (inputCheck(binding.titleTxt.text.toString()) && inputCheck(binding.textTxt.text.toString())) {
                noteViewModel.updateNote(note)
                Toast.makeText(requireContext(), "Updated ", Toast.LENGTH_LONG).show()
                replaceFragment(HomeFragment.newInstance())
            } else {
                if (!inputCheck(binding.titleTxt.text.toString())) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill out the Title field!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (!inputCheck(binding.textTxt.text.toString())) {
                    Toast.makeText(
                        requireContext(),
                        "Please fill out the Text field!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun deleteNote(notes: Notes?) {
        noteViewModel.deleteNote(notes)
        Toast.makeText(
            requireContext(),
            "Deleted ",
            Toast.LENGTH_LONG
        ).show()
        replaceFragment(HomeFragment.newInstance())
    }

    private fun inputCheck(input: String): Boolean {
        return input.isNotEmpty()
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
            replace(R.id.frameLayout, fragment)
            disallowAddToBackStack()
            commit()
        }
    }

    private inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    override fun onAddUrlBtnClick() {
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

        urlDialogView.findViewById<TextView>(R.id.cancelBtn).setOnClickListener {
            dialog.dismiss()
        }

        urlDialogView.findViewById<TextView>(R.id.confirmAddUrlBtn).setOnClickListener {
            val url = urlDialogView.findViewById<EditText>(R.id.urlDialogTxt).text.toString()
            if (!url.contains(".")) {
                Toast.makeText(requireContext(), "Invalid URL! ", Toast.LENGTH_LONG).show()
            } else {
                binding.urlTxt.text = url
                dialog.dismiss()
            }
            binding.urlLayout.visibility = View.VISIBLE
        }

        dialog.show()
    }

    override fun onAddImageBtnClick() {
        contract.launch(arrayOf("image/*"))
        binding.imageLayout.visibility = View.VISIBLE
    }
}