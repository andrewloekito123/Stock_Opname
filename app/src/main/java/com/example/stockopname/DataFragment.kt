package com.example.stockopname

import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DataFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var db: AppDatabase
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataAdapter: DataAdapter

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
        val view = inflater.inflate(R.layout.fragment_data, container, false)
        recyclerView = view.findViewById(R.id.rvData)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val btExport = view.findViewById<Button>(R.id.btExport)
        db = AppDatabase.build(requireContext())
        GlobalScope.launch(Dispatchers.Main) {
            val dataList = db.dataDao.fetch()
            dataAdapter = DataAdapter(dataList) { data ->
                onDeleteButtonClick(data.KodeBarang)
            }
            recyclerView.adapter = dataAdapter
            recyclerView.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
        }
        btExport.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    val dataList = db.dataDao.fetch()
                    val csv = StringBuilder()
                    csv.append("Tanggal,Kode Barang,Nama,Merk,Stok Opname,Kemasan\n")
                    for (dataEntity in dataList) {
                        csv.append("${dataEntity.Tanggal},${dataEntity.KodeBarang},${dataEntity.Nama},${dataEntity.Merk},${dataEntity.StokOpname},${dataEntity.Kemasan}\n")
                    }
                    val externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val currentTimeMillis = System.currentTimeMillis()
                    val fileName = "data_$currentTimeMillis.csv"
                    val internalFile = File(externalDir, fileName)
                    saveToFile(csv.toString(), internalFile)
                    db.dataDao.deleteAll()
                }
            }
        }
        return view
    }
    private fun onDeleteButtonClick(KodeBarang: String) {
        GlobalScope.launch(Dispatchers.IO) {
            db.dataDao.deleteByKodeBarang(KodeBarang)

            withContext(Dispatchers.Main) {
                dataAdapter.updateData(db.dataDao.fetch()) // Assuming fetch() gets fresh data
                Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun saveToFile(content: String, file: File) {
        withContext(Dispatchers.IO) {
            val outputStream: OutputStreamWriter
            try {
                outputStream = OutputStreamWriter(FileOutputStream(file))
                outputStream.write(content)
                outputStream.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Data berhasil diexport ke ${file.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DataFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}