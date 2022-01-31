package com.opti.android.map

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_payment.view.*
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "PaymentFragment"
private const val ARG_PaymentData_ID = "PaymentData_id"

class PaymentFragment : Fragment() {

    private lateinit var PaymentData: PaymentData
    private lateinit var titlePayment: TextInputEditText
    private lateinit var dataEditField: TextInputEditText
    private lateinit var editValue: TextInputEditText
    private lateinit var editDebitCredit: MaterialAutoCompleteTextView
    private lateinit var dataField: TextInputLayout
    private lateinit var addButton: Button
    private lateinit var debitCredit: TextInputLayout
    private var isCredit = true
    private val PaymentDataDetailViewModel: PaymentDetailViewModel by lazy {
        ViewModelProviders.of(this).get(PaymentDetailViewModel::class.java)
    }
    private val paymentListViewModel: PaymentListViewModel by lazy {
        ViewModelProviders.of(this).get(PaymentListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentData = PaymentData()
        val PaymentDataId: UUID = arguments?.getSerializable(ARG_PaymentData_ID) as UUID
        PaymentDataDetailViewModel.loadPaymentData(PaymentDataId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        titlePayment = view.findViewById(R.id.titlePayment) as TextInputEditText
        dataField = view.findViewById(R.id.dataField) as TextInputLayout
        editValue = view.findViewById(R.id.editValue) as TextInputEditText
        dataEditField = view.findViewById(R.id.dataEditField) as TextInputEditText
        addButton = view.findViewById(R.id.addButton) as Button
        debitCredit = view.findViewById(R.id.debitCredit) as TextInputLayout
        editDebitCredit = view.findViewById(R.id.editDebitCredit) as MaterialAutoCompleteTextView
        val items = listOf("Приход", "Расход")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_plus_minus_item, items)
        debitCredit.editDebitCredit.setText("Расход")
        (debitCredit.editDebitCredit as? AutoCompleteTextView)?.setAdapter(adapter)

        return view
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        editText.setTextColor(Color.BLACK)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val PaymentDataId = arguments?.getSerializable(ARG_PaymentData_ID) as UUID
        PaymentDataDetailViewModel.loadPaymentData(PaymentDataId)
        PaymentDataDetailViewModel.PaymentDataLiveData.observe(
            viewLifecycleOwner,
            Observer { PaymentData ->
                PaymentData?.let {
                    this.PaymentData = PaymentData
                    updateUI()
                }
            })
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val currentDate = sdf.format(Date())
        dataEditField.setText(currentDate)
    }

    override fun onStart() {
        super.onStart()

        paymentListViewModel.PaymentDataListLiveData.observe(
            viewLifecycleOwner,
            Observer { PaymentDatas ->
                PaymentDatas?.let {
                    Log.i(TAG, "Got PaymentDataLiveData ${PaymentDatas.size}")
                }
            }
        )

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                PaymentData.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {

            }
        }
        titlePayment.addTextChangedListener(titleWatcher)

        val valueWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                PaymentData.value = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }
        editValue.addTextChangedListener(valueWatcher)

        debitCredit.editDebitCredit.setOnItemClickListener { _, view, position, id ->
            PaymentData.isCredit = position == 0
        }

        addButton.isEnabled = false
        val editTexts = listOf(titlePayment, editValue, dataEditField)
        for (editText in editTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val et1 = titlePayment.text.toString().trim()
                    val et2 = editValue.text.toString().trim()
                    val et3 = dataEditField.text.toString().trim()

                    addButton.isEnabled = et1.isNotEmpty()
                            && et2.isNotEmpty()
                            && et3.isNotEmpty()

                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {

                }

                override fun afterTextChanged(
                    s: Editable
                ) {

                }
            })
        }

        addButton.setOnClickListener {
            if (addButton.isEnabled) {
                paymentListViewModel.addPayment(PaymentData)
                view?.hideKeyboard()
                activity?.onBackPressed()
            }
        }
        disableEditText(dataEditField)
    }

    override fun onStop() {
        super.onStop()
        PaymentDataDetailViewModel.savePaymentData(PaymentData)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun updateUI() {
        titlePayment.setText(PaymentData.title)
        dataEditField.text = PaymentData.date.toString().toEditable()
        isCredit = PaymentData.isCredit
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    companion object {

        fun newInstance(PaymentDataId: UUID): PaymentFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PaymentData_ID, PaymentDataId)
            }
            return PaymentFragment().apply {
                arguments = args
            }
        }
    }
}