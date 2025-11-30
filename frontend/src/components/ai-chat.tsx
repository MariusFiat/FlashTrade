
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Brain, Send } from "lucide-react"
import { useState } from "react"

const mockMessages = [
  {
    role: "assistant",
    content:
      "Hello! I'm your AI trading assistant. Ask me anything about market trends, stock analysis, or trading strategies.",
  },
]

export function AIChat() {
  const [messages, setMessages] = useState(mockMessages)
  const [input, setInput] = useState("")

  const handleSend = () => {
    if (!input.trim()) return

    setMessages([
      ...messages,
      { role: "user", content: input },
      { role: "assistant", content: "Based on current market analysis, I can help you with that..." },
    ])
    setInput("")
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
        <div className="space-y-3 max-h-80 overflow-y-auto">
          {messages.map((message, index) => (
            <div
              key={index}
              className={`p-3 rounded-lg text-sm ${
                message.role === "user" ? "bg-primary text-primary-foreground ml-8" : "bg-secondary/50 mr-8"
              }`}
            >
              {message.content}
            </div>
          ))}
        </div>

        <div className="flex gap-2">
          <Input
            placeholder="Ask about stocks, trends, strategies..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleSend()}
            className="bg-secondary border-border"
          />
          <Button size="icon" onClick={handleSend}>
            <Send className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex flex-wrap gap-2">
          <Button
            variant="outline"
            size="sm"
            className="text-xs bg-transparent"
            onClick={() => setInput("What stocks should I buy today?")}
          >
            Stock recommendations
          </Button>
          <Button
            variant="outline"
            size="sm"
            className="text-xs bg-transparent"
            onClick={() => setInput("Analyze my portfolio risk")}
          >
            Portfolio analysis
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}
