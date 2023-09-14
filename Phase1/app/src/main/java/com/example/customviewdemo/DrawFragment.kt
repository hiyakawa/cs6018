package com.example.customviewdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.customviewdemo.databinding.FragmentDrawBinding

class DrawFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentDrawBinding.inflate(inflater)
        val viewModel: SimpleViewModel by activityViewModels()

        val customView = binding.customView

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnClear.setOnClickListener {
            customView.clearCanvas()
        }

        val customPath = viewModel.pathData.value
        if (customPath != null) {
            customView.setPath(customPath.path)
            customView.setWidth(customPath.strokeWidth)
        }

        binding.sizeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                val strokeWidth = progress.toFloat()
                binding.seekBarProgress.text = strokeWidth.toInt().toString()
                customView.setWidth(strokeWidth)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
            }
        })

        return binding.root
    }
}
