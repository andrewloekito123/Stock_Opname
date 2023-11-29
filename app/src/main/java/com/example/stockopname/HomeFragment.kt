package com.example.stockopname

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.renderscript.ScriptGroup.Input
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var jsonData: JSONArray
    private lateinit var dataDao: DataDao
    private lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val btnPickDate = view.findViewById<Button>(R.id.btnPickDate)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val etKodebarang = view.findViewById<EditText>(R.id.etKodeBarang)
        val tvMerk = view.findViewById<TextView>(R.id.tvMerk)
        val tvNama = view.findViewById<TextView>(R.id.tvNama)
        val tvKemasan = view.findViewById<TextView>(R.id.tvKemasan)
        val etStokOpname = view.findViewById<EditText>(R.id.etStokOpname)
        val jsonString = loadJsonFromAsset(requireContext(), "master_barang.json")
        db = AppDatabase.build(requireContext())
        jsonData = JSONArray(jsonString)
        val btSubmit = view.findViewById<Button>(R.id.btSubmit)

        btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this.requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    tvDate.text =
                        (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        etKodebarang.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val kodeBarang = etKodebarang.text.toString()
                for (i in 0 until jsonData.length()) {
                    val kodeBarangJson = jsonData.getJSONObject(i).getString("ITEM")
                    if (kodeBarang == kodeBarangJson) {
                        val namaBarang = jsonData.getJSONObject(i).getString("NAMA")
                        tvNama.text = namaBarang
                        tvMerk.text = jsonData.getJSONObject(i).getString("MERK")
                        tvKemasan.text = jsonData.getJSONObject(i).getString("KMSN")
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
                tvNama.text = ""
                tvMerk.text = ""
                tvKemasan.text = ""
            }
        })
        btSubmit.setOnClickListener {
            if (tvDate.text.toString() == "" || etKodebarang.text.toString() == ""
                ||  tvNama.text.toString() == "" || tvMerk.text.toString() == ""
                || tvKemasan.text.toString() == "" || etStokOpname.text.toString() == "") {
                Toast.makeText(requireContext(), "Field tidak boleh kosong", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val dataEntity = DataEntity(
                id = 0,
                tvDate.text.toString(),
                etKodebarang.text.toString(),
                tvMerk.text.toString(),
                tvNama.text.toString(),
                etStokOpname.text.toString().toInt(),
                tvKemasan.text.toString()
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.dataDao.insert(dataEntity)
                withContext(Dispatchers.Main) {
                    tvDate.text = ""
                    etKodebarang.text.clear()
                    tvMerk.text = ""
                    tvNama.text = ""
                    etStokOpname.text.clear()
                    tvKemasan.text = ""
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Data berhasil disimpan", Toast.LENGTH_LONG).show()
                }
            }
        }


        return view

    }
    private fun loadJsonFromAsset(context: Context, fileName: String): String {
        var json: String? = null
        try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json ?: ""
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}