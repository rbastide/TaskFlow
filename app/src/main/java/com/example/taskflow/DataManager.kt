package com.example.taskflow

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("TaskFlowPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()


    fun saveTasks(tasks: List<Task>) {
        val json = gson.toJson(tasks)
        sharedPreferences.edit().putString("TASKS_LIST", json).apply()
    }

    fun getTasks(): List<Task> {
        val json = sharedPreferences.getString("TASKS_LIST", null)
        return if (json != null) {
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }


    fun saveFlowCoins(coins: Int) {
        sharedPreferences.edit().putInt("FLOW_COINS", coins).apply()
    }

    fun getFlowCoins(): Int {
        return sharedPreferences.getInt("FLOW_COINS", 50)
    }
}