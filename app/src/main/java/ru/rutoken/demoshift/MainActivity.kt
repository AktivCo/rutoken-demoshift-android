package ru.rutoken.demoshift

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.rutoken.demoshift.databinding.FragmentUserBinding
import ru.rutoken.demoshift.user.User
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: FragmentUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = User(
            0,
            "Иванов Иван Иванович",
            "Генеральный директор",
            "ООО Организация больших и малых закупок",
            Date()
        )

        binding.userFullName.text = user.fullName
        binding.userPosition.text = user.position ?: getString(R.string.field_not_set)
        binding.userOrganization.text = user.organization ?: getString(R.string.field_not_set)
        binding.userCertificateExpires.text = if (user.certificateExpires != null) SimpleDateFormat(
            "d MMMM yyyy",
            Locale.getDefault()
        ).format(user.certificateExpires) else getString(R.string.field_not_set)
    }
}
