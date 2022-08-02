package com.untitled.multimeter.createaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untitled.multimeter.data.model.CreateAccountModel
import com.untitled.multimeter.data.model.UserInfo
import com.untitled.multimeter.data.source.UserRepository
import io.realm.kotlin.mongodb.User

class CreateAccountViewModel(private val repository: UserRepository) : ViewModel() {

    /**
     * Attempts to register the user with the specified email and password
     *
     * @param email email
     * @param password password
     *
     * @return
     * LiveData of acquired User wrapped in Result class on success,
     * and error wrapped in Result otherwise
     */
    fun registerUser(createAccountModel: CreateAccountModel) : LiveData<Result<Unit>> {
        return repository.register(createAccountModel) as MutableLiveData<Result<Unit>>
    }
}