package com.gunshot.detector.features

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EmergencyContactManager(context: Context) {
    companion object { private const val PREFS_NAME = "emergency_contacts_prefs"; private const val KEY_CONTACTS = "contacts_set" }
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _contacts = MutableStateFlow<List<String>>(loadContacts())
    val contacts: StateFlow<List<String>> = _contacts
    private fun loadContacts(): List<String> = prefs.getStringSet(KEY_CONTACTS, emptySet())?.toList()?.sorted() ?: emptyList()
    private fun saveContacts(contacts: Set<String>) = prefs.edit().putStringSet(KEY_CONTACTS, contacts).apply()
    fun addContact(phoneNumber: String): Boolean { val cleaned = phoneNumber.trim().replace(Regex("[^+\\d]"), ""); if (cleaned.length < 4) return false; val current = _contacts.value.toMutableSet(); if (!current.add(cleaned)) return false; saveContacts(current); _contacts.value = current.toList().sorted(); return true }
    fun removeContact(phoneNumber: String) { val current = _contacts.value.toMutableSet(); current.remove(phoneNumber); saveContacts(current); _contacts.value = current.toList().sorted() }
    fun getContactsList(): List<String> = _contacts.value
}