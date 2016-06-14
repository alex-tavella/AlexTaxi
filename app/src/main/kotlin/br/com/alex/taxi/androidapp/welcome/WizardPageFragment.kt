package br.com.alex.taxi.androidapp.welcome

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.alex.taxi.androidapp.R


/**
 * Created by alex on 05/06/16.
 */
class WizardPageFragment : Fragment() {

    companion object {

        private val EXTRA_IMAGE = "image"

        private val EXTRA_TITLE = "title"

        private val EXTRA_DETAIL = "detail"

        fun newInstance(pageResource: WizardPageResource): WizardPageFragment {
            val wizardPageFragment = WizardPageFragment()
            val params = Bundle()
            params.putInt(EXTRA_IMAGE, pageResource.iconId)
            params.putInt(EXTRA_TITLE, pageResource.titleTextId)
            params.putInt(EXTRA_DETAIL, pageResource.detailTextId)
            wizardPageFragment.arguments = params
            return wizardPageFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_wizard, container, false)

        val imageView = rootView.findViewById(R.id.iv_wizard_img) as ImageView
        imageView.setImageResource(arguments.getInt(EXTRA_IMAGE))

        val titleTextView = rootView.findViewById(R.id.tv_wizard_title) as TextView
        titleTextView.setText(arguments.getInt(EXTRA_TITLE))

        val detailTextView = rootView.findViewById(R.id.tv_wizard_detail) as TextView
        detailTextView.setText(arguments.getInt(EXTRA_DETAIL))

        return rootView
    }
}