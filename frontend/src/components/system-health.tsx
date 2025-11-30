
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Server, Database, Wifi, Zap } from "lucide-react"
import { Progress } from "@/components/ui/progress"

export function SystemHealth() {
  const services = [
    { name: "Trading Engine", status: "Operational", uptime: "99.98%", icon: Zap, color: "text-profit" },
    { name: "Database", status: "Operational", uptime: "99.95%", icon: Database, color: "text-profit" },
    { name: "API Gateway", status: "Operational", uptime: "99.99%", icon: Server, color: "text-profit" },
    { name: "WebSocket", status: "Operational", uptime: "99.92%", icon: Wifi, color: "text-profit" },
  ]

  const metrics = [
    { label: "CPU Usage", value: 45 },
    { label: "Memory", value: 62 },
    { label: "Network", value: 38 },
  ]

  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Server className="h-5 w-5 text-primary" />
          System Health
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="space-y-3">
          {services.map((service, index) => {
            const Icon = service.icon
            return (
              <div key={index} className="flex items-center justify-between p-3 bg-secondary/30 rounded-lg">
                <div className="flex items-center gap-3">
                  <Icon className={`h-4 w-4 ${service.color}`} />
                  <div>
                    <div className="text-sm font-medium">{service.name}</div>
                    <div className="text-xs text-muted-foreground">Uptime: {service.uptime}</div>
                  </div>
                </div>
                <Badge variant="secondary" className="bg-profit/20 text-profit">
                  {service.status}
                </Badge>
              </div>
            )
          })}
        </div>

        <div className="space-y-3 pt-4 border-t border-border/50">
          {metrics.map((metric, index) => (
            <div key={index} className="space-y-2">
              <div className="flex items-center justify-between text-sm">
                <span className="text-muted-foreground">{metric.label}</span>
                <span className="font-medium">{metric.value}%</span>
              </div>
              <Progress value={metric.value} className="h-2" />
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}
