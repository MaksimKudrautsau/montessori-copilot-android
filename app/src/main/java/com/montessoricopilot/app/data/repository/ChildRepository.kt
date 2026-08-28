package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.user.ChildEntity
import com.montessoricopilot.app.data.user.UserDao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ChildRepository(private val userDao: UserDao) {

    fun observeChildren(): Flow<List<ChildEntity>> = userDao.observeChildren()

    suspend fun getChild(childId: Int): ChildEntity? = userDao.getChild(childId)

    suspend fun addChild(name: String, birthDate: LocalDate): Int =
        userDao.insertChild(ChildEntity(name = name, birthDateEpochDay = birthDate.toEpochDay())).toInt()

    suspend fun deleteChild(child: ChildEntity) = userDao.deleteChild(child)
}
