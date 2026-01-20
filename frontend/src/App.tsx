import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './hooks/useAuth'
import { ProtectedRoute } from './components/ProtectedRoute'

// Pages
import LoginPage from './pages/login/page'
import RegisterPage from './pages/register/page'
import DashboardPage from './pages/dashboard/page'
import AdminPage from './pages/admin/page'
import PortfolioPage from './pages/dashboard/portfolio/page'
import WalletPage from './pages/dashboard/wallet/page'
import MarketsPage from './pages/dashboard/markets/page'
import HistoryPage from './pages/dashboard/history/page'
import AccountPage from './pages/dashboard/account/page'
import AIInsightsPage from './pages/dashboard/ai-insights/page'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/portfolio"
            element={
              <ProtectedRoute>
                <PortfolioPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/wallet"
            element={
              <ProtectedRoute>
                <WalletPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/markets"
            element={
              <ProtectedRoute>
                <MarketsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/history"
            element={
              <ProtectedRoute>
                <HistoryPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/account"
            element={
              <ProtectedRoute>
                <AccountPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/ai-insights"
            element={
              <ProtectedRoute>
                <AIInsightsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <ProtectedRoute>
                <AdminPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </Router>
    </AuthProvider>
  )
}

export default App
