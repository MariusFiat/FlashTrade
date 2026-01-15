
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Label } from "@/components/ui/label"
import { useState, useEffect } from "react"
import { AlertCircle, Loader2 } from "lucide-react"
import type { UserProfile } from "@/types/user"
import { userApi } from "@/services/userApi"

export function AccountSettings() {
  const [isEditing, setIsEditing] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: ''
  })

  useEffect(() => {
    fetchUserProfile()
  }, [])

  const fetchUserProfile = async () => {
    try {
      setIsLoading(true)
      setError(null)

      const data = await userApi.getUserProfile()
      setProfile(data)
      setFormData({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        phoneNumber: data.phoneNumber || ''
      })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load profile')
    } finally {
      setIsLoading(false)
    }
  }

  const handleSave = async () => {
    try {
      setIsSaving(true)
      setError(null)
      setSuccess(null)

      const result = await userApi.updateUserProfile({
        email: formData.email,
        firstName: formData.firstName,
        lastName: formData.lastName,
        phoneNumber: formData.phoneNumber || null
      })

      setSuccess(result.message || 'Profile updated successfully')
      setIsEditing(false)
      await fetchUserProfile()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to save changes')
    } finally {
      setIsSaving(false)
    }
  }

  const handleCancel = () => {
    if (profile) {
      setFormData({
        firstName: profile.firstName,
        lastName: profile.lastName,
        email: profile.email,
        phoneNumber: profile.phoneNumber || ''
      })
    }
    setIsEditing(false)
    setError(null)
    setSuccess(null)
  }

  if (isLoading) {
    return (
      <Card className="bg-card border-border/50">
        <CardContent className="flex items-center justify-center py-8">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="bg-card border-border/50">
      <CardHeader className="flex flex-row items-center justify-between space-y-0">
        <CardTitle>Profile Information</CardTitle>
        <Button variant="outline" size="sm" onClick={() => isEditing ? handleCancel() : setIsEditing(true)}>
          {isEditing ? "Cancel" : "Edit"}
        </Button>
      </CardHeader>
      <CardContent className="space-y-4">
        {error && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        {success && (
          <Alert className="border-green-500/50 bg-green-500/10">
            <AlertDescription className="text-green-600 dark:text-green-400">{success}</AlertDescription>
          </Alert>
        )}
        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-2">
            <Label htmlFor="firstName">First Name</Label>
            <Input 
              id="firstName" 
              value={formData.firstName}
              onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              disabled={!isEditing} 
              className="bg-secondary border-border" 
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="lastName">Last Name</Label>
            <Input 
              id="lastName" 
              value={formData.lastName}
              onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              disabled={!isEditing} 
              className="bg-secondary border-border" 
            />
          </div>
        </div>
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input
            id="email"
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            disabled={!isEditing}
            className="bg-secondary border-border"
          />
        </div>
        <div className="space-y-2">
          <Label htmlFor="phone">Phone Number</Label>
          <Input
            id="phone"
            type="tel"
            value={formData.phoneNumber}
            onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
            disabled={!isEditing}
            className="bg-secondary border-border"
          />
        </div>
        {isEditing && (
          <Button className="w-full" onClick={handleSave} disabled={isSaving}>
            {isSaving ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Saving...
              </>
            ) : (
              'Save Changes'
            )}
          </Button>
        )}
      </CardContent>
    </Card>
  )
}
