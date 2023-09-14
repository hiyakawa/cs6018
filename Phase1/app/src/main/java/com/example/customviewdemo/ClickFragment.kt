package com.example.customviewdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.customviewdemo.databinding.FragmentClickBinding

class ClickFragment : Fragment() {

    private var buttonFunction : () -> Unit = {}

    fun setButtonFunction(newFunc: () -> Unit) {
        buttonFunction = newFunc
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentClickBinding.inflate(inflater, container, false)

        binding.clickMe.setOnClickListener {
            buttonFunction()
        }

        return binding.root
    }
}