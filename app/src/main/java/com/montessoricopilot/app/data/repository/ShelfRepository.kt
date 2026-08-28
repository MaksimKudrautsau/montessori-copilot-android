package com.montessoricopilot.app.data.repository

import com.montessoricopilot.app.data.user.ShelfItemEntity
import com.montessoricopilot.app.data.user.UserDao
import com.montessoricopilot.app.logic.PythonBridge
import com.montessoricopilot.app.logic.RotationRequest
import com.montessoricopilot.app.logic.ShelfItemForPython
import com.montessoricopilot.app.logic.ShelfRotationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ShelfRepository(private val userDao: UserDao) {

    fun observeForChild(childId: Int): Flow<List<ShelfItemEntity>> =
        userDao.observeShelfForChild(childId)

    suspend fun addItem(childId: Int, activityId: Int?, customTitle: String?, active: Boolean) {
        userDao.insertShelfItem(
            ShelfItemEntity(
                childId = childId,
                activityId = activityId,
                customTitle = customTitle,
                status = if (active) "active" else "storage",
                datePlacedEpochDay = LocalDate.now().toEpochDay(),
            )
        )
    }

    suspend fun moveToStorage(item: ShelfItemEntity) =
        userDao.updateShelfItem(item.copy(status = "storage"))

    suspend fun moveToActiveShelf(item: ShelfItemEntity) =
        userDao.updateShelfItem(
            item.copy(status = "active", datePlacedEpochDay = LocalDate.now().toEpochDay())
        )

    /** Runs the (Python, rule-based — no AI) rotation-due check for every
     *  shelf item this child currently has. See logic/rotation.py.
     *  `suspend` + withContext(Default): PythonBridge is a blocking
     *  native (Chaquopy/JNI) call and must never run on the caller's
     *  dispatcher (Main, for a ViewModel's viewModelScope). */
    suspend fun rotationStatus(items: List<ShelfItemEntity>): List<ShelfRotationResult> {
        if (items.isEmpty()) return emptyList()
        val request = RotationRequest(
            shelfItems = items.map { ShelfItemForPython(it.id, it.status, it.datePlacedEpochDay) },
            todayEpochDay = LocalDate.now().toEpochDay(),
        )
        return withContext(Dispatchers.Default) { PythonBridge.rotationStatus(request) }
    }
}
