package br.com.alex.taxi.androidapp.home.navigation

import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.alex.taxi.androidapp.R
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

/**
 * Created by alex on 07/06/16.
 */
class HomeFragment: Fragment() {

    val mMapFragment: SupportMapFragment by lazy { childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment }

    val mFab: FloatingActionButton by lazy { view?.findViewById(R.id.fab_order_taxi) as FloatingActionButton }

    var mListener: OnOrderTaxiFabClickListener? = null

    var mOnMapListener: OnMapListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.fragment_home, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMapFragment.getMapAsync(mOnMapListener)
        mFab.setOnClickListener { mListener?.onOrderRideClicked() }

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu_home)
        }
    }

    override fun onDestroy() {
        mOnMapListener?.onMapReleased()
        super.onDestroy()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnOrderTaxiFabClickListener) {
            mListener = context
        }

        if (context is OnMapListener) {
            mOnMapListener = context
        }
    }

    interface OnOrderTaxiFabClickListener {
        fun onOrderRideClicked()
    }

    interface OnMapListener: OnMapReadyCallback {
        fun onMapReleased()
    }
}