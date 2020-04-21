package ru.rutoken.demoshift

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.rutoken.demoshift.databinding.ActivityMainBinding
import ru.rutoken.demoshift.databinding.ToolbarBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(ToolbarBinding.inflate(layoutInflater).toolbar)
    }
}
