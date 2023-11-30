package com.example.stockopname

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.stockopname.databinding.ActivityInsertBinding
import com.example.stockopname.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter

class InsertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInsertBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        Toast.makeText(this, "tes", Toast.LENGTH_SHORT).show()
        binding.btInsert.setOnClickListener {
            if(binding.etInsertKodeBarang.text.toString().isEmpty() || binding.etInsertMerk.text.toString().isEmpty() || binding.etInsertNama.text.toString().isEmpty() || binding.etInsertKemasan.text.toString().isEmpty()){
                Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                appendDataToJsonFile(binding.etInsertKodeBarang.text.toString(),
                    binding.etInsertMerk.text.toString(),
                    binding.etInsertNama.text.toString(),
                    binding.etInsertKemasan.text.toString())
                Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                Toast.makeText(this, "Data gagal ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun appendDataToJsonFile(KodeBarang: String, Merk : String, Nama : String, Kemasan : String) {
        GlobalScope.launch(Dispatchers.IO) {
            // File name in assets
            val assetsFileName = "master_barang.json"

            // Copy the file from assets to internal storage
            val internalFile = File(filesDir, "master_barang.json")
            copyFileFromAssets(assetsFileName, internalFile)

            // Load existing JSON data from internal storage
            val existingJsonArray = loadJsonArrayFromFile(internalFile)

            // Append user input to the existing JSON array
            val newItemObject = JSONObject().apply {
                put("ITEM", KodeBarang)
                put("MERK", Merk)
                put("NAMA", Nama) // Assuming you want to use userInput as NAMA
                put("KMSN", Kemasan)
            }
            existingJsonArray.put(newItemObject)

            // Save the updated JSON array back to the internal file
            saveJsonArrayToFile(internalFile, existingJsonArray)
        }
    }
    private fun copyFileFromAssets(assetsFileName: String, destinationFile: File) {
        try {
            val inputStream: InputStream = assets.open(assetsFileName)
            val outputStream = FileOutputStream(destinationFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadJsonArrayFromFile(file: File): JSONArray {
        return try {
            val inputStream = file.inputStream()
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            JSONArray(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
            JSONArray()
        }
    }

    private fun saveJsonArrayToFile(file: File, jsonArray: JSONArray) {
        try {
            val outputStream = FileOutputStream(file)
            val writer = OutputStreamWriter(outputStream)
            writer.write(jsonArray.toString())
            writer.flush()
            writer.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}