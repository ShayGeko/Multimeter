package com.untitled.multimeter.data.model

import io.realm.kotlin.notifications.ListChange
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.flow.Flow

class RealmListString(input: ArrayList<String>) : RealmList<String> {
    private val data = input

    override val size: Int
        get() = data.size

    override fun contains(element: String): Boolean {
        return data.contains(element)
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        return data.containsAll(elements)
    }

    override fun get(index: Int): String {
        return data[index]
    }

    override fun indexOf(element: String): Int {
        return data.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun iterator(): MutableIterator<String> {
        return data.iterator()
    }

    override fun lastIndexOf(element: String): Int {
        return data.lastIndexOf(element)
    }

    override fun add(element: String): Boolean {
        return data.add(element)
    }

    override fun add(index: Int, element: String) {
        return data.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<String>): Boolean {
        return data.addAll(index, elements)
    }

    override fun addAll(elements: Collection<String>): Boolean {
        return data.addAll(elements)
    }

    override fun clear() {
        return data.clear()
    }

    override fun listIterator(): MutableListIterator<String> {
        return data.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<String> {
        return data.listIterator(index)
    }

    override fun remove(element: String): Boolean {
        return data.remove(element)
    }

    override fun removeAll(elements: Collection<String>): Boolean {
        return data.removeAll(elements)
    }

    override fun removeAt(index: Int): String {
        return data.removeAt(index)
    }

    override fun retainAll(elements: Collection<String>): Boolean {
        return data.retainAll(elements.toSet())
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<String> {
        return data.subList(fromIndex, toIndex)
    }

    override fun asFlow(): Flow<ListChange<String>> {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: String): String {
        return data.set(index, element)
    }
}