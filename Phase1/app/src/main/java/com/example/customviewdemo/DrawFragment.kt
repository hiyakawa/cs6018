package com.example.customviewdemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.customviewdemo.databinding.FragmentDrawBinding

class DrawFragment : Fragment() {

    private val viewModel: SimpleViewModel by activityViewModels()
    private lateinit var customView: CustomView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDrawBinding.inflate(inflater)

        customView = binding.customView

        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnClear.setOnClickListener {
            customView.clearCanvas()
        }

        binding.sizeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                val strokeWidth = progress.toFloat()
                binding.seekBarProgress.text = strokeWidth.toInt().toString()
                customView.setWidth(strokeWidth)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        binding.redBtn.setOnClickListener {
            customView.setColor(Color.RED)
        }

        binding.greenBtn.setOnClickListener {
            customView.setColor(Color.GREEN)
        }

        binding.blueBtn.setOnClickListener {
            customView.setColor(Color.BLUE)
        }

        binding.blackBtn.setOnClickListener {
            customView.setColor(Color.BLACK)
        }

        binding.eraserBtn.setOnClickListener{
            customView.toggleEraserMode()
        }
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        viewModel.setPath(customView.getPaths())
    }

    override fun onResume() {
        super.onResume()
        val customPaths = viewModel.pathData.value
        if (customPaths != null) {
            customView.setPaths(customPaths)
        }
    }
}