import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Eye, EyeOff, AlertCircle } from "lucide-react"
import { Link, useNavigate } from "react-router-dom"
import { Checkbox } from "@/components/ui/checkbox"
import { useAuth } from "@/hooks/useAuth"

export function RegisterForm() {
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const { register } = useAuth()

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)

    const formData = new FormData(e.currentTarget)
    const email = formData.get('email') as string
    const password = formData.get('password') as string
    const confirmPassword = formData.get('confirmPassword') as string
    const firstName = formData.get('firstName') as string
    const lastName = formData.get('lastName') as string

    if (password !== confirmPassword) {
      setError('Passwords do not match')
      setIsLoading(false)
      return
    }

    try {
      await register({ email, password, firstName, lastName })
      navigate("/dashboard")
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Card className="border-border/50">
      <CardHeader className="space-y-1">
        <CardTitle className="text-2xl font-bold">{"Create an account"}</CardTitle>
        <CardDescription>{"Start trading with AI-powered insights today"}</CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-4">
          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="firstName">{"First name"}</Label>
              <Input id="firstName" name="firstName" placeholder="First name" required className="bg-secondary border-border" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="lastName">{"Last name"}</Label>
              <Input id="lastName" name="lastName" placeholder="Last name" required className="bg-secondary border-border" />
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="email">{"Email"}</Label>
            <Input
              id="email"
              name="email"
              type="email"
              placeholder="trader@example.com"
              required
              className="bg-secondary border-border"
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="password">{"Password"}</Label>
            <div className="relative">
              <Input
                id="password"
                name="password"
                type={showPassword ? "text" : "password"}
                placeholder="Create a strong password"
                required
                className="bg-secondary border-border pr-10"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
              </button>
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="confirmPassword">{"Confirm password"}</Label>
            <div className="relative">
              <Input
                id="confirmPassword"
                name="confirmPassword"
                type={showConfirmPassword ? "text" : "password"}
                placeholder="Re-enter your password"
                required
                className="bg-secondary border-border pr-10"
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                {showConfirmPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
              </button>
            </div>
          </div>
          <div className="flex items-center space-x-2 mb-2">
            <Checkbox id="terms" required />
            <label
              htmlFor="terms"
              className="text-sm text-muted-foreground leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
            >
              {"I agree to the "}
              <a href="https://dotfic.com/zDyk" target="_blank" rel="noopener noreferrer" className="text-primary hover:underline">
                {"terms and conditions"}
              </a>
            </label>
          </div>
        </CardContent>
        <CardFooter className="flex flex-col gap-4">
          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? "Creating account..." : "Create account"}
          </Button>
          <div className="text-sm text-center text-muted-foreground">
            {"Already have an account? "}
            <Link to="/login" className="text-primary hover:underline font-medium">
              {"Sign in"}
            </Link>
          </div>
        </CardFooter>
      </form>
    </Card>
  )
}
