package br.com.alex.taxi.androidapp.welcome

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import br.com.alex.taxi.androidapp.R
import br.com.alex.taxi.model.User


/**
 * Created by alex on 06/06/16.
 */
class RegistrationFragment : Fragment(), TextWatcher {

    companion object {
        private val EXTRA_USER = "user"
    }

    private val mEtUsername: EditText by lazy { view?.findViewById(R.id.et_username) as EditText}

    private var mUserValidationListener: UserValidationListener? = null

    private var mUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUser = savedInstanceState?.getParcelable(EXTRA_USER) ?: User(-1L, "")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater!!.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mEtUsername.addTextChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        validateUser()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(EXTRA_USER, mUser)
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is UserValidationListener) {
            mUserValidationListener = context
        }
    }

    override fun onDetach() {
        mUserValidationListener = null
        super.onDetach()
    }

    override fun afterTextChanged(text: Editable?) {
        mUser?.name = text.toString()
        validateUser()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    private fun validateUser() {
        if (mUser?.isValid() ?: false) {
            mUserValidationListener?.onUserValid(mUser!!)
        } else {
            mUserValidationListener?.onUserInvalid()
        }
    }
}