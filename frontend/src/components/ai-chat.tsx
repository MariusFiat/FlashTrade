import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Brain, Send, Loader2 } from "lucide-react"
import { useState, useRef, useEffect } from "react"
import { aiApi, ChatMessage } from "@/services/aiApi"

export function AIChat() {
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      role: "assistant",
      content: "Hello! I'm your AI trading assistant. Ask me about market trends, stock analysis, or trading strategies.",
      timestamp: new Date().toISOString()
    },
  ])
  const [input, setInput] = useState("")
  const [loading, setLoading] = useState(false)
  const [suggestions, setSuggestions] = useState<string[]>([
    "Recommend stocks to buy",
    "Analyze my portfolio risk"
  ])
  const messagesEndRef = useRef<HTMLDivElement>(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const handleSend = async () => {
    if (!input.trim() || loading) return

    const userMessage: ChatMessage = {
      role: "user",
      content: input,
      timestamp: new Date().toISOString()
    }

    setMessages(prev => [...prev, userMessage])
    setInput("")
    setLoading(true)

    try {
      const response = await aiApi.sendChatMessage(input)
      
      const assistantMessage: ChatMessage = {
        role: "assistant",
        content: response.response,
        timestamp: new Date().toISOString()
      }

      setMessages(prev => [...prev, assistantMessage])
      
      if (response.suggestions && response.suggestions.length > 0) {
        setSuggestions(response.suggestions)
      }
    } catch (error) {
      console.error('Error sending message:', error)
      const errorMessage: ChatMessage = {
        role: "assistant",
        content: "I apologize, but I encountered an error. Please try again.",
        timestamp: new Date().toISOString()
      }
      setMessages(prev => [...prev, errorMessage])
    } finally {
      setLoading(false)
    }
  }

  const handleSuggestionClick = (suggestion: string) => {
    setInput(suggestion)
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Brain className="h-5 w-5 text-primary" />
          AI Assistant
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-3 max-h-80 overflow-y-auto pr-2">
          {messages.map((message, index) => (
            <div
              key={index}
              className={`p-3 rounded-lg text-sm whitespace-pre-line ${
                message.role === "user" 
                  ? "bg-primary text-primary-foreground ml-8" 
                  : "bg-secondary/50 mr-8"
              }`}
            >
              {message.content}
            </div>
          ))}
          {loading && (
            <div className="flex items-center gap-2 p-3 bg-secondary/50 rounded-lg mr-8">
              <Loader2 className="h-4 w-4 animate-spin" />
              <span className="text-sm text-muted-foreground">AI is thinking...</span>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        <div className="flex gap-2">
          <Input
            placeholder="Ask about stocks, trends, strategies..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && !loading && handleSend()}
            disabled={loading}
            className="bg-secondary border-border"
          />
          <Button size="icon" onClick={handleSend} disabled={loading || !input.trim()}>
            {loading ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <Send className="h-4 w-4" />
            )}
          </Button>
        </div>

        <div className="flex flex-wrap gap-2">
          {suggestions.map((suggestion, index) => (
            <Button
              key={index}
              variant="outline"
              size="sm"
              className="text-xs bg-transparent"
              onClick={() => handleSuggestionClick(suggestion)}
              disabled={loading}
            >
              {suggestion}
            </Button>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}