package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey val email: String,
    val fullName: String,
    val phoneNumber: String,
    val university: String,
    val faculty: String,
    val department: String,
    val academicLevel: String,
    val isCurrentlyLoggedIn: Boolean = false
)

@Entity(tableName = "chat_message")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val sender: String, // "user" or "model"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "book_note")
data class BookNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // "Book", "Note", "Lab Manual"
    val faculty: String,
    val resourceUrl: String = ""
)

@Entity(tableName = "question_item")
data class QuestionItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val question: String,
    val options: String, // Comma separated options if multiple choice
    val correctAnswer: String,
    val category: String // "Midterm", "Final", "Quiz"
)

@Entity(tableName = "lecture_item")
data class LectureItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val professor: String,
    val classroom: String,
    val startTime: String, // e.g. "09:00 AM"
    val endTime: String,   // e.g. "10:30 AM"
    val dayOfWeek: String  // e.g. "Monday"
)

@Entity(tableName = "exam_item")
data class ExamItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val date: String,      // e.g. "June 18, 2026"
    val startTime: String, // e.g. "11:00 AM"
    val duration: String,  // e.g. "2 hours"
    val hall: String       // e.g. "Grand Auditorium"
)

@Entity(tableName = "notification_item")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
