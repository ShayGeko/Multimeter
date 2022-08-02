package com.untitled.multimeter.data.model

import io.realm.kotlin.notifications.ListChange
import io.realm.kotlin.types.ObjectId
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.flow.Flow

class RealmListObjectId(input: ArrayList<ObjectId>) : RealmList<ObjectId> {
    private val data = input

    override val size: Int
        get() = data.size

    override fun contains(element: ObjectId): Boolean {
        return data.contains(element)
    }

    override fun containsAll(elements: Collection<ObjectId>): Boolean {
        return data.containsAll(elements)
    }

    override fun get(index: Int): ObjectId {
        return data[index]
    }

    override fun indexOf(element: ObjectId): Int {
        return data.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    override fun iterator(): MutableIterator<ObjectId> {
        return data.iterator()
    }

    override fun lastIndexOf(element: ObjectId): Int {
        return data.lastIndexOf(element)
    }

    override fun add(element: ObjectId): Boolean {
        return data.add(element)
    }

    override fun add(index: Int, element: ObjectId) {
        return data.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<ObjectId>): Boolean {
        return data.addAll(index, elements)
    }

    override fun addAll(elements: Collection<ObjectId>): Boolean {
        return data.addAll(elements)
    }

    override fun clear() {
        return data.clear()
    }

    override fun listIterator(): MutableListIterator<ObjectId> {
        return data.listIterator()
    }

    override fun listIterator(index: Int): MutableListIterator<ObjectId> {
        return data.listIterator(index)
    }

    override fun remove(element: ObjectId): Boolean {
        return data.remove(element)
    }

    override fun removeAll(elements: Collection<ObjectId>): Boolean {
        return data.removeAll(elements)
    }

    override fun removeAt(index: Int): ObjectId {
        return data.removeAt(index)
    }

    override fun retainAll(elements: Collection<ObjectId>): Boolean {
        return data.retainAll(elements.toSet())
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<ObjectId> {
        return data.subList(fromIndex, toIndex)
    }

    override fun asFlow(): Flow<ListChange<ObjectId>> {
        TODO("Not yet implemented")
    }

    override fun set(index: Int, element: ObjectId): ObjectId {
        return data.set(index, element)
    }

    fun toArrayList(): ArrayList<ObjectId> {
        return data
    }
}