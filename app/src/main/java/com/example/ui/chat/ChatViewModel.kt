package com.example.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.chat.ChatMessage
import com.example.data.chat.ChatKnowledgeBase
import com.example.data.chat.GeminiChatService
import com.example.data.chat.MessageSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val chatService = GeminiChatService()

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            sender = MessageSender.BOT,
            text = ChatKnowledgeBase.initialGreetings,
            quickReplies = ChatKnowledgeBase.quickQuestions
        )
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isTyping.value) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = userText.trim()
        )
        _messages.value = _messages.value + userMessage
        _isTyping.value = true

        viewModelScope.launch {
            try {
                val botReply = chatService.getAssistantResponse(
                    prompt = userText,
                    conversationHistory = _messages.value
                )
                _messages.value = _messages.value + botReply
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatKnowledgeBase.getLocalResponse(userText)
            } finally {
                _isTyping.value = false
            }
        }
    }

    fun resetChat() {
        _messages.value = listOf(
            ChatMessage(
                sender = MessageSender.BOT,
                text = ChatKnowledgeBase.initialGreetings,
                quickReplies = ChatKnowledgeBase.quickQuestions
            )
        )
    }
}
