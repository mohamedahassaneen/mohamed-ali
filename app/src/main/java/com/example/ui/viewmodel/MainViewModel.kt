package com.example.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.network.GeminiContent
import com.example.data.network.GeminiPart
import com.example.data.network.GeminiRetrofitClient
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object ForgotPassword : Screen()
    object Home : Screen()
    object BooksNotes : Screen()
    object QuestionBank : Screen()
    object LectureSchedule : Screen()
    object ExamSchedule : Screen()
    object AiAssistant : Screen()
    object Notifications : Screen()
    object Profile : Screen()
    object Settings : Screen()
}

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // Theme state (system dark mode can act as default if desired)
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Navigation and Backstack
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen, clearStack: Boolean = false) {
        if (clearStack) {
            backStack.clear()
        } else {
            backStack.add(_currentScreen.value)
        }
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    // Auth flows
    val loggedInStudent: StateFlow<StudentProfile?> = repository.loggedInProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val registeredProfiles = mutableStateListOf<StudentProfile>()

    // Message input & state
    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Db Flows
    val chatHistory: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bookNotes: StateFlow<List<BookNote>> = repository.bookNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val questions: StateFlow<List<QuestionItem>> = repository.questions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val lectures: StateFlow<List<LectureItem>> = repository.lectures
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val exams: StateFlow<List<ExamItem>> = repository.exams
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Temp form states
    val loginError = mutableStateOf("")
    val registerError = mutableStateOf("")
    val forgotPasswordStatus = mutableStateOf("")

    // Toggle Dark Mode
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    // Login logic
    fun login(email: String, pword: String) {
        if (email.isEmpty() || pword.isEmpty()) {
            loginError.value = "All fields are required."
            return
        }
        viewModelScope.launch {
            val success = repository.loginStudent(email)
            if (success) {
                loginError.value = ""
                navigateTo(Screen.Home, clearStack = true)
            } else {
                loginError.value = "Incorrect details or student not found."
            }
        }
    }

    // Register student
    fun register(
        fullName: String,
        email: String,
        pword: String,
        pwordConfirm: String,
        phone: String,
        university: String,
        faculty: String,
        department: String,
        level: String
    ) {
        if (fullName.isEmpty() || email.isEmpty() || pword.isEmpty() || phone.isEmpty() ||
            university.isEmpty() || faculty.isEmpty() || department.isEmpty() || level.isEmpty()) {
            registerError.value = "All fields must be completed."
            return
        }
        if (pword != pwordConfirm) {
            registerError.value = "Passwords do not match."
            return
        }
        if (pword.length < 6) {
            registerError.value = "Password must be at least 6 characters."
            return
        }

        viewModelScope.launch {
            val existing = repository.getProfileByEmail(email)
            if (existing != null) {
                registerError.value = "Email is already registered."
                return@launch
            }

            val p = StudentProfile(
                email = email,
                fullName = fullName,
                phoneNumber = phone,
                university = university,
                faculty = faculty,
                department = department,
                academicLevel = level,
                isCurrentlyLoggedIn = true
            )
            repository.registerStudent(p)
            registerError.value = ""
            navigateTo(Screen.Home, clearStack = true)
        }
    }

    // Forgot Password simulation
    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            forgotPasswordStatus.value = "Please enter your student email."
            return
        }
        viewModelScope.launch {
            val user = repository.getProfileByEmail(email)
            if (user != null) {
                forgotPasswordStatus.value = "Success: A credentials recovery token has been transmitted to $email."
            } else {
                forgotPasswordStatus.value = "Error: Input student email could not be located in database records."
            }
        }
    }

    // Logout student
    fun logout() {
        viewModelScope.launch {
            repository.logoutStudent()
            navigateTo(Screen.Login, clearStack = true)
        }
    }

    // Send chat messages
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return

        viewModelScope.launch {
            // First save user message
            val userMsg = ChatMessage(text = text, sender = "user")
            repository.saveChatMessage(userMsg)

            // Let UI load replying status
            _isChatLoading.value = true

            // Gather past logs to give context
            val history = chatHistory.value.plus(userMsg).map {
                GeminiContent(
                    parts = listOf(GeminiPart(text = it.text)),
                    role = if (it.sender == "user") "user" else "model"
                )
            }

            // Call Retrofit client directly
            val reply = GeminiRetrofitClient.fetchChatResponse(history)

            // Save reply
            val aimsg = ChatMessage(text = reply, sender = "model")
            repository.saveChatMessage(aimsg)

            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    // Mark notifications read
    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // Addition elements for rich interactions
    fun addNewBook(title: String, desc: String, category: String, college: String) {
        viewModelScope.launch {
            repository.addBookNote(
                BookNote(title = title, description = desc, category = category, faculty = college)
            )
        }
    }

    fun addNewQuestion(subject: String, question: String, optionsStr: String, answer: String, type: String) {
        viewModelScope.launch {
            repository.addQuestion(
                QuestionItem(subject = subject, question = question, options = optionsStr, correctAnswer = answer, category = type)
            )
        }
    }

    fun addNewLecture(subject: String, prof: String, classroom: String, start: String, end: String, day: String) {
        viewModelScope.launch {
            repository.addLecture(
                LectureItem(subject = subject, professor = prof, classroom = classroom, startTime = start, endTime = end, dayOfWeek = day)
            )
        }
    }

    fun addNewExam(subject: String, date: String, start: String, duration: String, hall: String) {
        viewModelScope.launch {
            repository.addExam(
                ExamItem(subject = subject, date = date, startTime = start, duration = duration, hall = hall)
            )
        }
    }
}

// Factory instantiation
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
