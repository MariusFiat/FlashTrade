import { Bell, User, TrendingUp, Menu } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Link, useLocation } from "react-router-dom"

export function DashboardHeader() {
  const { pathname } = useLocation()

  return (
    <header className="sticky top-0 z-50 w-full border-b border-border/50 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto flex h-16 items-center justify-between px-6">
        {/* Logo */}
        <div className="flex items-center gap-6">
          <Link to="/dashboard" className="flex items-center gap-2">
            <div className="bg-primary p-1.5 rounded-lg">
              <TrendingUp className="h-5 w-5 text-primary-foreground" />
            </div>
            <span className="text-xl font-bold hidden sm:inline">FlashTrade</span>
          </Link>

          {/* Navigation */}
          <nav className="hidden md:flex items-center gap-6 text-sm">
            <Link
              to="/dashboard"
              className={`font-medium hover:text-primary transition-colors ${
                pathname === "/dashboard" ? "text-primary" : "text-muted-foreground"
              }`}
            >
              Dashboard
            </Link>
            <Link
              to="/dashboard/portfolio"
              className={`hover:text-primary transition-colors ${
                pathname === "/dashboard/portfolio" ? "text-primary font-medium" : "text-muted-foreground"
              }`}
            >
              Portfolio
            </Link>
            <Link
              to="/dashboard/history"
              className={`hover:text-primary transition-colors ${
                pathname === "/dashboard/history" ? "text-primary font-medium" : "text-muted-foreground"
              }`}
            >
              History
            </Link>
            <Link
              to="/dashboard/markets"
              className={`hover:text-primary transition-colors ${
                pathname === "/dashboard/markets" ? "text-primary font-medium" : "text-muted-foreground"
              }`}
            >
              Markets
            </Link>
          </nav>
        </div>

        {/* Actions */}
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" className="relative">
            <Bell className="h-5 w-5" />
            <span className="absolute top-1 right-1 h-2 w-2 bg-primary rounded-full" />
          </Button>

          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon" className="rounded-full">
                <User className="h-5 w-5" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-56">
              <DropdownMenuLabel>My Account</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem asChild>
                <Link to="/dashboard/account">Account Settings</Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild>
                <Link to="/dashboard/wallet">Wallet</Link>
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem className="text-destructive">Log out</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <Button variant="ghost" size="icon" className="md:hidden">
            <Menu className="h-5 w-5" />
          </Button>
        </div>
      </div>
    </header>
  )
}
