package dk.itu.moapd.scootersharing.jonli.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.scootersharing.jonli.models.Account
import kotlinx.coroutines.launch

class BalanceViewModel : BaseViewModel() {

    var balance = MutableLiveData<Double?>(null)

    init {
        viewModelScope.launch {
            getBalance()
        }
    }

    private fun getBalance() {
        auth.currentUser?.let { user ->
            val task = database.child("account")
                .child(user.uid)
                .get()

            task.addOnSuccessListener {
                it.getValue(Account::class.java)?.let { account ->
                    balance.value = account.balance
                }
            }

            task.addOnCanceledListener {
                createBalance()
            }
        }
    }

    private fun createBalance() {
        auth.currentUser?.let { user ->
            val uid = database.child("account")
                .child(user.uid)
                .push()
                .key

            uid?.let {
                database.child("account")
                    .child(user.uid)
                    .child(it)
                    .setValue(
                        Account(
                            0.0,
                        ),
                    )
            }
            getBalance()
        }
    }

    fun updateBalance(newAmount: Double) {
        val newBalance = balance.value?.plus(newAmount)
        auth.currentUser?.let {
            database.child("account")
                .child(it.uid)
                .setValue(
                    Account(
                        newBalance ?: 0.0,
                    ),
                )
        }
    }
}

class BalanceViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BalanceViewModel::class.java)) {
            return BalanceViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
