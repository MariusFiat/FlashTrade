
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Shield, Smartphone, Key } from "lucide-react"

export function SecuritySettings() {
  return (
    <Card className="bg-card border-border/50">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Shield className="h-5 w-5 text-primary" />
          Security
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="space-y-4">
          <div className="flex items-center justify-between p-4 bg-secondary/50 rounded-lg">
            <div className="flex items-center gap-3">
              <div className="bg-primary/10 p-2 rounded-lg">
                <Key className="h-5 w-5 text-primary" />
              </div>
              <div>
                <div className="font-medium">Password</div>
                <p className="text-sm text-muted-foreground">Last changed 45 days ago</p>
              </div>
            </div>
            <Button variant="outline" size="sm">
              Change
            </Button>
          </div>

          <div className="flex items-center justify-between p-4 bg-secondary/50 rounded-lg">
            <div className="flex items-center gap-3">
              <div className="bg-primary/10 p-2 rounded-lg">
                <Smartphone className="h-5 w-5 text-primary" />
              </div>
              <div>
                <div className="font-medium flex items-center gap-2">
                  Two-Factor Authentication
                  <Badge variant="secondary" className="text-xs">
                    Enabled
                  </Badge>
                </div>
                <p className="text-sm text-muted-foreground">Extra security for your account</p>
              </div>
            </div>
            <Button variant="outline" size="sm">
              Manage
            </Button>
          </div>
        </div>

        <div className="space-y-3">
          <h4 className="font-medium">Active Sessions</h4>
          <div className="space-y-2">
            <div className="flex items-center justify-between p-3 bg-secondary/30 rounded-lg border border-border/50">
              <div>
                <div className="text-sm font-medium">Chrome on MacOS</div>
                <p className="text-xs text-muted-foreground">San Francisco, US • Current session</p>
              </div>
              <Badge variant="secondary" className="bg-profit/20 text-profit">
                Active
              </Badge>
            </div>
            <div className="flex items-center justify-between p-3 bg-secondary/30 rounded-lg border border-border/50">
              <div>
                <div className="text-sm font-medium">Safari on iPhone</div>
                <p className="text-xs text-muted-foreground">San Francisco, US • 2 days ago</p>
              </div>
              <Button variant="ghost" size="sm" className="text-xs text-destructive">
                Revoke
              </Button>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
