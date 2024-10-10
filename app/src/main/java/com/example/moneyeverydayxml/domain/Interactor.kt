package com.example.moneyeverydayxml.domain

class Interactor(
    private val repository: RepositoryInterface
) : InteractorInterface {
    override fun saveData(amount: String, date: String, clearDate: Long) {
        repository.saveData(amount, date, clearDate)
    }

    override fun getData():Long {
        return repository.getData()
    }
}