package com.example.notesapp.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.data.FragmentViewModel
import com.example.notesapp.databinding.FragmentBottomNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NoteBottomSheet : BottomSheetDialogFragment() {
    interface ButtonClickListener {
        fun onAddImageBtnClick()
        fun onAddUrlBtnClick()
    }

    var buttonClickListener: ButtonClickListener? = null

    private var _binding: FragmentBottomNoteBinding? = null
    private val binding
        get() = _binding!!

    private val fragmentViewModel: FragmentViewModel by lazy {
        ViewModelProvider(requireActivity())[FragmentViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        buttonClickListener = parentFragment as? ButtonClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflater = requireActivity().layoutInflater
        _binding = FragmentBottomNoteBinding.inflate(inflater, null, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            deleteBtn.setOnClickListener { openDeleteDialog() }
            closeBottomSheetBtn.setOnClickListener { dismiss() }
            addImageBtn.setOnClickListener {
                buttonClickListener?.onAddImageBtnClick()
                dismiss()
            }
            addURLBtn.setOnClickListener {
                buttonClickListener?.onAddUrlBtnClick()
                dismiss()
            }
        }

        fragmentViewModel.deleteConfirm.observe(viewLifecycleOwner) {
            binding.deleteBtn.isEnabled = !it
        }

        selectColor()
    }


    private fun openDeleteDialog() {
        val view =
            requireActivity().layoutInflater.inflate(R.layout.delete_alert_dialog, null, false)

        val dialog = Dialog(requireActivity()).apply {
            setContentView(view)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        view.findViewById<TextView>(R.id.cancelBtn).setOnClickListener { dialog.dismiss() }
        view.findViewById<TextView>(R.id.deleteBtn).setOnClickListener {
            fragmentViewModel.setSharedData("Confirm")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun selectColor() {
        val colorButtons = mapOf(
            binding.grayBtn to Pair(binding.checkGrayBtn, "#3F3F3F"),
            binding.redBtn to Pair(binding.checkRedBtn, "#EF5350"),
            binding.blueBtn to Pair(binding.checkBlueBtn, "#2396EF"),
            binding.yellowBtn to Pair(binding.checkYellowBtn, "#FFC30C"),
            binding.greenBtn to Pair(binding.checkGreenBtn, "#4AB050")
        )

        val checkButtons = colorButtons.values.map { it.first }

        colorButtons.forEach { (colorButton, pair) ->
            val (checkButton, color) = pair
            colorButton.setOnClickListener {
                checkButtons.forEach { it.visibility = View.GONE }
                checkButton.visibility = View.VISIBLE
                fragmentViewModel.setColor(color)
                dialog?.dismiss()
            }
        }
    }
}