package com.opti.android.map

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "PaymentDataListFragment"
private const val INPUT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy"
private const val OUTPUT_DATE_FORMAT = "dd MMM HH:mm"

class PaymentListFragment : Fragment() {

    private lateinit var paymentDataRecyclerView: RecyclerView
    private var adapter: PaymentDataAdapter = PaymentDataAdapter(emptyList())
    private val paymentListViewModel: PaymentListViewModel by lazy {
        ViewModelProviders.of(this).get(PaymentListViewModel::class.java)
    }
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onPaymentDataSelected(PaymentDataId: UUID)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as? Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment_list, container, false)
        paymentDataRecyclerView =
            view.findViewById(R.id.PaymentData_recycler_view) as RecyclerView
        paymentDataRecyclerView.layoutManager = LinearLayoutManager(context)
        paymentDataRecyclerView.adapter = adapter

        val dealTitle = view.findViewById<TextView>(R.id.deal_title)
        dealTitle.setOnClickListener {
            val PaymentData = PaymentData()
            callbacks?.onPaymentDataSelected(PaymentData.id)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        paymentListViewModel.PaymentDataListLiveData.observe(
            viewLifecycleOwner,
            Observer { PaymentDatas ->
                PaymentDatas?.let {
                    Log.i(TAG, "Got paymentLiveData ${PaymentDatas.size}")
                    updateUI(PaymentDatas)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(PaymentDatas: List<PaymentData>) {
        adapter.PaymentDatas = PaymentDatas
        paymentDataRecyclerView.adapter = adapter
    }

    private inner class PaymentDataHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var PaymentData: PaymentData

        private val titleTextView: TextView = itemView.findViewById(R.id.payment_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.data_value)
        private val valueTextView: TextView = itemView.findViewById(R.id.payment_value)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(PaymentData: PaymentData) {
            this.PaymentData = PaymentData
            titleTextView.text = this.PaymentData.title
            dateTextView.text = getData(this.PaymentData.date)
            valueTextView.setTextColor(Color.RED)
            if (PaymentData.isCredit) {
                valueTextView.setTextColor(0xffadcb00)
                valueTextView.text = "+" + this.PaymentData.value + " " + "₴"
            } else {
                valueTextView.setTextColor(0xffe02b2b)
                valueTextView.text = "-" + this.PaymentData.value + " " + "₴"
            }
        }

        override fun onClick(v: View) {
//            callbacks?.onPaymentDataSelected(PaymentData.id)
        }
    }

    fun TextView.setTextColor(color: Long) = this.setTextColor(color.toInt())

    private fun getData(data: Date): String {
        val df = DateTimeFormatter.ofPattern(INPUT_DATE_FORMAT)
        val date = LocalDateTime.parse(data.toString(), df)
        val formatter = DateTimeFormatter.ofPattern(OUTPUT_DATE_FORMAT)
        return date.format(formatter)
    }

    private inner class PaymentDataAdapter(var PaymentDatas: List<PaymentData>) :
        RecyclerView.Adapter<PaymentDataHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : PaymentDataHolder {
            val layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater.inflate(R.layout.list_item_payment, parent, false)
            return PaymentDataHolder(view)
        }

        override fun onBindViewHolder(holder: PaymentDataHolder, position: Int) {
            val PaymentData = PaymentDatas[position]
            holder.bind(PaymentData)
        }

        override fun getItemCount() = PaymentDatas.size
    }

    companion object {
        fun newInstance(): PaymentListFragment {
            return PaymentListFragment()
        }
    }
}