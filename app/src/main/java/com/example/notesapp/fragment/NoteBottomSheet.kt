package com.example.notesapp.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.data.FragmentViewModel
import com.example.notesapp.databinding.DeleteAlertDialogBinding
import com.example.notesapp.databinding.FragmentBottomNoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class NoteBottomSheet : BottomSheetDialogFragment() {
    interface ButtonClickListener {
        fun onAddImageBtnClick()
        fun onAddUrlBtnClick()
    }

    var buttonClickListener: ButtonClickListener? = null

    private var _bindingBottom: FragmentBottomNoteBinding? = null
    private val bindingBottom
        get() = _bindingBottom!!

    private var _bindingDelete: DeleteAlertDialogBinding? = null
    private val bindingDelete
        get() = _bindingDelete!!

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
        _bindingBottom = FragmentBottomNoteBinding.inflate(inflater, null, false)

        return bindingBottom.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingBottom.deleteBtn.setOnClickListener {
            openDeleteDialog()
        }

        bindingBottom.closeBottomSheetBtn.setOnClickListener {
            dismiss()
        }

        bindingBottom.addImageBtn.setOnClickListener {
            buttonClickListener?.onAddImageBtnClick()
            dismiss()
        }

        bindingBottom.addURLBtn.setOnClickListener {
            buttonClickListener?.onAddUrlBtnClick()
            dismiss()
        }

        fragmentViewModel.deleteConfirm.observe(viewLifecycleOwner) {
            if (it == true) {
                bindingBottom.deleteBtn.isEnabled = false
            }
        }

        selectColor()
    }


    private fun openDeleteDialog() {
        val inflater = requireActivity().layoutInflater
        _bindingDelete = DeleteAlertDialogBinding.inflate(inflater, null, false)
        val view = bindingDelete.root

        val dialog = Dialog(requireActivity())
        dialog.setContentView(view)

        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.show()

        bindingDelete.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        bindingDelete.deleteBtn.setOnClickListener {
            fragmentViewModel.setSharedData("Confirm")
            dialog.dismiss()
        }
    }

    private fun selectColor() {
        bindingBottom.grayBtn.setOnClickListener {
            bindingBottom.checkGrayBtn.visibility = View.VISIBLE
            bindingBottom.checkBlueBtn.visibility = View.GONE
            bindingBottom.checkGreenBtn.visibility = View.GONE
            bindingBottom.checkYellowBtn.visibility = View.GONE
            bindingBottom.checkRedBtn.visibility = View.GONE

            val result = "#3F3F3F"
            fragmentViewModel.setColor(result)
            dialog?.dismiss()
        }

        bindingBottom.redBtn.setOnClickListener {
            bindingBottom.checkRedBtn.visibility = View.VISIBLE
            bindingBottom.checkBlueBtn.visibility = View.GONE
            bindingBottom.checkGreenBtn.visibility = View.GONE
            bindingBottom.checkYellowBtn.visibility = View.GONE
            bindingBottom.checkGrayBtn.visibility = View.GONE

            val result = "#EF5350"
            fragmentViewModel.setColor(result)
            dialog?.dismiss()
        }

        bindingBottom.blueBtn.setOnClickListener {
            bindingBottom.checkBlueBtn.visibility = View.VISIBLE
            bindingBottom.checkGrayBtn.visibility = View.GONE
            bindingBottom.checkGreenBtn.visibility = View.GONE
            bindingBottom.checkYellowBtn.visibility = View.GONE
            bindingBottom.checkRedBtn.visibility = View.GONE

            val result = "#2396EF"
            fragmentViewModel.setColor(result)
            dialog?.dismiss()
        }

        bindingBottom.yellowBtn.setOnClickListener {
            bindingBottom.checkYellowBtn.visibility = View.VISIBLE
            bindingBottom.checkBlueBtn.visibility = View.GONE
            bindingBottom.checkGreenBtn.visibility = View.GONE
            bindingBottom.checkGrayBtn.visibility = View.GONE
            bindingBottom.checkRedBtn.visibility = View.GONE

            val result = "#FFC30C"
            fragmentViewModel.setColor(result)
            dialog?.dismiss()
        }

        bindingBottom.greenBtn.setOnClickListener {
            bindingBottom.checkGreenBtn.visibility = View.VISIBLE
            bindingBottom.checkBlueBtn.visibility = View.GONE
            bindingBottom.checkGrayBtn.visibility = View.GONE
            bindingBottom.checkYellowBtn.visibility = View.GONE
            bindingBottom.checkRedBtn.visibility = View.GONE

            val result = "#4AB050"
            fragmentViewModel.setColor(result)
            dialog?.dismiss()
        }
    }
}