package com.example.stockopname

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class DataAdapter(private var dataList: List<DataEntity>, private val onDeleteClickListener: (DataEntity) -> Unit) :
    RecyclerView.Adapter<DataAdapter.DataViewHolder>() {
    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    fun updateData(newDataList: List<DataEntity>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return DataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val currentItem = dataList[position]

        holder.itemView.findViewById<TextView>(R.id.tvRvDate).text = currentItem.Tanggal
        holder.itemView.findViewById<TextView>(R.id.tvRvKode).text = currentItem.KodeBarang
        holder.itemView.findViewById<TextView>(R.id.tvRvMerk).text = currentItem.Merk
        holder.itemView.findViewById<TextView>(R.id.tvRvNama).text = currentItem.Nama
        holder.itemView.findViewById<TextView>(R.id.tvRvStok).text = currentItem.StokOpname.toString()
        holder.itemView.findViewById<TextView>(R.id.tvRvKemasan).text = currentItem.Kemasan
        holder.itemView.findViewById<Button>(R.id.btRvDelete).setOnClickListener {
            onDeleteClickListener(currentItem)
        }

    }

    override fun getItemCount() = dataList.size
}
