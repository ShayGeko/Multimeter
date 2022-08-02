package com.untitled.multimeter.data.model

import io.realm.kotlin.notifications.ListChange
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.flow.Flow

class RealmListDouble(input: ArrayList<Double>) : RealmList<Double> {
    private val data = input

    override val size: Int
        get() = data.size

    override fun contains(element: Double): Boolean {
        return data.contains(element)
    }

    override fun containsAll(elements: Collection<Double>): Boolean {
        return data.containsAll(elements)
    }

    override fun get(index: Int): Double {
        return data[index]
    }

    override fun indexOf(element: Double): Int {
        return data.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun iterator(): MutableIterator<Double> {
        return data.iterator()
    }

    override fun lastIndexOf(element: Double): Int {
        return data.lastIndexOf(element)
    }

    override fun add(element: Double): Boolean {
        return data.add(element)
    }

    override fun add(index: Int, element: Double) {
        return data.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<Double>): Boolean {
        return data.addAll(index, elements)
    }

    override fun addAll(elements: Collection<Double>): Boolean {
        return data.addAll(elements)
    }

    override fun clear() {
        return data.clear()
    }

    override fun listIterator(): MutableListIterator<Double> {
        return data.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<Double> {
        return data.listIterator(index)
    }

    override fun remove(element: Double): Boolean {
        return data.remove(element)
    }

    override fun removeAll(elements: Collection<Double>): Boolean {
        return data.removeAll(elements)
    }

    override fun removeAt(index: Int): Double {
        return data.removeAt(index)
    }

    override fun retainAll(elements: Collection<Double>): Boolean {
        return data.retainAll(elements.toSet())
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Double> {
        return data.subList(fromIndex, toIndex)
    }

    override fun asFlow(): Flow<ListChange<Double>> {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: Double): Double {
        return data.set(index, element)
    }

    fun toArrayList(): ArrayList<Double> {
        return data
    }
}