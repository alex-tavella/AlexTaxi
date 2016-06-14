package br.com.alex.taxi.androidapp.welcome

import br.com.alex.taxi.model.User

/**
 * Created by alex on 06/06/16.
 */
interface UserValidationListener {
    fun onUserValid(user: User)
    fun onUserInvalid()
}