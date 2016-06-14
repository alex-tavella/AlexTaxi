package br.com.alex.taxi.androidapp.commons.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import br.com.alex.taxi.androidapp.R

/**
 * Created by alex on 07/06/16.
 */
class AlertDialogFragment : DialogFragment() {

    companion object {

        val TAG_ERROR_DIALOG = "error"

        val TAG_SUBMIT_DIALOG = "submit"

        private val EXTRA_TITLE = "title"

        private val EXTRA_MESSAGE = "message"

        private val EXTRA_KIND = "kind"
    }

    private var mTitle: String? = null

    private var mMessage: String? = null

    private var mKind: Kind? = null

    private var mOnSubmitListener: OnSubmitListener? = null

    enum class Kind {
        Error, Submit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = arguments
        if (arguments != null) {
            mTitle = arguments.getString(EXTRA_TITLE, null)
            mMessage = arguments.getString(EXTRA_MESSAGE, null)
            mKind = Kind.values()[arguments.getInt(EXTRA_KIND)]
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity
        val builder = AlertDialog.Builder(activity)

        if (mTitle != null && !mTitle!!.isEmpty()) {
            builder.setTitle(mTitle)
        }

        if (mMessage != null && !mMessage!!.isEmpty()) {
            builder.setMessage(mMessage)
        }

        builder.setPositiveButton(android.R.string.ok,
                { dialogInterface, i -> when(mKind) {
                    Kind.Error -> {mOnSubmitListener?.onDialogOk()}
                    Kind.Submit -> {mOnSubmitListener?.onDialogSubmit()}
                } })

        if (mKind == Kind.Submit) {
            builder.setNegativeButton(android.R.string.cancel,
                    { dialogInterface, i -> mOnSubmitListener?.onDialogCancel() })
        }

        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnSubmitListener) {
            mOnSubmitListener = context
        }
    }

    object Factory {

        fun createErrorDialog(context: Context,
                              errorMessage: String =
                              context.getString(R.string.dialog_content_unexpected_error)): AlertDialogFragment {
            val dialogFragment = AlertDialogFragment()
            val args = Bundle()

            val title = context.getString(R.string.dialog_title_unexpected_error)

            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_MESSAGE, errorMessage)
            args.putInt(EXTRA_KIND, Kind.Error.ordinal)
            dialogFragment.arguments = args
            return dialogFragment
        }

        fun createSubmitDialog(context: Context, title: String, message: String): AlertDialogFragment {
            val dialogFragment = AlertDialogFragment()
            val args = Bundle()

            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_MESSAGE, message)
            args.putInt(EXTRA_KIND, Kind.Submit.ordinal)
            dialogFragment.arguments = args
            return dialogFragment
        }
    }

    interface OnSubmitListener {
        fun onDialogOk()
        fun onDialogSubmit()
        fun onDialogCancel()
    }
}

fun AppCompatActivity.displayErrorMessage(errorMessage: String) {
    val currentDisplayingErrorDialog = supportFragmentManager
            .findFragmentByTag(AlertDialogFragment.TAG_ERROR_DIALOG)
    if (currentDisplayingErrorDialog == null) {
        AlertDialogFragment.Factory.createErrorDialog(this, errorMessage)
                .show(supportFragmentManager, AlertDialogFragment.TAG_ERROR_DIALOG)
    }
}

fun Fragment.displayErrorMessage(errorMessage: String) {
    val currentDisplayingErrorDialog = childFragmentManager
            .findFragmentByTag(AlertDialogFragment.TAG_ERROR_DIALOG)
    if (currentDisplayingErrorDialog == null) {
        AlertDialogFragment.Factory.createErrorDialog(context, errorMessage)
                .show(childFragmentManager, AlertDialogFragment.TAG_ERROR_DIALOG)
    }
}