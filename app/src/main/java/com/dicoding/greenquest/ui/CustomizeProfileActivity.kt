package com.dicoding.greenquest.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.greenquest.R
import com.dicoding.greenquest.databinding.ActivityCustomizeProfileBinding

class CustomizeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomizeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageResIds = arrayOf(
            R.drawable.sapi, // Gambar pertama
            R.drawable.bebek, // Gambar kedua
            R.drawable.kelinci, // Gambar ketiga
            R.drawable.sapi, // Gambar keempat
            R.drawable.bebek, // Gambar kelima
            R.drawable.kelinci  // Gambar keenam
        )

        val gridAdapter = ImageGridAdapter(imageResIds)

        // Menyembunyikan GridView pada awalnya
        binding.gridView.adapter = gridAdapter

        // Set onClickListener untuk btnEditProfilePic
        binding.btnEditProfilePic.setOnClickListener {
            // Tampilkan GridView saat tombol diklik
            binding.gridView.visibility = GridView.VISIBLE
        }

        binding.btnBack.setOnClickListener {
            finish() // Menutup activity dan kembali ke fragment sebelumnya
        }

        binding.root.setOnClickListener {
            if (binding.gridView.visibility == GridView.VISIBLE) {
                // Periksa apakah klik terjadi di luar GridView
                val gridViewRect = android.graphics.Rect()
                binding.gridView.getGlobalVisibleRect(gridViewRect)
                if (!gridViewRect.contains(it.x.toInt(), it.y.toInt())) {
                    binding.gridView.visibility = GridView.GONE
                }
            }
        }
    }

    // Adapter untuk menampilkan gambar dalam GridView
    private inner class ImageGridAdapter(private val imageResIds: Array<Int>) : BaseAdapter() {
        override fun getCount(): Int = imageResIds.size

        override fun getItem(position: Int): Any = imageResIds[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View {
            val imageView: ImageView
            if (convertView == null) {
                imageView = ImageView(this@CustomizeProfileActivity)
                val params = android.widget.AbsListView.LayoutParams(250, 250) // Ubah LayoutParams
                imageView.layoutParams = params
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                imageView = convertView as ImageView
            }
            imageView.setImageResource(imageResIds[position])
            return imageView
        }
    }
}
