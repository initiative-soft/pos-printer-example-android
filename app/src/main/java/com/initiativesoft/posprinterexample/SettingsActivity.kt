package com.initiativesoft.posprinterexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.initiativesoft.posprinterlib.helper.FailureReason
import com.initiativesoft.posprinterlib.interfaces.DiscoverEvent
import com.initiativesoft.posprinterlib.printer.Printer
import com.initiativesoft.posprinterlib.printer.PrinterFactory
import com.initiativesoft.posprinterlib.printer.Vendor
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), DiscoverEvent {
    private lateinit var printer: Printer
    private lateinit var recycleView: RecyclerView
    private lateinit var printerListAdapter: PrinterListAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        layoutManager = LinearLayoutManager(this)
        printerListAdapter = PrinterListAdapter()
        recycleView = recycleView_printerList
        recycleView.apply {
            setHasFixedSize(true)
            layoutManager = this@SettingsActivity.layoutManager
            adapter = printerListAdapter
        }

        printer = PrinterFactory.createPrinterOfVendor(Vendor.SAM4S)
        printer.setOnDiscoverEvent(this)

        button_startDiscovery.setOnClickListener {
            printer.startDiscover(3)
        }

        button_stopDiscovery.setOnClickListener {
            printer.stopDiscover()
        }

        printerListAdapter.setButtonClickListener {
            val ip = (it.parent as ConstraintLayout).getTag(R.id.tag_ip) as String

            val intent = Intent()
            intent.putExtra(MainActivity.REQUEST_EXTRA_IP_KEY, ip)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onDiscover(arrayList: ArrayList<String>) {
        runOnUiThread {
            printerListAdapter.addIpFromList(arrayList)
        }
    }

    override fun onDiscoverFailed(failureReason: FailureReason) {
        val snackbar = Snackbar.make(constraintLayout_settings,
                "Discover failed with ${failureReason.name}",
                Snackbar.LENGTH_LONG)

        snackbar.show()
    }
}