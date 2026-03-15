package com.docvault.core.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun inflateBinding(): VB

    abstract fun initViews()

    abstract fun observeState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateBinding()
        setContentView(binding.root)
        initViews()
        observeState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
