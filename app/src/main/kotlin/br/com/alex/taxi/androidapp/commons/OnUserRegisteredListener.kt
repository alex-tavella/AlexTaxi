package br.com.alex.taxi.androidapp.commons

import br.com.alex.taxi.model.User

/**
 * Created by alex on 15/06/16.
 */
interface OnUserRegisteredListener {
    fun onUserRegistered(user: User)
}