package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Student Profile
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: StudentProfile)

    @Query("SELECT * FROM student_profile WHERE email = :email LIMIT 1")
    suspend fun getProfileByEmail(email: String): StudentProfile?

    @Query("SELECT * FROM student_profile WHERE isCurrentlyLoggedIn = 1 LIMIT 1")
    fun getLoggedInProfileFlow(): Flow<StudentProfile?>

    @Query("SELECT * FROM student_profile WHERE isCurrentlyLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInProfile(): StudentProfile?

    @Query("UPDATE student_profile SET isCurrentlyLoggedIn = 0")
    suspend fun logoutAllProfiles()

    @Query("UPDATE student_profile SET isCurrentlyLoggedIn = 1 WHERE email = :email")
    suspend fun loginProfile(email: String)

    // AI Chat History
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_message ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessage>>

    @Query("DELETE FROM chat_message")
    suspend fun clearChatHistory()

    // Books & Notes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookNote(book: BookNote)

    @Query("SELECT * FROM book_note ORDER BY id DESC")
    fun getAllBookNotesFlow(): Flow<List<BookNote>>

    // Question Bank
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionItem(question: QuestionItem)

    @Query("SELECT * FROM question_item ORDER BY id DESC")
    fun getAllQuestionsFlow(): Flow<List<QuestionItem>>

    // Lecture Schedule
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: LectureItem)

    @Query("SELECT * FROM lecture_item ORDER BY id ASC")
    fun getAllLecturesFlow(): Flow<List<LectureItem>>

    // Exam Schedule
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamItem)

    @Query("SELECT * FROM exam_item ORDER BY date ASC")
    fun getAllExamsFlow(): Flow<List<ExamItem>>

    // Notifications
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("SELECT * FROM notification_item ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationItem>>

    @Query("UPDATE notification_item SET isRead = 1")
    suspend fun markAllNotificationsAsRead()
}
