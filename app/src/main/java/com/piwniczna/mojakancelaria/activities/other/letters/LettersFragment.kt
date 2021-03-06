package com.piwniczna.mojakancelaria.activities.other.letters

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.piwniczna.mojakancelaria.DB.DataService
import com.piwniczna.mojakancelaria.R
import com.piwniczna.mojakancelaria.activities.clients.clients_list.ClientsFragment
import com.piwniczna.mojakancelaria.models.*
import com.piwniczna.mojakancelaria.trackingmore.APIService
import com.piwniczna.mojakancelaria.utils.SpannedText
import java.lang.Exception
import java.net.ConnectException
import kotlin.collections.ArrayList


class LettersFragment(var outgoing: Boolean)  : Fragment() {
    lateinit var lettersListView: ListView
    lateinit var title: TextView
    lateinit var lettersList: ArrayList<Letter>
    lateinit var lettersListAdapter: LettersListAdapter
    lateinit var dbService: DataService
    lateinit var letterEntityList: ArrayList<LetterEntity>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_letters, container, false)
        dbService = DataService(this.context!!)

        lettersListView = view.findViewById(R.id.letters_list_view) as ListView

        lettersList = arrayListOf()
        lettersListAdapter = LettersListAdapter(this.context!!, lettersList)
        lettersListView.adapter = lettersListAdapter

        lettersListView.setOnItemLongClickListener { _, _, position, _ ->
            Log.e(letterEntityList[position].number," - delete")

            val builder = AlertDialog.Builder(this.context)
            val letterNum = letterEntityList[position].number
            val message =
                SpannedText.getSpannedText(getString(R.string.delete_letter, letterNum))

            builder.setTitle(R.string.warning)
            builder.setMessage(message)

            builder.setPositiveButton(getString(R.string.delete)) { dialog, which ->
                deleteLetterFromDB(letterEntityList[position])

            }

            builder.setNegativeButton(R.string.cancel) { dialog, which -> }

            builder.show()

            true
        }

        val title = view.findViewById<TextView>(R.id.letters_title)
        if(outgoing){
            title.text=getString(R.string.sent)
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sent, 0, 0, 0)
        }
        else{
            title.text=getString(R.string.awizo)
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_awizo, 0, 0, 0)
        }

        toastMessage(getString(R.string.loading_data))
        getLettersFromDB()

        return view
    }

    private fun toastMessage(message: String) {
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(context, message, duration)
        toast.show()
    }

    fun onBackPressed() {
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            ClientsFragment()
        )?.commit()
    }

    fun deleteLetterFromDB(letter: LetterEntity){
        AsyncTask.execute {
            dbService.deleteLetter(letter)
            Thread.sleep(2000)
            activity!!.runOnUiThread{
                lettersList.clear()
                lettersListAdapter.notifyDataSetChanged()
                getLettersFromDB()
            }
        }
    }

    fun getLettersFromDB() {
        AsyncTask.execute {
            val rawLetters = dbService.getLetters().filter { it.outgoing == outgoing }
            letterEntityList = ArrayList(rawLetters.reversed())
            val numbers = rawLetters.map { it -> it.number }
            try {
                lettersList.clear()

                for (n in numbers.reversed()){
                    Log.e("Loading ","-$n")
                    val letter = APIService.getLetter(n)
                    lettersList.add(letter)
                    activity?.runOnUiThread {
                        lettersListAdapter.notifyDataSetChanged()
                    }
                }

                Thread.sleep(600)
                activity?.runOnUiThread {
                    toastMessage(getString(R.string.loaded))
                }
            }
            catch (e: ConnectException){
                e.printStackTrace()
                Thread.sleep(500)
                activity?.runOnUiThread {
                    toastMessage(getString(R.string.connection_error))
                }
            }
            catch (e: Exception){
                e.printStackTrace()
                Thread.sleep(500)
                activity?.runOnUiThread {
                    toastMessage(getString(R.string.download_error))
                }
            }
        }
    }




}
