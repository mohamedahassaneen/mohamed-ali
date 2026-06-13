package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(private val appDao: AppDao) {
    // Auth & Profile
    val loggedInProfile: Flow<StudentProfile?> = appDao.getLoggedInProfileFlow()

    suspend fun getProfileByEmail(email: String): StudentProfile? = appDao.getProfileByEmail(email)

    suspend fun registerStudent(profile: StudentProfile) = appDao.insertProfile(profile)

    suspend fun loginStudent(email: String): Boolean {
        val user = appDao.getProfileByEmail(email)
        return if (user != null) {
            appDao.logoutAllProfiles()
            appDao.loginProfile(email)
            true
        } else {
            false
        }
    }

    suspend fun logoutStudent() = appDao.logoutAllProfiles()

    // Chat
    val chatMessages: Flow<List<ChatMessage>> = appDao.getAllMessagesFlow()

    suspend fun saveChatMessage(message: ChatMessage) = appDao.insertChatMessage(message)

    suspend fun clearChatHistory() = appDao.clearChatHistory()

    // Books & Notes
    val bookNotes: Flow<List<BookNote>> = appDao.getAllBookNotesFlow()

    suspend fun addBookNote(book: BookNote) = appDao.insertBookNote(book)

    // Question Bank
    val questions: Flow<List<QuestionItem>> = appDao.getAllQuestionsFlow()

    suspend fun addQuestion(question: QuestionItem) = appDao.insertQuestionItem(question)

    // Lecture Schedule
    val lectures: Flow<List<LectureItem>> = appDao.getAllLecturesFlow()

    suspend fun addLecture(lecture: LectureItem) = appDao.insertLecture(lecture)

    // Exam Schedule
    val exams: Flow<List<ExamItem>> = appDao.getAllExamsFlow()

    suspend fun addExam(exam: ExamItem) = appDao.insertExam(exam)

    // Notifications
    val notifications: Flow<List<NotificationItem>> = appDao.getAllNotificationsFlow()

    suspend fun addNotification(notification: NotificationItem) = appDao.insertNotification(notification)

    suspend fun markAllNotificationsAsRead() = appDao.markAllNotificationsAsRead()

    // Database pre-population
    suspend fun prepopulateIfEmpty() {
        // Run checks using lists instead of individual flows to make it non-blocking and robust
        val booksExist = appDao.getAllBookNotesFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!booksExist) {
            val sampleBooks = listOf(
                BookNote(
                    title = "Introduction to Quantum Computing",
                    description = "Comprehensive guide on quantum logic circuits, qubits, and Grover's algorithm.",
                    category = "Book",
                    faculty = "Faculty of Science",
                    resourceUrl = "https://example.com/files/quantum_intro.pdf"
                ),
                BookNote(
                    title = "Advanced OS Summarized Notes",
                    description = "Concise cheat sheet covering VM paging, process scheduling, mutexes, and deadlocks.",
                    category = "Note",
                    faculty = "Faculty of Computer Science",
                    resourceUrl = "https://example.com/files/os_cheat_sheet.pdf"
                ),
                BookNote(
                    title = "Database Systems & Design Manual",
                    description = "Lab guide covering relational algebra, PostgreSQL queries, indexing, and normalization rules.",
                    category = "Lab Manual",
                    faculty = "Faculty of Information Technology",
                    resourceUrl = ""
                ),
                BookNote(
                    title = "AI & Deep Learning 101",
                    description = "Standard textbook highlighting foundational concepts in DNNs, Backpropagation, and Transformers.",
                    category = "Book",
                    faculty = "Faculty of Engineering",
                    resourceUrl = "https://example.com/files/deep_learning.pdf"
                )
            )
            for (book in sampleBooks) {
                appDao.insertBookNote(book)
            }
        }

        val lecturesExist = appDao.getAllLecturesFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!lecturesExist) {
            val sampleLectures = listOf(
                LectureItem(
                    subject = "Data Structures & Algos",
                    professor = "Dr. Sarah Jenkins",
                    classroom = "Lab 3",
                    startTime = "09:00 AM",
                    endTime = "10:30 AM",
                    dayOfWeek = "Monday"
                ),
                LectureItem(
                    subject = "Database Systems",
                    professor = "Prof. Alan Turing",
                    classroom = "Lecture Hall B",
                    startTime = "11:00 AM",
                    endTime = "12:30 PM",
                    dayOfWeek = "Tuesday"
                ),
                LectureItem(
                    subject = "Human-Computer Interaction",
                    professor = "Prof. Grace Hopper",
                    classroom = "Studio Hall A",
                    startTime = "01:00 PM",
                    endTime = "02:30 PM",
                    dayOfWeek = "Wednesday"
                ),
                LectureItem(
                    subject = "Distributed Architectures",
                    professor = "Dr. Leslie Lamport",
                    classroom = "Lab 5",
                    startTime = "10:00 AM",
                    endTime = "11:30 AM",
                    dayOfWeek = "Thursday"
                )
            )
            for (lec in sampleLectures) {
                appDao.insertLecture(lec)
            }
        }

        val examsExist = appDao.getAllExamsFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!examsExist) {
            val sampleExams = listOf(
                ExamItem(
                    subject = "Database Systems Final",
                    date = "June 22, 2026",
                    startTime = "09:00 AM",
                    duration = "3 hours",
                    hall = "Main Gym / Exam Hall A"
                ),
                ExamItem(
                    subject = "Distributed Architectures Midterm",
                    date = "June 25, 2026",
                    startTime = "11:00 AM",
                    duration = "2 hours",
                    hall = "Room 102, CS Wing"
                ),
                ExamItem(
                    subject = "Human-Computer Interaction Practical",
                    date = "June 28, 2026",
                    startTime = "01:00 PM",
                    duration = "1.5 hours",
                    hall = "Lab 1 and 2"
                )
            )
            for (ex in sampleExams) {
                appDao.insertExam(ex)
            }
        }

        val questionsExist = appDao.getAllQuestionsFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!questionsExist) {
            val sampleQuestions = listOf(
                QuestionItem(
                    subject = "Algorithms",
                    question = "What is the average time complexity of Search in a balanced Binary Search Tree (BST)?",
                    options = "O(1),O(log n),O(n),O(n log n)",
                    correctAnswer = "O(log n)",
                    category = "Quiz"
                ),
                QuestionItem(
                    subject = "Operating Systems",
                    question = "In garbage collection, which reference type prevents an object from being reclaimed by the garbage collector?",
                    options = "Weak reference,Soft reference,Strong reference,Phantom reference",
                    correctAnswer = "Strong reference",
                    category = "Midterm"
                ),
                QuestionItem(
                    subject = "Network Engineering",
                    question = "Which layer of the OSI model determines the routing of packets from source to destination?",
                    options = "Transport Layer,Network Layer,Data Link Layer,Application Layer",
                    correctAnswer = "Network Layer",
                    category = "Final"
                )
            )
            for (q in sampleQuestions) {
                appDao.insertQuestionItem(q)
            }
        }

        val notificationsExist = appDao.getAllNotificationsFlow().firstOrNull()?.isNotEmpty() ?: false
        if (!notificationsExist) {
            val sampleNotifications = listOf(
                NotificationItem(
                    title = "Midterm Examination Schedules Released!",
                    body = "The official summer midterm slots have been updated. Make sure to review your exam cards for time conflicts and room locations.",
                    isRead = false
                ),
                NotificationItem(
                    title = "New Book Resources Added",
                    body = "The Science and Engineering Faculty library lists have been updated. Log into the books tab to view raw download links.",
                    isRead = false
                ),
                NotificationItem(
                    title = "AI Companion Active Page",
                    body = "You have unlimited access to the Smart AI Chatbot powered by Gemini Model 3.5 Flash. Ask math questions, code reviews, and lecture explainers whenever needed.",
                    isRead = true
                )
            )
            for (notif in sampleNotifications) {
                appDao.insertNotification(notif)
            }
        }
    }
}
