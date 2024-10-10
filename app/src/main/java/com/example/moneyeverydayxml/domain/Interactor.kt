package com.example.moneyeverydayxml.domain

class Interactor(
    private val repository: RepositoryInterface
) : InteractorInterface {
    override fun saveData(amount: String, date: String, summary: Int) {
        repository.saveData(amount, date, summary)
    }

    override fun saveClearDate(clearDate: Long) {
        repository.saveClearDate(clearDate)
    }

    override fun loadData() {
        repository.loadData()
    }

    override fun getClearDate(): Long {
        return repository.getClearDate()
    }

    override fun getDatesList(): List<String> {
        return repository.getDatesList()
    }

    override fun getDatesFromMemory(): String {
        TODO("Not yet implemented")
    }

    override fun getSumFromMemory(): String {
        return repository.getSumFromMemory()
    }
}