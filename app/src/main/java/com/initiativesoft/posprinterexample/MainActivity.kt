package com.initiativesoft.posprinterexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.view.View
import android.widget.CheckedTextView
import android.widget.RadioButton
import com.initiativesoft.posprinterlib.interfaces.ConnectionEvent
import com.initiativesoft.posprinterlib.printer.*
import com.initiativesoft.posprinterlib.text.TextAlignment
import com.initiativesoft.posprinterlib.text.TextStyle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ConnectionEvent {
    lateinit var printer: Printer
    lateinit var commandBlock: CommandBlock
    lateinit var textFormat: TextFormat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_settings_white_36dp)

        printer = PrinterFactory.createPrinterOfVendor(Vendor.SAM4S)
        printer.setOnConnectionEvent(this)
        commandBlock = printer.createCommandBlock()
        textFormat = printer.createTextFormat()

        button_addText.setOnClickListener {
            commandBlock.addText(textInputEditText_textToPrint.text.toString())
        }

        button_addLineFeed.setOnClickListener {
            val amount = textInputEditText_lineFeedAmount.text
                    .toString().toIntOrNull() ?: 1

            commandBlock.addLineFeed(amount)
        }

        button_addCut.setOnClickListener {
            commandBlock.addCut(true)
        }

        val checkedLambda: (it: View) -> Unit = {
            (it as CheckedTextView).toggle()
        }

        checkedTextView_bold.setOnClickListener(checkedLambda)
        checkedTextView_underline.setOnClickListener(checkedLambda)
        checkedTextView_reversed.setOnClickListener(checkedLambda)


        val radioArray = arrayOf<RadioButton>(radioButton_alignLeft,
        radioButton_alignCenter, radioButton_alignRight)
        button_send.setOnClickListener {
            textFormat.setStyle(TextStyle.Bold, checkedTextView_bold.isChecked)
            textFormat.setStyle(TextStyle.Underline, checkedTextView_underline.isChecked)
            textFormat.setStyle(TextStyle.Reverse, checkedTextView_reversed.isChecked)

            var alignment = TextAlignment.Left
            for(radioButton in radioArray) {
                if(!radioButton.isChecked) {
                    continue
                }

                val id = radioButton.id
                alignment = when(id) {
                    R.id.radioButton_alignCenter -> TextAlignment.Center
                    R.id.radioButton_alignRight -> TextAlignment.Right
                    else -> TextAlignment.Left
                }

                break
            }

            textFormat.setAlignment(alignment)
            printer.queueCommandBlock(commandBlock)

            commandBlock = printer.createCommandBlock()
            textFormat = printer.createTextFormat()
            commandBlock.textFormat = textFormat
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data ?: return

        val ip = data.getStringExtra(REQUEST_EXTRA_IP_KEY)
        printer.connectAsync(ip)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                //You can do whatever you want here
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE_SELECT_IP)
                return true
            }
        }
        return false
    }

    override fun onConnect(ipAddress: String) {
        Snackbar.make(constraintLayout_main, "Connected to $ipAddress",
                Snackbar.LENGTH_SHORT).show()
    }

    override fun onDisconnect(ipAddress: String) {
        Snackbar.make(constraintLayout_main, "Disconnected from $ipAddress",
                Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val REQUEST_CODE_SELECT_IP = 0x100
        const val REQUEST_EXTRA_IP_KEY = "REQUEST_EXTRA_IP_KEY"
    }
}
