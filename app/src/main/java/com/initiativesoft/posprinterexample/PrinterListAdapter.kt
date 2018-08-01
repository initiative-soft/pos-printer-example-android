package com.initiativesoft.posprinterexample

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.adapter_printer_list.view.*

typealias OnButtonClick = (v: View) -> Unit
class PrinterListAdapter:
        RecyclerView.Adapter<PrinterListAdapter.ViewHolder>()
{
    private val printerIpList = ArrayList<String>()
    private var buttonOnClickHandler: OnButtonClick? = null

    fun addIpFromList(ipList: ArrayList<String>) {
        printerIpList.addAll(ipList)

        notifyDataSetChanged()
    }

    fun setButtonClickListener(l : OnButtonClick) {
        buttonOnClickHandler = l
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_printer_list, parent, false) as ConstraintLayout
        view.button_select.setOnClickListener(_onButtonClickHandler)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return printerIpList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ip = printerIpList[position]
        holder.constraintLayout.textView_ip.text = ip
        holder.constraintLayout.setTag(R.id.tag_ip, ip)
    }

    private val _onButtonClickHandler: OnButtonClick = {
        buttonOnClickHandler?.invoke(it)
    }


    class ViewHolder(val constraintLayout: ConstraintLayout):
            RecyclerView.ViewHolder(constraintLayout)
}

