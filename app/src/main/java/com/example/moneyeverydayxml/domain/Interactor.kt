package com.example.moneyeverydayxml.domain

class Interactor(
    private val repository: RepositoryInterface
) : InteractorInterface {
    override fun saveData(amount: String, date: String) {
        repository.saveData(amount, date)
    }

    override fun saveClearDate(clearDate: Long) {
        repository.saveClearDate(clearDate)
    }

    override fun loadData(){
        repository.loadData()
    }

    override fun getClearDate(): Long {
       return repository.getClearDate()
    }
}